package com.crystaelix.simurail.content.bogey;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import com.crystaelix.simurail.api.extension.BezierConnectionExtension;
import com.crystaelix.simurail.api.math.Frame3d;
import com.crystaelix.simurail.api.math.SimurailMath;
import com.crystaelix.simurail.api.physics.AttachableBoxPhysicsObject;
import com.crystaelix.simurail.api.physics.SimurailJoints;
import com.crystaelix.simurail.api.track.TrackTypeEntries;
import com.crystaelix.simurail.api.track.TrackTypeEntry;
import com.crystaelix.simurail.config.SimurailConfig;
import com.crystaelix.simurail.config.SimurailPhysicsConfig;
import com.crystaelix.simurail.content.SimurailForceGroups;
import com.crystaelix.simurail.content.track.CurvedTrackSegment;
import com.crystaelix.simurail.content.track.TrackSegment;
import com.crystaelix.simurail.content.track.TrackSegmentHelper;
import com.crystaelix.simurail.extension.SignalEdgeGroupExtension;
import com.crystaelix.simurail.extension.TrackObserverExtension;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.entity.TravellingPoint.ITrackSelector;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.signal.SignalEdgeGroup;
import com.simibubi.create.content.trains.track.BezierConnection;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.physics.constraint.ConstraintJointAxis;
import dev.ryanhcode.sable.api.physics.constraint.FixedConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.FixedConstraintHandle;
import dev.ryanhcode.sable.api.physics.constraint.GenericConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.GenericConstraintHandle;
import dev.ryanhcode.sable.api.physics.force.QueuedForceGroup;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.physics.mass.MassData;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.mixinterface.clip_overwrite.ClipContextExtension;
import dev.ryanhcode.sable.physics.config.block_properties.PhysicsBlockPropertyHelper;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.createmod.catnip.data.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

public class PhysicsBogeyAxle {

	public static final double ANGULAR_Y_LIMIT = Math.PI / 3;
	public static final double ANGULAR_Z_LIMIT = Math.PI / 3;

	protected final PhysicsBogeyBlockEntity bogey;
	protected final boolean logicalFront;
	protected final Frame3d axleFrame = new Frame3d();

	protected final Vector3d lastOffset = new Vector3d();
	protected final Vector3d targetOffset = new Vector3d();
	protected float vertOffset;
	protected double offsetTimer = 0;

	protected TrackSegment trackSegment;
	protected boolean trackReversed;
	protected ServerSubLevel trackSubLevel;
	protected Pose3dc trackSubLevelPose;

	protected TrackGraph trackGraph;
	protected TravellingPoint trackPoint = new TravellingPoint();
	protected TravellingPoint probe = new TravellingPoint();

	protected BlockHitResult clipResult;
	protected ServerSubLevel clipSubLevel;

	protected GenericConstraintHandle joint;
	protected GenericConstraintHandle bogeyJoint;
	protected boolean yFixed = false;
	protected boolean zFixed = false;
	protected double yFixedTimer = 0;
	protected double zFixedTimer = 0;

	protected double speed;
	protected double targetSpeed;
	protected double visualSpeedLerpFactor;
	protected double visualSpeed;
	protected double trackRecheckTime = 0.5;

	protected AttachableBoxPhysicsObject axleBox;
	protected FixedConstraintHandle axleBoxJoint;

	protected double kLateralSigned;
	protected double kLateral;
	protected double kVertical;

	public PhysicsBogeyAxle(PhysicsBogeyBlockEntity bogey, boolean logicalFront) {
		this.bogey = bogey;
		this.logicalFront = logicalFront;
	}

	protected void init(ServerSubLevel subLevel) {
		double latOffset = bogey.options.type.axleSpacing() / 2;
		vertOffset = bogey.options.getAxleOffset();
		targetOffset.x = logicalFront ? latOffset : -latOffset;
		targetOffset.y = bogey.isInverted() ? 0.5 + vertOffset : -1.5 - vertOffset;
		if(offsetTimer > 0) {
			targetOffset.lerp(lastOffset, offsetTimer, axleFrame.position);
		}
		else {
			axleFrame.position.set(targetOffset);
		}
		if(trackSegment == null) {
			trackRecheckTime = 0.05;
		}
		updateTrack(subLevel, 0);
	}

	protected void resetOffset() {
		lastOffset.set(axleFrame.position);
		double latOffset = bogey.options.type.axleSpacing() / 2;
		vertOffset = bogey.options.getAxleOffset();
		targetOffset.x = logicalFront ? latOffset : -latOffset;
		targetOffset.y = bogey.isInverted() ? 0.5 + vertOffset : -1.5 - vertOffset;
		offsetTimer = 1;
		trackRecheckTime = 0.05;
	}

	protected void updateVisualSpeed() {
		visualSpeed = visualSpeed * 0.95 * (1 - bogey.getBrakeStrength());
	}

