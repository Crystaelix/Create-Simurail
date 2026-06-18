package com.crystaelix.simurail.api.physics;

import java.util.EnumSet;
import java.util.Set;

import org.joml.Quaterniondc;
import org.joml.Vector3dc;

import dev.ryanhcode.sable.api.physics.constraint.ConstraintJointAxis;
import dev.ryanhcode.sable.api.physics.constraint.GenericConstraintConfiguration;

public class SimurailJoints {

	public static final Set<ConstraintJointAxis> LOCKED_PIVOT_JOINT_AXES = EnumSet.of(ConstraintJointAxis.LINEAR_X);
	public static final Set<ConstraintJointAxis> LOCKED_RAIL_JOINT_AXES = EnumSet.of(ConstraintJointAxis.ANGULAR_X);
	public static final Set<ConstraintJointAxis> LOCKED_COUPLER_JOINT_AXES = Set.of();

	public static GenericConstraintConfiguration pivotJoint(Vector3dc pos1, Vector3dc pos2, Quaterniondc orientation1, Quaterniondc orientation2) {
		return new GenericConstraintConfiguration(pos1, pos2, orientation1, orientation2, LOCKED_PIVOT_JOINT_AXES);
	}

	public static GenericConstraintConfiguration railJoint(Vector3dc pos1, Vector3dc pos2, Quaterniondc orientation1, Quaterniondc orientation2) {
		return new GenericConstraintConfiguration(pos1, pos2, orientation1, orientation2, LOCKED_RAIL_JOINT_AXES);
	}

	public static GenericConstraintConfiguration couplerJoint(Vector3dc pos1, Vector3dc pos2, Quaterniondc orientation1, Quaterniondc orientation2) {
		return new GenericConstraintConfiguration(pos1, pos2, orientation1, orientation2, LOCKED_COUPLER_JOINT_AXES);
	}
}
