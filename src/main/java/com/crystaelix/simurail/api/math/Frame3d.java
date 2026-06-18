package com.crystaelix.simurail.api.math;

import org.joml.Matrix3d;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import dev.ryanhcode.sable.companion.math.Pose3dc;

public class Frame3d implements Frame3dc {

	public final Vector3d direction;
	public final Vector3d vertical;
	public final Vector3d lateral;
	public final Vector3d position;

	public Frame3d() {
		this.direction = new Vector3d(1, 0, 0);
		this.vertical = new Vector3d(0, 1, 0);
		this.lateral = new Vector3d(0, 0, 1);
		this.position = new Vector3d();
	}

	public Frame3d(Vector3dc direction, Vector3dc vertical, Vector3dc lateral, Vector3dc position) {
		this.direction = new Vector3d(direction);
		this.vertical = new Vector3d(vertical);
		this.lateral = new Vector3d(lateral);
		this.position = new Vector3d(position);
	}

	public Frame3d(Basis3dc frame, Vector3dc position) {
		this.direction = new Vector3d(frame.direction());
		this.vertical = new Vector3d(frame.vertical());
		this.lateral = new Vector3d(frame.lateral());
		this.position = new Vector3d(position);
	}

	@Override
	public Vector3d direction() {
		return direction;
	}

	@Override
	public Vector3d vertical() {
		return vertical;
	}

	@Override
	public Vector3d lateral() {
		return lateral;
	}

	@Override
	public Vector3d position() {
		return position;
	}

	public Frame3d set(Frame3dc frame) {
		direction.set(frame.direction());
		vertical.set(frame.vertical());
		lateral.set(frame.lateral());
		position.set(frame.position());
		return this;
	}

	public Frame3d set(Basis3dc basis, Vector3dc position) {
		direction.set(basis.direction());
		vertical.set(basis.vertical());
		lateral.set(basis.lateral());
		this.position.set(position);
		return this;
	}

	public Frame3d set(Vector3dc direction, Vector3dc vertical, Vector3dc lateral, Vector3dc position) {
		this.direction.set(direction);
		this.vertical.set(vertical);
		this.lateral.set(lateral);
		this.position.set(position);
		return this;
	}

	public Frame3d direction(Vector3dc direction) {
		this.direction.set(direction);
		return this;
	}

	public Frame3d vertical(Vector3dc vertical) {
		this.vertical.set(vertical);
		return this;
	}

	public Frame3d lateral(Vector3dc lateral) {
		this.lateral.set(lateral);
		return this;
	}

	public Frame3d position(Vector3dc position) {
		this.position.set(position);
		return this;
	}

	public Frame3d orthogonalized(Vector3dc direction, Vector3dc vertical, Vector3dc position) {
		this.direction.set(direction).normalize();
		direction.cross(vertical, lateral).normalize();
		lateral.cross(this.direction, this.vertical);
		this.position.set(position);
		return this;
	}

	@Override
	public Frame3d basis(Basis3dc basis, Frame3d dest) {
		dest.direction.set(basis.direction());
		dest.vertical.set(basis.vertical());
		dest.lateral.set(basis.lateral());
		dest.position.set(position);
		return dest;
	}

	public Frame3d basis(Basis3dc basis) {
		direction.set(basis.direction());
		vertical.set(basis.vertical());
		lateral.set(basis.lateral());
		return this;
	}

	@Override
	public Frame3d orthogonalize(Frame3d dest) {
		direction.normalize(dest.direction);
		direction.cross(vertical, dest.lateral).normalize();
		dest.lateral.cross(dest.direction, dest.vertical);
		dest.position.set(position);
		return dest;
	}

	public Frame3d orthogonalize() {
		direction.normalize();
		direction.cross(vertical, lateral).normalize();
		lateral.cross(direction, vertical);
		return this;
	}

	@Override
	public Frame3d normalize(Frame3d dest) {
		direction.normalize(dest.direction);
		vertical.normalize(dest.vertical);
		lateral.normalize(dest.lateral);
		dest.position.set(position);
		return dest;
	}

	public Frame3d normalize() {
		direction.normalize();
		vertical.normalize();
		lateral.normalize();
		return this;
	}

	@Override
	public Frame3d transform(Pose3dc pose, Frame3d dest) {
		pose.transformNormal(direction, dest.direction);
		pose.transformNormal(vertical, dest.vertical);
		pose.transformNormal(lateral, dest.lateral);
		pose.transformPosition(position, dest.position);
		return dest;
	}

	public Frame3d transform(Pose3dc pose) {
		pose.transformNormal(direction);
		pose.transformNormal(vertical);
		pose.transformNormal(lateral);
		pose.transformPosition(position);
		return this;
	}

	@Override
	public Frame3d transformInverse(Pose3dc pose, Frame3d dest) {
		pose.transformNormalInverse(direction, dest.direction);
		pose.transformNormalInverse(vertical, dest.vertical);
		pose.transformNormalInverse(lateral, dest.lateral);
		pose.transformPositionInverse(position, dest.position);
		return this;
	}

	public Frame3d transformInverse(Pose3dc pose) {
		pose.transformNormalInverse(direction);
		pose.transformNormalInverse(vertical);
		pose.transformNormalInverse(lateral);
		pose.transformPositionInverse(position);
		return this;
	}

	@Override
	public Matrix3d matrix(Matrix3d dest) {
		return dest.set(direction, vertical, lateral);
	}

	private final Matrix3d tempMatrix = new Matrix3d();

	@Override
	public Quaterniond orientation(Quaterniond dest) {
		synchronized(tempMatrix) {
			return dest.setFromUnnormalized(matrix(tempMatrix));
		}
	}
}
