package com.crystaelix.simurail.api.math;

import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import dev.ryanhcode.sable.companion.math.Pose3dc;
import net.minecraft.util.Mth;

public class CubicBezier3d implements CubicBezier3dc {

	public final Vector3d end1;
	public final Vector3d end2;
	public final Vector3d handle1;
	public final Vector3d handle2;

	public CubicBezier3d() {
		this.end1 = new Vector3d();
		this.end2 = new Vector3d();
		this.handle1 = new Vector3d();
		this.handle2 = new Vector3d();
	}

	public CubicBezier3d(Vector3dc end1, Vector3dc end2, Vector3dc handle1, Vector3dc handle2) {
		this.end1 = new Vector3d(end1);
		this.end2 = new Vector3d(end2);
		this.handle1 = new Vector3d(handle1);
		this.handle2 = new Vector3d(handle2);
	}

	@Override
	public Vector3d end1() {
		return end1;
	}

	@Override
	public Vector3d end2() {
		return end2;
	}

	@Override
	public Vector3d handle1() {
		return handle1;
	}

	@Override
	public Vector3d handle2() {
		return handle2;
	}

	public CubicBezier3d set(CubicBezier3dc bezier) {
		end1.set(bezier.end1());
		end2.set(bezier.end2());
		handle1.set(bezier.handle1());
		handle2.set(bezier.handle2());
		return this;
	}

	public CubicBezier3d set(Vector3dc end1, Vector3dc end2, Vector3dc handle1, Vector3dc handle2) {
		this.end1.set(end1);
		this.end2.set(end2);
		this.handle1.set(handle1);
		this.handle2.set(handle2);
		return this;
	}

	public CubicBezier3d setHermite(Vector3dc end1, Vector3dc end2, Vector3dc dir1, Vector3dc dir2) {
		double x = end2.x() - end1.x();
		double y = end2.y() - end1.y();
		double z = end2.z() - end1.z();
		double length = (dir1.dot(x, y, z) - dir2.dot(x, y, z)) / 4;
		this.end1.set(end1);
		this.end2.set(end2);
		this.handle1.set(dir1).normalize(length).add(end1);
		this.handle2.set(dir2).normalize(length).add(end2);
		return this;
	}

	@Override
	public CubicBezier3d transformPosition(Pose3dc pose, CubicBezier3d dest) {
		pose.transformPosition(end1, dest.end1);
		pose.transformPosition(end2, dest.end2);
		pose.transformPosition(handle1, dest.handle1);
		pose.transformPosition(handle2, dest.handle2);
		return dest;
	}

	public CubicBezier3d transformPosition(Pose3dc pose) {
		pose.transformPosition(end1);
		pose.transformPosition(end2);
		pose.transformPosition(handle1);
		pose.transformPosition(handle2);
		return this;
	}

	@Override
	public CubicBezier3d transformPositionInverse(Pose3dc pose, CubicBezier3d dest) {
		pose.transformPositionInverse(end1, dest.end1);
		pose.transformPositionInverse(end2, dest.end2);
		pose.transformPositionInverse(handle1, dest.handle1);
		pose.transformPositionInverse(handle2, dest.handle2);
		return this;
	}

	public CubicBezier3d transformPositionInverse(Pose3dc pose) {
		pose.transformPositionInverse(end1);
		pose.transformPositionInverse(end2);
		pose.transformPositionInverse(handle1);
		pose.transformPositionInverse(handle2);
		return this;
	}

	@Override
	public CubicBezier3d transformNormal(Pose3dc pose, CubicBezier3d dest) {
		pose.transformNormal(end1, dest.end1);
		pose.transformNormal(end2, dest.end2);
		pose.transformNormal(handle1, dest.handle1);
		pose.transformNormal(handle2, dest.handle2);
		return dest;
	}

	public CubicBezier3d transformNormal(Pose3dc pose) {
		pose.transformNormal(end1);
		pose.transformNormal(end2);
		pose.transformNormal(handle1);
		pose.transformNormal(handle2);
		return this;
	}

