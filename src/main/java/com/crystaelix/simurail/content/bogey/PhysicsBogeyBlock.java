package com.crystaelix.simurail.content.bogey;

import com.crystaelix.simurail.content.SimurailBlockEntities;
import com.crystaelix.simurail.content.SimurailItemTags;
import com.crystaelix.simurail.content.SimurailItems;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;

import dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PhysicsBogeyBlock extends HorizontalKineticBlock implements IBE<PhysicsBogeyBlockEntity>, BlockSubLevelAssemblyListener, ProperWaterloggedBlock {

	public static final BooleanProperty INVERTED = BlockStateProperties.INVERTED;

	public static final VoxelShape SHAPE = box(0, 5, 0, 16, 16, 16);
	public static final VoxelShape INVERTED_SHAPE = box(0, 0, 0, 16, 11, 16);

	public PhysicsBogeyBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(INVERTED, false).setValue(WATERLOGGED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(INVERTED, WATERLOGGED);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState state = defaultBlockState().setValue(HORIZONTAL_FACING, getPreferredHorizontalFacing(context));
		return withWater(state, context);
	}

	@Override
	public Direction getPreferredHorizontalFacing(BlockPlaceContext context) {
		Direction mainDirection = super.getPreferredHorizontalFacing(context);
		if(mainDirection == null) {
			mainDirection = context.getHorizontalDirection();
		}
		else if(mainDirection.getOpposite() == context.getHorizontalDirection()) {
			mainDirection = mainDirection.getOpposite();
		}
		if(!context.replacingClickedOnBlock() && context.getClickedFace().getAxis() == Direction.Axis.Y) {
			BlockPos relativePos = context.getClickedPos().relative(context.getClickedFace().getOpposite());
			BlockState blockState = context.getLevel().getBlockState(relativePos);
			if(blockState.hasProperty(TrackBlock.SHAPE)) {
				switch(blockState.getValue(TrackBlock.SHAPE)) {
				case XO, AE, AW, TE, TW, CR_PDX, CR_NDX: {
					if(mainDirection.getAxis() == Direction.Axis.X) {
						return mainDirection;
					}
					else {
						return Direction.EAST;
					}
				}
				case ZO, AN, AS, TN, TS, CR_PDZ, CR_NDZ: {
					if(mainDirection.getAxis() == Direction.Axis.Z) {
						return mainDirection;
					}
					else {
						return Direction.SOUTH;
					}
				}
				default:
				}
			}
		}
		return mainDirection;
	}

	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(worldIn, pos, state, placer, stack);
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
	public Direction.Axis getRotationAxis(BlockState state) {
		return state.getValue(HORIZONTAL_FACING).getAxis();
	}

	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face.getAxis() == state.getValue(HORIZONTAL_FACING).getAxis();
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return state.getValue(INVERTED) ? INVERTED_SHAPE : SHAPE;
	}

	@Override
	public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
		if(state.getValue(INVERTED)) {
			return new ItemStack(SimurailItems.INVERTED_PHYSICS_BOGEY.get());
		}
		return new ItemStack(this);
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if(stack.is(AllItems.WRENCH) || stack.is(SimurailItems.STEERING_CONNECTOR) || stack.is(SimurailItemTags.CEE_WIRE_SPOOLS)) {
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}
		if(stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof KineticBlock && hasShaftTowards(level, pos, state, hitResult.getDirection())) {
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}
		CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> withBlockEntityDo(level, pos, be -> PhysicsBogeyScreenOpener.displayScreen(be, player)));
		return ItemInteractionResult.SUCCESS;
	}

	@Override
	public Class<PhysicsBogeyBlockEntity> getBlockEntityClass() {
		return PhysicsBogeyBlockEntity.class;
	}

	@Override
	public BlockEntityType<PhysicsBogeyBlockEntity> getBlockEntityType() {
		return SimurailBlockEntities.PHYSICS_BOGEY.get();
	}

	@Override
	public void afterMove(ServerLevel originLevel, ServerLevel resultingLevel, BlockState newState, BlockPos oldPos, BlockPos newPos) {
		withBlockEntityDo(resultingLevel, newPos, PhysicsBogeyBlockEntity::afterMove);
	}

	// Mixin overridable

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		super.onPlace(state, level, pos, oldState, isMoving);
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		super.tick(state, level, pos, random);
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		return super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
		return super.onSneakWrenched(state, context);
	}
}
