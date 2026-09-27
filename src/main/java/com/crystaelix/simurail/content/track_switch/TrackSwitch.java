package com.crystaelix.simurail.content.track_switch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.joml.Quaterniond;
import org.joml.Vector3d;

import com.crystaelix.simurail.api.math.SimurailMath;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.content.trains.signal.SingleBlockEntityEdgePoint;

import dev.ryanhcode.sable.companion.math.JOMLConversion;
import it.unimi.dsi.fastutil.doubles.DoubleArraySet;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.objects.ObjectDoublePair;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelAccessor;

public class TrackSwitch extends SingleBlockEntityEdgePoint {

	protected boolean initialized = false;
	protected final Quaterniond trackRot = new Quaterniond();
	protected final Vector3d trackLat = new Vector3d();

	protected boolean active = false;
	protected TrackNodeLocation straightExit;
	protected TrackNodeLocation leftExit;
	protected TrackNodeLocation rightExit;
	protected TrackSwitchState state = TrackSwitchState.STRAIGHT;

	@Override
	public void blockEntityRemoved(BlockPos blockEntityPos, boolean front) {
		active = false;
		super.blockEntityRemoved(blockEntityPos, front);
	}

	@Override
	public void invalidate(LevelAccessor level) {
		active = false;
		super.invalidate(level);
	}

	@Override
	public void setLocation(Couple<TrackNodeLocation> nodes, double position) {
		initialized = false;
		active = false;
		super.setLocation(nodes, position);
	}

	@Override
	public void tick(TrackGraph graph, boolean preTrains) {
		if(!initialized) {
			updateRot(graph);
			updateExits(graph);
			initialized = true;
		}
	}

	public void updateRot(TrackGraph graph) {
		TrackEdge edge = graph.getConnection(edgeLocation.map(graph::locateNode));
		if(edge != null) {
			Vector3d xDir = JOMLConversion.toJOML(edge.isTurn() ? edge.getTurn().axes.getSecond().scale(-1) : edge.getDirection(false));
			Vector3d yDir = JOMLConversion.toJOML(edge.getNormal(graph, 1));
			SimurailMath.rot(xDir, yDir, trackRot);
			trackRot.transform(SimurailMath.DIR_ZP, trackLat);
		}
	}

	public void updateExits(TrackGraph graph) {
		active = false;
		leftExit = null;
		rightExit = null;
		straightExit = null;

		TrackEdge edge = graph.getConnection(edgeLocation.map(graph::locateNode));
		if(edge == null || Math.abs(position - (edge.getLength() - 0.5)) > 0.875) {
			return;
		}

		List<TrackEdge> exitEdges = graph.getConnectionsFrom(edge.node2).values().
				stream().
				filter(e -> e != edge).
				filter(e -> !e.node2.getLocation().equals(edgeLocation.getFirst())).
				filter(e -> edge.getDirection(false).dot(e.getDirection(true)) > 0.875).
				toList();

		if(exitEdges.size() <= 1) {
			return;
		}

		active = true;

		TrackEdge leftEdge = null;
		TrackEdge rightEdge = null;
		TrackEdge straightEdge = null;
		double leftTurn = Double.MAX_VALUE;
		double rightTurn = -Double.MAX_VALUE;
		List<ObjectDoublePair<TrackEdge>> straightCandidates = new ArrayList<>(4);
		DoubleSet straightTurns = new DoubleArraySet(4);

		for(TrackEdge exitEdge : exitEdges) {
			double turn = 0;
			if(exitEdge.isTurn()) {
				turn = SimurailMath.cachedControlPoints(exitEdge.getTurn()).curvature(0, turnCurvature).dot(trackLat);
			}
			if(turn < leftTurn) {
				leftTurn = turn;
				leftEdge = exitEdge;
			}
			if(turn > rightTurn) {
				rightTurn = turn;
				rightEdge = exitEdge;
			}
			if(exitEdges.size() > 2 &&
					!straightTurns.contains(turn) &&
					(straightCandidates.size() < 3 || Math.abs(turn) < Math.abs(straightCandidates.getLast().rightDouble()))) {
				straightTurns.add(turn);
				straightCandidates.add(ObjectDoublePair.of(exitEdge, turn));
				straightCandidates.sort(Comparator.comparingDouble(c -> Math.abs(c.rightDouble())));
				if(straightCandidates.size() > 3) {
					straightTurns.remove(straightCandidates.removeLast().rightDouble());
				}
			}
		}

		if(exitEdges.size() > 2 && leftTurn < 0 && rightTurn > 0) {
			for(ObjectDoublePair<TrackEdge> candidate : straightCandidates) {
				if(candidate.rightDouble() != leftTurn && candidate.rightDouble() != rightTurn) {
					straightEdge = candidate.left();
					break;
				}
			}
		}

		if(leftTurn <= 0 && rightTurn <= 0) {
			leftExit = leftEdge.node2.getLocation();
			straightExit = rightEdge.node2.getLocation();
		}
		else if(leftTurn >= 0 && rightTurn >= 0) {
			rightExit = rightEdge.node2.getLocation();
			straightExit = leftEdge.node2.getLocation();
		}
		else {
			leftExit = leftEdge.node2.getLocation();
			rightExit = rightEdge.node2.getLocation();
			if(straightEdge != null) {
				straightExit = straightEdge.node2.getLocation();
			}
		}

		validateState();
	}