	@Override
	public CubicBezier3d transformNormalInverse(Pose3dc pose, CubicBezier3d dest) {
		pose.transformNormalInverse(end1, dest.end1);
		pose.transformNormalInverse(end2, dest.end2);
		pose.transformNormalInverse(handle1, dest.handle1);
		pose.transformNormalInverse(handle2, dest.handle2);
		return this;
	}

	public CubicBezier3d transformNormalInverse(Pose3dc pose) {
		pose.transformNormalInverse(end1);
		pose.transformNormalInverse(end2);
		pose.transformNormalInverse(handle1);
		pose.transformNormalInverse(handle2);
		return this;
	}

	@Override
	public CubicBezier3d transformNormal(Quaterniondc quat, CubicBezier3d dest) {
		quat.transform(end1, dest.end1);
		quat.transform(end2, dest.end2);
		quat.transform(handle1, dest.handle1);
		quat.transform(handle2, dest.handle2);
		return dest;
	}

	public CubicBezier3d transformNormal(Quaterniondc quat) {
		quat.transform(end1);
		quat.transform(end2);
		quat.transform(handle1);
		quat.transform(handle2);
		return this;
	}

	@Override
	public CubicBezier3d transformNormalInverse(Quaterniondc quat, CubicBezier3d dest) {
		quat.transformInverse(end1, dest.end1);
		quat.transformInverse(end2, dest.end2);
		quat.transformInverse(handle1, dest.handle1);
		quat.transformInverse(handle2, dest.handle2);
		return this;
	}

	public CubicBezier3d transformNormalInverse(Quaterniondc quat) {
		quat.transformInverse(end1);
		quat.transformInverse(end2);
		quat.transformInverse(handle1);
		quat.transformInverse(handle2);
		return this;
	}

	@Override
	public CubicBezier3d reverse(CubicBezier3d dest) {
		dest.end1.set(end2);
		dest.end2.set(end1);
		dest.handle1.set(handle2);
		dest.handle2.set(handle1);
		return dest;
	}

	public CubicBezier3d reverse() {
		double x, y, z;
		x = end1.x; y = end1.y; z = end1.z;
		end1.set(end2);
		end2.set(x, y, z);
		x = handle1.x; y = handle1.y; z = handle1.z;
		handle1.set(handle2);
		handle2.set(x, y, z);
		return this;
	}

	@Override
	public CubicBezier3d add(Vector3dc vec, CubicBezier3d dest) {
		end1.add(vec, dest.end1);
		end2.add(vec, dest.end2);
		handle1.add(vec, dest.handle1);
		handle2.add(vec, dest.handle2);
		return dest;
	}

	public CubicBezier3d add(Vector3dc vec) {
		end1.add(vec);
		end2.add(vec);
		handle1.add(vec);
		handle2.add(vec);
		return this;
	}

	@Override
	public CubicBezier3d sub(Vector3dc vec, CubicBezier3d dest) {
		end1.sub(vec, dest.end1);
		end2.sub(vec, dest.end2);
		handle1.sub(vec, dest.handle1);
		handle2.sub(vec, dest.handle2);
		return dest;
	}

	public CubicBezier3d sub(Vector3dc vec) {
		end1.sub(vec);
		end2.sub(vec);
		handle1.sub(vec);
		handle2.sub(vec);
		return this;
	}

	@Override
	public double length(double t0, double t1) {
		if(t0 == t1) {
			return 0;
		}
		double s = 0;
		double h = (t1 - t0) * 0.5;
		double m = (t0 + t1) * 0.5;
		for(int i = 0; i < X4.length; i++) {
			double t = h * X4[i] + m;
			double vx = velocity(t, 0);
			double vy = velocity(t, 1);
			double vz = velocity(t, 2);
			s += W4[i] * Vector3d.length(vx, vy, vz);
		}
		return h * s;
	}

	@Override
	public Vector3d position(double t, Vector3d dest) {
		return dest.set(position(t, 0), position(t, 1), position(t, 2));
	}

	@Override
	public double position(double t, int component) {
		double p01 = Mth.lerp(t, end1.get(component), handle1.get(component));
		double p12 = Mth.lerp(t, handle1.get(component), handle2.get(component));
		double p23 = Mth.lerp(t, handle2.get(component), end2.get(component));
		double p02 = Mth.lerp(t, p01, p12);
		double p13 = Mth.lerp(t, p12, p23);
		return Mth.lerp(t, p02, p13);
	}

