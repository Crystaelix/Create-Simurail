package com.crystaelix.simurail.compat.offroad.brass_borehead_bearing;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.ryanhcode.offroad.content.blocks.borehead_bearing.BoreheadBearingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

public class BrassBoreheadBearingBlockEntity extends BoreheadBearingBlockEntity {

	protected FilteringBehaviour filtering;

	public BrassBoreheadBearingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);
		filtering = new FilteringBehaviour(this, new FilterSlot());
		filtering.setLabel(Component.translatable("block.simurail.brass_borehead_bearing.filter"));
		behaviours.add(filtering);
	}

	public boolean canMine(BlockState state) {
		if(filtering == null || filtering.getFilter().isEmpty()) {
			return true;
		}
		ItemStack stack = new ItemStack(state.getBlock());
		return stack.isEmpty() || !filtering.test(stack);
	}

	public FilteringBehaviour getFilteringBehaviour() {
		return filtering;
	}

	private static class FilterSlot extends CenteredSideValueBoxTransform {

		public FilterSlot() {
			super(BrassBoreheadBearingBlock::isFilterSide);
		}

		@Override
		public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
			Vec3 offset = super.getLocalOffset(level, pos, state);
			Direction facing = state.getValue(BlockStateProperties.FACING);
			if(getSide().getAxis() == facing.getAxis()) {
				return offset;
			}
			// 12 px slab so shfits center by 2px
			return offset.subtract(Vec3.atLowerCornerOf(facing.getNormal()).scale(2 / 16.0));
		}

		// adds roll so filter rendering gets aligned
		@Override
		public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
			super.rotate(level, pos, state, ms);

			Direction side = getSide();
			Direction facing = state.getValue(BlockStateProperties.FACING);
			if(facing.getAxis() == side.getAxis()) {
				return;
			}

			// the frame Sided#rotate leaves behind, as world directions
			Direction boxUp;
			Direction boxRight;
			if(side.getAxis().isVertical()) {
				boxUp = side == Direction.UP ? Direction.NORTH : Direction.SOUTH;
				boxRight = Direction.WEST;
			}
			else {
				boxUp = Direction.UP;
				boxRight = side.getClockWise();
			}

			float roll;
			if(facing == boxUp) {
				return;
			}
			else if(facing == boxUp.getOpposite()) {
				roll = 180;
			}
			else if(facing == boxRight) {
				roll = 270;
			}
			else {
				roll = 90;
			}

			TransformStack.of(ms).rotateZDegrees(roll);
		}
	}
}
