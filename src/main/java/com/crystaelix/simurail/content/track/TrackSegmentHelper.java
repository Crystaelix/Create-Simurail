package com.crystaelix.simurail.content.track;

import java.util.Objects;
import java.util.Set;

import org.joml.Vector3d;
import org.joml.Vector3dc;

import com.crystaelix.simurail.api.math.SimurailMath;
import com.crystaelix.simurail.api.track.TrackSegment;
import com.crystaelix.simurail.api.track.TrackSegments;
import com.crystaelix.simurail.api.track.TrackTypeOverrides;
import com.crystaelix.simurail.api.util.SubLevelUtil;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackGraphHelper;
import com.simibubi.create.content.trains.graph.TrackGraphLocation;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;
import com.simibubi.create.content.trains.track.TrackPropagator;
import com.simibubi.create.content.trains.track.TrackShape;

import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import it.unimi.dsi.fastutil.objects.ObjectDoublePair;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class TrackSegmentHelper {

	public static TrackSegment fromTrackEdge(TrackGraph graph, TrackEdge edge, boolean reverse) {
		if(edge.isInterDimensional()) {
			return null;
		}
		else if(edge.isTurn()) {
			BezierConnection curve = edge.getTurn();
			return new CurvedTrackSegment(edge.node1.getLocation().getDimension(), curve, reverse ? curve.getSegmentCount() - 1 : 0);
		}
		else {
			double startOffset = edge.getPosition(graph, 0).y - edge.node1.getLocation().getLocation().y;
			double endOffset = edge.getPosition(graph, 1).y - edge.node2.getLocation().getLocation().y;
			boolean startPortal = graph.getConnectionsFrom(edge.node1).values().stream().anyMatch(TrackEdge::isInterDimensional);
			boolean endPortal = graph.getConnectionsFrom(edge.node2).values().stream().anyMatch(TrackEdge::isInterDimensional);
			return new StraightTrackSegment(edge.node1.getLocation(), edge.node2.getLocation(), startOffset, endOffset, startPortal, endPortal, JOMLConversion.toJOML(edge.getNormal(graph, 0.5)), edge.getTrackMaterial());
		}
	}

	public static TrackSegment findTrackSegment(Level level, SubLevel subLevel, Vector3dc globalPos, Vector3dc globalDir, Vector3dc globalNormal, boolean inverted, Set<TrackType> validTypes) {
		return TrackSegments.getSegmentFinders().
				stream().
				map(f -> f.findTrackSegment(level, subLevel, globalPos, globalDir, globalNormal, inverted, validTypes)).
				filter(Objects::nonNull).
				reduce((r, e) -> r.rightDouble() - e.rightDouble() > SimurailMath.EPSILON ? e : r).
				map(ObjectDoublePair::first).
				orElse(null);
	}

	public static ObjectDoublePair<StraightTrackSegment> findBlockTrackSegment(Level level, SubLevel subLevel, Vector3dc globalPos, Vector3dc globalDir, Vector3dc globalVert, boolean inverted, Set<TrackType> validTypes) {
		Vector3d scale = subLevel.logicalPose().scale();
		if(!Mth.equal(scale.x, scale.y) || !Mth.equal(scale.x, scale.z)) {
			return null;
		}

		double score = Double.POSITIVE_INFINITY;
		BlockPos trackPos = null;
		ITrackBlock trackBlock = null;
		Pair<Vec3, AxisDirection> trackAxis = null;
		Vector3d trackStart = new Vector3d();
		Vector3d trackEnd = new Vector3d();

		MutableBlockPos checkPos = new MutableBlockPos();
		Vector3d checkLocalPos = new Vector3d();
		Vector3d checkLocalDir = new Vector3d();
		Vector3d checkLocalVert = new Vector3d();
		Vector3d checkTrackDelta = new Vector3d();
		Vector3d checkTrackVert = new Vector3d();
		Vector3d checkTrackLat = new Vector3d();
		Vector3d checkTrackCenter = new Vector3d();
		Vector3d checkTrackStart = new Vector3d();

		for(SubLevel checkSubLevel : SubLevelUtil.getIntersectingSubLevels(level, globalPos, 1)) {
			if(subLevel == checkSubLevel) {
				continue;
			}
			Pose3dc checkPose = checkSubLevel == null ? SimurailMath.POSE_I : checkSubLevel.logicalPose();
			Vector3dc checkScale = checkPose.scale();
			if(!Mth.equal(checkScale.x(), checkScale.y()) || !Mth.equal(checkScale.y(), checkScale.z()) || !Mth.equal(checkScale.x(), scale.x)) {
				continue;
			}
			checkPose.transformPositionInverse(globalPos, checkLocalPos);
			checkPose.transformNormalInverse(globalDir, checkLocalDir);
			checkPose.transformNormalInverse(globalVert, checkLocalVert);
			for(int x = Mth.floor(checkLocalPos.x) - 1; x < Mth.ceil(checkLocalPos.x) + 1; ++x) {
				for(int y = Mth.floor(checkLocalPos.y) - 2; y < Mth.ceil(checkLocalPos.y) + 2; ++y) {
					for(int z = Mth.floor(checkLocalPos.z) - 1; z < Mth.ceil(checkLocalPos.z) + 1; ++z) {
						checkPos.set(x, y, z);
						if(!level.isLoaded(checkPos)) {
							continue;
						}
						BlockState checkState = level.getBlockState(checkPos);
						if(checkState.getBlock() instanceof ITrackBlock checkBlock) {
							TrackType type = TrackTypeOverrides.getTrackType(checkBlock.getMaterial());
							if(!validTypes.contains(type) && !TrackTypeOverrides.isUniversal(type)) {
								continue;
							}
							TrackShape shape = checkState.getOptionalValue(TrackBlock.SHAPE).orElse(TrackShape.NONE);
							Pair<Vec3, AxisDirection> checkAxis = checkBlock.getNearestTrackAxis(level, checkPos, checkState, JOMLConversion.toMojang(checkLocalDir));
							JOMLConversion.toJOML(checkAxis.getFirst(), checkTrackDelta);
							JOMLConversion.toJOML(checkBlock.getUpNormal(level, checkPos, checkState), checkTrackVert).normalize();
							checkTrackDelta.cross(checkTrackVert, checkTrackLat).normalize();
							JOMLConversion.atBottomCenterOf(checkPos, checkTrackCenter).add(0, checkBlock.getElevationAtCenter(level, checkPos, checkState), 0);
							if(shape.isPortal()) {
								checkTrackDelta.mul(2);
								switch(shape) {
								case TN -> checkTrackCenter.add(0, 0, -0.5);
								case TS -> checkTrackCenter.add(0, 0, 0.5);
								case TE -> checkTrackCenter.add(0.5, 0, 0);
								default -> checkTrackCenter.add(-0.5, 0, 0);
								}
							}
							checkTrackStart.set(checkTrackCenter).fma(-0.5, checkTrackDelta);
							if(!TrackSegment.inSegmentRange(checkTrackStart, checkTrackDelta, checkTrackVert, checkTrackLat, checkLocalPos, checkLocalVert, inverted)) {
								continue;
							}
							double orientScore = 1 - Math.abs(checkLocalDir.dot(checkTrackDelta) / checkTrackDelta.length());
							if(orientScore > 0.7) {
								continue;
							}
							double distScore = SimurailMath.distanceSquaredLinePoint(checkTrackCenter, checkTrackDelta, checkLocalPos);
							double checkScore = distScore + TrackSegments.ORIENT_SCORE_WEIGHT * orientScore;
							if(checkScore < score) {
								score = checkScore;
								trackPos = checkPos.immutable();
								trackBlock = checkBlock;
								trackAxis = checkAxis;
								trackStart.set(checkTrackStart);
								trackStart.add(checkTrackDelta, trackEnd);
							}
						}
					}
				}
			}
		}
		if(trackPos == null) {
			return null;
		}
		TrackGraphLocation graphLocation = TrackGraphHelper.getGraphLocationAt(level, trackPos, trackAxis.getSecond(), trackAxis.getFirst());
		if(graphLocation == null) {
			// Broken track, notify
			TrackPropagator.onRailAdded(level, trackPos, level.getBlockState(trackPos));
			return null;
		}
		TrackGraph graph = graphLocation.graph;
		TrackEdge edge = graph.getConnection(graphLocation.edge.map(graph::locateNode));
		if(edge == null) {
			// Also broken track, notify
			TrackPropagator.onRailAdded(level, trackPos, level.getBlockState(trackPos));
			return null;
		}
		double startOffset = edge.getPosition(graph, 0).y - edge.node1.getLocation().getLocation().y;
		double endOffset = edge.getPosition(graph, 1).y - edge.node2.getLocation().getLocation().y;
		boolean startPortal = graph.getConnectionsFrom(edge.node1).values().stream().anyMatch(TrackEdge::isInterDimensional);
		boolean endPortal = graph.getConnectionsFrom(edge.node2).values().stream().anyMatch(TrackEdge::isInterDimensional);
		Vector3d trackNormal = JOMLConversion.toJOML(edge.getNormal(graph, 0.5));
		return ObjectDoublePair.of(new StraightTrackSegment(graphLocation.edge.getFirst(), graphLocation.edge.getSecond(), startOffset, endOffset, startPortal, endPortal, trackNormal, trackBlock.getMaterial()), score);
	}
}
