package com.crystaelix.simurail.content.physics_roller;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class PhysicsRollerBlockItem extends BlockItem {

	public PhysicsRollerBlockItem(PhysicsRollerBlock block, Properties properties) {
		super(block, properties);
	}

	@Override
	public InteractionResult place(BlockPlaceContext context) {
		BlockPos clickedPos = context.getClickedPos();
		Level level = context.getLevel();
		BlockState stateBelow = level.getBlockState(clickedPos.below());
		if(!Block.isFaceFull(stateBelow.getCollisionShape(level, clickedPos.below()), Direction.UP)) {
			return super.place(context);
		}
		Direction clickedFace = context.getClickedFace();
		return super.place(BlockPlaceContext.at(context, clickedPos.relative(Direction.UP), clickedFace));
	}
}
