package com.crystaelix.simurail.content.remote_controller;

import com.crystaelix.simurail.content.SimurailBlockEntities;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;

import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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

public class RemoteControllerBlock extends WrenchableDirectionalBlock implements IBE<RemoteControllerBlockEntity> {

	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public static final VoxelShaper SHAPE = VoxelShaper.forDirectional(box(2, 0, 2, 14, 3, 14), Direction.UP);

	public RemoteControllerBlock(Properties properties) {
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
				withBlockEntityDo(level, pos, be -> player.openMenu(be, buf -> RemoteControllerMenu.prepare(buf, be)));
			}
			return ItemInteractionResult.SUCCESS;
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if(level.isClientSide()) {
			withBlockEntityDo(level, pos, be -> RemoteControllerOutline.setTargetOutline(level, pos, be.getTargetPos()));
		}
		return InteractionResult.SUCCESS;
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
		if(!level.getBlockTicks().willTickThisTick(pos, this)) {
			level.scheduleTick(pos, this, 1);
		}
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		updatePower(state, level, pos);
		Direction attachedFace = state.getValue(FACING).getOpposite();
		BlockPos attachedPos = pos.relative(attachedFace);
		level.blockUpdated(pos, level.getBlockState(pos).getBlock());
		level.blockUpdated(attachedPos, level.getBlockState(attachedPos).getBlock());
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		if(state.getBlock() == oldState.getBlock() || movedByPiston) {
			return;
		}
		updatePower(state, level, pos);
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		IBE.onRemove(state, level, pos, newState);
	}

	public void updatePower(BlockState state, Level level, BlockPos pos) {
		if(level.isClientSide()) {
			return;
		}
		int value = 0;
		for(Direction direction : Iterate.directions) {
			value = Math.max(level.getSignal(pos.relative(direction), direction), value);
			if(state.getValue(FACING).getOpposite() != direction) {
				value = Math.max(level.getSignal(pos.relative(direction), Direction.UP), value);
			}
		}

		boolean previouslyPowered = state.getValue(POWERED);
		if(previouslyPowered != value > 0) {
			level.setBlock(pos, state.cycle(POWERED), Block.UPDATE_CLIENTS);
			if(!previouslyPowered) {
				withBlockEntityDo(level, pos, be -> be.updateRisingEdge());
			}
		}

		int power = value;
		withBlockEntityDo(level, pos, be -> be.setPower(power));
	}

	@Override
	public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
		return originalState;
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
		return side != null;
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
	public Class<RemoteControllerBlockEntity> getBlockEntityClass() {
		return RemoteControllerBlockEntity.class;
	}

	@Override
	public BlockEntityType<RemoteControllerBlockEntity> getBlockEntityType() {
		return SimurailBlockEntities.REMOTE_CONTROLLER.get();
	}
}
