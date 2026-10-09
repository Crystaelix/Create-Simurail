package com.crystaelix.simurail.content.physics_roller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

import org.joml.Vector3d;
import org.joml.Vector3dc;

import com.crystaelix.simurail.api.math.SimurailMath;
import com.crystaelix.simurail.api.physics.HorizontalPointing;
import com.crystaelix.simurail.config.SimurailConfig;
import com.crystaelix.simurail.content.SimurailBlockTags;
import com.crystaelix.simurail.content.bogey.PhysicsBogeyAxle;
import com.crystaelix.simurail.content.bogey.PhysicsBogeyBlockEntity;
import com.crystaelix.simurail.content.connector.ConnectorConnectable;
import com.simibubi.create.content.contraptions.actors.roller.PaveTask;
import com.simibubi.create.content.contraptions.actors.roller.RollerMovementBehaviour;
import com.simibubi.create.content.contraptions.actors.roller.TrackPaverV2;
import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.entity.TravellingPoint.ITrackSelector;
import com.simibubi.create.content.trains.entity.TravellingPoint.SteerDirection;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.damageTypes.CreateDamageSources;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.infrastructure.config.AllConfigs;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import foundry.veil.api.network.VeilPacketManager;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class PhysicsRollerBlockEntity extends SmartBlockEntity implements HorizontalPointing, ConnectorConnectable {

	public static final double ACTIVE_AREA_OFFSET = -2;
	public static final double ACTIVE_AREA_REACH = 0.95;

	public static final double MAX_ROLL = Math.PI / 36;
	public static final double MAX_PITCH = Math.PI / 4;
	public static final double MAX_MISALIGNMENT = Math.PI / 8;

	public static final float FILTER_SLOT_OFFSET = 3;
	public static final float MODE_SLOT_OFFSET = -3;

	public static final int SHARED_VALUE_MAX_RANGE = 64;

	public static final Component MODE = Component.translatable("gui.simurail.physics_roller.mode");
	public static final Component MATERIAL = Component.translatable("create.contraptions.mechanical_roller.pave_material");

	protected final Vector3dc localCenter;

	public FilteringBehaviour filtering;
	public ScrollOptionBehaviour<PhysicsRollerMode> mode;

	private BlockPos bogeyPos;

	protected BlockPos lastVisitedPos;

	protected boolean dontPropagate;

	protected PhysicsRollerTravellingPoint rollerScout = new PhysicsRollerTravellingPoint();

	public float visualSpeed = 0;
	protected float lastVisualSpeed = 0;
	protected float manuallyAnimatedSpeed;

	public PhysicsRollerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		localCenter = JOMLConversion.atCenterOf(pos);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		behaviours.add(filtering = new FilteringBehaviour(this, new PhysicsRollerValueBox(FILTER_SLOT_OFFSET)));
		behaviours.add(mode = new ScrollOptionBehaviour<>(PhysicsRollerMode.class, MODE, this, new PhysicsRollerValueBox(MODE_SLOT_OFFSET)));

		filtering.setLabel(MATERIAL.copy());
		filtering.withCallback(this::onFilterChanged);
		filtering.withPredicate(this::isValidMaterial);
		mode.withCallback(this::onModeChanged);
	}

	@Override
	public Direction getFacing() {
		return getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
	}

	@Override
	public AABB getOutline(Direction direction) {
		return new AABB(getBlockPos());
	}

	@Override
	public boolean canConnectTo(Direction selfDir, ConnectorConnectable other, Direction otherDir) {
		if(other instanceof PhysicsBogeyBlockEntity otherBogey) {
			if(otherDir.getAxis() != getFacing().getAxis() || Sable.HELPER.getContaining(this) != Sable.HELPER.getContaining(otherBogey)) {
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public double connectionRange(ConnectorConnectable other) {
		if(other instanceof PhysicsBogeyBlockEntity) {
			return SimurailConfig.server().blocks.connectionRollerRange.get();
		}
		return 0;
	}

	@Override
	public void connect(boolean front, ConnectorConnectable other, boolean otherFront) {
		if(other instanceof PhysicsBogeyBlockEntity bogey) {
			bogeyPos = bogey.getBlockPos();
			shareValuesToAdjacent();
		}		
	}

	@Override
	public void disconnect(boolean front) {
		bogeyPos = null;
		shareValuesToAdjacent();
	}

	protected void onModeChanged(int mode) {
		shareValuesToAdjacent();
	}

	protected void onFilterChanged(ItemStack newFilter) {
		shareValuesToAdjacent();
	}

	public boolean isValidMaterial(ItemStack newFilter) {
		if(newFilter.isEmpty()) {
			return false;
		}
		BlockState appliedState = RollerMovementBehaviour.getStateToPaveWith(newFilter);
		if(appliedState.isAir() || appliedState.getBlock() instanceof EntityBlock || appliedState.getBlock() instanceof StairBlock) {
			return false;
		}
		VoxelShape shape = appliedState.getShape(level, worldPosition);
		if(shape.isEmpty() || !shape.bounds().equals(Shapes.block().bounds())) {
			return false;
		}
		return !appliedState.getCollisionShape(level, worldPosition).isEmpty();
	}

	public void searchForSharedValues() {
		BlockState state = getBlockState();
		Direction lineAxis = state.getValue(PhysicsRollerBlock.FACING).getClockWise();
		for(int direction : Iterate.positiveAndNegative) {
			BlockPos neighbourPos = worldPosition.relative(lineAxis, direction);
			if(level.getBlockState(neighbourPos) != state ||
					!(level.getBlockEntity(neighbourPos) instanceof PhysicsRollerBlockEntity roller)) {
				continue;
			}
			acceptSharedValues(roller.mode.getValue(), roller.filtering.getFilter(), roller.bogeyPos);
			shareValuesToAdjacent();
			return;
		}
	}

	protected void acceptSharedValues(int rollingMode, ItemStack filter, BlockPos bogeyPos) {
		dontPropagate = true;
		filtering.setFilter(filter.copy());
		mode.setValue(rollingMode);
		this.bogeyPos = bogeyPos;
		dontPropagate = false;
		notifyUpdate();
	}

	public void shareValuesToAdjacent() {
		if(dontPropagate || level.isClientSide()) {
			return;
		}
		BlockState state = getBlockState();
		Direction lineAxis = state.getValue(PhysicsRollerBlock.FACING).getClockWise();
		for(int direction : Iterate.positiveAndNegative) {
			for(int distance = 1; distance < SHARED_VALUE_MAX_RANGE; ++distance) {
				BlockPos neighbourPos = worldPosition.relative(lineAxis, direction * distance);
				if(level.getBlockState(neighbourPos) != state ||
						!(level.getBlockEntity(neighbourPos) instanceof PhysicsRollerBlockEntity roller)) {
					break;
				}
				roller.acceptSharedValues(mode.getValue(), filtering.getFilter(), bogeyPos);
			}
		}
	}

	@Override
	public void tick() {
		super.tick();
		if(!level.isClientSide()) {
			updateMotion();
			if(lastVisualSpeed != visualSpeed) {
				VeilPacketManager.tracking(this).sendPacket(new PhysicsRollerRenderDataPacket(this));
			}
			lastVisualSpeed = visualSpeed;
		}
	}

	protected void updateMotion() {
		if(!level.hasNeighborSignal(worldPosition)) {
			visualSpeed = 0;
			lastVisitedPos = null;
			return;
		}
		SubLevel subLevel = Sable.HELPER.getContaining(this);
		if(subLevel == null) {
			visualSpeed = 0;
			lastVisitedPos = null;
			return;
		}

		Direction rollerFacing = getFacing();
		PhysicsBogeyBlockEntity bogey = null;
		SubLevel trackSubLevel = null;
		globalTrackVel.set(0);
		if(bogeyPos != null && level.getBlockEntity(bogeyPos) instanceof PhysicsBogeyBlockEntity be) {
			Direction bogeyFacing = be.getFacing();
			if(rollerFacing.getAxis() == bogeyFacing.getAxis()) {
				bogey = be;
				boolean front = rollerFacing == bogeyFacing;
				PhysicsBogeyAxle axle = bogey.getAxle(!front);
				if(!axle.hasTrack()) {
					visualSpeed = 0;
					lastVisitedPos = null;
					return;
				}
				trackSubLevel = Sable.HELPER.getContaining(level, axle.getTrackFrame().position());
				Sable.HELPER.getVelocity(level, axle.getTrackFrame().position(), globalTrackVel);
			}
		}

		Pose3dc selfPose = subLevel.logicalPose();
		Pose3dc trackPose = trackSubLevel == null ? SimurailMath.POSE_I : trackSubLevel.logicalPose();

		trackPose.transformNormal(SimurailMath.DIR_YP, trackRollerVertical);
		selfPose.transformNormalInverse(trackRollerVertical);
		if(!isPavingOrientationAllowed(trackRollerVertical, rollerFacing)) {
			visualSpeed = 0;
			lastVisitedPos = null;
			return;
		}

		Sable.HELPER.getVelocity(level, localCenter, globalRollerVel);
		globalRollerVel.sub(globalTrackVel, globalMotion).mul(0.05);
		selfPose.transformNormalInverse(globalMotion, localMotion);
		visualSpeed = calculateAnimatedSpeed(localMotion, rollerFacing);
		if(!isPavingMotionAllowed(localMotion, rollerFacing)) {
			lastVisitedPos = null;
			return;
		}

		visitPos.set(getDirection()).mul(ACTIVE_AREA_REACH).add(0, ACTIVE_AREA_OFFSET, 0).add(localCenter);
		selfPose.transformPosition(visitPos);
		if(mode.get() == PhysicsRollerMode.TUNNEL_PAVE) {
			trackPose.transformPositionInverse(visitPos);
		}
		BlockPos visitedPos = BlockPos.containing(visitPos.x, visitPos.y, visitPos.z);
		if(visitedPos.equals(lastVisitedPos)) {
			return;
		}
		lastVisitedPos = visitedPos;
		visitNewPosition(subLevel, trackSubLevel, visitedPos, bogey, localMotion);
	}

	protected boolean isPavingOrientationAllowed(Vector3dc up, Direction facing) {
		Direction lateral = facing.getClockWise();
		double pitch = Math.asin(Mth.clamp(up.dot(facing.getStepX(), 0, facing.getStepZ()), -1, 1));
		double roll = Math.atan2(up.dot(lateral.getStepX(), 0, lateral.getStepZ()), up.y());
		return Math.abs(pitch) <= MAX_PITCH && Math.abs(roll) <= MAX_ROLL;
	}

	protected boolean isPavingMotionAllowed(Vector3dc localMotion, Direction facing) {
		double alignment = localMotion.dot(facing.getStepX(), 0, facing.getStepZ()) / localMotion.length();
		return Math.acos(Mth.clamp(alignment, -1, 1)) <= MAX_MISALIGNMENT;
	}

	protected float calculateAnimatedSpeed(Vector3dc localMotion, Direction facing) {
		double length = facing.getAxis().choose(localMotion.x(), 0, localMotion.z()) * facing.getAxisDirection().getStep();
		if(length < 0.0078125) {
			return 0;
		}
		return (int)(-length * 1000 - 100) / 100 * 100;
	}

	protected void visitNewPosition(SubLevel subLevel, SubLevel trackSubLevel, BlockPos visitedPos, PhysicsBogeyBlockEntity bogey, Vector3dc localMotion) {
		BlockState stateVisited = level.getBlockState(visitedPos);
		if(!stateVisited.isRedstoneConductor(level, visitedPos)) {
			damageEntities(subLevel, visitedPos, localMotion);
		}
		List<IItemHandler> materials = getMaterialSources();
		BlockState stateToPaveWith = getStateToPaveWith(materials);
		for(BlockPos toBreak : getPositionsToBreak(visitedPos, stateToPaveWith, bogey)) {
			destroyBlock(toBreak, materials);
		}
		triggerPaver(trackSubLevel, visitedPos, bogey, materials, stateToPaveWith);
	}

	protected void damageEntities(SubLevel subLevel, BlockPos visitedPos, Vector3dc localMotion) {
		DamageSource damageSource = CreateDamageSources.roller(level);
		float damage = (float)Mth.clamp(6 * Math.pow(localMotion.length(), 0.4) + 1, 2, 10);
		for(Entity entity : level.getEntitiesOfClass(Entity.class, new AABB(visitedPos))) {
			if(entity instanceof ItemEntity) {
				continue;
			}
			// ignore passengers
			if(Sable.HELPER.getTrackingOrVehicleSubLevel(entity) == subLevel) {
				continue;
			}
			entity.hurt(damageSource, damage);
			localMotion.add(0, localMotion.length() / 4, 0, motionBoost);
			if(motionBoost.length() > 4) {
				motionBoost.normalize(4);
			}
			entity.setDeltaMovement(entity.getDeltaMovement().add(motionBoost.x, motionBoost.y, motionBoost.z));
			entity.hurtMarked = true;
		}
	}

	protected List<BlockPos> getPositionsToBreak(BlockPos visitedPos, BlockState stateToPaveWith, PhysicsBogeyBlockEntity bogey) {
		List<BlockPos> positions = new ArrayList<>();
		if(mode.get() != PhysicsRollerMode.TUNNEL_PAVE) {
			return positions;
		}
		int startingY = stateToPaveWith.isAir() || !hasMaterial(getMaterialSources(), stateToPaveWith) ? 1 : 0;
		// Bogey
		PaveTask profileForTracks = createHeightProfileForTracks(bogey);
		if(profileForTracks != null) {
			for(Couple<Integer> coords : profileForTracks.keys()) {
				float height = profileForTracks.get(coords);
				BlockPos targetPosition = BlockPos.containing(coords.getFirst(), height, coords.getSecond());
				boolean shouldPlaceSlab = height > Math.floor(height) + 0.45;
				if(startingY == 1 && shouldPlaceSlab && level.getBlockState(targetPosition.above()).
						getOptionalValue(BlockStateProperties.SLAB_TYPE).
						orElse(SlabType.DOUBLE) == SlabType.BOTTOM) {
					startingY = 2;
				}
				for(int i = startingY; i <= (shouldPlaceSlab ? 3 : 2); i++) {
					if(testBreakerTarget(targetPosition.above(i), i, stateToPaveWith)) {
						positions.add(targetPosition.above(i));
					}
				}
			}
			return positions;
		}
		// Bogey but no track
		if(bogey != null) {
			return positions;
		}
		// Otherwise
		for(int i = startingY; i <= 2; ++i) {
			BlockPos target = visitedPos.above(i);
			if(testBreakerTarget(target, i, stateToPaveWith)) {
				positions.add(target);
			}
		}
		return positions;
	}

	protected boolean testBreakerTarget(BlockPos target, int columnY, BlockState stateToPaveWith) {
		BlockState stateToPaveWithAsSlab = getStateToPaveWithAsSlab(stateToPaveWith);
		BlockState state = level.getBlockState(target);
		if(columnY == 0 && state.is(stateToPaveWith.getBlock())) {
			return false;
		}
		if(stateToPaveWithAsSlab != null && columnY == 1 && state.is(stateToPaveWithAsSlab.getBlock())) {
			return false;
		}
		return canBreak(target, state);
	}

	protected boolean canBreak(BlockPos pos, BlockState state) {
		for(Direction side : Iterate.directions) {
			if(level.getBlockState(pos.relative(side)).is(BlockTags.PORTALS)) {
				return false;
			}
		}
		if(!BlockBreakingKineticBlockEntity.isBreakable(state, state.getDestroySpeed(level, pos))) {
			return false;
		}
		return !state.getCollisionShape(level, pos).isEmpty() && !state.is(SimurailBlockTags.ROLLER_NON_BREAKABLE);
	}

	protected void destroyBlock(BlockPos pos, List<IItemHandler> materials) {
		BlockState state = level.getBlockState(pos);
		boolean noHarvest = state.is(BlockTags.NEEDS_IRON_TOOL) || state.is(BlockTags.NEEDS_STONE_TOOL) ||
				state.is(BlockTags.NEEDS_DIAMOND_TOOL);
		// only while filtered, preventing unfiltered roller paving with dropped blocks
		boolean collect = !filtering.getFilter().isEmpty();
		BlockHelper.destroyBlock(level, pos, 1, stack -> {
			if(noHarvest || level.random.nextBoolean()) {
				return;
			}
			ItemStack remainder = collect ? depositMaterial(materials, stack) : stack;
			if(!remainder.isEmpty()) {
				Block.popResource(level, pos, remainder);
			}
		});
	}

	protected void triggerPaver(SubLevel trackSubLevel, BlockPos visitedPos, PhysicsBogeyBlockEntity bogey, List<IItemHandler> materials, BlockState stateToPaveWith) {
		if(stateToPaveWith.isAir()) {
			return;
		}
		PhysicsRollerMode rollingMode = mode.get();
		int maxDepth = rollingMode == PhysicsRollerMode.TUNNEL_PAVE ? 0 : AllConfigs.server().kinetics.rollerFillDepth.get();
		if(rollingMode != PhysicsRollerMode.TUNNEL_PAVE && !isFillGrounded(visitedPos, maxDepth, stateToPaveWith)) {
			return;
		}
		List<Pair<BlockPos, Boolean>> paveSet = new ArrayList<>();
		PaveTask profileForTracks = createHeightProfileForTracks(bogey);
		if(trackSubLevel == null || rollingMode == PhysicsRollerMode.TUNNEL_PAVE) {
			if(profileForTracks == null) {
				paveSet.add(Pair.of(visitedPos, false));
			}
			else for(Couple<Integer> coords : profileForTracks.keys()) {
				float height = profileForTracks.get(coords);
				boolean shouldPlaceSlab = height > Math.floor(height) + 0.45;
				BlockPos targetPosition = BlockPos.containing(coords.getFirst(), height, coords.getSecond());
				paveSet.add(Pair.of(targetPosition, shouldPlaceSlab));
			}
		}
		if(paveSet.isEmpty()) {
			return;
		}
		for(int yOffset = 0; yOffset <= maxDepth; ++yOffset) {
			Set<Pair<BlockPos, Boolean>> currentLayer = new HashSet<>();
			if(rollingMode == PhysicsRollerMode.WIDE_FILL) {
				for(Pair<BlockPos, Boolean> anchor : paveSet) {
					int radius = (yOffset + 1) / 2;
					for(int i = -radius; i <= radius; i++) {
						for(int j = -radius; j <= radius; j++) {
							if(Math.abs(i) + Math.abs(j) <= radius) {
								currentLayer.add(Pair.of(anchor.getFirst().offset(i, -yOffset, j), anchor.getSecond()));
							}
						}
					}
				}
			}
			else for(Pair<BlockPos, Boolean> anchor : paveSet) {
				currentLayer.add(Pair.of(anchor.getFirst().below(yOffset), anchor.getSecond()));
			}
			boolean completelyBlocked = true;
			for(Pair<BlockPos, Boolean> currentPos : currentLayer) {
				if(yOffset == 0 && currentPos.getSecond()) {
					tryFill(currentPos.getFirst().above(), materials, stateToPaveWith, true);
				}
				if(tryFill(currentPos.getFirst(), materials, stateToPaveWith, false) != PaveResult.FAIL) {
					completelyBlocked = false;
				}
			}
			// everything filled at once or nothing (create's roller stalls the contraption)
			if(!hasMaterial(materials, stateToPaveWith) || completelyBlocked && yOffset > 0) {
				return;
			}
		}
	}

	protected boolean isFillGrounded(BlockPos visitedPos, int maxDepth, BlockState stateToPaveWith) {
		for(int yOffset = 1; yOffset <= maxDepth + 1; ++yOffset) {
			BlockPos supportPos = visitedPos.below(yOffset);
			if(!level.isLoaded(supportPos)) {
				return false;
			}
			BlockState support = level.getBlockState(supportPos);
			if(support.is(stateToPaveWith.getBlock()) || !isReplaceableByPaving(supportPos, support)) {
				return true;
			}
		}
		return false;
	}

	protected PaveResult tryFill(BlockPos targetPos, List<IItemHandler> materials, BlockState toPlace, boolean placeSlab) {
		BlockState toPlaceAsSlab = getStateToPaveWithAsSlab(toPlace);
		if(placeSlab && toPlaceAsSlab == null) {
			return PaveResult.PASS;
		}
		if(!level.isLoaded(targetPos)) {
			return PaveResult.FAIL;
		}
		BlockState existing = level.getBlockState(targetPos);
		if(existing.is(toPlace.getBlock())) {
			return PaveResult.PASS;
		}
		if(!isReplaceableByPaving(targetPos, existing)) {
			return PaveResult.FAIL;
		}
		if(placeSlab) {
			if(!consumeMaterial(materials, toPlaceAsSlab) && !consumeMaterial(materials, toPlace)) {
				return PaveResult.FAIL;
			}
		}
		else if(!consumeMaterial(materials, toPlace)) {
			return PaveResult.FAIL;
		}
		level.setBlockAndUpdate(targetPos, placeSlab ? toPlaceAsSlab : toPlace);
		return PaveResult.SUCCESS;
	}

	protected boolean isReplaceableByPaving(BlockPos pos, BlockState state) {
		if(state.is(BlockTags.LEAVES) || state.canBeReplaced()) {
			return true;
		}
		return state.getCollisionShape(level, pos).isEmpty() && !state.is(BlockTags.PORTALS);
	}

	protected PaveTask createHeightProfileForTracks(PhysicsBogeyBlockEntity bogey) {
		if(bogey == null) {
			return null;
		}
		Direction rollerFacing = getFacing();
		Direction bogeyFacing = bogey.getFacing();
		if(rollerFacing.getAxis() != bogeyFacing.getAxis()) {
			return null;
		}
		boolean front = rollerFacing == bogeyFacing;
		PhysicsBogeyAxle axle = bogey.getAxle(!front);
		TrackGraph graph = axle.getTrackGraph();
		if(graph == null) {
			return null;
		}

		TravellingPoint point = axle.getTrackPoint();
		rollerScout.node1 = point.node1;
		rollerScout.node2 = point.node2;
		rollerScout.edge = point.edge;
		rollerScout.position = point.position;

		Direction.Axis axis = bogeyFacing.getAxis();
		double distanceToTravel = 2;
		int axisStep = rollerFacing.getAxisDirection().getStep();
		int alignStep = front ? 1 : -1;
		double spacing = bogey.getOptions().type.logicalAxleSpacing() * 0.5;
		Vec3i offset = getBlockPos().subtract(bogey.getBlockPos());
		double latOffset = axis.choose(offset.getZ(), 0, -offset.getX()) * axisStep;
		double dirOffset = (axis.choose(offset.getX(), 0, offset.getZ()) * axisStep + spacing + 1) * alignStep - distanceToTravel * 0.5;

		PaveTask heightProfile = new PaveTask(latOffset, latOffset);
		ITrackSelector steering = axle.followOtherOrSteer(rollerScout);

		rollerScout.traversalCallback = (edge, coords) -> {};
		rollerScout.travel(graph, dirOffset, steering);
		rollerScout.traversalCallback = (edge, coords) -> {
			if(edge == null || edge.isInterDimensional() || !edge.node1.getLocation().dimension.equals(level.dimension())) {
				return;
			}
			TrackPaverV2.pave(heightProfile, graph, edge, coords.getFirst(), coords.getSecond());
		};
		rollerScout.travel(graph, distanceToTravel, steering);

		for(Couple<Integer> entry : heightProfile.keys()) {
			heightProfile.put(entry.getFirst(), entry.getSecond(), offset.getY() + heightProfile.get(entry));
		}

		return heightProfile;
	}

	public BlockState getStateToPaveWith(List<IItemHandler> materials) {
		for(IItemHandler source : materials) {
			for(int slot = 0; slot < source.getSlots(); ++slot) {
				ItemStack stack = source.getStackInSlot(slot);
				if(filtering.test(stack) && isValidMaterial(stack)) {
					return RollerMovementBehaviour.getStateToPaveWith(stack);
				}
			}
		}
		return Blocks.AIR.defaultBlockState();
	}

	protected BlockState getStateToPaveWithAsSlab(BlockState stateToPaveWith) {
		if(stateToPaveWith.hasProperty(SlabBlock.TYPE)) {
			return stateToPaveWith.setValue(SlabBlock.TYPE, SlabType.BOTTOM);
		}
		Block block = stateToPaveWith.getBlock();
		if(block == null) {
			return null;
		}
		ResourceLocation rl = BuiltInRegistries.BLOCK.getKey(block);
		String namespace = rl.getNamespace();
		String blockName = rl.getPath();
		int nameLength = blockName.length();
		List<String> possibleSlabLocations = new ArrayList<>();
		possibleSlabLocations.add(blockName + "_slab");
		if(blockName.endsWith("s") && nameLength > 1) {
			possibleSlabLocations.add(blockName.substring(0, nameLength - 1) + "_slab");
		}
		if(blockName.endsWith("planks") && nameLength > 7) {
			possibleSlabLocations.add(blockName.substring(0, nameLength - 7) + "_slab");
		}
		for(String locationAttempt : possibleSlabLocations) {
			Optional<Block> result = BuiltInRegistries.BLOCK.getOptional(ResourceLocation.fromNamespaceAndPath(namespace, locationAttempt));
			if(result.isPresent()) {
				return result.get().defaultBlockState();
			}
		}
		return null;
	}

	public List<IItemHandler> getMaterialSources() {
		BlockState state = getBlockState();
		Direction lineAxis = state.getValue(PhysicsRollerBlock.FACING).getClockWise();
		List<IItemHandler> sources = new ArrayList<>();
		addMaterialSource(sources, worldPosition);
		boolean[] endOfLine = new boolean[Iterate.positiveAndNegative.length];
		int endsReached = 0;
		for(int distance = 1; distance < SHARED_VALUE_MAX_RANGE && endsReached < endOfLine.length; ++distance) {
			for(int i = 0; i < endOfLine.length; ++i) {
				if(endOfLine[i]) {
					continue;
				}
				BlockPos rollerPos = worldPosition.relative(lineAxis, Iterate.positiveAndNegative[i] * distance);
				if(level.getBlockState(rollerPos) != state) {
					endOfLine[i] = true;
					++endsReached;
					continue;
				}
				addMaterialSource(sources, rollerPos);
			}
		}
		return sources;
	}

	protected void addMaterialSource(List<IItemHandler> sources, BlockPos rollerPos) {
		IItemHandler source = level.getCapability(Capabilities.ItemHandler.BLOCK, rollerPos.above(), Direction.DOWN);
		if(source != null) {
			sources.add(source);
		}
	}

	protected boolean hasMaterial(List<IItemHandler> materials, BlockState toPlace) {
		for(IItemHandler source : materials) {
			if(findMaterial(source, toPlace) >= 0) {
				return true;
			}
		}
		return false;
	}

	protected boolean consumeMaterial(List<IItemHandler> materials, BlockState toPlace) {
		for(IItemHandler source : materials) {
			int slot = findMaterial(source, toPlace);
			int count = toPlace.getOptionalValue(BlockStateProperties.SLAB_TYPE).orElse(SlabType.BOTTOM) == SlabType.DOUBLE ? 2 : 1;
			if(slot >= 0 && source.extractItem(slot, count, true).getCount() == count) {
				source.extractItem(slot, count, false);
				return true;
			}
		}
		return false;
	}

	protected ItemStack depositMaterial(List<IItemHandler> materials, ItemStack stack) {
		for(IItemHandler source : materials) {
			stack = ItemHandlerHelper.insertItemStacked(source, stack, false);
			if(stack.isEmpty()) {
				break;
			}
		}
		return stack;
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

	@Override
	protected AABB createRenderBoundingBox() {
		return new AABB(worldPosition).inflate(1);
	}

	public float getAnimatedSpeed() {
		return manuallyAnimatedSpeed != 0 ? manuallyAnimatedSpeed : visualSpeed;
	}

	public void setAnimatedSpeed(float speed) {
		manuallyAnimatedSpeed = speed;
	}

	@Override
	protected void write(CompoundTag tag, Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		if(bogeyPos != null) {
			tag.put("bogey_offset", NbtUtils.writeBlockPos(bogeyPos.subtract(getBlockPos())));
		}
	}

	@Override
	protected void read(CompoundTag tag, Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		bogeyPos = NbtUtils.readBlockPos(tag, "bogey_offset").
				map(c -> c.offset(getBlockPos())).
				or(() -> NbtUtils.readBlockPos(tag, "bogey")).
				orElse(null);
	}

	protected enum PaveResult {
		FAIL, PASS, SUCCESS;
	}

	protected class PhysicsRollerTravellingPoint extends TravellingPoint {
		public BiConsumer<TrackEdge, Couple<Double>> traversalCallback;

		@Override
		protected Double edgeTraversedFrom(TrackGraph graph, boolean forward, IEdgePointListener edgePointListener, ITurnListener turnListener, double prevPos, double totalDistance) {
			double from = forward ? prevPos : position;
			double to = forward ? position : prevPos;
			traversalCallback.accept(edge, Couple.create(from, to));
			return super.edgeTraversedFrom(graph, forward, edgePointListener, turnListener, prevPos, totalDistance);
		}
	}

	protected final Vector3d trackDir = new Vector3d();
	protected final Vector3d trackRollerVertical = new Vector3d();

	protected final Vector3d globalRollerVel = new Vector3d();
	protected final Vector3d globalTrackVel = new Vector3d();
	protected final Vector3d globalMotion = new Vector3d();
	protected final Vector3d localMotion = new Vector3d();
	protected final Vector3d motionBoost = new Vector3d();

	protected final Vector3d visitPos = new Vector3d();
}
