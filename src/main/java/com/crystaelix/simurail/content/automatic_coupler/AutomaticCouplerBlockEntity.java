package com.crystaelix.simurail.content.automatic_coupler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import com.crystaelix.simurail.content.SimurailBlocks;
import com.crystaelix.simurail.content.SimurailCouplers;
import com.crystaelix.simurail.content.SimurailSoundEvents;
import com.crystaelix.simurail.content.bogey.PhysicsBogeyBlockEntity;
import com.crystaelix.simurail.content.gangway_frame.GangwayFrame;
import com.crystaelix.simurail.content.gangway_frame.GangwayFrameBlockShape;
import com.crystaelix.simurail.content.gangway_frame.GangwayFrameShape;
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
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AutomaticCouplerBlockEntity extends SmartBlockEntity implements MenuProvider, BlockEntitySubLevelActor, SteeringConnectable, GangwayFrame {

	public static final double SHORT_LENGTH = 0.5;
	public static final double LONG_LENGTH = 1;

	protected boolean initialized = false;

	protected boolean isShort = false;
	protected CouplerType type = SimurailCouplers.KNUCKLE;
	protected int color = DyeColor.GRAY.getFireworkColor();

	protected BlockPos partnerPos;
	protected UUID partnerSubLevelID;
	protected BlockPos connectedPos;
	protected boolean connectedFront;

	protected final Vector3dc localCenter;
	protected double lastJointLength = 0;
	protected GenericConstraintHandle joint;

	protected BlockPos gangwayPartnerPos;
	protected UUID gangwayPartnerSubLevelID;

	public float gangwayRestLength = 0;
	public int gangwayColor = DyeColor.GRAY.getFireworkColor();

	protected Pose3d gangwayEndPose = new Pose3d();
	protected boolean hasGangwayPartner = false;
	public int gangwayTimer;

	protected VoxelShape collisionShape = Shapes.empty();

	public AutomaticCouplerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		localCenter = JOMLConversion.atCenterOf(pos);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	@Override
	public Component getDisplayName() {
		return SimurailBlocks.AUTOMATIC_COUPLER.get().getName();
	}

	public void cycleLength() {
		isShort = !isShort;
		if(!level.isClientSide()) {
			setChanged();
			sendData();
			if(partnerPos != null && Sable.HELPER.getContaining(this) instanceof ServerSubLevel subLevel) {
				SubLevelPhysicsSystem physics = SubLevelContainer.getContainer(subLevel.getLevel()).physicsSystem();
				physics.getPipeline().wakeUp(subLevel);
			}
		}
	}

	public void cycleType() {
		type = CouplerTypeRegistry.next(type);
		if(type == null) {
			type = SimurailCouplers.KNUCKLE;
		}
		if(!level.isClientSide()) {
			setChanged();
			sendData();
		}
		if(partnerPos != null && level.getBlockEntity(partnerPos) instanceof AutomaticCouplerBlockEntity partner) {
			partner.type = type;
			if(!level.isClientSide()) {
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
	public GangwayFrameBlockShape getGangwayShape() {
		return getBlockState().getValue(AutomaticCouplerBlock.GANGWAY_SHAPE);
	}

	@Override
	public Vector3d getGangwayCenter(Vector3d dest) {
		BlockPos pos = getBlockPos();
		return getGangwayShape().center(getFacing(), dest).add(pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public boolean isPowered() {
		return getBlockState().getValue(BlockStateProperties.POWERED);
	}

	@Override
	public boolean isGangwayPowered() {
		if(isPowered()) {
			return true;
		}
		return GangwayFrame.getNeighbors(this, level, 15).stream().anyMatch(GangwayFrame::isPowered);
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

	public BlockPos getPartner() {
		return partnerPos;
	}

	public boolean isPrimary() {
		return partnerPos != null && joint != null;
	}

	@Override
	public boolean canConnectSteeringTo(Direction selfDir, SteeringConnectable other, Direction otherDir) {
		if(other instanceof PhysicsBogeyBlockEntity otherBogey) {
			if(otherDir != getFacing() || Sable.HELPER.getContaining(this) != Sable.HELPER.getContaining(otherBogey)) {
				return false;
			}
			Vec3i normal = getFacing().getNormal();
			Vec3i delta = getBlockPos().subtract(other.getBlockPos());
			if(normal.getX() * delta.getX() + normal.getZ() * delta.getZ() <= 0) {
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public double connectionRange(SteeringConnectable other) {
		if(other instanceof PhysicsBogeyBlockEntity) {
			return SimurailConfig.server().blocks.couplerConnectionRange.get();
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

	@Override
	public void setGangwayPartner(BlockPos gangwayPartnerPos) {
		if(gangwayPartnerPos.equals(this.gangwayPartnerPos)) {
			return;
		}
		if(level.getBlockEntity(gangwayPartnerPos) instanceof GangwayFrame partner &&
				getGangwayShape().connectsTo() == partner.getGangwayShape()) {
			SubLevel partnerSubLevel = Sable.HELPER.getContaining(level, gangwayPartnerPos);
			if(this.gangwayPartnerPos != null) {
				removeGangwayPartner();
			}
			this.gangwayPartnerPos = gangwayPartnerPos;
			gangwayPartnerSubLevelID = partnerSubLevel == null ? null : partnerSubLevel.getUniqueId();
			partner.setGangwayPartnerReverse(getBlockPos());
			if(!level.isClientSide()) {
				setChanged();
				sendData();
				level.playSound(null, getBlockPos(), SimurailSoundEvents.GANGWAY_CONNECT.get(), SoundSource.BLOCKS, 0.25F, 1F);
				level.playSound(null, gangwayPartnerPos, SimurailSoundEvents.GANGWAY_CONNECT.get(), SoundSource.BLOCKS, 0.25F, 1F);
			}
		}
	}

	@Override
	public void setGangwayPartnerReverse(BlockPos gangwayPartnerPos) {
		this.gangwayPartnerPos = gangwayPartnerPos;
		if(gangwayPartnerPos != null) {
			SubLevel partnerSubLevel = Sable.HELPER.getContaining(level, gangwayPartnerPos);
			gangwayPartnerSubLevelID = partnerSubLevel == null ? null : partnerSubLevel.getUniqueId();
		}
		else {
			gangwayPartnerSubLevelID = null;
		}
		if(!level.isClientSide()) {
			setChanged();
			sendData();
		}
	}

	@Override
	public void removeGangwayPartner() {
		if(gangwayPartnerPos == null) {
			return;
		}
		if(gangwayPartnerPos != null && level.getBlockEntity(gangwayPartnerPos) instanceof GangwayFrame partner) {
			partner.setGangwayPartnerReverse(null);
			if(!level.isClientSide()) {
				level.playSound(null, gangwayPartnerPos, SimurailSoundEvents.GANGWAY_DISCONNECT.get(), SoundSource.BLOCKS, 0.25F, 1F);
			}
		}
		gangwayPartnerPos = null;
		gangwayPartnerSubLevelID = null;
		if(!level.isClientSide()) {
			setChanged();
			sendData();
			level.playSound(null, getBlockPos(), SimurailSoundEvents.GANGWAY_DISCONNECT.get(), SoundSource.BLOCKS, 0.25F, 1F);
		}
	}

	@Override
	public GangwayFrame getGangwayPartner() {
		if(gangwayPartnerPos != null && level.getBlockEntity(gangwayPartnerPos) instanceof GangwayFrame partner) {
			return partner;
		}
		return null;
	}

	public void setColor(int color) {
		this.color = color;
		if(!level.isClientSide()) {
			setChanged();
			sendData();
		}
	}

	public void setGangwayColor(int color) {
		gangwayColor = color;
		if(!level.isClientSide()) {
			setChanged();
			sendData();
		}
	}

	public void afterMove() {
		if(partnerPos != null) {
			setPartner(partnerPos);
		}
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
		hasGangwayPartner = getGangwayPartner() != null;
		collisionShape = getBlockState().getShape(level, gangwayPartnerPos);
	}

	@Override
	public void tick() {
		init();
		super.tick();
		GangwayFrame gangwayPartner = getGangwayPartner();
		GangwayFrameBlockShape gangwayShape = getGangwayShape();
		Direction facing = getFacing();
		int shapeIndex = 0;

		if(gangwayPartner != null != hasGangwayPartner) {
			hasGangwayPartner = gangwayPartner != null;
			gangwayTimer = 10 - gangwayTimer;
		}

		BlockPos selfPos = getBlockPos();
		SubLevel selfSubLevel = Sable.HELPER.getContaining(this);
		Pose3dc selfPose = selfSubLevel == null ? SimurailMath.POSE_I : selfSubLevel.logicalPose();
		if(gangwayPartner != null) {
			BlockPos partnerPos = gangwayPartner.getBlockPos();
			SubLevel partnerSubLevel = Sable.HELPER.getContaining(level, partnerPos);
			Pose3dc partnerPose = partnerSubLevel == null ? SimurailMath.POSE_I : partnerSubLevel.logicalPose();

			gangwayPartner.getGangwayCenter(gangwayEndPose.position());
			partnerPose.transformPosition(gangwayEndPose.position());
			selfPose.transformPositionInverse(gangwayEndPose.position());
			gangwayEndPose.position().sub(selfPos.getX(), selfPos.getY(), selfPos.getZ());

			selfPose.orientation().conjugate(gangwayEndPose.orientation());
			gangwayEndPose.orientation().mul(partnerPose.orientation());
			gangwayEndPose.orientation().mul(gangwayPartner.getOrientation()).rotateY(Math.PI);

			gangwayShape.center(facing, gangwayCenterOffset);

			double x = gangwayEndPose.position().x - gangwayCenterOffset.x;
			double y = gangwayEndPose.position().y - gangwayCenterOffset.y;
			double z = gangwayEndPose.position().z - gangwayCenterOffset.z;
			double length = getDirection().dot(x, y, z) * 0.5625;

			shapeIndex = Math.clamp((int)Math.round(length * 16) - 1, 0, 29);
		}
		else {
			shapeIndex = Math.clamp((int)Math.round(gangwayRestLength * 16) - 1, 0, 29);
		}
		collisionShape = switch(gangwayShape) {
		case D -> AutomaticCouplerBlock.D_SHAPES[shapeIndex].get(getFacing());
		case U -> AutomaticCouplerBlock.U_SHAPES[shapeIndex].get(getFacing());
		case null, default -> getBlockState().getShape(level, getBlockPos());
		};
		if(gangwayTimer > 0) {
			gangwayTimer--;
		}
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
			double maxDist = 6;
			if(gangwayPartnerPos != null) {
				if(level.getBlockEntity(gangwayPartnerPos) instanceof GangwayFrame partner) {
					SubLevel selfSubLevel = Sable.HELPER.getContaining(this);
					SubLevel otherSubLevel = Sable.HELPER.getContaining(level, gangwayPartnerPos);
					if(selfSubLevel != otherSubLevel && Sable.HELPER.distanceSquaredWithSubLevels(level, getBlockPos().getCenter(), gangwayPartnerPos.getCenter()) > Mth.square(maxDist)) {
						removeGangwayPartner();
					}
					else if(partner.getGangwayPartner() != this) {
						removeGangwayPartner();
					}
				}
				else {
					removeGangwayPartner();
				}
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
						SimurailPhysicsConfig config = SimurailConfig.server().physics;

						Pose3dc selfPose = subLevel.logicalPose();
						Pose3dc partnerPose = partnerSubLevel == null ? SimurailMath.POSE_I : partnerSubLevel.logicalPose();

						this.getJointPosition(this.jointPos);
						partner.getJointPosition(partner.jointPos);

						selfPose.transformPositionInverse(partnerPose.transformPosition(partner.jointPos, this.partnerJointPos));
						partnerPose.transformPositionInverse(selfPose.transformPosition(this.jointPos, partner.partnerJointPos));

						this.partnerJointPos.sub(this.jointPos, this.jointDir);
						partner.jointPos.sub(partner.partnerJointPos, partner.jointDir);

						double jointLength = this.getLength() + partner.getLength();

						SimurailMath.rot(this.jointDir, this.jointRot);
						SimurailMath.rot(partner.jointDir, partner.jointRot);

						if(jointPos.distanceSquared(partnerJointPos) > Mth.square(jointLength + 1)) {
							removePartner();
							removeJoint();
							return;
						}

						double invMass = subLevel.getMassTracker().getInverseNormalMass(this.jointPos, this.jointDir);
						double partnerInvMass = partnerSubLevel == null ? 0 : partnerSubLevel.getMassTracker().getInverseNormalMass(partner.jointPos, partner.jointDir);
						double normalMass = 1 / (invMass + partnerInvMass);

						double frequecy = config.couplerSpringFrequency.get();
						double dampingRate = config.couplerSpringDampingRate.get();
						double stiffness = normalMass * frequecy * frequecy;
						double damping = normalMass * frequecy * dampingRate * 2;

						SubLevelPhysicsSystem physics = SubLevelContainer.getContainer(subLevel.getLevel()).physicsSystem();
						if(joint == null || !joint.isValid()) {
							removeJoint();
							double linearDamping = config.couplerPassiveLinearDamping.get();
							double angularDamping = config.couplerPassiveAngularDamping.get();
							GenericConstraintConfiguration jointConfig = SimurailJoints.freeJoint(
									this.jointPos, partner.jointPos,
									this.jointRot, partner.jointRot);
							joint = physics.getPipeline().addConstraint(subLevel, partnerSubLevel, jointConfig);
							joint.setLimit(ConstraintJointAxis.LINEAR_X, jointLength - 0.5, jointLength + 0.5);
							joint.setMotor(ConstraintJointAxis.LINEAR_X, jointLength, stiffness, damping, false, 0);
							joint.setMotor(ConstraintJointAxis.LINEAR_Y, 0, 0, linearDamping, false, 0);
							joint.setMotor(ConstraintJointAxis.LINEAR_Z, 0, 0, linearDamping, false, 0);
							joint.setMotor(ConstraintJointAxis.ANGULAR_X, 0, 0, angularDamping, false, 0);
							joint.setMotor(ConstraintJointAxis.ANGULAR_Y, 0, 0, angularDamping, false, 0);
							joint.setMotor(ConstraintJointAxis.ANGULAR_Z, 0, 0, angularDamping, false, 0);
						}
						else {
							joint.setFrame1(this.jointPos, this.jointRot);
							joint.setFrame2(partner.jointPos, partner.jointRot);
							if(jointLength != lastJointLength) {
								joint.setLimit(ConstraintJointAxis.LINEAR_X, jointLength - 0.5, jointLength + 0.5);
								joint.setMotor(ConstraintJointAxis.LINEAR_X, jointLength, stiffness, damping, false, 0);
								physics.getPipeline().wakeUp(subLevel);
							}
						}
						lastJointLength = jointLength;
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
			tryConnectGangway();
		}
	}

	protected void removeJoint() {
		if(joint != null) {
			joint.remove();
			joint = null;
			lastJointLength = 0;
		}
	}

	public void tryConnectGangway() {
		if(getGangwayShape() == GangwayFrameBlockShape.NONE || isGangwayPowered()) {
			return;
		}
		GangwayFrame partner = GangwayFrame.findGangwayPartner(this, level);
		if(partner == null) {
			return;
		}
		setGangwayPartner(partner.getBlockPos());
		Set<GangwayFrame> visited = new HashSet<>();
		Couple<GangwayFrame> selfCouple = Couple.create(this, this);
		Couple<GangwayFrame> partnerCouple = Couple.create(partner, partner);
		for(int i = 0; i < 15; ++i) {
			for(boolean cw : Iterate.trueAndFalse) {
				if(selfCouple.get(cw) != null) {
					GangwayFrame cSelf = selfCouple.get(cw);
					GangwayFrame cPartner = partnerCouple.get(cw);
					GangwayFrameShape selfShape = cSelf.getGangwayShape();
					GangwayFrameShape partnerShape = cPartner.getGangwayShape();
					Direction selfOffset = selfShape.adjacentOffset(cSelf.getFacing(), cw);
					Direction partnerOffset = partnerShape.adjacentOffset(cPartner.getFacing(), !cw);
					BlockPos selfPos = cSelf.getBlockPos().relative(selfOffset);
					BlockPos partnerPos = cPartner.getBlockPos().relative(partnerOffset);
					if(level.getBlockEntity(selfPos) instanceof GangwayFrame selfNeighbor &&
							!visited.contains(selfNeighbor) &&
							cSelf.getFacing() == selfNeighbor.getFacing()) {
						GangwayFrameShape neighborShape = selfNeighbor.getGangwayShape();
						int selfAdj = selfShape.adjacentTo(neighborShape, cw);
						int neighborAdj = neighborShape.adjacentTo(selfShape, !cw);
						if((selfAdj != 0 || neighborAdj != 0) &&
								level.getBlockEntity(partnerPos) instanceof GangwayFrame partnerNeighbor &&
								cPartner.getFacing() == partnerNeighbor.getFacing() &&
								neighborShape.connectsTo().equals(partnerNeighbor.getGangwayShape())) {
							visited.add(selfNeighbor);
							selfCouple.set(cw, selfNeighbor);
							partnerCouple.set(cw, partnerNeighbor);
							selfNeighbor.setGangwayPartner(partnerPos);
							if(selfAdj < 0 || neighborAdj < 0) {
								cw = !cw;
							}
							continue;
						}
					}
					selfCouple.set(cw, null);
					partnerCouple.set(cw, null);
				}
			}
		}
	}

	public void tryDisconnectGangway() {
		removeGangwayPartner();
		if(getGangwayShape() == GangwayFrameBlockShape.NONE) {
			return;
		}
		GangwayFrame.getNeighbors(this, level, 15).forEach(GangwayFrame::removeGangwayPartner);
	}

	@Override
	public Iterable<SubLevel> sable$getConnectionDependencies() {
		if(partnerSubLevelID != null) {
			SubLevel subLevel = SubLevelContainer.getContainer(level).getSubLevel(partnerSubLevelID);
			if(subLevel != null) {
				return List.of(subLevel);
			}
		}
		return List.of();
	}

	@Override
	protected AABB createRenderBoundingBox() {
		return super.createRenderBoundingBox().inflate(4);
	}

	@Override
	public AutomaticCouplerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new AutomaticCouplerMenu(windowId, this);
	}

	@Override
	public void setBlockState(BlockState blockState) {
		GangwayFrameBlockShape oldShape = getGangwayShape();
		super.setBlockState(blockState);
		GangwayFrameBlockShape newShape = getGangwayShape();
		if(newShape != oldShape) {
			removeGangwayPartner();
		}
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
		tag.putInt("color", color);

		tag.putDouble("gangway_rest_length", gangwayRestLength);
		tag.putInt("gangway_color", gangwayColor);

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

		Pair<BlockPos, UUID> gangwayPartner = SchematicContextUtil.writeTransform(gangwayPartnerPos, gangwayPartnerSubLevelID);

		if(gangwayPartner.getFirst() != null) {
			tag.put("gangway_partner", NbtUtils.writeBlockPos(gangwayPartner.getFirst()));
			if(gangwayPartner.getSecond() != null) {
				tag.putUUID("gangway_partner_id", gangwayPartner.getSecond());
			}
		}
	}

	@Override
	public void writeSafe(CompoundTag tag, Provider registries) {
		super.writeSafe(tag, registries);
		tag.putBoolean("is_short", isShort);
		tag.putString("type", type.id().toString());
		tag.putInt("color", color);

		tag.putDouble("gangway_rest_length", gangwayRestLength);
		tag.putInt("gangway_color", gangwayColor);
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
		if(tag.contains("color")) {
			color = tag.getInt("color");
		}

		gangwayRestLength = tag.getFloat("gangway_rest_length");
		if(tag.contains("gangway_color")) {
			gangwayColor = tag.getInt("gangway_color");
		}

		connectedPos = NbtUtils.readBlockPos(tag, "connected").orElse(null);
		connectedFront = tag.getBoolean("connected_front");

		Pair<BlockPos, UUID> partner = SchematicContextUtil.readTransform(
				NbtUtils.readBlockPos(tag, "partner").orElse(null),
				tag.hasUUID("partner_id") ? tag.getUUID("partner_id") : null);

		partnerPos = partner.getFirst();
		partnerSubLevelID = partner.getSecond();

		Pair<BlockPos, UUID> gangwayPartner = SchematicContextUtil.readTransform(
				NbtUtils.readBlockPos(tag, "gangway_partner").orElse(null),
				tag.hasUUID("gangway_partner_id") ? tag.getUUID("gangway_partner_id") : null);

		gangwayPartnerPos = gangwayPartner.getFirst();
		gangwayPartnerSubLevelID = gangwayPartner.getSecond();
	}

	// Mutable physics fields
	protected final Vector3d endPos = new Vector3d();
	protected final Vector3d globalEndPos = new Vector3d();

	protected final Vector3d jointPos = new Vector3d();
	protected final Vector3d partnerJointPos = new Vector3d();

	protected final Vector3d jointDir = new Vector3d();
	protected final Quaterniond jointRot = new Quaterniond();

	protected final Vector3d gangwayCenterOffset = new Vector3d();
}
