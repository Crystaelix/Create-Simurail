package com.crystaelix.simurail.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.simibubi.create.content.trains.track.TrackBlock;

import dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.LevelTickAccess;

@Mixin(TrackBlock.class)
public abstract class TrackBlockMixin extends Block implements BlockSubLevelAssemblyListener {

	public TrackBlockMixin(Properties properties) {
		super(properties);
	}

	@Shadow
	private void updateGirders(BlockState state, Level kevel, BlockPos pos, LevelTickAccess<Block> blockTicks) {};

	@Override
	public void afterMove(ServerLevel originLevel, ServerLevel resultingLevel, BlockState newState, BlockPos oldPos, BlockPos newPos) {
		LevelTickAccess<Block> blockTicks = resultingLevel.getBlockTicks();
		// if this is true then onPlace was not called
		if(!blockTicks.hasScheduledTick(newPos, this)) {
			resultingLevel.scheduleTick(newPos, this, 1);
			updateGirders(newState, resultingLevel, newPos, blockTicks);
		}
	}
}
