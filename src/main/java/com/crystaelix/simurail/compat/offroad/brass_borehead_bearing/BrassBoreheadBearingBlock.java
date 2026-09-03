package com.crystaelix.simurail.compat.offroad.brass_borehead_bearing;

import com.crystaelix.simurail.compat.offroad.SimurailOffroadBlockEntities;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;

import dev.ryanhcode.offroad.content.blocks.borehead_bearing.BoreheadBearingBlock;
import dev.ryanhcode.offroad.content.blocks.borehead_bearing.BoreheadBearingBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class BrassBoreheadBearingBlock extends BoreheadBearingBlock {

	public BrassBoreheadBearingBlock(Properties properties) {
		super(properties);
	}

	@Override
	public BlockEntityType<? extends BoreheadBearingBlockEntity> getBlockEntityType() {
		return SimurailOffroadBlockEntities.BRASS_BOREHEAD_BEARING.get();
	}

	public static boolean isFilterSide(BlockState state, Direction direction) {
		if(!(state.getBlock() instanceof DirectionalAxisKineticBlock block)) {
			return false;
		}
		return direction != state.getValue(BlockStateProperties.FACING) && direction.getAxis() != block.getRotationAxis(state);
	}
}
