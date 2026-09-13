package com.crystaelix.simurail.content.automatic_coupler.copycat;

import java.util.Optional;
import java.util.function.Consumer;

import com.crystaelix.simurail.content.SimurailBlockEntities;
import com.crystaelix.simurail.content.SimurailBlocks;
import com.crystaelix.simurail.content.gangway_frame.GangwayFrameBlock;
import com.crystaelix.simurail.content.gangway_frame.GangwayFrameBlockEntity;
import com.crystaelix.simurail.content.gangway_frame.GangwayFrameBlockShape;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.copycat.CopycatBlockEntity;
import com.simibubi.create.content.decoration.copycat.CopycatSpecialCases;
import com.simibubi.create.content.decoration.copycat.WaterloggedCopycatBlock;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntityTicker;

import dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener;
import dev.ryanhcode.sable.api.block.BlockSubLevelCollisionShape;
import dev.ryanhcode.sable.api.physics.collider.SableCollisionContext;
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
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

// Copycat version of AutomaticCouplerBlock
public abstract class CopycatAutomaticCouplerBlock extends WaterloggedCopycatBlock implements BlockSubLevelCollisionShape, BlockSubLevelAssemblyListener {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<CopycatAutomaticCouplerShape> SHAPE = EnumProperty.create("shape", CopycatAutomaticCouplerShape.class);
	public static final BooleanProperty GANGWAY = BooleanProperty.create("gangway");
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

	public CopycatAutomaticCouplerBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().
				setValue(SHAPE, CopycatAutomaticCouplerShape.FULL).
				setValue(GANGWAY, false).
				setValue(POWERED, false).
				setValue(TRIGGERED, false).
				setValue(WATERLOGGED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING, SHAPE, GANGWAY, POWERED, TRIGGERED);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Level level = context.getLevel();
		BlockState oldState = level.getBlockState(context.getClickedPos());
		if(oldState.is(SimurailBlocks.GANGWAY_FRAME) && GangwayFrameBlockShape.COUPLER.contains(oldState.getValue(GangwayFrameBlock.SHAPE))) {
			BlockState state = defaultBlockState().
					setValue(FACING, oldState.getValue(FACING)).
					setValue(POWERED, oldState.getValue(POWERED)).
					setValue(SHAPE, oldState.getValue(GangwayFrameBlock.SHAPE) == GangwayFrameBlockShape.D ?
							CopycatAutomaticCouplerShape.TOP : CopycatAutomaticCouplerShape.BOTTOM).
					setValue(GANGWAY, true).
					setValue(WATERLOGGED, oldState.getValue(WATERLOGGED));
			return state;
		}