	protected void updateSignalGroup() {
		if(trackGraph != null && trackPoint.edge != null) {
			UUID signalGroupId = trackPoint.edge.getEdgeData().getGroupAtPosition(trackGraph, trackPoint.position);
			if(signalGroupId != null) {
				SignalEdgeGroup signalGroup = Create.RAILWAYS.signalEdgeGroups.get(signalGroupId);
				if(signalGroup != null) {
					((SignalEdgeGroupExtension)signalGroup).simurail$queueBogey(bogey);
				}
			}
		}
	}

	protected void updateInnerProbe() {
		if(trackGraph != null && trackPoint.edge != null) {
			resetProbe(trackPoint);
			double travelDist = bogey.options.type.axleSpacing();
			if(logicalFront) travelDist *= -1;
			if(trackReversed) travelDist *= -1;
			probe.travel(trackGraph,
					travelDist,
					followOtherOrSteer(probe),
					(distance, couple) -> {
						if(couple.getFirst() instanceof TrackObserverExtension observer) {
							observer.simurail$keepAlive(bogey);
						}
						return false;
					},
					probe.ignoreTurns(),
					$ -> true);
		}
	}

	protected void updateOffsetChange() {
		if(vertOffset != bogey.options.getAxleOffset()) {
			resetOffset();
		}
	}

	protected void updateOffset(ServerSubLevel subLevel, double timeStep) {
		if(offsetTimer > 0) {
			offsetTimer -= timeStep / SimurailConfig.server().physics.axleSpacingUpdateTime.get();
			if(offsetTimer > 0) {
				targetOffset.lerp(lastOffset, offsetTimer, axleFrame.position);
			}
			else {
				offsetTimer = 0;
				axleFrame.position.set(targetOffset);
			}
		}
	}

	protected void updateTrack(ServerSubLevel subLevel, double timeStep) {
		trackRecheckTime -= timeStep;

		ServerLevel level = subLevel.getLevel();
		axleFrame.transform(bogey.pivotPose, globalAxleFrame);
		globalAxleFrame.transformInverse(subLevel.logicalPose(), bogeyAxleFrame);

		t:if(trackSegment != null) {
			trackSubLevel = (ServerSubLevel)Sable.HELPER.getContaining(level, trackSegment.start());
			trackSubLevelPose = trackSubLevel == null ? SimurailMath.POSE_I : trackSubLevel.logicalPose();
			globalAxleFrame.transformInverse(trackSubLevelPose, trackAxleFrame);

			boolean inverted = bogey.isInverted();
			if(!trackSegment.inSegmentRange(trackAxleFrame.position, trackAxleFrame.vertical, inverted)) {
				// No longer on track segment
				double t = trackSegment.projectT(trackAxleFrame.position);
				if(trackSegment instanceof CurvedTrackSegment curve) {
					CurvedTrackSegment nextSegment = curve.next(t < 0.5);
					while(nextSegment != null) {
						if(!nextSegment.inLineRange(trackAxleFrame.position, trackAxleFrame.vertical, inverted)) {
							break;
						}
						if(nextSegment.inProjectionRange(trackAxleFrame.position, trackAxleFrame.vertical)) {
							trackSegment = nextSegment;
							trackRecheckTime = SimurailConfig.server().physics.axleTrackRecheckTime.get();
							bogey.setChanged();
							break t;
						}
						else {
							nextSegment = nextSegment.next(t < 0.5);
						}
					}
				}

				updateGraph(false);
				if(trackGraph != null) {
					resetProbe(trackPoint);
					probe.position = t < 0.5 ? 0.05 : probe.edge.getLength() - 0.05;
					probe.travel(trackGraph, t < 0.5 ? -0.1 : 0.1,
							followOrSteer(probe),
							probe.ignoreEdgePoints(),
							probe.ignoreTurns(),
							$ -> true);
					if(!probe.blocked) {
						TrackSegment nextSegment = TrackSegmentHelper.fromTrackEdge(probe.edge, t < 0.5);
						while(nextSegment != null) {
							if(!nextSegment.inLineRange(trackAxleFrame.position, trackAxleFrame.vertical, inverted)) {
								break;
							}
							if(nextSegment.inProjectionRange(trackAxleFrame.position, trackAxleFrame.vertical)) {
								trackSegment = nextSegment;
								trackRecheckTime = SimurailConfig.server().physics.axleTrackRecheckTime.get();
								bogey.setChanged();
								break t;
							}
							else if(nextSegment instanceof CurvedTrackSegment curve) {
								nextSegment = curve.next(t < 0.5);
							}
							else {
								break;
							}
						}
					}
				}

				findTrack(subLevel);
			}
		}

		if(trackRecheckTime <= 0) {
			findTrack(subLevel);
		}

		if(!bogey.options.enabled) {
			yFixed = zFixed = false;
			yFixedTimer = zFixedTimer = 0;
		}

		RigidBodyHandle.of(subLevel.getLevel(), bogey.pivot).getLinearVelocity(globalAxleVel);

		if(trackSegment != null) {
			ServerSubLevel newTrackSubLevel = (ServerSubLevel)Sable.HELPER.getContaining(level, trackSegment.start());
			if(trackSubLevel != newTrackSubLevel) {
				removeJoint();
				trackSubLevel = newTrackSubLevel;
			}
			trackSubLevelPose = trackSubLevel == null ? SimurailMath.POSE_I : trackSubLevel.logicalPose();
			globalAxleFrame.transformInverse(trackSubLevelPose, trackAxleFrame);

			double t = trackSegment.projectT(trackAxleFrame.position);
			trackSegment.frame(t, trackFrame);
			trackReversed = trackAxleFrame.direction.dot(trackFrame.direction) < 0;
			if(trackReversed) {
				trackFrame.direction.negate();
				trackFrame.lateral.negate();
			}
			trackFrame.orientation(globalTrackRot).premul(trackSubLevelPose.orientation());

			bogeyForceFrame.set(trackFrame);
			bogeyForceFrame.position.fma(0.5, trackFrame.vertical);
			bogeyForceFrame.transform(trackSubLevelPose).transformInverse(subLevel.logicalPose());

			Sable.HELPER.getVelocity(subLevel.getLevel(), trackAxleFrame.position, globalTrackVel);
			globalAxleVel.sub(globalTrackVel, globalRelVel);
		}
		else {
			yFixed = zFixed = false;
			yFixedTimer = zFixedTimer = 0;
			removeJoint();
			globalAxleFrame.orientation(globalTrackRot);

			clipResult = findGround(subLevel);
			JOMLConversion.toJOML(clipResult.getLocation(), clipPos);
			clipSubLevel = (ServerSubLevel)Sable.HELPER.getContaining(level, clipPos);

			bogeyForceFrame.set(axleFrame);
			bogeyForceFrame.position.fma(0.5, axleFrame.vertical);
			bogeyForceFrame.transform(bogey.pivotPose).transformInverse(subLevel.logicalPose());

			Sable.HELPER.getVelocity(level, clipPos, globalHitVel);
			globalAxleVel.sub(globalHitVel, globalRelVel);
		}

		subLevel.logicalPose().transformNormalInverse(globalRelVel, bogeyRelVel);
		speed = bogeyRelVel.dot(bogeyForceFrame.direction);
		updateGraph(true);
	}

