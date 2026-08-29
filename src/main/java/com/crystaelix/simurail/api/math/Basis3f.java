package com.crystaelix.simurail.api.math;

import org.joml.Matrix3f;
import org.joml.Quaterniondc;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import dev.ryanhcode.sable.companion.math.Pose3dc;

public class Basis3f implements Basis3fc {

	public final Vector3f direction;
	public final Vector3f vertical;
	public final Vector3f lateral;

	public Basis3f() {
		this.direction = new Vector3f(1, 0, 0);
		this.vertical = new Vector3f(0, 1, 0);
		this.lateral = new Vector3f(0, 0, 1);
	}

	public Basis3f(Vector3fc direction, Vector3fc vertical, Vector3fc lateral) {
		this.direction = new Vector3f(direction);
		this.vertical = new Vector3f(vertical);
		this.lateral = new Vector3f(lateral);
	}

	@Override
	public Vector3f direction() {
		return direction;
	}

	@Override
	public Vector3f vertical() {
		return vertical;
	}

	@Override
	public Vector3f lateral() {
		return lateral;
	}

	public Basis3f set(Basis3fc basis) {
		direction.set(basis.direction());
		vertical.set(basis.vertical());
		lateral.set(basis.lateral());
		return this;
	}

	public Basis3f set(Basis3dc basis) {
		direction.set(basis.direction());
		vertical.set(basis.vertical());
		lateral.set(basis.lateral());
		return this;
	}

	public Basis3f set(Frame3dc frame) {
		direction.set(frame.direction());
		vertical.set(frame.vertical());
		lateral.set(frame.lateral());
		return this;
	}

	public Basis3f set(Vector3fc direction, Vector3fc vertical, Vector3fc lateral) {
		this.direction.set(direction);
		this.vertical.set(vertical);
		this.lateral.set(lateral);
		return this;
	}

	public Basis3f direction(Vector3fc direction) {
		this.direction.set(direction);
		return this;
	}

	public Basis3f vertical(Vector3fc vertical) {
		this.vertical.set(vertical);
		return this;
	}

	public Basis3f lateral(Vector3fc lateral) {
		this.lateral.set(lateral);
		return this;
	}

	public Basis3f orthogonalized(Vector3fc direction, Vector3fc vertical) {
		this.direction.set(direction).normalize();
		direction.cross(vertical, lateral).normalize();
		lateral.cross(this.direction, this.vertical);
		return this;
	}

	@Override
	public Basis3f orthogonalize(Basis3f dest) {
		direction.normalize(dest.direction);
		direction.cross(vertical, dest.lateral).normalize();
		dest.lateral.cross(dest.direction, dest.vertical);
		return dest;
	}

	public Basis3f orthogonalize() {
		direction.normalize();
		direction.cross(vertical, lateral).normalize();
		lateral.cross(direction, vertical);
		return this;
	}

	@Override
	public Basis3f normalize(Basis3f dest) {
		direction.normalize(dest.direction);
		vertical.normalize(dest.vertical);
		lateral.normalize(dest.lateral);
		return dest;
	}

	public Basis3f normalize() {
		direction.normalize();
		vertical.normalize();
		lateral.normalize();
		return this;
	}

	@Override
	public Basis3f transform(Quaternionfc quat, Basis3f dest) {
		quat.transform(direction, dest.direction);
		quat.transform(vertical, dest.vertical);
		quat.transform(lateral, dest.lateral);
		return dest;
	}

	public Basis3f transform(Quaternionfc quat) {
		quat.transform(direction);
		quat.transform(vertical);
		quat.transform(lateral);
		return this;
	}

	@Override
	public Basis3f transformInverse(Quaternionfc quat, Basis3f dest) {
		quat.transformInverse(direction, dest.direction);
		quat.transformInverse(vertical, dest.vertical);
		quat.transformInverse(lateral, dest.lateral);
		return dest;
	}

	public Basis3f transformInverse(Quaternionfc quat) {
		quat.transformInverse(direction);
		quat.transformInverse(vertical);
		quat.transformInverse(lateral);
		return this;
	}

	@Override
	public Basis3f transform(Pose3dc pose, Basis3f dest) {
		return transform(pose.orientation(), dest);
	}

	public Basis3f transform(Pose3dc pose) {
		return transform(pose.orientation());
	}

	@Override
	public Basis3f transformInverse(Pose3dc pose, Basis3f dest) {
		return transformInverse(pose.orientation(), dest);
	}

	public Basis3f transformInverse(Pose3dc pose) {
		return transformInverse(pose.orientation());
	}

	@Override
	public Basis3f transform(Quaterniondc quat, Basis3f dest) {
		quat.transform(direction, dest.direction);
		quat.transform(vertical, dest.vertical);
		quat.transform(lateral, dest.lateral);
		return dest;
	}

	public Basis3f transform(Quaterniondc quat) {
		quat.transform(direction);
		quat.transform(vertical);
		quat.transform(lateral);
		return this;
	}

	@Override
	public Basis3f transformInverse(Quaterniondc quat, Basis3f dest) {
		quat.transformInverse(direction, dest.direction);
		quat.transformInverse(vertical, dest.vertical);
		quat.transformInverse(lateral, dest.lateral);
		return dest;
	}

	public Basis3f transformInverse(Quaterniondc quat) {
		quat.transformInverse(direction);
		quat.transformInverse(vertical);
		quat.transformInverse(lateral);
		return this;
	}

	@Override
	public Matrix3f matrix(Matrix3f dest) {
		return dest.set(direction, vertical, lateral);
	}
}
