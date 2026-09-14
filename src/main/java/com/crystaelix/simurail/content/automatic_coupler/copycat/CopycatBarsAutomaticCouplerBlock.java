package com.crystaelix.simurail.content.automatic_coupler.copycat;

import com.mojang.serialization.MapCodec;

import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class CopycatBarsAutomaticCouplerBlock extends HorizontalDirectionalBlock {

	private static final MapCodec<CopycatBarsAutomaticCouplerBlock> CODEC = simpleCodec(CopycatBarsAutomaticCouplerBlock::new);

	public static final VoxelShaper SHAPES = VoxelShaper.forHorizontal(box(0, 0, 0, 16, 16, 3), Direction.SOUTH);
	public static final VoxelShaper TOP_SHAPES = VoxelShaper.forHorizontal(box(0, 4, 0, 16, 16, 3), Direction.SOUTH);
	public static final VoxelShaper BOTTOM_SHAPES = VoxelShaper.forHorizontal(box(0, 0, 0, 16, 12, 3), Direction.SOUTH);

	public static final EnumProperty<CopycatAutomaticCouplerShape> SHAPE = CopycatAutomaticCouplerBlock.SHAPE;

	public CopycatBarsAutomaticCouplerBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().
				setValue(SHAPE, CopycatAutomaticCouplerShape.FULL));
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