	protected void updateGraph(boolean updatePosition) {
		TrackEdge trackEdge = null;

		if(trackSegment == null) {
			trackGraph = null;
		}
		else {
			if(trackGraph != null) {
				trackEdge = trackSegment.graphEdge(trackGraph);
				if(trackEdge == null) {
					// Graph changed
					trackGraph = null;
				}
			}
			if(trackGraph == null) {
				Pair<TrackGraph, TrackEdge> connection = trackSegment.graphConnection();
				if(connection != null) {
					trackGraph = connection.getFirst();
					trackEdge = connection.getSecond();
				}
			}
		}

		if(trackEdge == null) {
			trackPoint.node1 = null;
			trackPoint.node2 = null;
			trackPoint.edge = null;
			trackPoint.position = 0;
		}
		else {
			trackPoint.node1 = trackEdge.node1;
			trackPoint.node2 = trackEdge.node2;
			trackPoint.edge = trackEdge;
			if(updatePosition) {
				double t = Math.clamp(trackSegment.projectT(trackAxleFrame.position), 0, 1);
				if(trackSegment instanceof CurvedTrackSegment curve) {
					BezierConnection connection = curve.curve();
					double graphLength = connection.getLength();
					double quadratureLength = ((BezierConnectionExtension)connection).simurail$quadratureLength();
					double quadraturePos = SimurailMath.length(connection, 0, curve.curveT(t));
					trackPoint.position = graphLength / quadratureLength * quadraturePos;
				}
				else {
					trackPoint.position = trackSegment.length() * t;
				}
			}
			else {
				trackPoint.position = 0;
			}
		}
	}

