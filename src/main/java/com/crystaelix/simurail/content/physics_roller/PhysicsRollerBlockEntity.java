package com.crystaelix.simurail.content.physics_roller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.contraptions.actors.roller.RollerMovementBehaviour;
import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.damageTypes.CreateDamageSources;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.infrastructure.config.AllConfigs;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.SectionPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

public class PhysicsRollerBlockEntity extends SmartBlockEntity {

	public static final Vec3 ACTIVE_AREA_OFFSET = new Vec3(0, -2, 0);
	public static final double ACTIVE_AREA_REACH = 0.45;

	public ScrollOptionBehaviour<PhysicsRollerMode> mode;

	@Nullable
	protected BlockPos lastVisitedPos;

	// wheel anim client side
	protected float animatedSpeed;

	public PhysicsRollerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		behaviours.add(mode = new ScrollOptionBehaviour<>(PhysicsRollerMode.class,
				Component.translatable("gui.simurail.physics_roller.mode"), this, new PhysicsRollerValueBox()));
	}

	public float getAnimatedSpeed() {
		return animatedSpeed;
	}

	@Override
	public void tick() {
		super.tick();

		SubLevel subLevel = Sable.HELPER.getContaining(this);
		if(subLevel == null || !level.hasNeighborSignal(worldPosition)) {
			animatedSpeed = 0;
			lastVisitedPos = null;
			return;
		}

		Direction facing = getBlockState().getValue(PhysicsRollerBlock.FACING);
		Pose3dc pose = subLevel.logicalPose();
		Vec3 center = getBlockPos().getCenter();
		Vec3 motion = Sable.HELPER.getVelocity(level, subLevel, center).scale(1 / 20D);
		Vec3 localMotion = pose.transformNormalInverse(motion);

		animatedSpeed = calculateAnimatedSpeed(localMotion, facing);
		if(level.isClientSide()) {
			return;
		}

		if(!VecHelper.isVecPointingTowards(localMotion, facing)) {
			lastVisitedPos = null;
			return;
		}

		Vec3 localOffset = Vec3.atLowerCornerOf(facing.getNormal()).scale(ACTIVE_AREA_REACH).add(ACTIVE_AREA_OFFSET);
		BlockPos visitedPos = BlockPos.containing(pose.transformPosition(center.add(localOffset)));
		if(visitedPos.equals(lastVisitedPos)) {
			return;
		}

		lastVisitedPos = visitedPos;
		visitNewPosition(subLevel, visitedPos, motion);
	}

	protected float calculateAnimatedSpeed(Vec3 localMotion, Direction facing) {
		double length = localMotion.length();
		if(length < 1 / 512D) {
			return 0;
		}

		float speed = (float)(((int)(-length * 1000 - 100)) / 100 * 100);
		return VecHelper.isVecPointingTowards(localMotion, facing.getOpposite()) ? -speed : speed;
	}

	protected void visitNewPosition(SubLevel subLevel, BlockPos visitedPos, Vec3 motion) {
		if(isOutOfBounds(visitedPos)) {
			return;
		}

		BlockState stateVisited = level.getBlockState(visitedPos);
		if(!stateVisited.isRedstoneConductor(level, visitedPos)) {
			damageEntities(subLevel, visitedPos, motion);
		}

		IItemHandler materials = getMaterialSource();
		BlockState stateToPaveWith = getStateToPaveWith(materials);

		for(BlockPos toBreak : getPositionsToBreak(visitedPos, stateToPaveWith)) {
			destroyBlock(toBreak);
		}
		triggerPaver(visitedPos, materials, stateToPaveWith);
	}

	protected void damageEntities(SubLevel subLevel, BlockPos visitedPos, Vec3 motion) {
		DamageSource damageSource = CreateDamageSources.roller(level);
		float damage = (float)Mth.clamp(6 * Math.pow(motion.length(), 0.4) + 1, 2, 10);

		for(Entity entity : level.getEntitiesOfClass(Entity.class, new AABB(visitedPos))) {
			if(entity instanceof ItemEntity) {
				continue;
			}
			
			// ignore passengers
			if(Sable.HELPER.getTrackingOrVehicleSubLevel(entity) == subLevel) {
				continue;
			}

			entity.hurt(damageSource, damage);

			Vec3 motionBoost = motion.add(0, motion.length() / 4, 0);
			if(motionBoost.length() > 4) {
				motionBoost = motionBoost.normalize().scale(4);
			}
			entity.setDeltaMovement(entity.getDeltaMovement().add(motionBoost));
			entity.hurtMarked = true;
		}
	}

	protected List<BlockPos> getPositionsToBreak(BlockPos visitedPos, BlockState stateToPaveWith) {
		List<BlockPos> positions = new ArrayList<>();
		if(mode.get() != PhysicsRollerMode.TUNNEL_PAVE) {
			return positions;
		}

		int startingY = stateToPaveWith.isAir() ? 1 : 0;
		for(int i = startingY; i <= 2; ++i) {
			BlockPos target = visitedPos.above(i);
			if(testBreakerTarget(target, i, stateToPaveWith)) {
				positions.add(target);
			}
		}

		return positions;
	}

	protected boolean testBreakerTarget(BlockPos target, int columnY, BlockState stateToPaveWith) {
		BlockState state = level.getBlockState(target);
		if(columnY == 0 && state.is(stateToPaveWith.getBlock())) {
			return false;
		}

		return canBreak(target, state);
	}

	protected boolean canBreak(BlockPos pos, BlockState state) {
		if(isOutOfBounds(pos)) {
			return false;
		}

		for(Direction side : Iterate.directions) {
			if(level.getBlockState(pos.relative(side)).is(BlockTags.PORTALS)) {
				return false;
			}
		}

		if(!BlockBreakingKineticBlockEntity.isBreakable(state, state.getDestroySpeed(level, pos))) {
			return false;
		}

		return !state.getCollisionShape(level, pos).isEmpty() && !AllTags.AllBlockTags.TRACKS.matches(state);
	}

	protected void destroyBlock(BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		boolean noHarvest = state.is(BlockTags.NEEDS_IRON_TOOL) || state.is(BlockTags.NEEDS_STONE_TOOL) ||
				state.is(BlockTags.NEEDS_DIAMOND_TOOL);

		BlockHelper.destroyBlock(level, pos, 1, stack -> {
			if(noHarvest || level.random.nextBoolean()) {
				return;
			}
			Block.popResource(level, pos, stack);
		});
	}

	protected void triggerPaver(BlockPos visitedPos, IItemHandler materials, BlockState stateToPaveWith) {
		if(stateToPaveWith.isAir()) {
			return;
		}
		PhysicsRollerMode rollingMode = mode.get();
		int maxDepth = rollingMode == PhysicsRollerMode.TUNNEL_PAVE ? 0 : AllConfigs.server().kinetics.rollerFillDepth.get();

		for(int yOffset = 0; yOffset <= maxDepth; ++yOffset) {
			Set<BlockPos> currentLayer = new HashSet<>();
			if(rollingMode == PhysicsRollerMode.WIDE_FILL) {
				int radius = (yOffset + 1) / 2;
				for(int i = -radius; i <= radius; ++i) {
					for(int j = -radius; j <= radius; ++j) {
						if(Math.abs(i) + Math.abs(j) <= radius) {
							currentLayer.add(visitedPos.offset(i, -yOffset, j));
						}
					}
				}
			}
			else {
				currentLayer.add(visitedPos.below(yOffset));
			}

			boolean completelyBlocked = true;
			for(BlockPos currentPos : currentLayer) {
				if(tryFill(currentPos, materials, stateToPaveWith) != PaveResult.FAIL) {
					completelyBlocked = false;
				}
			}

			// everything filled at once or nothing (create's roller stalls the contraption)
			if(!hasMaterial(materials, stateToPaveWith) || completelyBlocked && yOffset > 0) {
				return;
			}
		}
	}

	protected PaveResult tryFill(BlockPos targetPos, IItemHandler materials, BlockState toPlace) {
		if(!level.isLoaded(targetPos) || isOutOfBounds(targetPos)) {
			return PaveResult.FAIL;
		}

		BlockState existing = level.getBlockState(targetPos);
		if(existing.is(toPlace.getBlock())) {
			return PaveResult.PASS;
		}
		if(!existing.is(BlockTags.LEAVES) && !existing.canBeReplaced() &&
				(!existing.getCollisionShape(level, targetPos).isEmpty() || existing.is(BlockTags.PORTALS))) {
			return PaveResult.FAIL;
		}

		if(!consumeMaterial(materials, toPlace)) {
			return PaveResult.FAIL;
		}

		level.setBlockAndUpdate(targetPos, toPlace);
		return PaveResult.SUCCESS;
	}

	protected boolean isOutOfBounds(BlockPos pos) {
		return Sable.HELPER.isInPlotGrid(level, SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
	}

	@Nullable
	public IItemHandler getMaterialSource() {
		return level.getCapability(Capabilities.ItemHandler.BLOCK, worldPosition.above(), Direction.DOWN);
	}

	public BlockState getStateToPaveWith(IItemHandler materials) {
		if(materials != null) {
			for(int slot = 0; slot < materials.getSlots(); ++slot) {
				ItemStack stack = materials.getStackInSlot(slot);
				if(isValidMaterial(stack)) {
					return RollerMovementBehaviour.getStateToPaveWith(stack);
				}
			}
		}

		return Blocks.AIR.defaultBlockState();
	}

	protected boolean hasMaterial(IItemHandler materials, BlockState toPlace) {
		return findMaterial(materials, toPlace) >= 0;
	}

	protected boolean consumeMaterial(IItemHandler materials, BlockState toPlace) {
		int slot = findMaterial(materials, toPlace);
		return slot >= 0 && !materials.extractItem(slot, 1, false).isEmpty();
	}

	protected int findMaterial(IItemHandler materials, BlockState toPlace) {
		if(materials == null) {
			return -1;
		}

		for(int slot = 0; slot < materials.getSlots(); ++slot) {
			ItemStack stack = materials.getStackInSlot(slot);
			if(!stack.isEmpty() && RollerMovementBehaviour.getStateToPaveWith(stack).is(toPlace.getBlock())) {
				return slot;
			}
		}

		return -1;
	}

	public boolean isValidMaterial(ItemStack stack) {
		if(stack.isEmpty()) {
			return false;
		}

		BlockState appliedState = RollerMovementBehaviour.getStateToPaveWith(stack);
		if(appliedState.isAir()) {
			return false;
		}
		if(appliedState.getBlock() instanceof EntityBlock || appliedState.getBlock() instanceof StairBlock) {
			return false;
		}

		VoxelShape shape = appliedState.getShape(level, worldPosition);
		if(shape.isEmpty() || !shape.bounds().equals(Shapes.block().bounds())) {
			return false;
		}

		return !appliedState.getCollisionShape(level, worldPosition).isEmpty();
	}

	protected enum PaveResult {
		FAIL, PASS, SUCCESS;
	}


	private static class PhysicsRollerValueBox extends ValueBoxTransform.Sided {

		@Override
		@Nullable
		public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
			Direction facing = state.getValue(PhysicsRollerBlock.FACING);
			Vec3 offset = getModelOffset(toModelSide(getSide(), facing));
			if(offset == null) {
				return null;
			}

			return VecHelper.rotateCentered(offset, AngleHelper.horizontalAngle(facing) + 180, Axis.Y);
		}

		@Override
		public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
			Direction side = getSide();
			TransformStack stack = TransformStack.of(ms);

			// keep icon aligned with the block itself on the top face
			if(side.getAxis().isVertical()) {
				stack.rotateYDegrees(AngleHelper.horizontalAngle(state.getValue(PhysicsRollerBlock.FACING)));
			}

			stack.rotateYDegrees(AngleHelper.horizontalAngle(side) + 180).
					rotateXDegrees(side == Direction.UP ? 90 : 0);
		}

		@Override
		public boolean testHit(LevelAccessor level, BlockPos pos, BlockState state, Vec3 localHit) {
			if(!isSideActive(state, getSide())) {
				return false;
			}

			Vec3 offset = getLocalOffset(level, pos, state);
			return offset != null && localHit.distanceTo(offset) < scale / 3;
		}

		@Override
		protected boolean isSideActive(BlockState state, Direction direction) {
			return getModelOffset(toModelSide(direction, state.getValue(PhysicsRollerBlock.FACING))) != null;
		}

		@Override
		protected Vec3 getSouthLocation() {
			return Vec3.ZERO;
		}

		@Nullable
		protected static Vec3 getModelOffset(Direction modelSide) {
			return switch(modelSide) {
			case UP -> VecHelper.voxelSpace(8, 15.5F, 13);
			case SOUTH -> VecHelper.voxelSpace(8, 13, 15.5F);
			case WEST -> VecHelper.voxelSpace(0.5F, 8, 13);
			case EAST -> VecHelper.voxelSpace(15.5F, 8, 13);
			default -> null;
			};
		}

		protected static Direction toModelSide(Direction side, Direction facing) {
			if(side.getAxis().isVertical()) {
				return side;
			}

			Direction modelSide = Direction.NORTH;
			for(Direction current = facing; current != side; current = current.getClockWise()) {
				modelSide = modelSide.getClockWise();
			}

			return modelSide;
		}
	}
}