	public void trySetState(TrackSwitchState state) {
		this.state = state;
		validateState();
	}

	public void cycleState(boolean shiftDown) {
		switch(state) {
		case STRAIGHT -> {
			if(shiftDown) {
				if(leftExit != null) {
					trySetState(TrackSwitchState.LEFT);
				}
			}
			else {
				if(rightExit != null) {
					trySetState(TrackSwitchState.RIGHT);
				}
			}
		}
		case LEFT -> {
			if(!shiftDown) {
				if(straightExit != null) {
					trySetState(TrackSwitchState.STRAIGHT);
				}
				else if(rightExit != null) {
					trySetState(TrackSwitchState.RIGHT);
				}
			}
		}
		case RIGHT -> {
			if(shiftDown) {
				if(straightExit != null) {
					trySetState(TrackSwitchState.STRAIGHT);
				}
				else if(leftExit != null) {
					trySetState(TrackSwitchState.LEFT);
				}
			}
		}
		}
	}

	public void validateState() {
		if(straightExit == null && leftExit == null && rightExit == null) {
			return;
		}
		switch(state) {
		case STRAIGHT -> {
			if(straightExit == null) {
				state = TrackSwitchState.RIGHT;
			}
		}
		case LEFT -> {
			if(leftExit == null) {
				state = TrackSwitchState.STRAIGHT;
			}
		}
		case RIGHT -> {
			if(rightExit == null) {
				state = TrackSwitchState.STRAIGHT;
			}
		}
		}
	}

	public boolean isActive() {
		return active;
	}

	public TrackNodeLocation getCurrentExit() {
		validateState();
		return switch(state) {
		case STRAIGHT -> straightExit;
		case LEFT -> leftExit;
		case RIGHT -> rightExit;
		};
	}

	public Map.Entry<TrackNode, TrackEdge> selectTarget(List<Map.Entry<TrackNode, TrackEdge>> targets) {
		TrackNodeLocation currentExit = getCurrentExit();
		for(Map.Entry<TrackNode, TrackEdge> target : targets) {
			if(target.getKey().getLocation().equals(currentExit)) {
				return target;
			}
		}
		return null;
	}

	@Override
	public void write(CompoundTag tag, HolderLookup.Provider registries, DimensionPalette dimensions) {
		super.write(tag, registries, dimensions);
		tag.putByte("state", (byte)state.ordinal());
	}

	@Override
	public void read(CompoundTag tag, HolderLookup.Provider registries, boolean migration, DimensionPalette dimensions) {
		super.read(tag, registries, migration, dimensions);
		if(!migration) {
			state = TrackSwitchState.BY_ID.apply(tag.getByte("state"));
		}
	}

	private final Vector3d turnCurvature = new Vector3d();
}
