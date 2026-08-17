package com.crystaelix.simurail.mixin.compat.railways;

import org.spongepowered.asm.mixin.Mixin;

import com.railwayteam.railways.content.buffer.TrackBufferBlock;

import dev.ryanhcode.sable.api.block.BlockSubLevelCollisionShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@Mixin(TrackBufferBlock.class)
public abstract class TrackBufferBlockMixin extends HorizontalDirectionalBlock implements BlockSubLevelCollisionShape {

	protected TrackBufferBlockMixin(Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getSubLevelCollisionShape(BlockGetter blockGetter, BlockState state) {
		return Shapes.block();
	}
}
