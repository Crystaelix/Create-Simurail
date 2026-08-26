package com.crystaelix.simurail.content.bogey;

import com.crystaelix.simurail.content.SimurailBlockEntities;
import com.crystaelix.simurail.content.SimurailItems;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;

import dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.phys.shapes.Shapes;
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
		if(!context.replacingClickedOnBlock()) {
			Direction clickedFace = context.getClickedFace();
			BlockPos relativePos = context.getClickedPos().relative(context.getClickedFace().getOpposite());
			BlockState blockState = context.getLevel().getBlockState(relativePos);
			if(blockState.hasProperty(TrackBlock.SHAPE)) {
				switch(blockState.getValue(TrackBlock.SHAPE)) {
				case XO, TE, TW, CR_PDX, CR_NDX -> {
					if(clickedFace.getAxis() == Direction.Axis.Y && mainDirection.getAxis() != Direction.Axis.X) {
						for(Direction direction : context.getNearestLookingDirections()) {
							if(direction.getAxis() == Direction.Axis.X) {
								return direction;
							}
						}
					}
				}
				case AE, AW -> {
					if(clickedFace.getAxis() != Direction.Axis.Z && mainDirection.getAxis() != Direction.Axis.X) {
						for(Direction direction : context.getNearestLookingDirections()) {
							if(direction.getAxis() == Direction.Axis.X) {
								return direction;
							}
						}
					}
				}
				case ZO, TN, TS, CR_PDZ, CR_NDZ -> {
					if(clickedFace.getAxis() == Direction.Axis.Y && mainDirection.getAxis() != Direction.Axis.Z) {
						for(Direction direction : context.getNearestLookingDirections()) {
							if(direction.getAxis() == Direction.Axis.Z) {
								return direction;
							}
						}
					}
				}
				case AN, AS -> {
					if(clickedFace.getAxis() != Direction.Axis.X && mainDirection.getAxis() != Direction.Axis.Z) {
						for(Direction direction : context.getNearestLookingDirections()) {
							if(direction.getAxis() == Direction.Axis.Z) {
								return direction;
							}
						}
					}
				}
				case null, default -> {}
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
	public VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
		return Shapes.block();
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
		if(stack.isEmpty()) {
			if(!level.isClientSide()) {
				withBlockEntityDo(level, pos, be -> player.openMenu(be, buf -> PhysicsBogeyMenu.prepare(buf, be, player.isSecondaryUseActive())));
			}
			return ItemInteractionResult.SUCCESS;
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
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
	public void beforeMove(ServerLevel originLevel, ServerLevel resultingLevel, BlockState newState, BlockPos oldPos, BlockPos newPos) {
		withBlockEntityDo(originLevel, oldPos, bogey -> bogey.beforeMove(newPos));
	}

	@Override
	public void afterMove(ServerLevel originLevel, ServerLevel resultingLevel, BlockState newState, BlockPos oldPos, BlockPos newPos) {
		withBlockEntityDo(resultingLevel, newPos, PhysicsBogeyBlockEntity::afterMove);
	}

	// Mixin overridable

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		super.onPlace(state, level, pos, oldState, movedByPiston);
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