	protected void updateJoint(ServerSubLevel subLevel) {
		if(!bogey.options.enabled || trackSegment == null) {
			removeJoint();
			return;
		}

		PhysicsBogeyAxle other = other();
		if(other.trackSegment != null) {
			globalTrackRot.nlerp(other().globalTrackRot, 0.5, globalTrackJointRot);
		}
		else {
			globalTrackJointRot.set(globalTrackRot);
		}
		trackSubLevelPose.orientation().conjugate(trackJointRot).mul(globalTrackJointRot);
		bogeyAxleFrame.orientation(bogeyTrackJointRot);

		SimurailPhysicsConfig config = SimurailConfig.server().physics;
		SubLevelPhysicsSystem physics = SubLevelContainer.getContainer(subLevel.getLevel()).physicsSystem();
		if(offsetTimer > 0) {
			removeJoint();
		}
		if(joint == null || !joint.isValid()) {
			joint = null;
			double linearDamping = config.axlePassiveLinearDamping.get();
			double angularDamping = config.axlePassiveAngularDamping.get();
			GenericConstraintConfiguration jointConfig = SimurailJoints.railJoint(
					trackFrame.position, axleFrame.position,
					trackJointRot, SimurailMath.ROT_I);
			joint = physics.getPipeline().addConstraint(trackSubLevel, bogey.pivot, jointConfig);
			joint.setContactsEnabled(false);
			joint.setMotor(ConstraintJointAxis.LINEAR_Y, 0, 0, linearDamping, false, 0);
			joint.setMotor(ConstraintJointAxis.LINEAR_Z, 0, 0, linearDamping, false, 0);
			joint.setMotor(ConstraintJointAxis.ANGULAR_Y, 0, 0, angularDamping, false, 0);
			joint.setMotor(ConstraintJointAxis.ANGULAR_Z, 0, 0, angularDamping, false, 0);
			joint.setLimit(ConstraintJointAxis.ANGULAR_Y, -ANGULAR_Y_LIMIT, ANGULAR_Y_LIMIT);
			joint.setLimit(ConstraintJointAxis.ANGULAR_Z, -ANGULAR_Z_LIMIT, ANGULAR_Z_LIMIT);
		}
		else {
			joint.setFrame1(trackFrame.position, trackJointRot);
		}
		if(bogeyJoint == null || !bogeyJoint.isValid()) {
			bogeyJoint = null;
			GenericConstraintConfiguration jointConfig = SimurailJoints.freeJoint(
					trackFrame.position, bogeyAxleFrame.position,
					trackJointRot, bogeyTrackJointRot);
			bogeyJoint = physics.getPipeline().addConstraint(trackSubLevel, subLevel, jointConfig);
		}
		else {
			bogeyJoint.setFrame1(trackFrame.position, trackJointRot);
			bogeyJoint.setFrame2(bogeyAxleFrame.position, bogeyTrackJointRot);
		}
	}

	protected void updateLimits(ServerSubLevel subLevel, double timeStep) {
		if(trackSegment != null) {
			kLateralSigned = globalTrackCurvature.dot(globalAxleFrame.lateral);
			kLateral = Math.abs(kLateralSigned);
			kVertical = globalTrackCurvature.dot(globalAxleFrame.vertical);
		}
		else {
			kLateralSigned = 0;
			kLateral = 0;
			kVertical = 0;
		}

		if(!bogey.options.enabled || trackSegment == null) {
			removeJoint();
			return;
		}

		TrackTypeEntry trackType = TrackTypeEntries.getEntry(trackSegment.material());

		double yLimit = 0;
		double zLimit = 0;

		if(!yFixed) {
			double yTime = bogey.options.allowVerticalMovement ? 20 : 10;
			double yDist = Math.abs(SimurailMath.projectTLinePoint(trackFrame.position, trackFrame.vertical, trackAxleFrame.position));
			yLimit = Math.max(Math.min(yDist - 0.5 * timeStep, yDist - yDist * yFixedTimer / yTime), 0);
			yFixedTimer += timeStep;
			yFixed = yLimit < 0.5 * timeStep || yFixedTimer > yTime;
		}

		if(!zFixed) {
			double zDist = Math.abs(SimurailMath.projectTLinePoint(trackFrame.position, trackFrame.lateral, trackAxleFrame.position));
			zLimit = Math.max(Math.min(zDist - 0.5 * timeStep, zDist - zDist * zFixedTimer * 0.1), 0);
			zFixedTimer += timeStep;
			zFixed = zLimit < 0.5 * timeStep || zFixedTimer > 10;
		}

		double t = trackSegment.projectT(trackAxleFrame.position);
		trackSubLevelPose.transformNormal(trackSegment.curvature(t, globalTrackCurvature));
		double speedSq = Mth.square(speed);
		boolean checkVertical = bogey.isInverted() || !bogey.options.allowVerticalMovement;

		if(kLateral > SimurailMath.EPSILON) {
			double lateralMaxSpeedFactor = trackType.lateralMaxSpeedFactor().getAsDouble();
			double maxSpeedSq = lateralMaxSpeedFactor / kLateral;
			if(speedSq > maxSpeedSq) {
				zLimit = Float.MAX_VALUE;
			}
		}

		if(checkVertical) {
			if(Math.abs(kVertical) > SimurailMath.EPSILON && (bogey.isInverted() ? kVertical > 0 : kVertical < 0)) {
				double verticalMaxSpeedFactor = trackType.verticalMaxSpeedFactor().getAsDouble();
				double maxSpeedSq = verticalMaxSpeedFactor / Math.abs(kVertical);
				if(speedSq > maxSpeedSq) {
					yLimit = Float.MAX_VALUE;
				}
			}
		}

		if(checkVertical) {
			joint.setLimit(ConstraintJointAxis.LINEAR_Y, -yLimit, yLimit);
			bogeyJoint.setLimit(ConstraintJointAxis.LINEAR_Y, -yLimit - 0.0625, yLimit + 0.0625);
		}
		else {
			joint.setLimit(ConstraintJointAxis.LINEAR_Y, -yLimit, Float.MAX_VALUE);
			bogeyJoint.setLimit(ConstraintJointAxis.LINEAR_Y, -yLimit - 0.0625, Float.MAX_VALUE);
		}

		joint.setLimit(ConstraintJointAxis.LINEAR_Z, -zLimit, zLimit);
		bogeyJoint.setLimit(ConstraintJointAxis.LINEAR_Z, -zLimit - 0.125, zLimit + 0.125);
	}

