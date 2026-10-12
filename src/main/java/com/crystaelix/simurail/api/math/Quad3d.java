package com.crystaelix.simurail.api.math;

import java.util.Objects;

import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import dev.ryanhcode.sable.companion.math.Pose3dc;

public class Quad3d implements Quad3dc {

	public final Vector3d v0;
	public final Vector3d v1;
	public final Vector3d v2;
	public final Vector3d v3;

	public Quad3d() {
		this.v0 = new Vector3d();
		this.v1 = new Vector3d();
		this.v2 = new Vector3d();
		this.v3 = new Vector3d();
	}

	public Quad3d(Vector3dc v0, Vector3dc v1, Vector3dc v2, Vector3dc v3) {
		this.v0 = new Vector3d(v0);
		this.v1 = new Vector3d(v1);
		this.v2 = new Vector3d(v2);
		this.v3 = new Vector3d(v3);
	}

	@Override
	public Vector3d v0() {
		return v0;
	}

	@Override
	public Vector3d v1() {
		return v1;
	}

	@Override
	public Vector3d v2() {
		return v2;
	}

	@Override
	public Vector3d v3() {
		return v3;
	}

	public Quad3d set(Quad3dc quad) {
		v0.set(quad.v0());
		v1.set(quad.v1());
		v2.set(quad.v2());
		v3.set(quad.v3());
		return this;
	}

	public Quad3d v0(Vector3dc v0) {
		this.v0.set(v0);
		return this;
	}

	public Quad3d v1(Vector3dc v1) {
		this.v1.set(v1);
		return this;
	}

	public Quad3d v2(Vector3dc v2) {
		this.v2.set(v2);
		return this;
	}

	public Quad3d v3(Vector3dc v3) {
		this.v3.set(v3);
		return this;
	}

	@Override
	public Quad3d transformPosition(Pose3dc pose, Quad3d dest) {
		pose.transformPosition(v0, dest.v0);
		pose.transformPosition(v1, dest.v1);
		pose.transformPosition(v2, dest.v2);
		pose.transformPosition(v3, dest.v3);
		return dest;
	}

	public Quad3d transformPosition(Pose3dc pose) {
		pose.transformPosition(v0);
		pose.transformPosition(v1);
		pose.transformPosition(v2);
		pose.transformPosition(v3);
		return this;
	}

	@Override
	public Quad3d transformPositionInverse(Pose3dc pose, Quad3d dest) {
		pose.transformPositionInverse(v0, dest.v0);
		pose.transformPositionInverse(v1, dest.v1);
		pose.transformPositionInverse(v2, dest.v2);
		pose.transformPositionInverse(v3, dest.v3);
		return dest;
	}

	public Quad3d transformPositionInverse(Pose3dc pose) {
		pose.transformPositionInverse(v0);
		pose.transformPositionInverse(v1);
		pose.transformPositionInverse(v2);
		pose.transformPositionInverse(v3);
		return this;
	}

	@Override
	public Quad3d transformNormal(Pose3dc pose, Quad3d dest) {
		pose.transformNormal(v0, dest.v0);
		pose.transformNormal(v1, dest.v1);
		pose.transformNormal(v2, dest.v2);
		pose.transformNormal(v3, dest.v3);
		return dest;
	}

	public Quad3d transformNormal(Pose3dc pose) {
		pose.transformNormal(v0);
		pose.transformNormal(v1);
		pose.transformNormal(v2);
		pose.transformNormal(v3);
		return this;
	}

	@Override
	public Quad3d transformNormalInverse(Pose3dc pose, Quad3d dest) {
		pose.transformNormalInverse(v0, dest.v0);
		pose.transformNormalInverse(v1, dest.v1);
		pose.transformNormalInverse(v2, dest.v2);
		pose.transformNormalInverse(v3, dest.v3);
		return dest;
	}

	public Quad3d transformNormalInverse(Pose3dc pose) {
		pose.transformNormalInverse(v0);
		pose.transformNormalInverse(v1);
		pose.transformNormalInverse(v2);
		pose.transformNormalInverse(v3);
		return this;
	}

	@Override
	public Quad3d transformNormal(Quaterniondc quat, Quad3d dest) {
		quat.transform(v0, dest.v0);
		quat.transform(v1, dest.v1);
		quat.transform(v2, dest.v2);
		quat.transform(v3, dest.v3);
		return dest;
	}

	public Quad3d transformNormal(Quaterniondc quat) {
		quat.transform(v0);
		quat.transform(v1);
		quat.transform(v2);
		quat.transform(v3);
		return this;
	}

	@Override
	public Quad3d transformNormalInverse(Quaterniondc quat, Quad3d dest) {
		quat.transformInverse(v0, dest.v0);
		quat.transformInverse(v1, dest.v1);
		quat.transformInverse(v2, dest.v2);
		quat.transformInverse(v3, dest.v3);
		return dest;
	}

	public Quad3d transformNormalInverse(Quaterniondc quat) {
		quat.transformInverse(v0);
		quat.transformInverse(v1);
		quat.transformInverse(v2);
		quat.transformInverse(v3);
		return this;
	}

	@Override
	public Quad3d add(Vector3dc vec, Quad3d dest) {
		v0.add(vec, dest.v0);
		v1.add(vec, dest.v1);
		v2.add(vec, dest.v2);
		v3.add(vec, dest.v3);
		return dest;
	}

	public Quad3d add(Vector3dc vec) {
		v0.add(vec);
		v1.add(vec);
		v2.add(vec);
		v3.add(vec);
		return this;
	}

	@Override
	public Quad3d add(double x, double y, double z, Quad3d dest) {
		v0.add(x, y, z, dest.v0);
		v1.add(x, y, z, dest.v1);
		v2.add(x, y, z, dest.v2);
		v3.add(x, y, z, dest.v3);
		return dest;
	}

	public Quad3d add(double x, double y, double z) {
		v0.add(x, y, z);
		v1.add(x, y, z);
		v2.add(x, y, z);
		v3.add(x, y, z);
		return this;
	}

	@Override
	public Quad3d sub(Vector3dc vec, Quad3d dest) {
		v0.sub(vec, dest.v0);
		v1.sub(vec, dest.v1);
		v2.sub(vec, dest.v2);
		v3.sub(vec, dest.v3);
		return dest;
	}

	public Quad3d sub(Vector3dc vec) {
		v0.sub(vec);
		v1.sub(vec);
		v2.sub(vec);
		v3.sub(vec);
		return this;
	}

	@Override
	public Quad3d sub(double x, double y, double z, Quad3d dest) {
		v0.sub(x, y, z, dest.v0);
		v1.sub(x, y, z, dest.v1);
		v2.sub(x, y, z, dest.v2);
		v3.sub(x, y, z, dest.v3);
		return dest;
	}

	public Quad3d sub(double x, double y, double z) {
		v0.sub(x, y, z);
		v1.sub(x, y, z);
		v2.sub(x, y, z);
		v3.sub(x, y, z);
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(v0, v1, v2, v3);
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj instanceof Quad3dc other) {
			return Objects.equals(v0, other.v0()) &&
					Objects.equals(v1, other.v1()) &&
					Objects.equals(v2, other.v2()) &&
					Objects.equals(v3, other.v3());
		}
		return false;
	}
}
