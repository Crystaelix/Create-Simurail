package com.crystaelix.simurail.content.gangway_joint;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;

import dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CenteredGangwayJointBlock extends HorizontalDirectionalBlock implements BlockSubLevelAssemblyListener, ProperWaterloggedBlock {

	public static final MapCodec<CenteredGangwayJointBlock> CODEC = simpleCodec(CenteredGangwayJointBlock::new);
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public static final VoxelShape NORTH_SHAPE = box(0, 0, 8, 16, 16, 16);
	public static final VoxelShape SOUTH_SHAPE = box(0, 0, 0, 16, 16, 8);
	public static final VoxelShape WEST_SHAPE = box(8, 0, 0, 16, 16, 16);
	public static final VoxelShape EAST_SHAPE = box(0, 0, 0, 8, 16, 16);

	protected CenteredGangwayJointBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(POWERED, false).setValue(WATERLOGGED, false));
	}

	@Override
	protected MapCodec<CenteredGangwayJointBlock> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(POWERED, WATERLOGGED);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState state = defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
		return withWater(state, context);
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		updateWater(level, state, pos);
		return state;
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return fluidState(state);
	}

	@Override
	public void afterMove(ServerLevel originLevel, ServerLevel resultingLevel, BlockState newState, BlockPos oldPos, BlockPos newPos) {

	}
}