	protected void updateForces(ServerSubLevel subLevel, double timeStep) {
		if(!bogey.options.enabled) {
			removeJoint();
			return;
		}
		if(trackSegment != null) {
			removeAxleBox(subLevel);
			updateTrackForces(subLevel, timeStep);
		}
		else {
			updateWorldForces(subLevel, timeStep);
		}
	}

	protected void updateTrackForces(ServerSubLevel subLevel, double timeStep) {
		if(!bogey.options.enabled || trackSegment == null) {
			removeJoint();
			return;
		}

		SimurailPhysicsConfig config = SimurailConfig.server().physics;
		MassData massData = subLevel.getMassTracker();

		{
			double normalMass = 1 / massData.getInverseNormalMass(bogeyForceFrame.position, bogeyForceFrame.vertical);
			double friction = getTrackFriction(trackSegment);

			double brakeStrengthFactor = config.axleBrakeStrengthFactor.get();
			double brakeStrength = bogey.getBrakeStrength();

			double targetSpeedFactor = config.axleTargetSpeedFactor.get();
			targetSpeed = bogey.getSpeed() * targetSpeedFactor * bogey.getFacing().getAxisDirection().getStep();
			double targetSign = Math.signum(targetSpeed);
			double diffSpeed = targetSpeed - speed;
			double diffSign = Math.signum(diffSpeed);
			double driveForce = 0;

			if(targetSign == diffSign) {
				double stress = bogey.calculateStressApplied();
				double driveForceMultiplier = config.axleDriveForceFactor.get();
				double driveMag = Math.min(Math.abs(diffSpeed), Math.abs(targetSpeed)) * stress * driveForceMultiplier;
				driveForce = diffSign * driveMag * (1 - brakeStrength) * Math.clamp(friction, 0.05, 1);
			}

			double speedSign = Math.signum(speed);
			double speedSignMag = Math.clamp(Math.abs(speed), 0, 1);
			double signFactor = (speedSignMag * speedSignMag * speedSignMag * 0.25 + speedSignMag * 0.75) * speedSign;
			double brakeForce = signFactor * (brakeStrengthFactor * brakeStrength) * normalMass * Math.max(friction, 0.05);

			bogeyForceFrame.direction.mul(driveForce * timeStep, queuedTractionForce);
			bogeyForceFrame.direction.mul(-brakeForce * timeStep, queuedBrakeForce);

			if(friction < 1) {
				visualSpeed = Mth.lerp(friction * 0.9 + 0.1, targetSpeed, speed);
			}
			else {
				visualSpeed = speed;
			}
		}

		QueuedForceGroup tractionGroup = subLevel.getOrCreateQueuedForceGroup(SimurailForceGroups.TRACTION.get());
		tractionGroup.applyAndRecordPointForce(bogeyForceFrame.position, queuedTractionForce);
		QueuedForceGroup brakeGroup = subLevel.getOrCreateQueuedForceGroup(SimurailForceGroups.BRAKE.get());
		brakeGroup.applyAndRecordPointForce(bogeyForceFrame.position, queuedBrakeForce);

		if(trackSubLevel != null) {
			queuedReactionForce.set(queuedTractionForce).add(queuedBrakeForce);
			subLevel.logicalPose().transformNormal(queuedReactionForce);
			trackSubLevelPose.transformNormalInverse(queuedReactionForce);
			queuedReactionForce.negate();
			RigidBodyHandle.of(trackSubLevel).applyImpulseAtPoint(trackFrame.position, queuedReactionForce);
		}
	}

	protected double getTrackFriction(TrackSegment trackSegment) {
		return 1;
	}

