package com.crystaelix.simurail.api.math;

import org.joml.Matrix3d;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class Basis3d implements Basis3dc {

	public final Vector3d direction;
	public final Vector3d vertical;
	public final Vector3d lateral;

	public Basis3d() {
		this.direction = new Vector3d(1, 0, 0);
		this.vertical = new Vector3d(0, 1, 0);
		this.lateral = new Vector3d(0, 0, 1);
	}

	public Basis3d(Vector3dc direction, Vector3dc vertical, Vector3dc lateral) {
		this.direction = new Vector3d(direction);
		this.vertical = new Vector3d(vertical);
		this.lateral = new Vector3d(lateral);
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

	public Basis3d set(Basis3dc basis) {
		direction.set(basis.direction());
		vertical.set(basis.vertical());
		lateral.set(basis.lateral());
		return this;
	}

	public Basis3d set(Frame3dc frame) {
		direction.set(frame.direction());
		vertical.set(frame.vertical());
		lateral.set(frame.lateral());
		return this;
	}

	public Basis3d set(Vector3dc direction, Vector3dc vertical, Vector3dc lateral) {
		this.direction.set(direction);
		this.vertical.set(vertical);
		this.lateral.set(lateral);
		return this;
	}

	public Basis3d direction(Vector3dc direction) {
		this.direction.set(direction);
		return this;
	}

	public Basis3d vertical(Vector3dc vertical) {
		this.vertical.set(vertical);
		return this;
	}

	public Basis3d lateral(Vector3dc lateral) {
		this.lateral.set(lateral);
		return this;
	}

	public Basis3d orthogonalized(Vector3dc direction, Vector3dc vertical) {
		this.direction.set(direction).normalize();
		direction.cross(vertical, lateral).normalize();
		lateral.cross(this.direction, this.vertical);
		return this;
	}

	@Override
	public Basis3d orthogonalize(Basis3d dest) {
		direction.normalize(dest.direction);
		direction.cross(vertical, dest.lateral).normalize();
		dest.lateral.cross(dest.direction, dest.vertical);
		return dest;
	}

	public Basis3d orthogonalize() {
		direction.normalize();
		direction.cross(vertical, lateral).normalize();
		lateral.cross(direction, vertical);
		return this;
	}

	@Override
	public Basis3d normalize(Basis3d dest) {
		direction.normalize(dest.direction);
		vertical.normalize(dest.vertical);
		lateral.normalize(dest.lateral);
		return dest;
	}

	public Basis3d normalize() {
		direction.normalize();
		vertical.normalize();
		lateral.normalize();
		return this;
	}

	@Override
	public Basis3d transform(Quaterniondc quat, Basis3d dest) {
		quat.transform(direction, dest.direction);
		quat.transform(vertical, dest.vertical);
		quat.transform(lateral, dest.lateral);
		return dest;
	}

	public Basis3d transform(Quaterniondc quat) {
		quat.transform(direction);
		quat.transform(vertical);
		quat.transform(lateral);
		return this;
	}

	@Override
	public Basis3d transformInverse(Quaterniondc quat, Basis3d dest) {
		quat.transformInverse(direction, dest.direction);
		quat.transformInverse(vertical, dest.vertical);
		quat.transformInverse(lateral, dest.lateral);
		return this;
	}

	public Basis3d transformInverse(Quaterniondc quat) {
		quat.transformInverse(direction);
		quat.transformInverse(vertical);
		quat.transformInverse(lateral);
		return this;
	}

	@Override
	public Matrix3d matrix(Matrix3d dest) {
		return dest.set(direction, vertical, lateral);
	}
}
