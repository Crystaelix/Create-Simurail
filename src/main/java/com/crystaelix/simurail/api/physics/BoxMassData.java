package com.crystaelix.simurail.api.physics;

import org.joml.Matrix3d;
import org.joml.Matrix3dc;
import org.joml.Vector3dc;

import com.crystaelix.simurail.api.math.SimurailMath;

import dev.ryanhcode.sable.api.physics.mass.MassData;

public class BoxMassData implements MassData {

	private final double mass;
	private final Matrix3dc inertia;
	private final Matrix3dc inverseInertia;

	public BoxMassData(Vector3dc halfExtents, double mass) {
		this.mass = mass;
		double x = halfExtents.x();
		double y = halfExtents.y();
		double z = halfExtents.z();
		inertia = new Matrix3d().scale(y * y + z * z, x * x + z * z, x * x + y * y).scale(mass / 3);
		inverseInertia = inertia.invert(new Matrix3d());
	}

	@Override
	public double getMass() {
		return mass;
	}

	@Override
	public double getInverseMass() {
		return 1 / mass;
	}

	@Override
	public Matrix3dc getInertiaTensor() {
		return inertia;
	}

	@Override
	public Matrix3dc getInverseInertiaTensor() {
		return inverseInertia;
	}

	@Override
	public Vector3dc getCenterOfMass() {
		return SimurailMath.VEC_0;
	}
}