	protected void updateWorldForces(ServerSubLevel subLevel, double timeStep) {
		if(!bogey.options.enabled || trackSegment != null || !bogey.options.type.groundDrivable()) {
			return;
		}
		// TODO check if we want to move wheels out of ground
		//createAxleBox(subLevel);

		ServerLevel level = subLevel.getLevel();

		SimurailPhysicsConfig config = SimurailConfig.server().physics;
		double targetSpeedFactor = config.axleTargetSpeedFactor.get();
		targetSpeed = bogey.getSpeed() * targetSpeedFactor * bogey.getFacing().getAxisDirection().getStep();

		if(clipResult.getType() == HitResult.Type.MISS) {
			if(targetSpeed != 0) {
				visualSpeed = targetSpeed;
			}
			return;
		}

		Pose3dc clipSubLevelPose = clipSubLevel == null ? SimurailMath.POSE_I : clipSubLevel.logicalPose();
		MassData massData = subLevel.getMassTracker();

		{
			double normalMass = 1 / massData.getInverseNormalMass(bogeyForceFrame.position, bogeyForceFrame.vertical);
			double frictionFactor = config.axleDerailFrictionFactor.get();
			double friction = PhysicsBlockPropertyHelper.getFriction(level.getBlockState(clipResult.getBlockPos())) * frictionFactor;

			double brakeStrengthFactor = config.axleBrakeStrengthFactor.get();
			double brakeStrength = bogey.getBrakeStrength();

			double targetSign = Math.signum(targetSpeed);
			double diffSpeed = targetSpeed - speed;
			double diffSign = Math.signum(diffSpeed);
			double driveForce = 0;

			if(targetSign == diffSign) {
				double stress = bogey.calculateStressApplied();
				double driveForceMultiplier = config.axleDriveForceFactor.get();
				double driveMag = Math.min(Math.abs(diffSpeed), Math.abs(targetSpeed)) * stress * driveForceMultiplier;
				driveForce = diffSign * driveMag * (1 - brakeStrength) * Math.clamp(friction, 0.05, 1);
			}

			double speedSign = Math.signum(speed);
			double speedSignMag = Math.clamp(Math.abs(speed), 0, 1);
			double signFactor = (speedSignMag * speedSignMag * speedSignMag * 0.25 + speedSignMag * 0.75) * speedSign;
			double brakeForce = signFactor * (brakeStrengthFactor * brakeStrength) * normalMass * Math.max(friction, 0.05);

			bogeyForceFrame.direction.mul(driveForce * timeStep, queuedTractionForce);
			bogeyForceFrame.direction.mul(-brakeForce * timeStep, queuedBrakeForce);

			if(friction < 1) {
				visualSpeed = Mth.lerp(friction * 0.9 + 0.1, targetSpeed, speed);
			}
			else {
				visualSpeed = speed;
			}
		}

		QueuedForceGroup tractionGroup = subLevel.getOrCreateQueuedForceGroup(SimurailForceGroups.TRACTION.get());
		tractionGroup.applyAndRecordPointForce(bogeyForceFrame.position, queuedTractionForce);
		QueuedForceGroup brakeGroup = subLevel.getOrCreateQueuedForceGroup(SimurailForceGroups.BRAKE.get());
		brakeGroup.applyAndRecordPointForce(bogeyForceFrame.position, queuedBrakeForce);

		if(clipSubLevel != null) {
			queuedReactionForce.set(queuedTractionForce).add(queuedBrakeForce);
			subLevel.logicalPose().transformNormal(queuedReactionForce);
			clipSubLevelPose.transformNormalInverse(queuedReactionForce);
			queuedReactionForce.negate();
			RigidBodyHandle.of(clipSubLevel).applyImpulseAtPoint(clipPos, queuedReactionForce);
		}
	}

	protected void findTrack(ServerSubLevel subLevel) {
		Pose3dc pivotPose = bogey.pivotPose;
		axleFrame.transform(pivotPose, globalAxleFrame);
		trackSegment = TrackSegmentHelper.findTrackSegment(subLevel.getLevel(), subLevel, globalAxleFrame.position, globalAxleFrame.direction, globalAxleFrame.vertical, bogey.isInverted(), bogey.options.type.trackTypes());
		if(trackSegment != null) {
			trackRecheckTime = SimurailConfig.server().physics.axleTrackRecheckTime.get();
		}
		else {
			trackRecheckTime = SimurailConfig.server().physics.axleTrackCheckTime.get();
		}
		bogey.setChanged();
	}

	protected BlockHitResult findGround(ServerSubLevel subLevel) {
		ServerLevel level = subLevel.getLevel();
		Vec3 clipStart = bogey.pivotPose.transformPosition(new Vec3(axleFrame.position.x, axleFrame.position.y + 0.5, axleFrame.position.z));
		Vec3 clipEnd = JOMLConversion.toMojang(globalAxleFrame.position);
		ClipContext clipContext = new ClipContext(clipStart, clipEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());
		((ClipContextExtension)clipContext).sable$setIgnoredSubLevel(subLevel);
		return level.clip(clipContext);
	}

	protected void resetProbe(TravellingPoint point) {
		probe.node1 = point.node1;
		probe.node2 = point.node2;
		probe.edge = point.edge;
		probe.position = point.position;
	}