	@Override
	public Vector3d velocity(double t, Vector3d dest) {
		return dest.set(velocity(t, 0), velocity(t, 1), velocity(t, 2));
	}

	@Override
	public double velocity(double t, int component) {
		double q0 = 3 * (handle1.get(component) - end1.get(component));
		double q1 = 3 * (handle2.get(component) - handle1.get(component));
		double q2 = 3 * (end2.get(component) - handle2.get(component));
		double q01 = Mth.lerp(t, q0, q1);
		double q12 = Mth.lerp(t, q1, q2);
		return Mth.lerp(t, q01, q12);
	}

	@Override
	public Vector3d acceleration(double t, Vector3d dest) {
		return dest.set(acceleration(t, 0), acceleration(t, 1), acceleration(t, 2));
	}

	@Override
	public double acceleration(double t, int component) {
		double r0 = 6 * (handle2.get(component) - 2 * handle1.get(component) + end1.get(component));
		double r1 = 6 * (end2.get(component) - 2 * handle2.get(component) + handle1.get(component));
		return Mth.lerp(t, r0, r1);
	}

	@Override
	public Vector3d curvature(double t, Vector3d dest) {
		double vx = velocity(t, 0);
		double vy = velocity(t, 1);
		double vz = velocity(t, 2);
		double ax = acceleration(t, 0);
		double ay = acceleration(t, 1);
		double az = acceleration(t, 2);
		double vLenSq = Vector3d.lengthSquared(vx, vy, vz);

		if(vLenSq < SimurailMath.EPSILON_SQ) {
			return dest.set(ax, ay, az);
		}

		double vLenSqInv = 1 / vLenSq;
		double va = (vx * ax + vy * ay + vz * az) * vLenSqInv;
		double cx = (ax - vx * va) * vLenSqInv;
		double cy = (ay - vy * va) * vLenSqInv;
		double cz = (az - vz * va) * vLenSqInv;

		return dest.set(cx, cy, cz);
	}

	@Override
	public double[] lengthLUT(double t0, double t1, double[] dest) {
		int segments = dest.length - 1;
		if(segments < 1) {
			throw new IllegalArgumentException("Must have at least 1 segment");
		}
		for(int i = 0; i <= segments; ++i) {
			dest[i] = length(t0, Mth.lerp((double)i / segments, t0, t1));
		}
		return dest;
	}

	private static final double[] X3 = {
			0,
			0.7745966692414834, -0.7745966692414834
	};
	private static final double[] W3 = {
			0.8888888888888888,
			0.5555555555555556, 0.5555555555555556
	};

	private static final double[] X4 = {
			0.3399810435848563, -0.3399810435848563,
			0.8611363115940526, -0.8611363115940526,
	};
	private static final double[] W4 = {
			0.6521451548625462, 0.6521451548625462,
			0.3478548451374538, 0.3478548451374538,
	};

	private static final double[] X5 = {
			0,
			0.5384693101056831, -0.5384693101056831,
			0.9061798459386640, -0.9061798459386640
	};
	private static final double[] W5 = {
			0.5688888888888890,
			0.4786286704993665, 0.4786286704993665,
			0.2369268850561890, 0.2369268850561890
	};

	private static final double[] X6 = {
			0.2386191860831969, -0.2386191860831969,
			0.6612093864662645, -0.6612093864662645,
			0.9324695142031521, -0.9324695142031521
	};
	private static final double[] W6 = {
			0.4679139345726912, 0.4679139345726912,
			0.3607615730481386, 0.3607615730481386,
			0.1713244923791702, 0.1713244923791702
	};

	private static final double[] X7 = {
			0,
			0.4058451513773972, -0.4058451513773972,
			0.7415311855993945, -0.7415311855993945,
			0.9491079123427585, -0.9491079123427585,
	};
	private static final double[] W7 = {
			0.4179591836734694,
			0.3818300505051189, 0.3818300505051189,
			0.2797053914892766, 0.2797053914892766,
			0.1294849661688697, 0.1294849661688697,
	};
}
