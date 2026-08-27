package com.crystaelix.simurail.content.bogey;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public class PowerlessBogeyBlock extends PhysicsBogeyBlock {

	public PowerlessBogeyBlock(Properties properties) {
		super(properties);
	}

	@Override
	public boolean hasShaftTowards(LevelReader level, BlockPos pos, BlockState state, Direction face) {
		return false;
	}

}