	protected void removeJoint() {
		if(joint != null) {
			joint.remove();
			joint = null;
		}
		if(bogeyJoint != null) {
			bogeyJoint.remove();
			bogeyJoint = null;
		}
	}

	protected void createAxleBox(ServerSubLevel subLevel) {
		SubLevelPhysicsSystem physics = SubLevelContainer.getContainer(subLevel.getLevel()).physicsSystem();
		Quaterniond rot = SimurailMath.rot(new Vector3d(1, 1, 0), new Vector3d(-1, 1, 0), new Quaterniond());
		if(axleBox == null || axleBox.isRemoved()) {
			Pose3d axleBoxPose = new Pose3d();
			axleBoxPose.position().set(globalAxleFrame.position);
			axleBoxPose.orientation().set(bogey.pivotPose.orientation()).mul(rot);
			axleBox = new AttachableBoxPhysicsObject(subLevel, axleBoxPose, new Vector3d(Math.sqrt(0.5) * 0.0625, Math.sqrt(0.5) * 0.0625, 0.5), 0);
			physics.addObject(axleBox);
		}
		if(axleBoxJoint == null || !axleBoxJoint.isValid()) {
			FixedConstraintConfiguration jointConfig = new FixedConstraintConfiguration(axleFrame.position, new Vector3d(0, -0.3125, 0), rot);
			axleBoxJoint = physics.getPipeline().addConstraint(bogey.pivot, axleBox, jointConfig);
			axleBoxJoint.setContactsEnabled(false);
		}
	}

	protected void removeAxleBox(ServerSubLevel subLevel) {
		if(axleBoxJoint != null) {
			axleBoxJoint.remove();
			axleBoxJoint = null;
		}
		if(axleBox != null) {
			SubLevelPhysicsSystem physics = SubLevelContainer.getContainer(subLevel.getLevel()).physicsSystem();
			physics.removeObject(axleBox);
			axleBox = null;
		}
	}

	protected void invalidate(ServerSubLevel subLevel) {
		trackSegment = null;
		trackGraph = null;
		removeJoint();
		removeAxleBox(subLevel);
	}

	protected ITrackSelector followOrSteer(TravellingPoint point) {
		return (graph, pair) -> {
			PhysicsBogeyAxle followingAxle = null;
			TravellingPoint nextPoint = null;
			if(isCurrentFront()) {
				PhysicsBogeyBlockEntity nextBogey = bogey.getConnected(logicalFront);
				if(nextBogey != null) {
					followingAxle = nextBogey.getAxle(!bogey.getConnectedToFront(logicalFront));
				}
			}
			else {
				followingAxle = other();
			}
			if(followingAxle != null && followingAxle.trackGraph == graph) {
				nextPoint = followingAxle.trackPoint;
			}
			if(nextPoint == null || nextPoint.edge == null) {
				return steer(point).apply(graph, pair);
			}
			else {
				MutableBoolean flag = new MutableBoolean(false);
				Map.Entry<TrackNode, TrackEdge> result = follow(nextPoint, flag::setValue).apply(graph, pair);
				if(flag.booleanValue()) {
					return result;
				}
				else {
					return steer(point).apply(graph, pair);
				}
			}
		};
	}


	protected ITrackSelector followOtherOrSteer(TravellingPoint point) {
		return (graph, pair) -> {
			PhysicsBogeyAxle followingAxle = other();
			TravellingPoint nextPoint = null;
			if(followingAxle != null && followingAxle.trackGraph == graph) {
				nextPoint = followingAxle.trackPoint;
			}
			if(nextPoint == null || nextPoint.edge == null) {
				return steer(point).apply(graph, pair);
			}
			else {
				MutableBoolean flag = new MutableBoolean(false);
				Map.Entry<TrackNode, TrackEdge> result = follow(nextPoint, flag::setValue).apply(graph, pair);
				if(flag.booleanValue()) {
					return result;
				}
				else {
					return steer(point).apply(graph, pair);
				}
			}
		};
	}

	protected ITrackSelector steer(TravellingPoint point) {
		return (graph, pair) -> {
			List<Map.Entry<TrackNode, TrackEdge>> validTargets = pair.getSecond();
			double closest = -Double.MAX_VALUE;
			Map.Entry<TrackNode, TrackEdge> best = null;

			boolean forward = pair.getFirst();
			Vector3d trackDir = JOMLConversion.toJOML(point.edge.getDirection(!forward));
			double steerValue = bogey.getGroupSteerValue();
			Vector3d trackLat = trackAxleFrame.lateral;

			Vector3d steerTarget = new Vector3d();
			steerTarget.fma(forward ? 1 : -1, trackDir);
			steerTarget.fma(steerValue, trackLat);

			Vector3d checkDir = new Vector3d();

			for(Map.Entry<TrackNode, TrackEdge> entry : validTargets) {
				TrackEdge edge = entry.getValue();
				Vec3 p1 = edge.getPosition(null, 0);
				Vec3 p2 = edge.getPosition(null, 1);
				checkDir.set(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z).normalize();
				double dot = steerTarget.dot(checkDir);
				if(dot > closest) {
					closest = dot;
					best = entry;
				}
			}
			if(best == null) {
				return validTargets.get(0);
			}
			return best;
		};
	}

