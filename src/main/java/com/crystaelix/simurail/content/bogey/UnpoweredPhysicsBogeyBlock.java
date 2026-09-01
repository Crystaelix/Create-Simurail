package com.crystaelix.simurail.content.bogey;

import com.crystaelix.simurail.content.SimurailItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public class UnpoweredPhysicsBogeyBlock extends PhysicsBogeyBlock {

	public UnpoweredPhysicsBogeyBlock(Properties properties) {
		super(properties);
	}

	@Override
	public boolean hasShaftTowards(LevelReader level, BlockPos pos, BlockState state, Direction face) {
		return false;
	}

	@Override
	public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
		if(state.getValue(INVERTED)) {
			return new ItemStack(SimurailItems.INVERTED_UNPOWERED_PHYSICS_BOGEY.get());
		}
		return new ItemStack(this);
	}

}