		Direction clickedFace = context.getClickedFace();
		Direction direction;
		CopycatAutomaticCouplerShape shape = CopycatAutomaticCouplerShape.FULL;
		if(clickedFace.getAxis() == Direction.Axis.Y) {
			direction = context.getHorizontalDirection().getOpposite();
		}
		else {
			direction = clickedFace;
			double y = context.getClickLocation().y - context.getClickedPos().getY();
			if(y < 0.33) {
				shape = CopycatAutomaticCouplerShape.BOTTOM;
			}
			if(y > 0.67) {
				shape = CopycatAutomaticCouplerShape.TOP;
			}
		}
		BlockState state = defaultBlockState().
				setValue(FACING, direction).
				setValue(SHAPE, shape).
				setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
		return withWater(state, context);
	}

	@Override
	protected abstract VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context);

	protected abstract VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, int shapeIndex);

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		if(!(context instanceof SableCollisionContext) && level.getBlockEntity(pos) instanceof CopycatAutomaticCouplerBlockEntity be && be.collisionShape != null) {
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
	public abstract VoxelShape getSubLevelCollisionShape(BlockGetter blockGetter, BlockState state);

	protected boolean hasGangway(BlockState state, BlockGetter level, BlockPos pos) {
		return state.getValue(SHAPE) != CopycatAutomaticCouplerShape.FULL && state.getValue(GANGWAY);
	}

	protected boolean hitGangway(BlockState state, Vec3 hitLocation, LevelReader level, BlockPos pos, Player player) {
		return hasGangway(state, level, pos) && (state.getValue(SHAPE) == CopycatAutomaticCouplerShape.TOP ? hitLocation.y <= 0.25 : hitLocation.y >= 0.75);
	}

	protected GangwayFrameBlockShape getGangwayShape(BlockState state, BlockGetter level, BlockPos pos) {
		return hasGangway(state, level, pos) ?
				(state.getValue(SHAPE) == CopycatAutomaticCouplerShape.TOP ? GangwayFrameBlockShape.D : GangwayFrameBlockShape.U) :
					GangwayFrameBlockShape.NONE;
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
		if(level.isClientSide()) {
			return;
		}
		boolean previouslyPowered = state.getValue(POWERED);
		boolean isPowered = level.hasNeighborSignal(pos);
		if(isPowered) {
			level.setBlock(pos, state.setValue(POWERED, true).setValue(TRIGGERED, false), UPDATE_CLIENTS);
			if(previouslyPowered != isPowered) {
				withCouplerBlockEntityDo(level, pos, CopycatAutomaticCouplerBlockEntity::tryDisconnectGangway);
			}
		}
		else if(!state.getValue(TRIGGERED)) {
			level.setBlock(pos, state.setValue(POWERED, false), UPDATE_CLIENTS);
		}
	}

	@Override
	protected boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
		return state.getValue(SHAPE) != CopycatAutomaticCouplerShape.FULL && !state.getValue(GANGWAY) &&
				useContext.getItemInHand().is(SimurailBlocks.GANGWAY_FRAME.asItem());
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		double hitX = state.getValue(FACING).getAxis().choose(hitResult.getLocation().z - pos.getZ(), 0, hitResult.getLocation().x - pos.getX());
		double hitY = hitResult.getLocation().y - hitResult.getBlockPos().getY();
		if(stack.getItem() instanceof DyeItem dye) {
			if(hitX >= 0.25 && hitX <= 0.75 && hitY >= 0.3125 && hitY <= 0.6875) {
				withCouplerBlockEntityDo(level, pos, be -> be.setColor(dye.getDyeColor().getFireworkColor()));
				level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS);
			}
			else if(hitGangway(state, hitResult.getLocation().subtract(Vec3.atLowerCornerOf(pos)), level, pos, player)){
				withCouplerBlockEntityDo(level, pos, be -> be.setGangwayColor(dye.getDyeColor().getFireworkColor()));
				level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS);
			}
			return ItemInteractionResult.SUCCESS;
		}
		else if(stack.isEmpty()) {
			if(hitX >= 0.25 && hitX <= 0.75 && hitY >= 0.3125 && hitY <= 0.6875) {
				if(player.isSecondaryUseActive()) {
					if(!state.getValue(POWERED)) {
						BlockState newState = state.setValue(POWERED, true).setValue(TRIGGERED, true);
						level.setBlock(pos, newState, UPDATE_CLIENTS);
						if(!level.isClientSide()) {
							withCouplerBlockEntityDo(level, pos, CopycatAutomaticCouplerBlockEntity::tryDisconnectGangway);
						}
					}
					else if(state.getValue(TRIGGERED)) {
						BlockState newState = state.setValue(POWERED, false).setValue(TRIGGERED, false);
						level.setBlock(pos, newState, UPDATE_CLIENTS);
					}
				}
				else {
					withCouplerBlockEntityDo(level, pos, CopycatAutomaticCouplerBlockEntity::cycleLength);
					IWrenchable.playRotateSound(level, pos);
				}
			}
			else if(hitGangway(state, hitResult.getLocation().subtract(Vec3.atLowerCornerOf(pos)), level, pos, player)){
				if(!level.isClientSide()) {
					withCouplerBlockEntityDo(level, pos, be -> {
						if(be.getGangwayPartner() == null) {
							be.tryConnectGangway();
						}
						else if(player.isSecondaryUseActive()) {
							be.tryDisconnectGangway();
						}
					});
				}
			}
			return ItemInteractionResult.SUCCESS;
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
		double hitX = state.getValue(FACING).getAxis().choose(target.getLocation().z - pos.getZ(), 0, target.getLocation().x - pos.getX());
		double hitY = target.getLocation().y - pos.getY();
		if(hitX >= 0.25 && hitX <= 0.75 && hitY >= 0.3125 && hitY <= 0.6875) {
			return new ItemStack(this);
		}
		else if(hitGangway(state, target.getLocation().subtract(Vec3.atLowerCornerOf(pos)), level, pos, player)) {
			return SimurailBlocks.GANGWAY_FRAME.asStack();
		}
		return super.getCloneItemStack(state, target, level, pos, player);
	}

	@Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		Player player = context.getPlayer();
		double hitX = state.getValue(FACING).getAxis().choose(context.getClickLocation().z - pos.getZ(), 0, context.getClickLocation().x - pos.getX());
		double hitY = context.getClickLocation().y - pos.getY();
		if(hitX >= 0.25 && hitX <= 0.75 && hitY >= 0.3125 && hitY <= 0.6875) {
			withCouplerBlockEntityDo(level, pos, CopycatAutomaticCouplerBlockEntity::cycleType);
			IWrenchable.playRotateSound(level, pos);
			return InteractionResult.SUCCESS;
		}
		else if(hitGangway(state, context.getClickLocation().subtract(Vec3.atLowerCornerOf(pos)), level, pos, player)) {
			if(level.isClientSide()) {
				return InteractionResult.SUCCESS;
			}
			withCouplerBlockEntityDo(level, pos, be -> player.openMenu(be, buf -> CopycatAutomaticCouplerMenu.prepare(buf, be, true)));
			return InteractionResult.SUCCESS;
		}
		return super.onWrenched(state, context);
	}

	@Override
	public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
		if(!hasGangway(state, context.getLevel(), context.getClickedPos())) {
			return super.onSneakWrenched(state, context);
		}
		Level level = context.getLevel();
		if(level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		BlockPos pos = context.getClickedPos();
		Player player = context.getPlayer();
		boolean removedCoupler = hitGangway(state, context.getClickLocation().subtract(Vec3.atLowerCornerOf(pos)), level, pos, player);
		if(player != null && !player.isCreative()) {
			ItemStack stack = removedCoupler ? new ItemStack(this) : SimurailBlocks.GANGWAY_FRAME.asStack();
			player.getInventory().placeItemBackInInventory(stack);
		}
		CopycatAutomaticCouplerBlockEntity be = getCouplerBlockEntity(level, pos);
		if(removedCoupler) {
			super.onWrenched(state, context);
			BlockState newState = SimurailBlocks.GANGWAY_FRAME.getDefaultState().
					setValue(FACING, state.getValue(FACING)).
					setValue(GangwayFrameBlock.SHAPE, getGangwayShape(state, level, pos)).
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
			BlockState newState = state.setValue(GANGWAY, false);
			level.setBlock(pos, newState, UPDATE_ALL);
		}
		IWrenchable.playRemoveSound(level, pos);
		return InteractionResult.SUCCESS;
	}

	@Override
	public BlockEntityType<? extends CopycatBlockEntity> getBlockEntityType() {
		return SimurailBlockEntities.COPYCAT_COUPLER.get();
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return new SmartBlockEntityTicker<>();
	}

	public CopycatAutomaticCouplerBlockEntity getCouplerBlockEntity(BlockGetter worldIn, BlockPos pos) {
		return (CopycatAutomaticCouplerBlockEntity)super.getBlockEntity(worldIn, pos);
	}

	public Optional<CopycatAutomaticCouplerBlockEntity> getCouplerBlockEntityOptional(BlockGetter world, BlockPos pos) {
		return Optional.ofNullable(getCouplerBlockEntity(world, pos));
	}

	public void withCouplerBlockEntityDo(BlockGetter world, BlockPos pos, Consumer<CopycatAutomaticCouplerBlockEntity> action) {
		getCouplerBlockEntityOptional(world, pos).ifPresent(action);
	}

	@Override
	public boolean isIgnoredConnectivitySide(BlockAndTintGetter reader, BlockState state, Direction face, BlockPos fromPos, BlockPos toPos) {
		if(fromPos == null || toPos == null) {
			return true;
		}
		Direction facing = state.getValue(FACING);
		BlockState toState = reader.getBlockState(toPos);
		if(!toState.is(this)) {
			return facing != face.getOpposite();
		}
		BlockPos diff = fromPos.subtract(toPos);
		int coord = facing.getAxis().choose(diff.getX(), diff.getY(), diff.getZ());
		return facing == toState.getValue(FACING).getOpposite() &&
				(coord == 0 || coord != facing.getAxisDirection().getStep());
	}

	@Override
	public boolean canFaceBeOccluded(BlockState state, Direction face) {
		return state.getValue(FACING).getOpposite() == face;
	}

	@Override
	public boolean shouldFaceAlwaysRender(BlockState state, Direction face) {
		return state.getValue(FACING) == face;
	}

	@Override
	public boolean supportsExternalFaceHiding(BlockState state) {
		return true;
	}

	@Override
	public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir) {
		pos = pos.immutable();
		BlockPos otherPos = pos.relative(dir);
		BlockState material = getMaterial(level, pos);
		BlockState otherMaterial = getMaterial(level, otherPos);
		if(material == null) {
			material = AllBlocks.COPYCAT_BASE.getDefaultState();
		}
		if(otherMaterial == null) {
			otherMaterial = AllBlocks.COPYCAT_BASE.getDefaultState();
		}
		if(state.is(this) == neighborState.is(this)) {
			if(CopycatSpecialCases.isBarsMaterial(material) && CopycatSpecialCases.isBarsMaterial(otherMaterial)) {
				return state.getValue(FACING) == neighborState.getValue(FACING);
			}
			if(material.skipRendering(otherMaterial, dir.getOpposite())) {
				return isOccluded(state, neighborState, dir.getOpposite());
			}
		}
		return state.getValue(FACING) == dir.getOpposite() && material.skipRendering(neighborState, dir.getOpposite());
	}

	public static boolean isOccluded(BlockState state, BlockState other, Direction direction) {
		Direction facing = state.getValue(FACING);
		CopycatAutomaticCouplerShape shape = state.getValue(SHAPE);
		if(facing.getOpposite() == other.getValue(FACING) && direction == facing) {
			return true;
		}
		if(other.getValue(FACING) != facing) {
			return false;
		}
		if(other.getValue(SHAPE) != shape) {
			return false;
		}
		return direction.getAxis() != facing.getAxis();
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	public void afterMove(ServerLevel originLevel, ServerLevel resultingLevel, BlockState newState, BlockPos oldPos, BlockPos newPos) {
		withCouplerBlockEntityDo(resultingLevel, newPos, CopycatAutomaticCouplerBlockEntity::afterMove);
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
}
