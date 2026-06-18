package com.crystaelix.simurail.content.automatic_coupler;

import com.crystaelix.simurail.content.SimurailBlockEntities;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;

import dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener;
import dev.ryanhcode.sable.api.block.BlockSubLevelCollisionShape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AutomaticCouplerBlock extends HorizontalDirectionalBlock implements IBE<AutomaticCouplerBlockEntity>, BlockSubLevelCollisionShape, BlockSubLevelAssemblyListener, ProperWaterloggedBlock, IWrenchable {

	public static final MapCodec<AutomaticCouplerBlock> CODEC = simpleCodec(AutomaticCouplerBlock::new);
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public static final VoxelShape EAST_SHAPE = box(0, 5, 5, 3, 11, 11);
	public static final VoxelShape WEST_SHAPE = box(13, 5, 5, 16, 11, 11);
	public static final VoxelShape SOUTH_SHAPE = box(5, 5, 0, 11, 11, 3);
	public static final VoxelShape NORTH_SHAPE = box(5, 5, 13, 11, 11, 16);
	public static final VoxelShape EAST_COLLISION_SHAPE = box(0, 5, 5, 0.25, 11, 11);
	public static final VoxelShape WEST_COLLISION_SHAPE = box(15.75, 5, 5, 16, 11, 11);
	public static final VoxelShape SOUTH_COLLISION_SHAPE = box(5, 5, 0, 11, 11, 0.25);
	public static final VoxelShape NORTH_COLLISION_SHAPE = box(5, 5, 15.75, 11, 11, 16);

	public AutomaticCouplerBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(POWERED, false).setValue(WATERLOGGED, false));
	}

	@Override
	protected MapCodec<AutomaticCouplerBlock> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING, POWERED, WATERLOGGED);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction direction = context.getClickedFace();
		if(direction.getAxis() == Direction.Axis.Y) {
			direction = context.getHorizontalDirection().getOpposite();
		}
		BlockState state = defaultBlockState().
				setValue(FACING, direction).
				setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
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
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return switch(state.getValue(FACING)) {
		case EAST -> EAST_SHAPE;
		case WEST -> WEST_SHAPE;
		case SOUTH -> SOUTH_SHAPE;
		case NORTH -> NORTH_SHAPE;
		case null, default -> throw new IllegalArgumentException("Unexpected value: " + state.getValue(FACING));
		};
	}

	@Override
	public VoxelShape getSubLevelCollisionShape(BlockGetter blockGetter, BlockState state) {
		return switch(state.getValue(FACING)) {
		case EAST -> EAST_COLLISION_SHAPE;
		case WEST -> WEST_COLLISION_SHAPE;
		case SOUTH -> SOUTH_COLLISION_SHAPE;
		case NORTH -> NORTH_COLLISION_SHAPE;
		case null, default -> throw new IllegalArgumentException("Unexpected value: " + state.getValue(FACING));
		};
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
		if(level.isClientSide()) {
			return;
		}
		boolean previouslyPowered = state.getValue(POWERED);
		if(previouslyPowered != level.hasNeighborSignal(pos)) {
			level.setBlock(pos, state.cycle(POWERED), Block.UPDATE_CLIENTS);
		}
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if(stack.isEmpty()) {
			withBlockEntityDo(level, pos, AutomaticCouplerBlockEntity::cycleLength);
			IWrenchable.playRotateSound(level, pos);
			return ItemInteractionResult.SUCCESS;
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		withBlockEntityDo(level, pos, AutomaticCouplerBlockEntity::cycleType);
		IWrenchable.playRotateSound(level, pos);
		return InteractionResult.SUCCESS;
	}

	@Override
	public Class<AutomaticCouplerBlockEntity> getBlockEntityClass() {
		return AutomaticCouplerBlockEntity.class;
	}

	@Override
	public BlockEntityType<AutomaticCouplerBlockEntity> getBlockEntityType() {
		return SimurailBlockEntities.COUPLER.get();
	}

	@Override
	public void afterMove(ServerLevel originLevel, ServerLevel resultingLevel, BlockState newState, BlockPos oldPos, BlockPos newPos) {
		withBlockEntityDo(resultingLevel, newPos, AutomaticCouplerBlockEntity::afterMove);
	}
}
