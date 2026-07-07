package com.crystaelix.simurail.content.automatic_coupler;

import java.util.stream.IntStream;

import com.crystaelix.simurail.content.SimurailBlockEntities;
import com.crystaelix.simurail.content.SimurailBlocks;
import com.crystaelix.simurail.content.gangway_frame.GangwayFrameBlock;
import com.crystaelix.simurail.content.gangway_frame.GangwayFrameBlockEntity;
import com.crystaelix.simurail.content.gangway_frame.GangwayFrameBlockShape;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;

import dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener;
import dev.ryanhcode.sable.api.block.BlockSubLevelCollisionShape;
import dev.ryanhcode.sable.api.physics.collider.SableCollisionContext;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AutomaticCouplerBlock extends HorizontalDirectionalBlock implements IBE<AutomaticCouplerBlockEntity>, BlockSubLevelCollisionShape, BlockSubLevelAssemblyListener, ProperWaterloggedBlock, IWrenchable {

	public static final MapCodec<AutomaticCouplerBlock> CODEC = simpleCodec(AutomaticCouplerBlock::new);
	public static final EnumProperty<GangwayFrameBlockShape> GANGWAY_SHAPE = EnumProperty.create("gangway_shape", GangwayFrameBlockShape.class, GangwayFrameBlockShape.COUPLER);
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public static final VoxelShaper SHAPES = VoxelShaper.forHorizontal(box(5, 5, 0, 11, 11, 3), Direction.SOUTH);
	public static final VoxelShaper[] D_SHAPES = IntStream.range(0, 30).
			mapToObj(i -> VoxelShaper.forHorizontal(Shapes.or(box(5, 5, 0, 11, 11, 3), GangwayFrameBlockShape.D.getShape(Direction.SOUTH, i)), Direction.SOUTH)).
			toArray(VoxelShaper[]::new);
	public static final VoxelShaper[] U_SHAPES = IntStream.range(0, 30).
			mapToObj(i -> VoxelShaper.forHorizontal(Shapes.or(box(5, 5, 0, 11, 11, 3), GangwayFrameBlockShape.U.getShape(Direction.SOUTH, i)), Direction.SOUTH)).
			toArray(VoxelShaper[]::new);
	public static final VoxelShaper SUBLEVEL_SHAPES = VoxelShaper.forHorizontal(box(5, 5, 0, 11, 11, 0.25), Direction.SOUTH);
	public static final VoxelShaper SUBLEVEL_D_SHAPES = VoxelShaper.forHorizontal(Shapes.or(box(5, 5, 0, 11, 11, 0.25), box(0, 2, 0, 16, 4, 0.25)), Direction.SOUTH);
	public static final VoxelShaper SUBLEVEL_U_SHAPES = VoxelShaper.forHorizontal(Shapes.or(box(5, 5, 0, 11, 11, 0.25), box(0, 12, 0, 16, 14, 0.25)), Direction.SOUTH);

	public AutomaticCouplerBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(GANGWAY_SHAPE, GangwayFrameBlockShape.NONE).setValue(POWERED, false).setValue(WATERLOGGED, false));
	}

	@Override
	protected MapCodec<AutomaticCouplerBlock> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING, GANGWAY_SHAPE, POWERED, WATERLOGGED);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Level level = context.getLevel();
		BlockState oldState = level.getBlockState(context.getClickedPos());
		if(oldState.is(SimurailBlocks.GANGWAY_FRAME) && GangwayFrameBlockShape.COUPLER.contains(oldState.getValue(GangwayFrameBlock.SHAPE))) {
			BlockState state = defaultBlockState().
					setValue(FACING, oldState.getValue(FACING)).
					setValue(POWERED, oldState.getValue(POWERED)).
					setValue(GANGWAY_SHAPE, oldState.getValue(GangwayFrameBlock.SHAPE)).
					setValue(WATERLOGGED, oldState.getValue(WATERLOGGED));
			return state;
		}

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
		return switch(state.getValue(GANGWAY_SHAPE)) {
		case D -> D_SHAPES[0].get(state.getValue(FACING));
		case U -> U_SHAPES[0].get(state.getValue(FACING));
		case null, default -> SHAPES.get(state.getValue(FACING));
		};
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		if(!(context instanceof SableCollisionContext) && level.getBlockEntity(pos) instanceof AutomaticCouplerBlockEntity be && be.collisionShape != null) {
			return be.collisionShape;
		}
		return state.getShape(level, pos, context);
	}

	@Override
	protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return state.getShape(level, pos, context);
	}

	@Override
	public VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
		return state.getShape(level, pos);
	}

	@Override
	public VoxelShape getSubLevelCollisionShape(BlockGetter blockGetter, BlockState state) {
		return switch(state.getValue(GANGWAY_SHAPE)) {
		case D -> SUBLEVEL_D_SHAPES.get(state.getValue(FACING));
		case U -> SUBLEVEL_U_SHAPES.get(state.getValue(FACING));
		case null, default -> SUBLEVEL_SHAPES.get(state.getValue(FACING));
		};
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
		if(level.isClientSide()) {
			return;
		}
		boolean previouslyPowered = state.getValue(POWERED);
		boolean isPowered = level.hasNeighborSignal(pos);
		if(previouslyPowered != isPowered) {
			level.setBlock(pos, state.cycle(POWERED), UPDATE_CLIENTS);
			if(isPowered) {
				withBlockEntityDo(level, pos, AutomaticCouplerBlockEntity::tryDisconnectGangway);
			}
		}
	}

	@Override
	protected boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
		return useContext.getItemInHand().is(SimurailBlocks.GANGWAY_FRAME.asItem());
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		double hitY = hitResult.getLocation().y - hitResult.getBlockPos().getY();
		if(hitY > 0.3 && hitY < 0.7) {
			if(stack.isEmpty()) {
				withBlockEntityDo(level, pos, AutomaticCouplerBlockEntity::cycleLength);
				IWrenchable.playRotateSound(level, pos);
				return ItemInteractionResult.SUCCESS;
			}
			if(stack.getItem() instanceof DyeItem dye) {
				withBlockEntityDo(level, pos, be -> be.setColor(dye.getDyeColor().getFireworkColor()));
				level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS);
				return ItemInteractionResult.SUCCESS;
			}
		}
		else {
			if(stack.isEmpty()) {
				if(!level.isClientSide()) {
					withBlockEntityDo(level, pos, be -> {
						if(be.getGangwayPartner() == null) {
							be.tryConnectGangway();
						}
						else {
							be.tryDisconnectGangway();
						}
					});
				}
				return ItemInteractionResult.SUCCESS;
			}
			if(stack.getItem() instanceof DyeItem dye) {
				withBlockEntityDo(level, pos, be -> be.setGangwayColor(dye.getDyeColor().getFireworkColor()));
				level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS);
				return ItemInteractionResult.SUCCESS;
			}
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
		double hitY = target.getLocation().y - pos.getY();
		if(hitY > 0.3 && hitY < 0.7) {
			return new ItemStack(this);
		}
		return SimurailBlocks.GANGWAY_FRAME.asStack();
	}

	@Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		double hitY = context.getClickLocation().y - pos.getY();
		if(hitY > 0.3 && hitY < 0.7) {
			withBlockEntityDo(level, pos, AutomaticCouplerBlockEntity::cycleType);
			IWrenchable.playRotateSound(level, pos);
			return InteractionResult.SUCCESS;
		}
		else {
			if(level.isClientSide()) {
				return InteractionResult.SUCCESS;
			}
			Player player = context.getPlayer();
			withBlockEntityDo(level, pos, be -> player.openMenu(be, buf -> AutomaticCouplerMenu.prepare(buf, be, true)));
			return InteractionResult.SUCCESS;
		}
	}

	@Override
	public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
		if(state.getValue(GANGWAY_SHAPE) == GangwayFrameBlockShape.NONE) {
			return IWrenchable.super.onSneakWrenched(state, context);
		}
		Level level = context.getLevel();
		if(level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		BlockPos pos = context.getClickedPos();
		Player player = context.getPlayer();
		double hitY = context.getClickLocation().y - pos.getY();
		boolean removedCoupler = hitY > 0.3 && hitY < 0.7;
		if(player != null && !player.isCreative()) {
			ItemStack stack = removedCoupler ? SimurailBlocks.AUTOMATIC_COUPLER.asStack() : SimurailBlocks.GANGWAY_FRAME.asStack();
			player.getInventory().placeItemBackInInventory(stack);
		}
		AutomaticCouplerBlockEntity be = getBlockEntity(level, pos);
		if(removedCoupler) {
			BlockState newState = SimurailBlocks.GANGWAY_FRAME.getDefaultState().
					setValue(FACING, state.getValue(FACING)).
					setValue(GangwayFrameBlock.SHAPE, state.getValue(GANGWAY_SHAPE)).
					setValue(POWERED, state.getValue(POWERED)).
					setValue(WATERLOGGED, state.getValue(WATERLOGGED));
			level.setBlock(pos, newState, UPDATE_ALL);
			if(level.getBlockEntity(pos) instanceof GangwayFrameBlockEntity newBE) {
				newBE.restLength = be.gangwayRestLength;
				newBE.color = be.gangwayColor;
				newBE.setGangwayPartnerReverse(be.gangwayPartnerPos);
			}
		}
		else {
			be.removeGangwayPartner();
			be.gangwayRestLength = 0;
			be.gangwayColor = DyeColor.GRAY.getFireworkColor();
			BlockState newState = state.setValue(GANGWAY_SHAPE, GangwayFrameBlockShape.NONE);
			level.setBlock(pos, newState, UPDATE_ALL);
		}
		IWrenchable.playRemoveSound(level, pos);
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
}
