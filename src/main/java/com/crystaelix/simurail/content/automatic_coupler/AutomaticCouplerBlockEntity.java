package com.crystaelix.simurail.content.automatic_coupler;

import java.util.List;
import java.util.UUID;

import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import com.crystaelix.simurail.api.coupler.CouplerType;
import com.crystaelix.simurail.api.coupler.CouplerTypeRegistry;
import com.crystaelix.simurail.api.math.SimurailMath;
import com.crystaelix.simurail.api.physics.SimurailJoints;
import com.crystaelix.simurail.api.util.SchematicContextUtil;
import com.crystaelix.simurail.api.util.SubLevelUtil;
import com.crystaelix.simurail.config.SimurailConfig;
import com.crystaelix.simurail.config.SimurailPhysicsConfig;
import com.crystaelix.simurail.content.SimurailCouplers;
import com.crystaelix.simurail.content.SimurailSoundEvents;
import com.crystaelix.simurail.content.bogey.PhysicsBogeyBlockEntity;
import com.crystaelix.simurail.content.steering_connector.SteeringConnectable;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import dev.ryanhcode.sable.api.physics.constraint.ConstraintJointAxis;
import dev.ryanhcode.sable.api.physics.constraint.GenericConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.GenericConstraintHandle;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class AutomaticCouplerBlockEntity extends SmartBlockEntity implements BlockEntitySubLevelActor, SteeringConnectable {

	public static final double SHORT_LENGTH = 0.5;
	public static final double LONG_LENGTH = 1;

	protected boolean initialized = false;

	protected boolean isShort = false;
	protected CouplerType type = SimurailCouplers.KNUCKLE;

	protected BlockPos partnerPos;
	protected UUID partnerSubLevelID;
	protected BlockPos connectedPos;
	protected boolean connectedFront;

	protected final Vector3dc localCenter;
	protected GenericConstraintHandle joint;

	public AutomaticCouplerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		localCenter = JOMLConversion.atCenterOf(pos);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	public void cycleLength() {
		if(!level.isClientSide()) {
			isShort = !isShort;
			setChanged();
			sendData();
			if(partnerPos != null && Sable.HELPER.getContaining(this) instanceof ServerSubLevel subLevel) {
				SubLevelPhysicsSystem physics = SubLevelContainer.getContainer(subLevel.getLevel()).physicsSystem();
				physics.getPipeline().wakeUp(subLevel);
			}
		}
	}

	public void cycleType() {
		if(!level.isClientSide()) {
			type = CouplerTypeRegistry.next(type);
			if(type == null) {
				type = SimurailCouplers.KNUCKLE;
			}
			setChanged();
			sendData();
			if(partnerPos != null && level.getBlockEntity(partnerPos) instanceof AutomaticCouplerBlockEntity partner) {
				partner.type = type;
				partner.setChanged();
				partner.sendData();
			}
		}
	}

	@Override
	public Direction getFacing() {
		return getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
	}

	@Override
	public AABB getOutline(Direction direction) {
		return AABB.ofSize(
				getBlockPos().getCenter().add(direction.getStepX() * 0.40625, 0, direction.getStepZ() * 0.40625),
				direction.getStepX() == 0 ? 0.375 : 0.1875, 0.375, direction.getStepZ() == 0 ? 0.375 : 0.1875);
	}

	public void setPartner(BlockPos partnerPos) {
		if(level.getBlockEntity(partnerPos) instanceof AutomaticCouplerBlockEntity partner) {
			SubLevel selfSubLevel = Sable.HELPER.getContaining(this);
			SubLevel partnerSubLevel = Sable.HELPER.getContaining(partner);
			if(this.partnerPos != null) {
				removePartner();
			}
			this.partnerPos = partnerPos;
			this.partnerSubLevelID = partnerSubLevel == null ? null : partnerSubLevel.getUniqueId();
			partner.partnerPos = getBlockPos();
			partner.partnerSubLevelID = selfSubLevel == null ? null : selfSubLevel.getUniqueId();
			if(!level.isClientSide()) {
				setChanged();
				sendData();
				partner.setChanged();
				partner.sendData();

				Vec3 globalSelfPos = Sable.HELPER.projectOutOfSubLevel(level, getBlockPos().getCenter());
				Vec3 globalPartnerPos = Sable.HELPER.projectOutOfSubLevel(level, partnerPos.getCenter());
				double x = globalSelfPos.x / 2 + globalPartnerPos.x / 2;
				double y = globalSelfPos.y / 2 + globalPartnerPos.y / 2;
				double z = globalSelfPos.z / 2 + globalPartnerPos.z / 2;

				level.playSound(null, x, y, z, SimurailSoundEvents.COUPLER_CONNECT.get(), SoundSource.BLOCKS, 1F, 1F);
			}
		}
	}

	public void removePartner() {
		if(partnerPos != null && level.getBlockEntity(partnerPos) instanceof AutomaticCouplerBlockEntity partner) {
			partner.partnerPos = null;
			partner.partnerSubLevelID = null;
			if(!level.isClientSide()) {
				if(partner.connectedPos != null && level.getBlockEntity(partner.connectedPos) instanceof PhysicsBogeyBlockEntity bogey) {
					bogey.disconnectSteering(partner.connectedFront);
				}
				partner.removeJoint();
				partner.setChanged();
				partner.sendData();

				Vec3 globalSelfPos = Sable.HELPER.projectOutOfSubLevel(level, getBlockPos().getCenter());
				Vec3 globalPartnerPos = Sable.HELPER.projectOutOfSubLevel(level, partnerPos.getCenter());
				double x = globalSelfPos.x / 2 + globalPartnerPos.x / 2;
				double y = globalSelfPos.y / 2 + globalPartnerPos.y / 2;
				double z = globalSelfPos.z / 2 + globalPartnerPos.z / 2;

				level.playSound(null, x, y, z, SimurailSoundEvents.COUPLER_DISCONNECT.get(), SoundSource.BLOCKS, 1F, 1F);
			}
		}
		partnerPos = null;
		partnerSubLevelID = null;
		if(!level.isClientSide()) {
			if(connectedPos != null && level.getBlockEntity(connectedPos) instanceof PhysicsBogeyBlockEntity bogey) {
				bogey.disconnectSteering(connectedFront);
			}
			removeJoint();
			setChanged();
			sendData();
		}
	}

	@Override
	public boolean canConnectSteeringTo(SteeringConnectable other) {
		if(other instanceof PhysicsBogeyBlockEntity otherBogey) {
			return Sable.HELPER.getContaining(this) == Sable.HELPER.getContaining(otherBogey);
		}
		return false;
	}

	@Override
	public double connectionRange(SteeringConnectable other) {
		if(canConnectSteeringTo(other)) {
			return SimurailConfig.SERVER.blocks.couplerConnectionRange.get();
		}
		return 0;
	}

	@Override
	public void connectSteering(boolean front, SteeringConnectable other, boolean otherFront) {
		if(other instanceof PhysicsBogeyBlockEntity bogey) {
			connectedPos = bogey.getBlockPos();
			connectedFront = otherFront;
		}
	}

	@Override
	public void disconnectSteering(boolean front) {
		connectedPos = null;
		setChanged();
	}

	public void afterMove() {
		if(partnerPos != null) {
			setPartner(partnerPos);
		}
	}

	public boolean isPowered() {
		return getBlockState().getValue(BlockStateProperties.POWERED);
	}

	public Vector3dc getDirection() {
		return switch(getFacing()) {
		case EAST -> SimurailMath.DIR_XP;
		case WEST -> SimurailMath.DIR_XN;
		case SOUTH -> SimurailMath.DIR_ZP;
		case NORTH -> SimurailMath.DIR_ZN;
		case null, default -> throw new IllegalArgumentException("Unexpected value: " + getFacing());
		};
	}

	public double getLength() {
		return (isShort ? SHORT_LENGTH : LONG_LENGTH) - 0.0625;
	}

	public Vector3d getEndPosition(Vector3d dest) {
		return dest.set(localCenter).fma(getLength() + 0.0625 - 0.5, getDirection());
	}

	public Vector3d getJointPosition(Vector3d dest) {
		return dest.set(localCenter).fma(0.0625 - 0.5, getDirection());
	}

	// Sometimes physicsTick happens before tick?
	public void init() {
		if(initialized) {
			return;
		}

		initialized = true;
	}

	@Override
	public void tick() {
		init();
		super.tick();
	}

	@Override
	public void lazyTick() {
		if(!level.isClientSide()) {
			if(connectedPos != null) {
				SubLevel selfSubLevel = Sable.HELPER.getContaining(this);
				SubLevel otherSubLevel = Sable.HELPER.getContaining(level, connectedPos);
				BlockEntity be = level.getBlockEntity(connectedPos);
				if(selfSubLevel != otherSubLevel) {
					disconnectSteering(false);
				}
				else if(be instanceof PhysicsBogeyBlockEntity bogey) {
					if(partnerPos != null && level.getBlockEntity(partnerPos) instanceof AutomaticCouplerBlockEntity partner) {
						if(partner.connectedPos != null && level.getBlockEntity(partner.connectedPos) instanceof PhysicsBogeyBlockEntity otherBogey) {
							bogey.connectSteering(connectedFront, otherBogey, partner.connectedFront);
						}
					}
				}
				else {
					disconnectSteering(false);
				}
			}
			if(partnerPos != null && !(level.getBlockEntity(partnerPos) instanceof AutomaticCouplerBlockEntity)) {
				removePartner();
			}
		}
	}

	@Override
	public void sable$physicsTick(ServerSubLevel subLevel, RigidBodyHandle handle, double timeStep) {
		init();

		getEndPosition(endPos);
		subLevel.logicalPose().transformPosition(endPos, globalEndPos);

		if(!isPowered() && partnerPos == null) {
			removeJoint();
			findPartner(subLevel);
		}
		if(partnerPos != null) {
			if(level.getBlockEntity(partnerPos) instanceof AutomaticCouplerBlockEntity partner) {
				ServerSubLevel partnerSubLevel = (ServerSubLevel)Sable.HELPER.getContaining(partner);
				if(partnerSubLevel == subLevel ||
						isPowered() || partner.isPowered() ||
						!type.canConnectTo(partner.type)) {
					removePartner();
					removeJoint();
				}
				else {
					if(this.joint != null && partner.joint != null) {
						partner.removeJoint();
					}
					if(partner.joint == null) {
						Pose3dc selfPose = subLevel.logicalPose();
						Pose3dc partnerPose = partnerSubLevel == null ? SimurailMath.POSE_I : partnerSubLevel.logicalPose();

						this.getJointPosition(this.jointPos);
						partner.getJointPosition(partner.jointPos);

						selfPose.transformPositionInverse(partnerPose.transformPosition(partner.jointPos, this.partnerJointPos));
						partnerPose.transformPositionInverse(selfPose.transformPosition(this.jointPos, partner.partnerJointPos));

						this.partnerJointPos.sub(this.jointPos, this.jointDir);
						partner.jointPos.sub(partner.partnerJointPos, partner.jointDir);

						SimurailMath.rot(this.jointDir, this.jointRot);
						SimurailMath.rot(partner.jointDir, partner.jointRot);

						double jointLength = this.getLength() + partner.getLength();

						if(joint == null || !joint.isValid()) {
							removeJoint();
							SimurailPhysicsConfig config = SimurailConfig.SERVER.physics;
							SubLevelPhysicsSystem physics = SubLevelContainer.getContainer(subLevel.getLevel()).physicsSystem();
							double linearDamping = config.couplerPassiveLinearDamping.get();
							double angularDamping = config.couplerPassiveAngularDamping.get();
							GenericConstraintConfiguration jointConfig = SimurailJoints.couplerJoint(
									this.jointPos, partner.jointPos,
									this.jointRot, partner.jointRot);
							joint = physics.getPipeline().addConstraint(subLevel, partnerSubLevel, jointConfig);
							joint.setLimit(ConstraintJointAxis.LINEAR_X, jointLength, jointLength);
							joint.setMotor(ConstraintJointAxis.LINEAR_Y, 0, 0, linearDamping, false, 0);
							joint.setMotor(ConstraintJointAxis.LINEAR_Z, 0, 0, linearDamping, false, 0);
							joint.setMotor(ConstraintJointAxis.ANGULAR_X, 0, 0, angularDamping, false, 0);
							joint.setMotor(ConstraintJointAxis.ANGULAR_Y, 0, 0, angularDamping, false, 0);
							joint.setMotor(ConstraintJointAxis.ANGULAR_Z, 0, 0, angularDamping, false, 0);
						}
						else {
							joint.setFrame1(this.jointPos, this.jointRot);
							joint.setFrame2(partner.jointPos, partner.jointRot);
							joint.setLimit(ConstraintJointAxis.LINEAR_X, jointLength, jointLength);
						}
					}
				}
			}
			else {
				removePartner();
				removeJoint();
			}
		}
		else {
			removeJoint();
		}
	}

	protected void findPartner(ServerSubLevel subLevel) {
		double minDistSq = Double.POSITIVE_INFINITY;
		BlockPos pos = null;

		MutableBlockPos checkPos = new MutableBlockPos();
		Vector3d checkSelfEndPos = new Vector3d();
		Vector3d checkEndPos = new Vector3d();

		for(SubLevel checkSubLevel : SubLevelUtil.getIntersectingSubLevels(level, globalEndPos, 2)) {
			if(subLevel == checkSubLevel) {
				continue;
			}
			Pose3dc checkPose = checkSubLevel == null ? SimurailMath.POSE_I : checkSubLevel.logicalPose();
			checkPose.transformPositionInverse(globalEndPos, checkSelfEndPos);
			for(int x = Mth.floor(checkSelfEndPos.x) - 2; x < Mth.ceil(checkSelfEndPos.x) + 2; ++x) {
				for(int y = Mth.floor(checkSelfEndPos.y) - 2; y < Mth.ceil(checkSelfEndPos.y) + 2; ++y) {
					for(int z = Mth.floor(checkSelfEndPos.z) - 2; z < Mth.ceil(checkSelfEndPos.z) + 2; ++z) {
						checkPos.set(x, y, z);
						if(level.getBlockEntity(checkPos) instanceof AutomaticCouplerBlockEntity checkPartner &&
								checkPartner.partnerPos == null &&
								!checkPartner.isPowered() &&
								type.canConnectTo(checkPartner.type)) {
							double distSq = checkPartner.getEndPosition(checkEndPos).distanceSquared(checkSelfEndPos);
							if(distSq < 0.02 && distSq < minDistSq) {
								minDistSq = distSq;
								pos = checkPos.immutable();
							}
						}
					}
				}
			}
		}

		if(pos != null) {
			setPartner(pos);
		}
	}

	protected void removeJoint() {
		if(joint != null) {
			joint.remove();
			joint = null;
		}
	}

	@Override
	public Iterable<SubLevel> sable$getConnectionDependencies() {
		if(partnerSubLevelID != null) {
			return List.of(SubLevelContainer.getContainer(level).getSubLevel(partnerSubLevelID));
		}
		return List.of();
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if(!level.isClientSide()) {
			removeJoint();
		}
	}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		tag.putBoolean("is_short", isShort);
		tag.putString("type", type.id().toString());

		if(connectedPos != null) {
			tag.put("connected", NbtUtils.writeBlockPos(connectedPos));
			tag.putBoolean("connected_front", connectedFront);
		}

		Pair<BlockPos, UUID> partner = SchematicContextUtil.writeTransform(partnerPos, partnerSubLevelID);

		if(partner.getFirst() != null) {
			tag.put("partner", NbtUtils.writeBlockPos(partner.getFirst()));
			if(partner.getSecond() != null) {
				tag.putUUID("partner_id", partner.getSecond());
			}
		}
	}

	@Override
	public void writeSafe(CompoundTag tag, Provider registries) {
		super.writeSafe(tag, registries);
		tag.putBoolean("is_short", isShort);
		tag.putString("type", type.id().toString());
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		isShort = tag.getBoolean("is_short");

		if(tag.contains("type")) {
			type = CouplerTypeRegistry.get(ResourceLocation.tryParse(tag.getString("type")));
			if(type == null) {
				type = SimurailCouplers.KNUCKLE;
			}
		}

		connectedPos = NbtUtils.readBlockPos(tag, "connected").orElse(null);
		connectedFront = tag.getBoolean("connected_front");

		Pair<BlockPos, UUID> partner = SchematicContextUtil.readTransform(
				NbtUtils.readBlockPos(tag, "partner").orElse(null),
				tag.hasUUID("partner_id") ? tag.getUUID("partner_id") : null);

		partnerPos = partner.getFirst();
		partnerSubLevelID = partner.getSecond();
	}

	// Mutable physics fields
	protected final Vector3d endPos = new Vector3d();
	protected final Vector3d globalEndPos = new Vector3d();

	protected final Vector3d jointPos = new Vector3d();
	protected final Vector3d partnerJointPos = new Vector3d();

	protected final Vector3d jointDir = new Vector3d();
	protected final Quaterniond jointRot = new Quaterniond();
}
