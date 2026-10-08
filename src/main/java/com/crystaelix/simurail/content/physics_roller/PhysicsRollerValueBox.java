package com.crystaelix.simurail.content.physics_roller;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;

import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class PhysicsRollerValueBox extends ValueBoxTransform.Sided {

	protected final float offset;

	public PhysicsRollerValueBox(float offset) {
		this.offset = offset;
	}

	@Override
	public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
		Direction facing = state.getValue(PhysicsRollerBlock.FACING);
		Vec3 modelOffset = getModelOffset(toModelSide(getSide(), facing));
		if(modelOffset == null) {
			return null;
		}

		return VecHelper.rotateCentered(modelOffset, AngleHelper.horizontalAngle(facing) + 180, Axis.Y);
	}

	@Override
	public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
		Direction side = getSide();
		PoseTransformStack stack = TransformStack.of(ms);

		// keep icon aligned with the block itself on the top face
		if(side.getAxis().isVertical()) {
			stack.rotateYDegrees(AngleHelper.horizontalAngle(state.getValue(PhysicsRollerBlock.FACING)));
		}

		stack.rotateYDegrees(AngleHelper.horizontalAngle(side) + 180).
		rotateXDegrees(side == Direction.UP ? 90 : 0);
	}

	@Override
	public boolean testHit(LevelAccessor level, BlockPos pos, BlockState state, Vec3 localHit) {
		if(!isSideActive(state, getSide())) {
			return false;
		}

		Vec3 localOffset = getLocalOffset(level, pos, state);
		return localOffset != null && localHit.distanceTo(localOffset) < scale / 3;
	}

	@Override
	protected boolean isSideActive(BlockState state, Direction direction) {
		return getModelOffset(toModelSide(direction, state.getValue(PhysicsRollerBlock.FACING))) != null;
	}

	@Override
	protected Vec3 getSouthLocation() {
		return Vec3.ZERO;
	}

	@Nullable
	protected Vec3 getModelOffset(Direction modelSide) {
		float slot = 8 + offset;
		return switch(modelSide) {
		case UP -> VecHelper.voxelSpace(slot, 15.5F, 11);
		case SOUTH -> VecHelper.voxelSpace(slot, 11, 15.5F);
		case WEST -> VecHelper.voxelSpace(0.5F, slot, 11);
		case EAST -> VecHelper.voxelSpace(15.5F, slot, 11);
		default -> null;
		};
	}

	protected static Direction toModelSide(Direction side, Direction facing) {
		if(side.getAxis().isVertical()) {
			return side;
		}
		Direction modelSide = Direction.NORTH;
		for(Direction current = facing; current != side; current = current.getClockWise()) {
			modelSide = modelSide.getClockWise();
		}
		return modelSide;
	}
}
