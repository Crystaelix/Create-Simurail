package com.crystaelix.simurail.content.gangway_frame.copycat;

import java.util.Optional;
import java.util.function.Consumer;

import com.crystaelix.simurail.content.SimurailBlockEntities;
import com.crystaelix.simurail.content.gangway_frame.GangwayFrameBlockShape;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.copycat.CopycatBlockEntity;
import com.simibubi.create.content.decoration.copycat.CopycatSpecialCases;
import com.simibubi.create.content.decoration.copycat.WaterloggedCopycatBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntityTicker;

import dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener;
import dev.ryanhcode.sable.api.block.BlockSubLevelCollisionShape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
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

// Copycat version of GangwayFrameBlock
public abstract class CopycatGangwayFrameBlock extends WaterloggedCopycatBlock implements BlockSubLevelCollisionShape, BlockSubLevelAssemblyListener {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<GangwayFrameBlockShape> SHAPE = EnumProperty.create("shape", GangwayFrameBlockShape.class, GangwayFrameBlockShape.COPYCAT);
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public static final double PI_DIV_6 = Math.PI / 6;

	public CopycatGangwayFrameBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(SHAPE, GangwayFrameBlockShape.U).setValue(POWERED, false).setValue(WATERLOGGED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING, SHAPE, POWERED);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction direction = context.getClickedFace();
		GangwayFrameBlockShape shape;
		if(direction.getAxis() == Direction.Axis.Y) {
			shape = direction == Direction.UP ? GangwayFrameBlockShape.D : GangwayFrameBlockShape.U;
			direction = context.getHorizontalDirection().getOpposite();
		}
		else {
			double x = switch(direction) {
			case SOUTH -> context.getClickLocation().x - context.getClickedPos().getX() - 0.5;
			case NORTH -> context.getClickedPos().getX() - context.getClickLocation().x + 0.5;
			case WEST -> context.getClickLocation().z - context.getClickedPos().getZ() - 0.5;
			case EAST -> context.getClickedPos().getZ() - context.getClickLocation().z + 0.5;
			default -> throw new IllegalArgumentException("Unexpected value: " + direction);
			};
			double y = context.getClickLocation().y - context.getClickedPos().getY() - 0.5;
			double angle = Math.atan2(y, x);
			if     (angle > PI_DIV_6 * 5)  shape = GangwayFrameBlockShape.L;
			else if(angle > PI_DIV_6 * 4)  shape = GangwayFrameBlockShape.LU;
			else if(angle > PI_DIV_6 * 2)  shape = GangwayFrameBlockShape.U;
			else if(angle > PI_DIV_6 * 1)  shape = GangwayFrameBlockShape.UR;
			else if(angle > -PI_DIV_6 * 1) shape = GangwayFrameBlockShape.R;
			else if(angle > -PI_DIV_6 * 2) shape = GangwayFrameBlockShape.RD;
			else if(angle > -PI_DIV_6 * 4) shape = GangwayFrameBlockShape.D;
			else if(angle > -PI_DIV_6 * 5) shape = GangwayFrameBlockShape.DL;
			else                           shape = GangwayFrameBlockShape.L;
		}
		BlockState state = defaultBlockState().
				setValue(FACING, direction).
				setValue(SHAPE, shape).
				setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
		return withWater(state, context);
	}

	@Override
	protected abstract VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context);

	@Override
	protected abstract VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos);

	@Override
	public abstract VoxelShape getSubLevelCollisionShape(BlockGetter blockGetter, BlockState state);

	protected boolean hitGangway(BlockState state, Vec3 hitLocation, LevelReader level, BlockPos pos, Player player) {
		double x = switch(state.getValue(FACING)) {
		case SOUTH -> hitLocation.x - pos.getX() - 0.5;
		case NORTH -> pos.getX() - hitLocation.x + 0.5;
		case WEST -> hitLocation.z - pos.getZ() - 0.5;
		case EAST -> pos.getZ() - hitLocation.z + 0.5;
		default -> throw new IllegalArgumentException("Unexpected value: " + state.getValue(FACING));
		};
		double y = hitLocation.y - pos.getY() - 0.5;
		return switch(state.getValue(SHAPE)) {
		case D, DRA, DLA -> y <= -0.25;
		case U, URA, ULA -> y >= 0.25;
		case L -> x <= -0.25;
		case R -> x >= 0.25;
		case DL -> y <= -0.25 && x <= -0.25;
		case RD -> y <= -0.25 && x >= 0.25;
		case LU -> y >= 0.25 && x <= -0.25;
		case UR -> y >= 0.25 && x >= 0.25;
		default -> false;
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
			level.setBlock(pos, state.cycle(POWERED), Block.UPDATE_CLIENTS);
			if(isPowered) {
				withGangwayBlockEntityDo(level, pos, be -> be.tryDisconnectGangway(level, 15));
			}
			else {
				withGangwayBlockEntityDo(level, pos, be -> be.tryConnectGangway(level, 15));
			}
		}
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if(hitGangway(state, hitResult.getLocation(), level, pos, player)) {
			if(stack.getItem() instanceof DyeItem dye) {
				withGangwayBlockEntityDo(level, pos, be -> be.setColor(dye.getDyeColor().getFireworkColor()));
				level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS);
				return ItemInteractionResult.SUCCESS;
			}
			else if(stack.isEmpty()) {
				if(!level.isClientSide()) {
					withGangwayBlockEntityDo(level, pos, be -> {
						if(be.getGangwayPartner() == null) {
							be.tryConnectGangway(level, 15);
						}
						else if(player.isSecondaryUseActive()) {
							be.tryDisconnectGangway(level, 15);
						}
					});
				}
				return ItemInteractionResult.SUCCESS;
			}
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
		if(hitGangway(state, target.getLocation(), level, pos, player)) {
			return new ItemStack(this);
		}
		return super.getCloneItemStack(state, target, level, pos, player);
	}

	@Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		Player player = context.getPlayer();
		if(!player.isSecondaryUseActive() && hitGangway(state, context.getClickLocation(), level, pos, player)) {
			if(!level.isClientSide()) {
				withGangwayBlockEntityDo(level, pos, be -> player.openMenu(be, buf -> CopycatGangwayFrameMenu.prepare(buf, be)));
			}
			return InteractionResult.SUCCESS;
		}
		return super.onWrenched(state, context);
	}

	@Override
	public BlockEntityType<? extends CopycatBlockEntity> getBlockEntityType() {
		return SimurailBlockEntities.COPYCAT_GANGWAY_FRAME.get();
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return new SmartBlockEntityTicker<>();
	}

	public CopycatGangwayFrameBlockEntity getGangwayBlockEntity(BlockGetter worldIn, BlockPos pos) {
		return (CopycatGangwayFrameBlockEntity)super.getBlockEntity(worldIn, pos);
	}

	public Optional<CopycatGangwayFrameBlockEntity> getGangwayBlockEntityOptional(BlockGetter world, BlockPos pos) {
		return Optional.ofNullable(getGangwayBlockEntity(world, pos));
	}

	public void withGangwayBlockEntityDo(BlockGetter world, BlockPos pos, Consumer<CopycatGangwayFrameBlockEntity> action) {
		getGangwayBlockEntityOptional(world, pos).ifPresent(action);
	}

	@Override
	public BlockState getAppearance(BlockState state, BlockAndTintGetter level, BlockPos pos, Direction side, BlockState queryState, BlockPos queryPos) {
		return super.getAppearance(state, level, pos, side, queryState, queryPos);
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
	public boolean canConnectTexturesToward(BlockAndTintGetter level, BlockPos fromPos, BlockPos toPos, BlockState fromState) {
		Direction facing = fromState.getValue(FACING);
		BlockState toState = level.getBlockState(toPos);
		if(toPos.equals(fromPos.relative(facing))) {
			return false;
		}
		BlockPos diff = fromPos.subtract(toPos);
		int coord = facing.getAxis().choose(diff.getX(), diff.getY(), diff.getZ());
		if(!toState.is(this)) {
			return coord != -facing.getAxisDirection().getStep();
		}
		if(isOccluded(fromState, toState, facing)) {
			return true;
		}
		if(coord == 0 && toState.getValue(FACING) == fromState.getValue(FACING) && toState.getValue(SHAPE) == fromState.getValue(SHAPE)) {
			return true;
		}
		return false;
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
		GangwayFrameBlockShape shape = state.getValue(SHAPE);
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
		withGangwayBlockEntityDo(resultingLevel, newPos, CopycatGangwayFrameBlockEntity::afterMove);
	}
}
