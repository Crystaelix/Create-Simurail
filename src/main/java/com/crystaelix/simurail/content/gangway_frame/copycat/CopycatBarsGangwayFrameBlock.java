package com.crystaelix.simurail.content.gangway_frame.copycat;

import com.crystaelix.simurail.content.gangway_frame.GangwayFrameBlockShape;
import com.mojang.serialization.MapCodec;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class CopycatBarsGangwayFrameBlock extends HorizontalDirectionalBlock {

	private static final MapCodec<CopycatBarsGangwayFrameBlock> CODEC = simpleCodec(CopycatBarsGangwayFrameBlock::new);

	public static final EnumProperty<GangwayFrameBlockShape> SHAPE = CopycatGangwayFrameBlock.SHAPE;

	public CopycatBarsGangwayFrameBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(SHAPE, GangwayFrameBlockShape.U));
	}

	@Override
	protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING, SHAPE);
	}
}