	protected ITrackSelector follow(TravellingPoint other, BooleanConsumer success) {
		return (graph, pair) -> {
			List<Map.Entry<TrackNode, TrackEdge>> validTargets = pair.getSecond();
			for(Map.Entry<TrackNode, TrackEdge> entry : validTargets) {
				if(entry.getKey() == other.node1 || entry.getKey() == other.node2) {
					if(success != null) {
						success.accept(true);
					}
					return entry;
				}
			}

			List<List<Map.Entry<TrackNode, TrackEdge>>> frontiers = new ArrayList<>(validTargets.size());
			List<Set<TrackEdge>> visiteds = new ArrayList<>(validTargets.size());

			for(Map.Entry<TrackNode, TrackEdge> validTarget : validTargets) {
				List<Map.Entry<TrackNode, TrackEdge>> e = new ArrayList<>();
				e.add(validTarget);
				frontiers.add(e);
				Set<TrackEdge> e2 = new HashSet<>();
				e2.add(validTarget.getValue());
				visiteds.add(e2);
			}

			for(int i = 0; i < 20; i++) {
				for(int j = 0; j < validTargets.size(); j++) {
					Map.Entry<TrackNode, TrackEdge> entry = validTargets.get(j);
					List<Map.Entry<TrackNode, TrackEdge>> frontier = frontiers.get(j);
					if(frontier.isEmpty()) {
						continue;
					}
					Map.Entry<TrackNode, TrackEdge> currentEntry = frontier.remove(0);
					for(Map.Entry<TrackNode, TrackEdge> nextEntry : graph.getConnectionsFrom(currentEntry.getKey()).entrySet()) {
						TrackEdge nextEdge = nextEntry.getValue();
						if(!visiteds.get(j).add(nextEdge)) {
							continue;
						}
						if(!currentEntry.getValue().canTravelTo(nextEdge)) {
							continue;
						}
						TrackNode nextNode = nextEntry.getKey();
						if(nextNode == other.node1 || nextNode == other.node2) {
							if(success != null) {
								success.accept(true);
							}
							return entry;
						}
						frontier.add(nextEntry);
					}
				}
			}

			if(success != null) {
				success.accept(false);
			}

			return validTargets.get(0);
		};
	}

	protected boolean isCurrentFront() {
		return logicalFront && speed > 0 || !logicalFront && speed < 0;
	}

	protected PhysicsBogeyAxle other() {
		return this == bogey.axleFront ? bogey.axleBack : bogey.axleFront;
	}

	public boolean hasTrack() {
		return trackSegment != null;
	}

	protected CompoundTag write() {
		CompoundTag tag = new CompoundTag();
		if(trackSegment != null) {
			tag.put("segment", trackSegment.write());
		}
		tag.putBoolean("y_fixed", yFixed);
		tag.putBoolean("z_fixed", zFixed);
		return tag;
	}

	protected void read(CompoundTag tag) {
		if(tag.contains("segment")) {
			trackSegment = TrackSegment.read(tag.getCompound("segment"));
		}
		yFixed = tag.getBoolean("y_fixed");
		zFixed = tag.getBoolean("z_fixed");
	}

	// Mutable physics fields
	protected final Frame3d globalAxleFrame = new Frame3d();
	protected final Frame3d bogeyAxleFrame = new Frame3d();
	protected final Frame3d trackAxleFrame = new Frame3d();

	protected final Frame3d trackFrame = new Frame3d();
	protected final Frame3d bogeyForceFrame = new Frame3d();
	protected final Quaterniond globalTrackRot = new Quaterniond();

	protected final Vector3d clipPos = new Vector3d();

	protected final Quaterniond globalTrackJointRot = new Quaterniond();
	protected final Quaterniond trackJointRot = new Quaterniond();
	protected final Quaterniond bogeyTrackJointRot = new Quaterniond();

	protected final Vector3d globalTrackCurvature = new Vector3d();

	protected final Vector3d queuedBrakeForce = new Vector3d();
	protected final Vector3d queuedTractionForce = new Vector3d();
	protected final Vector3d queuedReactionForce = new Vector3d();

	protected final Vector3d globalAxleVel = new Vector3d();
	protected final Vector3d globalTrackVel = new Vector3d();
	protected final Vector3d globalHitVel = new Vector3d();
	protected final Vector3d globalRelVel = new Vector3d();
	protected final Vector3d bogeyRelVel = new Vector3d();
}
