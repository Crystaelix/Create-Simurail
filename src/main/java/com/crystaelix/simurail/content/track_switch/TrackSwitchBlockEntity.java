package com.crystaelix.simurail.content.track_switch;

import java.util.List;
import java.util.Objects;

import org.joml.Quaterniond;
import org.joml.Quaterniondc;

import com.crystaelix.simurail.compat.SimurailCompat;
import com.crystaelix.simurail.compat.computercraft.SimurailComputerCraftProxy;
import com.crystaelix.simurail.content.SimurailBlockEntities;
import com.crystaelix.simurail.content.SimurailEdgePoints;
import com.simibubi.create.api.contraption.transformable.TransformableBlockEntity;
import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import dan200.computercraft.api.peripheral.PeripheralCapability;
import dev.ryanhcode.sable.util.SableNBTUtils;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class TrackSwitchBlockEntity extends SmartBlockEntity implements TransformableBlockEntity {

	protected TrackTargetingBehaviour<TrackSwitch> edgePoint;
	protected AbstractComputerBehaviour computerBehaviour;

	protected boolean straightSignal = false;
	protected boolean leftSignal = false;
	protected boolean rightSignal = false;

	protected TrackSwitchState state = TrackSwitchState.STRAIGHT;
	protected final Quaterniond trackRot = new Quaterniond();
	protected TrackNodeLocation straightExit = null;
	protected TrackNodeLocation leftExit = null;
	protected TrackNodeLocation rightExit = null;

	protected TrackSwitchState lastState = TrackSwitchState.STRAIGHT;
	protected final LerpedFloat lerpedAngle = LerpedFloat.linear();

	public TrackSwitchBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		SimurailCompat.COMPUTERCRAFT.ifLoaded(() -> () -> {
			event.registerBlockEntity(
					PeripheralCapability.get(),
					SimurailBlockEntities.TRACK_SWITCH.get(),
					(be, context) -> be.computerBehaviour.getPeripheralCapability());
		});
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		behaviours.add(edgePoint = new TrackTargetingBehaviour<>(this, SimurailEdgePoints.TRACK_SWITCH));
		behaviours.add(computerBehaviour = SimurailComputerCraftProxy.behaviour(this));
	}

	public TrackSwitch getTrackSwitch() {
		if(level instanceof PonderLevel) {
			return null;
		}
		if(edgePoint.getEdgePoint() == null) {
			edgePoint.createEdgePoint();
		}
		return edgePoint.getEdgePoint();
	}

	@Override
	public void initialize() {
		super.initialize();
		if(!level.isClientSide()) {
			Direction direction = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
			straightSignal = hasSignal(direction) || hasSignal(direction.getOpposite());
			leftSignal = hasSignal(direction.getClockWise());
			rightSignal = hasSignal(direction.getCounterClockWise());

			TrackSwitch sw = getTrackSwitch();
			if(sw != null) {
				state = sw.state;
				trackRot.set(sw.trackRot);
				straightExit = sw.straightExit;
				leftExit = sw.leftExit;
				rightExit = sw.rightExit;
			}
		}
	}

	@Override
	public void tick() {
		super.tick();
		if(!level.isClientSide()) {
			updateRedstone();
			updateSync();
		}
		else {
			if(lastState != state) {
				double angle = switch(state) {
				case STRAIGHT -> 0;
				case LEFT -> -Math.PI * 0.25;
				case RIGHT -> Math.PI * 0.25;
				};
				lerpedAngle.chaseTimed(angle, 5);
				lastState = state;
			}
			lerpedAngle.tickChaser();
		}
	}

	protected void updateSync() {
		TrackSwitch sw = getTrackSwitch();
		if(sw != null) {
			boolean changed = false;
			if(state != sw.state) {
				state = sw.state;
				changed = true;
			}
			if(!trackRot.equals(sw.trackRot, 1E-4)) {
				trackRot.set(sw.trackRot);
				changed = true;
			}
			if(!Objects.equals(straightExit, sw.straightExit)) {
				straightExit = sw.straightExit;
				changed = true;
			}
			if(!Objects.equals(leftExit, sw.leftExit)) {
				leftExit = sw.leftExit;
				changed = true;
			}
			if(!Objects.equals(rightExit, sw.rightExit)) {
				rightExit = sw.rightExit;
				changed = true;
			}
			if(changed) {
				sendData();
			}
		}
	}

	protected void updateRedstone() {
		Direction direction = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
		boolean newStraightSignal = hasSignal(direction) || hasSignal(direction.getOpposite());
		boolean newLeftSignal = hasSignal(direction.getCounterClockWise());
		boolean newRightSignal = hasSignal(direction.getClockWise());
		TrackSwitch sw = getTrackSwitch();
		if(sw != null) {
			if(newStraightSignal && !straightSignal) {
				sw.trySetState(TrackSwitchState.STRAIGHT);
			}
			else if(newLeftSignal && !leftSignal) {
				sw.trySetState(TrackSwitchState.LEFT);
			}
			else if(newRightSignal && !rightSignal) {
				sw.trySetState(TrackSwitchState.RIGHT);
			}
		}
		straightSignal = newStraightSignal;
		leftSignal = newLeftSignal;
		rightSignal = newRightSignal;
	}

	public boolean hasStraightExit() {
		return straightExit != null;
	}

	public boolean hasLeftExit() {
		return leftExit != null;
	}

	public boolean hasRightExit() {
		return rightExit != null;
	}

	public TrackSwitchState getState() {
		return state;
	}

	public void trySetState(TrackSwitchState state) {
		TrackSwitch sw = getTrackSwitch();
		if(sw != null) {
			sw.trySetState(state);
		}
	}

	public void cycleState(boolean shiftDown) {
		TrackSwitch sw = getTrackSwitch();
		if(sw != null) {
			sw.cycleState(shiftDown);
		}
	}

	public boolean hasSignal(Direction direction) {
		return level.hasSignal(getBlockPos().relative(direction), direction);
	}

	public void setPonderTrackRot(Quaterniondc trackRot) {
		this.trackRot.set(trackRot);
	}

	public void setPonderState(TrackSwitchState state) {
		this.state = state;
	}

	public void setPonderExits(TrackNodeLocation straight, TrackNodeLocation left, TrackNodeLocation right) {
		straightExit = straight;
		leftExit = left;
		rightExit = right;
	}

	@Override
	public void transform(BlockEntity blockEntity, StructureTransform transform) {
		edgePoint.transform(blockEntity, transform);
	}

	@Override
	protected AABB createRenderBoundingBox() {
		return new AABB(Vec3.atCenterOf(getBlockPos()), Vec3.atCenterOf(edgePoint.getGlobalPosition())).inflate(2);
	}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		if(clientPacket) {
			tag.putByte("state", (byte)state.ordinal());
			tag.put("track_rot", SableNBTUtils.writeQuaternion(trackRot));
			if(straightExit != null) {
				tag.put("straight_exit", straightExit.write(null));
			}
			if(leftExit != null) {
				tag.put("left_exit", leftExit.write(null));
			}
			if(rightExit != null) {
				tag.put("right_exit", rightExit.write(null));
			}
		}
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		if(clientPacket) {
			state = TrackSwitchState.BY_ID.apply(tag.getByte("state"));
			trackRot.set(SableNBTUtils.readQuaternion(tag.getCompound("track_rot")));
			straightExit = tag.contains("straight_exit") ? TrackNodeLocation.read(tag.getCompound("straight_exit"), null) : null;
			leftExit = tag.contains("left_exit") ? TrackNodeLocation.read(tag.getCompound("left_exit"), null) : null;
			rightExit = tag.contains("right_exit") ? TrackNodeLocation.read(tag.getCompound("right_exit"), null) : null;
		}
	}
}
