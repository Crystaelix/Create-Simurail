package com.crystaelix.simurail.content.bogey;

import java.util.List;
import java.util.UUID;

import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;

import com.crystaelix.simurail.api.math.Basis3d;
import com.crystaelix.simurail.api.math.MovingQuaternionfLerp;
import com.crystaelix.simurail.api.math.MovingVector3fLerp;
import com.crystaelix.simurail.api.math.SimurailMath;
import com.crystaelix.simurail.api.physics.AttachableBoxPhysicsObject;
import com.crystaelix.simurail.api.physics.SimurailJoints;
import com.crystaelix.simurail.api.util.SchematicContextUtil;
import com.crystaelix.simurail.compat.SimurailCompat;
import com.crystaelix.simurail.compat.computercraft.SimurailComputerCraftProxy;
import com.crystaelix.simurail.config.SimurailConfig;
import com.crystaelix.simurail.config.SimurailPhysicsConfig;
import com.crystaelix.simurail.content.SimurailBlockEntities;
import com.crystaelix.simurail.content.automatic_coupler.AutomaticCouplerBlockEntity;
import com.crystaelix.simurail.content.steering_connector.SteeringConnectable;
import com.google.common.collect.ImmutableList;
import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import dan200.computercraft.api.peripheral.PeripheralCapability;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import dev.ryanhcode.sable.api.physics.constraint.ConstraintJointAxis;
import dev.ryanhcode.sable.api.physics.constraint.GenericConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.GenericConstraintHandle;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.physics.mass.MassData;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import dev.ryanhcode.sable.util.SableNBTUtils;
import foundry.veil.api.network.VeilPacketManager;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class PhysicsBogeyBlockEntity extends KineticBlockEntity implements Nameable, MenuProvider, BlockEntitySubLevelActor, SteeringConnectable {

	public static final double LINEAR_Y_LIMIT = 0.5;
	public static final double LINEAR_Z_LIMIT = 1;
	public static final double ANGULAR_X_LIMIT = Math.PI / 9;
	public static final double ANGULAR_Y_LIMIT = Math.PI / 4;
	public static final double ANGULAR_Z_LIMIT = Math.PI / 4;
	public static final double TILT_LIMIT = Math.PI / 18;

	public static final Component NAME = Component.translatable("block.simurail.physics_bogey");
	public static final Component INVERTED_NAME = Component.translatable("item.simurail.inverted_physics_bogey");

	protected boolean initialized = false;

	protected Component customName = null;

	// Bogey type components
	protected final PhysicsBogeyOptions options;
	protected CompoundTag bogeyData;
	protected AbstractComputerBehaviour computerBehaviour;
	protected final PhysicsBogeyControlOverrides computerOverrides = new PhysicsBogeyControlOverrides();

	// Connection components
	protected BlockPos connectionFront;
	protected UUID connectionFrontSubLevelID;
	protected boolean connectionFrontToFront;
	protected BlockPos connectionBack;
	protected UUID connectionBackSubLevelID;
	protected boolean connectionBackToFront;
	protected PhysicsBogeySteerGroup steerGroup;

	// Physics components
	protected final Vector3dc localCenter;
	protected AttachableBoxPhysicsObject pivot;
	protected final Pose3d pivotPose = new Pose3d();
	protected Vector3d localPivotOffset;
	protected final Vector3d lastLocalPivotOffset = new Vector3d();
	protected Quaterniond localPivotRot;
	protected final Quaterniond lastLocalPivotRot = new Quaterniond();
	protected GenericConstraintHandle pivotJoint;
	protected final PhysicsBogeyAxle axleFront;
	protected final PhysicsBogeyAxle axleBack;
	public double visualSpeed = 0;
	protected double lastVisualSpeed = 0;

	// Client rendering components
	protected final MovingQuaternionfLerp renderPivotRot = MovingQuaternionfLerp.of(2);
	protected final MovingVector3fLerp renderPivotOffset = MovingVector3fLerp.of(2);
	public float renderAxleOffset;
	protected double distanceMoved;

	public PhysicsBogeyBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
		super(typeIn, pos, state);
		options = new PhysicsBogeyOptions(isInverted());
		localCenter = JOMLConversion.atCenterOf(pos);
		axleFront = new PhysicsBogeyAxle(this, true);
		axleBack = new PhysicsBogeyAxle(this, false);
	}

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		SimurailCompat.COMPUTERCRAFT.ifLoaded(() -> () -> {
			event.registerBlockEntity(
					PeripheralCapability.get(),
					SimurailBlockEntities.PHYSICS_BOGEY.get(),
					(be, context) -> be.computerBehaviour.getPeripheralCapability());
		});
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		behaviours.add(computerBehaviour = SimurailComputerCraftProxy.behaviour(this));
	}

	@Override
	public Component getName() {
		return customName != null ? customName : isInverted() ? INVERTED_NAME : NAME;
	}

	@Override
	public Component getDisplayName() {
		return getName();
	}

	@Override
	public Component getCustomName() {
		return customName;
	}

	public void setCustomName(Component customName) {
		this.customName = customName;
	}

	public PhysicsBogeyOptions getOptions() {
		return options;
	}

	public void setOptions(PhysicsBogeyOptions options) {
		if(computerBehaviour.hasAttachedComputer()) {
			this.options.setNonComputer(options);
		}
		else {
			this.options.set(options);
		}
		axleFront.resetOffset();
		axleBack.resetOffset();
		bogeyData = null;
		if(!level.isClientSide()) {
			setChanged();
			sendData();
		}
	}

	public PhysicsBogeyControlOverrides getComputerOverrides() {
		return computerOverrides;
	}

	@Override
	public Direction getFacing() {
		return getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
	}

	@Override
	public AABB getOutline(Direction direction) {
		return AABB.ofSize(
				getBlockPos().getCenter().add(direction.getStepX() * 0.28125, 0, direction.getStepZ() * 0.28125),
				direction.getStepX() == 0 ? 1 : 0.4375, 1, direction.getStepZ() == 0 ? 1 : 0.4375);
	}

	@Override
	public boolean canConnectSteeringTo(Direction selfDir, SteeringConnectable other, Direction otherDir) {
		if(other instanceof PhysicsBogeyBlockEntity) {
			SubLevel selfSubLevel = Sable.HELPER.getContaining(this);
			SubLevel otherSubLevel = Sable.HELPER.getContaining(level, other.getBlockPos());
			Pose3dc selfPose = selfSubLevel == null ? SimurailMath.POSE_I : selfSubLevel.logicalPose();
			Pose3dc otherPose = otherSubLevel == null ? SimurailMath.POSE_I : otherSubLevel.logicalPose();
			Vector3d selfNormal = selfPose.transformNormal(new Vector3d(selfDir.step()));
			Vector3d otherNormal = otherPose.transformNormal(new Vector3d(otherDir.step()));
			Vector3d selfPos = selfPose.transformPosition(JOMLConversion.atCenterOf(getBlockPos()));
			Vector3d otherPos = otherPose.transformPosition(JOMLConversion.atCenterOf(other.getBlockPos()));
			double x = otherPos.x - selfPos.x;
			double y = otherPos.y - selfPos.y;
			double z = otherPos.z - selfPos.z;
			return selfNormal.dot(x, y, z) > 0 && otherNormal.dot(x, y, z) < 0;
		}
		if(other instanceof AutomaticCouplerBlockEntity) {
			return other.canConnectSteeringTo(otherDir, this, selfDir);
		}
		return false;
	}

	@Override
	public double connectionRange(SteeringConnectable other) {
		if(other instanceof PhysicsBogeyBlockEntity otherBogey) {
			if(Sable.HELPER.getContaining(this) == Sable.HELPER.getContaining(otherBogey)) {
				return SimurailConfig.server().blocks.bogeyConnectionRangeSame.get();
			}
			else {
				return SimurailConfig.server().blocks.bogeyConnectionRangeDifferent.get();
			}
		}
		if(other instanceof AutomaticCouplerBlockEntity) {
			return other.connectionRange(this);
		}
		return 0;
	}

	@Override
	public void connectSteering(boolean front, SteeringConnectable other, boolean otherFront) {
		if(other instanceof PhysicsBogeyBlockEntity otherBogey) {
			SubLevel otherSubLevel = Sable.HELPER.getContaining(otherBogey);
			if(front) {
				if(connectionFront != null) {
					disconnectSteering(true);
				}
				connectionFront = otherBogey.getBlockPos();
				connectionFrontSubLevelID = otherSubLevel == null ? null : otherSubLevel.getUniqueId();
				connectionFrontToFront = otherFront;
			}
			else {
				if(connectionBack != null) {
					disconnectSteering(false);
				}
				connectionBack = otherBogey.getBlockPos();
				connectionBackSubLevelID = otherSubLevel == null ? null : otherSubLevel.getUniqueId();
				connectionBackToFront = otherFront;
			}
			if(steerGroup != null) {
				steerGroup.invalidate();
			}
			if(!level.isClientSide()) {
				setChanged();
				sendData();
			}
			// TODO sfx
			otherBogey.propagateConnectSteering(otherFront, this, front);
		}
		if(other instanceof AutomaticCouplerBlockEntity) {
			other.connectSteering(otherFront, this, front);
		}
	}

	protected void propagateConnectSteering(boolean front, PhysicsBogeyBlockEntity otherBogey, boolean otherFront) {
		SubLevel otherSubLevel = Sable.HELPER.getContaining(otherBogey);
		if(front) {
			connectionFront = otherBogey.getBlockPos();
			connectionFrontSubLevelID = otherSubLevel == null ? null : otherSubLevel.getUniqueId();
			connectionFrontToFront = otherFront;
		}
		else {
			connectionBack = otherBogey.getBlockPos();
			connectionBackSubLevelID = otherSubLevel == null ? null : otherSubLevel.getUniqueId();
			connectionBackToFront = otherFront;
		}
		if(steerGroup != null) {
			steerGroup.invalidate();
		}
		if(!level.isClientSide()) {
			setChanged();
			sendData();
		}
		// TODO sfx
	}

	@Override
	public void disconnectSteering(boolean front) {
		if(front) {
			if(connectionFront != null && level.getBlockEntity(connectionFront) instanceof PhysicsBogeyBlockEntity otherBogey) {
				otherBogey.propagateDisconnectSteering(connectionFrontToFront);
			}
			connectionFront = null;
			connectionFrontSubLevelID = null;
		}
		else {
			if(connectionBack != null && level.getBlockEntity(connectionBack) instanceof PhysicsBogeyBlockEntity otherBogey) {
				otherBogey.propagateDisconnectSteering(connectionBackToFront);
			}
			connectionBack = null;
			connectionBackSubLevelID = null;
		}
		if(steerGroup != null) {
			steerGroup.invalidate();
		}
		if(!level.isClientSide()) {
			setChanged();
			sendData();
		}
		// TODO sfx
	}

	protected void propagateDisconnectSteering(boolean front) {
		if(front) {
			connectionFront = null;
			connectionFrontSubLevelID = null;
		}
		else {
			connectionBack = null;
			connectionBackSubLevelID = null;
		}
		if(steerGroup != null) {
			steerGroup.invalidate();
		}
		if(!level.isClientSide()) {
			setChanged();
			sendData();
		}
		// TODO sfx
	}

	public void afterMove() {
		if(connectionFront != null && level.getBlockEntity(connectionFront) instanceof PhysicsBogeyBlockEntity otherBogey) {
			connectSteering(true, otherBogey, connectionFrontToFront);
		}
		if(connectionBack != null && level.getBlockEntity(connectionBack) instanceof PhysicsBogeyBlockEntity otherBogey) {
			connectSteering(false, otherBogey, connectionBackToFront);
		}
	}

	public boolean isInverted() {
		return getBlockState().getValue(BlockStateProperties.INVERTED);
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

	public Vector3dc getLateral() {
		return switch(getFacing()) {
		case EAST -> SimurailMath.DIR_ZP;
		case WEST -> SimurailMath.DIR_ZN;
		case SOUTH -> SimurailMath.DIR_XN;
		case NORTH -> SimurailMath.DIR_XP;
		case null, default -> throw new IllegalArgumentException("Unexpected value: " + getFacing());
		};
	}

	public Quaterniondc getJointOrientation() {
		return switch(getFacing()) {
		case EAST -> SimurailMath.ROT_XPYPZP;
		case WEST -> SimurailMath.ROT_XNYPZN;
		case SOUTH -> SimurailMath.ROT_ZPYPXN;
		case NORTH -> SimurailMath.ROT_ZNYPXP;
		case null, default -> throw new IllegalArgumentException("Unexpected value: " + getFacing());
		};
	}

	public PhysicsBogeyAxle getAxle(boolean front) {
		return front ? axleFront : axleBack;
	}

	public PhysicsBogeyBlockEntity getConnected(boolean front) {
		if(front) {
			if(connectionFront != null && level.getBlockEntity(connectionFront) instanceof PhysicsBogeyBlockEntity other) {
				return other;
			}
		}
		else {
			if(connectionBack != null && level.getBlockEntity(connectionBack) instanceof PhysicsBogeyBlockEntity other) {
				return other;
			}
		}
		return null;
	}

	public boolean getConnectedToFront(boolean front) {
		return front ? connectionFrontToFront : connectionBackToFront;
	}

	// Sometimes physicsTick happens before tick?
	public void init() {
		if(initialized) {
			return;
		}
		if(localPivotRot == null) {
			localPivotRot = new Quaterniond(getJointOrientation());
		}
		if(localPivotOffset == null) {
			localPivotOffset = new Vector3d();
		}
		lastLocalPivotRot.set(localPivotRot);
		lastLocalPivotOffset.set(localPivotOffset);
		renderPivotRot.initialize(localPivotRot);
		resetPivotPose();
		if(!level.isClientSide() && Sable.HELPER.getContaining(this) instanceof ServerSubLevel subLevel) {
			createPivot(subLevel);
			axleFront.init(subLevel);
			axleBack.init(subLevel);
		}
		initialized = true;
	}

	protected void resetPivotPose() {
		pivotPose.position().set(localCenter).add(localPivotOffset);
		pivotPose.orientation().set(localPivotRot);
		SubLevel subLevel = Sable.HELPER.getContaining(this);
		if(subLevel != null) {
			Pose3d containingPose = subLevel.logicalPose();
			containingPose.transformPosition(pivotPose.position());
			pivotPose.orientation().premul(containingPose.orientation());
		}
	}

	protected void createPivot(ServerSubLevel subLevel) {
		SimurailPhysicsConfig config = SimurailConfig.server().physics;
		SubLevelPhysicsSystem physics = SubLevelContainer.getContainer(subLevel.getLevel()).physicsSystem();
		if(pivot == null || pivot.isRemoved()) {
			pivot = new AttachableBoxPhysicsObject(subLevel, pivotPose, new Vector3d(0.5, 0.125, 0.125), config.bogeyPivotMass.get());
			physics.addObject(pivot);
		}
		if(pivotJoint == null || !pivotJoint.isValid()) {
			double angularDamping = config.bogeyPassiveAngularDamping.get();
			GenericConstraintConfiguration jointConfig = SimurailJoints.pivotJoint(
					localCenter, SimurailMath.VEC_0,
					getJointOrientation(), SimurailMath.ROT_XPYPZP);
			pivotJoint = physics.getPipeline().addConstraint(subLevel, pivot, jointConfig);
			pivotJoint.setContactsEnabled(false);
			pivotJoint.setMotor(ConstraintJointAxis.ANGULAR_Y, 0, 0, angularDamping, false, 0);
			pivotJoint.setMotor(ConstraintJointAxis.ANGULAR_Z, 0, 0, angularDamping, false, 0);
		}
	}

	protected void removePivot(ServerSubLevel subLevel) {
		axleFront.invalidate(subLevel);
		axleBack.invalidate(subLevel);
		if(pivotJoint != null) {
			pivotJoint.remove();
			pivotJoint = null;
		}
		if(pivot != null) {
			SubLevelPhysicsSystem physics = SubLevelContainer.getContainer(subLevel.getLevel()).physicsSystem();
			physics.removeObject(pivot);
			pivot = null;
		}
	}

	@Override
	public void tick() {
		init();
		super.tick();
		if(!level.isClientSide()) {
			if(!computerBehaviour.hasAttachedComputer() && computerOverrides.hasOverrides()) {
				computerOverrides.reset();
			}
			if(Sable.HELPER.getContaining(this) instanceof ServerSubLevel) {
				axleFront.updateVisualSpeed();
				axleBack.updateVisualSpeed();

				axleFront.updateSignalGroup();
				axleBack.updateSignalGroup();

				axleFront.updateInnerProbe();
				axleBack.updateInnerProbe();

				axleFront.updateOffsetChange();
				axleBack.updateOffsetChange();

				// TODO sfx
			}
			else {
				localPivotOffset.zero();
				localPivotRot.set(getJointOrientation());
				resetPivotPose();
			}
			visualSpeed = Math.abs(axleFront.visualSpeed) > Math.abs(axleBack.visualSpeed) ? axleFront.visualSpeed : axleBack.visualSpeed;
			if(!lastLocalPivotOffset.equals(localPivotOffset, 1E-4) || !lastLocalPivotRot.equals(localPivotRot, 1E-4) || lastVisualSpeed != visualSpeed) {
				VeilPacketManager.tracking(this).sendPacket(new PhysicsBogeyRenderDataPacket(this));
			}
			lastLocalPivotOffset.set(localPivotOffset);
			lastLocalPivotRot.set(localPivotRot);
			lastVisualSpeed = visualSpeed;
		}
		else {
			renderPivotOffset.step(localPivotOffset);
			renderPivotRot.step(localPivotRot);
			distanceMoved = Math.fma(visualSpeed, 0.05, distanceMoved);
		}
	}

	@Override
	public void lazyTick() {
		super.lazyTick();
		if(!level.isClientSide()) {
			if(Sable.HELPER.getContaining(this) instanceof ServerSubLevel) {
			}

			double maxDist = SimurailConfig.server().blocks.bogeyConnectionRangeDifferent.get();
			if(connectionFront != null) {
				if(level.getBlockEntity(connectionFront) instanceof PhysicsBogeyBlockEntity other) {
					SubLevel selfSubLevel = Sable.HELPER.getContaining(this);
					SubLevel otherSubLevel = Sable.HELPER.getContaining(other);
					if(selfSubLevel != otherSubLevel && Sable.HELPER.distanceSquaredWithSubLevels(level, getBlockPos().getCenter(), connectionFront.getCenter()) > Mth.square(maxDist)) {
						disconnectSteering(true);
					}
					else if(other.getConnected(connectionFrontToFront) != this) {
						disconnectSteering(true);
					}
				}
				else {
					disconnectSteering(true);
				}
			}
			if(connectionBack != null) {
				if(level.getBlockEntity(connectionBack) instanceof PhysicsBogeyBlockEntity other) {
					SubLevel selfSubLevel = Sable.HELPER.getContaining(this);
					SubLevel otherSubLevel = Sable.HELPER.getContaining(other);
					if(selfSubLevel != otherSubLevel && Sable.HELPER.distanceSquaredWithSubLevels(level, getBlockPos().getCenter(), connectionBack.getCenter()) > Mth.square(maxDist)) {
						disconnectSteering(false);
					}
					else if(other.getConnected(connectionBackToFront) != this) {
						disconnectSteering(true);
					}
				}
				else {
					disconnectSteering(false);
				}
			}
		}
	}

	@Override
	public void sable$physicsTick(ServerSubLevel subLevel, RigidBodyHandle handle, double timeStep) {
		init();
		if(pivot.isRemoved()) {
			createPivot(subLevel);
		}
		pivot.updatePose();
		pivotPose.set(pivot.getPose());
		subLevel.logicalPose().transformPositionInverse(pivotPose.position(), localPivotOffset).sub(localCenter);
		subLevel.logicalPose().orientation().conjugate(localPivotRot).mul(pivotPose.orientation());

		updateAxles(subLevel, handle, timeStep);
		updatePivotLimits(subLevel, timeStep);
		updateForces(subLevel, handle, timeStep);
	}

	protected void updateAxles(ServerSubLevel subLevel, RigidBodyHandle handle, double timeStep) {
		axleFront.updateOffset(subLevel, timeStep);
		axleBack.updateOffset(subLevel, timeStep);

		axleFront.updateTrack(subLevel, timeStep);
		axleBack.updateTrack(subLevel, timeStep);

		axleFront.updateJoint(subLevel);
		axleBack.updateJoint(subLevel);

		axleFront.updateLimits(subLevel, timeStep);
		axleBack.updateLimits(subLevel, timeStep);

		axleFront.updateForces(subLevel, timeStep);
		axleBack.updateForces(subLevel, timeStep);
	}

	protected void updatePivotLimits(ServerSubLevel subLevel, double timeStep) {
		boolean hasTrack = hasTrack();
		boolean bothTrack = !isDerailed();

		double linYLimit = options.enabled && options.allowVerticalOffset && hasTrack ? LINEAR_Y_LIMIT : 0;
		double linZLimit = options.enabled && options.allowLateralOffset && hasTrack ? LINEAR_Z_LIMIT : 0;
		double angXLimit = options.enabled && hasTrack ? ANGULAR_X_LIMIT : 0;
		double angYLimit = options.enabled && options.allowYawOffset && hasTrack ? ANGULAR_Y_LIMIT : 0;
		double angZLimit = options.enabled && options.allowPitchOffset && bothTrack ? ANGULAR_Z_LIMIT : 0;

		pivotJoint.setLimit(ConstraintJointAxis.LINEAR_Y, -linYLimit, linYLimit);
		pivotJoint.setLimit(ConstraintJointAxis.LINEAR_Z, -linZLimit, linZLimit);
		pivotJoint.setLimit(ConstraintJointAxis.ANGULAR_X, -angXLimit, angXLimit);
		pivotJoint.setLimit(ConstraintJointAxis.ANGULAR_Y, -angYLimit, angYLimit);
		pivotJoint.setLimit(ConstraintJointAxis.ANGULAR_Z, -angZLimit, angZLimit);
	}

	protected void updateForces(ServerSubLevel subLevel, RigidBodyHandle handle, double timeStep) {
		if(!options.enabled || !isActive()) {
			return;
		}

		RigidBodyHandle pivotHandle = RigidBodyHandle.of(subLevel.getLevel(), pivot);
		Vector3dc localDir = getDirection();
		Quaterniond rot = subLevel.logicalPose().orientation();
		globalBasis.orthogonalized(localDir, SimurailMath.DIR_YP).transform(rot);
		pivotPose.transformNormal(SimurailMath.DIR_YP, globalPivotVert);
		handle.getAngularVelocity(globalAngVel);
		pivotHandle.getAngularVelocity(globalPivotAngVel);
		globalAngVel.sub(globalPivotAngVel, globalRelAngVel);
		MassData massData = subLevel.getMassTracker();

		queuedTorque.zero();

		SimurailPhysicsConfig config = SimurailConfig.server().physics;

		// Linear Y
		if(options.allowVerticalOffset) {
			double normalMass = 1 / massData.getInverseNormalMass(localCenter, SimurailMath.DIR_YP);

			double frequency = config.bogeyVerticalSpringFrequency.get();
			double dampingRate = config.bogeyVerticalSpringDampingRate.get();
			double stiffness = normalMass * frequency * frequency;
			double damping = normalMass * frequency * dampingRate * 2;
			double maxForce = config.bogeyVerticalSpringMaxForce.get();

			pivotJoint.setMotor(ConstraintJointAxis.LINEAR_Y, 0, stiffness, damping, true, maxForce);
		}
		else {
			pivotJoint.setMotor(ConstraintJointAxis.LINEAR_Y, 0, 0, 1, false, 0);
		}

		// Linear Z
		if(options.allowLateralOffset) {
			double normalMass = 1 / massData.getInverseNormalMass(localCenter, getLateral());

			double frequency = config.bogeyLateralSpringFrequency.get();
			double dampingRate = config.bogeyLateralSpringDampingRate.get();
			double stiffness = normalMass * frequency * frequency;
			double damping = normalMass * frequency * dampingRate * 2;
			double maxForce = config.bogeyLateralSpringMaxForce.get();

			pivotJoint.setMotor(ConstraintJointAxis.LINEAR_Z, 0, stiffness, damping, true, maxForce);
		}
		else {
			pivotJoint.setMotor(ConstraintJointAxis.LINEAR_Z, 0, 0, 1, false, 0);
		}

		// Angular X
		{
			double offset = SimurailMath.angle(globalBasis.vertical, globalPivotVert, globalBasis.direction);
			double velocity = globalRelAngVel.dot(globalBasis.direction);

			double kLateral = getSignedLateralCurvature();
			double speed = getMovementSpeed();
			double centAcc = speed * speed * kLateral;

			double torqueOffset = massData.getCenterOfMass().y() - localCenter.y();
			double mass = massData.getMass();
			double centTorque = mass * centAcc * torqueOffset * Math.cos(offset);

			double tiltStrength = options.getTiltStrength() * 0.1;
			double tilt = Math.clamp(Math.atan(centAcc * tiltStrength), -TILT_LIMIT, TILT_LIMIT);

			double moment = SimurailMath.moment(massData, localCenter, localDir);

			double frequency = config.bogeyAngularSpringFrequency.get();
			double dampingRate = config.bogeyAngularSpringDampingRate.get();
			double stiffness = moment * frequency * frequency;
			double damping = moment * frequency * dampingRate * 2;
			double maxTorque = config.bogeyAngularSpringMaxTorque.get();

			double torqueMag = (centTorque + stiffness * (offset + tilt) - damping * velocity) / getActiveBogeyCount(subLevel);
			torqueMag = Math.clamp(torqueMag, -maxTorque, maxTorque);

			queuedTorque.fma(torqueMag * timeStep, globalBasis.direction);
		}

		rot.transformInverse(queuedTorque);
		handle.applyTorqueImpulse(queuedTorque);

		rot.transform(queuedTorque);
		pivotPose.transformNormalInverse(queuedTorque);
		queuedTorque.negate();
		pivotHandle.applyTorqueImpulse(queuedTorque);
	}

	protected boolean isActive() {
		return options.enabled && axleFront.hasTrack() && axleBack.hasTrack();
	}

	protected int getActiveBogeyCount(ServerSubLevel subLevel) {
		int count = 0;
		for(BlockEntitySubLevelActor actor : subLevel.getPlot().getBlockEntityActors()) {
			if(actor instanceof PhysicsBogeyBlockEntity bogey) {
				if(bogey.isActive()) {
					++count;
				}
			}
		}
		return count;
	}

	public double getControlStrength() {
		return Math.clamp((isInverted() ? level.getSignal(getBlockPos().below(), Direction.DOWN) : level.getSignal(getBlockPos().above(), Direction.UP)) / 15D, 0, 1);
	}

	public double getBrakeStrength() {
		if(computerBehaviour.hasAttachedComputer() && computerOverrides.overrideBrakeStrength) {
			return computerOverrides.getBrakeStrength();
		}
		return switch(options.controlMode) {
		case BRAKING -> getControlStrength();
		case BRAKING_INVERTED -> 1 - getControlStrength();
		case null, default -> 0;
		};
	}

	public double getSteerValue() {
		if(computerBehaviour.hasAttachedComputer() && computerOverrides.overrideSteerValue) {
			return computerOverrides.getSteerValue();
		}
		int value = switch(getFacing()) {
		case EAST -> level.getSignal(getBlockPos().south(), Direction.SOUTH) - level.getSignal(getBlockPos().north(), Direction.NORTH);
		case WEST -> level.getSignal(getBlockPos().north(), Direction.NORTH) - level.getSignal(getBlockPos().south(), Direction.SOUTH);
		case SOUTH -> level.getSignal(getBlockPos().west(), Direction.WEST) - level.getSignal(getBlockPos().east(), Direction.EAST);
		case NORTH -> level.getSignal(getBlockPos().east(), Direction.EAST) - level.getSignal(getBlockPos().west(), Direction.WEST);
		case null, default -> throw new IllegalArgumentException("Unexpected value: " + getFacing());
		};
		return Math.clamp(value / 15D, -1, 1);
	}

	public double getGroupSteerValue() {
		if(steerGroup == null) {
			PhysicsBogeySteerGroup.createAndUpdate(this);
		}
		return steerGroup.getSteerValue(this);
	}

	@Override
	public Iterable<SubLevel> sable$getConnectionDependencies() {
		ImmutableList.Builder<SubLevel> builder = ImmutableList.builderWithExpectedSize(2);
		SubLevelContainer container = SubLevelContainer.getContainer(level);
		if(connectionFront != null && connectionFrontSubLevelID != null) {
			SubLevel subLevel = container.getSubLevel(connectionFrontSubLevelID);
			if(subLevel != null) {
				builder.add(subLevel);
			}
		}
		if(connectionBack != null && connectionBackSubLevelID != null) {
			SubLevel subLevel = container.getSubLevel(connectionBackSubLevelID);
			if(subLevel != null) {
				builder.add(subLevel);
			}
		}
		return builder.build();
	}

	@Override
	public float calculateStressApplied() {
		if(!options.enabled) {
			return 0;
		}
		float stressMultiplier;
		if(computerBehaviour.hasAttachedComputer() && computerOverrides.overrideStressMultiplier) {
			stressMultiplier = (float)computerOverrides.getStressMultiplier();
		}
		else {
			stressMultiplier = (float)switch(options.controlMode) {
			case STRENGTH -> getControlStrength();
			case STRENGTH_INVERTED -> 1 - getControlStrength();
			case null, default -> 1;
			};
		}
		return options.getStress() * stressMultiplier;
	}

	@Override
	protected AABB createRenderBoundingBox() {
		return super.createRenderBoundingBox().inflate(16);
	}

	protected void updateRenderData(Vector3dc pivotOffset, Quaterniondc pivotRot, float visualSpeed) {
		this.localPivotOffset.set(pivotOffset);
		this.localPivotRot.set(pivotRot);
		this.visualSpeed = visualSpeed;
	}

	public CompoundTag getBogeyData() {
		if(bogeyData == null) {
			bogeyData = options.type.data(isInverted());
		}
		return bogeyData;
	}

	public Vector3f getRenderPivotOffset(float partialTick, Vector3f dest) {
		return renderPivotOffset.lerp(partialTick, dest);
	}

	public Quaternionf getRenderPivotRot(float partialTick, Quaternionf dest) {
		return renderPivotRot.nlerp(partialTick, dest);
	}

	public Vector3f getConnectorAnchorOffset(float partialTick, boolean front, Vector3f dest) {
		options.type.connectorAnchorOffset(isInverted(), dest);
		if(!front) {
			dest.x = -dest.x;
		}
		return dest;
	}

	public float getWheelAngle(float partialTick) {
		double distance = Math.fma(visualSpeed * 0.05, partialTick, distanceMoved);
		double angle = distance / options.type.wheelRadius();
		return (float)Math.toDegrees(angle) % 360;
	}

	public boolean hasTrack() {
		return axleFront.hasTrack() || axleBack.hasTrack();
	}

	public boolean isDerailed() {
		return !axleFront.hasTrack() || !axleBack.hasTrack();
	}

	public double getMovementSpeed() {
		return (axleFront.speed + axleBack.speed) * 0.5;
	}

	public double getLateralCurvature() {
		return Math.max(axleFront.kLateral, axleBack.kLateral);
	}

	public double getSignedLateralCurvature() {
		return (axleFront.kLateralSigned + axleBack.kLateralSigned) * 0.5;
	}

	public double getVerticalCurvature() {
		return (axleFront.kVertical + axleBack.kVertical) * 0.5;
	}

	@Override
	public PhysicsBogeyMenu createMenu(int windowId, Inventory inv, Player player) {
		return new PhysicsBogeyMenu(windowId, this);
	}

	@Override
	public void setBlockState(BlockState blockState) {
		super.setBlockState(blockState);
		if(!initialized) {
			return;
		}
		if(!level.isClientSide()) {
			if(pivot != null && Sable.HELPER.getContaining(this) instanceof ServerSubLevel subLevel) {
				if(pivotJoint != null) {
					pivotJoint.remove();
					pivotJoint = null;
				}
				createPivot(subLevel);
			}
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if(!level.isClientSide()) {
			if(Sable.HELPER.getContaining(this) instanceof ServerSubLevel subLevel) {
				removePivot(subLevel);
			}
		}
		computerBehaviour.removePeripheral();
	}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);

		tag.put("options", options.write());
		if(customName != null) {
			tag.putString("custom_name", Component.Serializer.toJson(customName, registries));
		}

		if(!clientPacket) {
			tag.put("axle_front", axleFront.write());
			tag.put("axle_back", axleBack.write());
			tag.put("computer_overrides", computerOverrides.write());
		}

		if(localPivotOffset != null) {
			tag.put("pivot_offset", SableNBTUtils.writeVector3d(localPivotOffset));
		}
		if(localPivotRot != null) {
			tag.put("pivot_rot", SableNBTUtils.writeQuaternion(localPivotRot));
		}
		tag.putDouble("visual_speed", visualSpeed);

		Pair<BlockPos, UUID> front = SchematicContextUtil.writeTransform(connectionFront, connectionFrontSubLevelID);
		Pair<BlockPos, UUID> back = SchematicContextUtil.writeTransform(connectionBack, connectionBackSubLevelID);

		if(front.getFirst() != null) {
			tag.put("connection_front", NbtUtils.writeBlockPos(front.getFirst()));
			if(front.getSecond() != null) {
				tag.putUUID("connection_front_id", front.getSecond());
			}
			tag.putBoolean("connection_front_front", connectionFrontToFront);
		}
		if(back.getFirst() != null) {
			tag.put("connection_back", NbtUtils.writeBlockPos(back.getFirst()));
			if(back.getSecond() != null) {
				tag.putUUID("connection_back_id", back.getSecond());
			}
			tag.putBoolean("connection_back_front", connectionBackToFront);
		}
	}

	@Override
	public void writeSafe(CompoundTag tag, HolderLookup.Provider registries) {
		super.writeSafe(tag, registries);
		tag.put("options", options.write());
		if(customName != null) {
			tag.putString("custom_name", Component.Serializer.toJson(customName, registries));
		}
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);

		if(tag.contains("options")) {
			options.read(tag.getCompound("options"));
			bogeyData = null;
		}
		if(tag.contains("custom_name")) {
			customName = Component.Serializer.fromJson(tag.getString("custom_name"), registries);
		}

		if(!clientPacket) {
			axleFront.read(tag.getCompound("axle_front"));
			axleBack.read(tag.getCompound("axle_back"));
			computerOverrides.read(tag.getCompound("computer_overrides"));
		}

		if(tag.contains("pivot_offset")) {
			if(localPivotOffset == null) {
				localPivotOffset = new Vector3d();
			}
			localPivotOffset.set(SableNBTUtils.readVector3d(tag.getCompound("pivot_offset")));
		}
		if(tag.contains("pivot_rot")) {
			if(localPivotRot == null) {
				localPivotRot = new Quaterniond(getJointOrientation());
			}
			localPivotRot.set(SableNBTUtils.readQuaternion(tag.getCompound("pivot_rot")));
		}

		visualSpeed = tag.getDouble("visual_speed");

		Pair<BlockPos, UUID> front = SchematicContextUtil.readTransform(
				NbtUtils.readBlockPos(tag, "connection_front").orElse(null),
				tag.hasUUID("connection_front_id") ? tag.getUUID("connection_front_id") : null);
		Pair<BlockPos, UUID> back = SchematicContextUtil.readTransform(
				NbtUtils.readBlockPos(tag, "connection_back").orElse(null),
				tag.hasUUID("connection_back_id") ? tag.getUUID("connection_back_id") : null);

		connectionFront = front.getFirst();
		connectionFrontSubLevelID = front.getSecond();
		connectionFrontToFront = tag.getBoolean("connection_front_front");
		connectionBack = back.getFirst();
		connectionBackSubLevelID = back.getSecond();
		connectionBackToFront = tag.getBoolean("connection_back_front");

		if(steerGroup != null) {
			steerGroup.invalidate();
		}
	}

	// Mutable physics fields
	protected Basis3d globalBasis = new Basis3d();
	protected Vector3d globalPivotVert = new Vector3d();
	protected Vector3d globalAngVel = new Vector3d();
	protected Vector3d globalPivotAngVel = new Vector3d();
	protected Vector3d globalRelAngVel = new Vector3d();
	protected Vector3d queuedTorque = new Vector3d();
}
