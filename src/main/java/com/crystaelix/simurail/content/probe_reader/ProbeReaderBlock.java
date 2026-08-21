package com.crystaelix.simurail.content.probe_reader;

import com.crystaelix.simurail.content.SimurailBlockEntities;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;

import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ProbeReaderBlock extends WrenchableDirectionalBlock implements IBE<ProbeReaderBlockEntity> {

	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public static final VoxelShaper SHAPE = VoxelShaper.forDirectional(box(2, 0, 2, 14, 3, 14), Direction.UP);

	public ProbeReaderBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(POWERED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(POWERED);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getClickedFace());
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE.get(state.getValue(FACING));
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if(!player.isSecondaryUseActive()) {
			if(!level.isClientSide()) {
				withBlockEntityDo(level, pos, be -> player.openMenu(be, buf -> ProbeReaderMenu.prepare(buf, be)));
			}
			return ItemInteractionResult.SUCCESS;
		}
		else if(stack.isEmpty()) {
			if(level.isClientSide()) {
				withBlockEntityDo(level, pos, be -> ProbeReaderOutline.setTargetOutline(level, pos, be.getTargetPos(), be.getTargetFront()));
			}
			return ItemInteractionResult.SUCCESS;
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean movedByPiston) {
		if(level.isClientSide()) {
			return;
		}
		Direction blockFacing = state.getValue(FACING);
		if(fromPos.equals(pos.relative(blockFacing.getOpposite()))) {
			if(!canSurvive(state, level, pos)) {
				level.destroyBlock(pos, true);
				return;
			}
		}
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		IBE.onRemove(state, level, pos, newState);
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return state.getValue(POWERED);
	}

	@Override
	public int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
		if(side != blockState.getValue(FACING)) {
			return 0;
		}
		return getSignal(blockState, blockAccess, pos, side);
	}

	@Override
	public int getSignal(BlockState state, BlockGetter blockAccess, BlockPos pos, Direction side) {
		return getBlockEntityOptional(blockAccess, pos).map(ProbeReaderBlockEntity::getSignal).orElse(0);
	}

	@Override
	public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
		return originalState;
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return direction != null;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		BlockPos neighborPos = pos.relative(state.getValue(FACING).getOpposite());
		return !level.getBlockState(neighborPos).canBeReplaced();
	}

	@Override
	protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
		return false;
	}

	@Override
	public Class<ProbeReaderBlockEntity> getBlockEntityClass() {
		return ProbeReaderBlockEntity.class;
	}

	@Override
	public BlockEntityType<ProbeReaderBlockEntity> getBlockEntityType() {
		return SimurailBlockEntities.PROBE_READER.get();
	}
}
