package com.crystaelix.simurail.api.math;

import org.joml.Matrix3d;
import org.joml.Matrix3dc;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import com.crystaelix.simurail.api.extension.BezierConnectionExtension;
import com.simibubi.create.content.trains.track.BezierConnection;

import dev.ryanhcode.sable.api.physics.mass.MassData;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import net.minecraft.util.Mth;

public class SimurailMath {

	public static final double SQRT_2 = Math.sqrt(2);
	public static final double EPSILON = 1E-6;
	public static final double EPSILON_SQ = 1E-12;

	public static final Vector3dc VEC_0 = new Vector3d();

	public static final Vector3dc DIR_XP = new Vector3d(1, 0, 0);
	public static final Vector3dc DIR_XN = new Vector3d(-1, 0, 0);
	public static final Vector3dc DIR_YP = new Vector3d(0, 1, 0);
	public static final Vector3dc DIR_YN = new Vector3d(0, -1, 0);
	public static final Vector3dc DIR_ZP = new Vector3d(0, 0, 1);
	public static final Vector3dc DIR_ZN = new Vector3d(0, 0, -1);

	public static final Quaterniondc ROT_I = new Quaterniond();
	public static final Quaterniondc ROT_XPYPZP = ROT_I;
	public static final Quaterniondc ROT_ZPYPXN = new Quaterniond(0, -SQRT_2/2, 0, SQRT_2/2);
	public static final Quaterniondc ROT_XNYPZN = new Quaterniond(0, 1, 0, 0);
	public static final Quaterniondc ROT_ZNYPXP = new Quaterniond(0, SQRT_2/2, 0, SQRT_2/2);

	public static final Pose3dc POSE_I = new Pose3d();

	// Vectors

	public static Vector3d slerp(Vector3dc v1, Vector3dc v2, double t, Vector3d dest) {
		double v1Len = v1.length();

		if(v1Len < EPSILON) {
			return dest.set(v2).mul(t);
		}

		double v2Len = v2.length();

		if(v2Len < EPSILON) {
			return dest.set(v1).mul(1 - t);
		}

		double v1LenInv = 1 / v1Len;
		double v1x = v1.x() * v1LenInv;
		double v1y = v1.y() * v1LenInv;
		double v1z = v1.z() * v1LenInv;

		double v2LenInv = 1 / v2Len;
		double v2x = v2.x() * v2LenInv;
		double v2y = v2.y() * v2LenInv;
		double v2z = v2.z() * v2LenInv;

		double cos = Math.clamp(v1x * v2x + v1y * v2y + v1z * v2z, -1, 1);

		if(cos > 0.9995 || cos < -0.9995) {
			return v1.lerp(v2, t, dest);
		}

		double angle = Math.acos(cos);
		double sin = Math.sin(angle);
		double s1 = Math.sin((1 - t) * angle) / sin;
		double s2 = Math.sin(t * angle) / sin;

		double len = Mth.lerp(t, v1Len, v2Len);
		double vx = (v1x * s1 + v2x * s2) * len;
		double vy = (v1y * s1 + v2y * s2) * len;
		double vz = (v1z * s1 + v2z * s2) * len;

		return dest.set(vx, vy, vz);
	}

	public static double angle(Vector3dc dir1, Vector3dc dir2, Vector3dc axis) {
		if(dir1.lengthSquared() < EPSILON_SQ || dir2.lengthSquared() < EPSILON_SQ) {
			return 0;
		}

		double aLenSq = axis.lengthSquared();

		if(aLenSq < EPSILON_SQ) {
			return 0;
		}

		double aLenInv = 1 / Math.sqrt(aLenSq);
		double ax = axis.x() * aLenInv;
		double ay = axis.y() * aLenInv;
		double az = axis.z() * aLenInv;

		double d1a = dir1.x() * ax + dir1.y() * ay + dir1.z() * az;
		double d1x = dir1.x() - d1a * ax;
		double d1y = dir1.y() - d1a * ay;
		double d1z = dir1.z() - d1a * az;

		if(Vector3d.lengthSquared(d1x, d1y, d1z) < EPSILON_SQ) {
			return 0;
		}

		double d2a = dir2.x() * ax + dir2.y() * ay + dir2.z() * az;
		double d2x = dir2.x() - d2a * ax;
		double d2y = dir2.y() - d2a * ay;
		double d2z = dir2.z() - d2a * az;

		if(Vector3d.lengthSquared(d2x, d2y, d2z) < EPSILON_SQ) {
			return 0;
		}

		double x = d1x * d2x + d1y * d2y + d1z * d2z;
		double y = (d1y * d2z - d1z * d2y) * ax + (d1z * d2x - d1x * d2z) * ay + (d1x * d2y - d1y * d2x) * az;

		return Math.atan2(y, x);
	}

	// 3D Distances

	public static double distanceSquaredLinePoint(Vector3dc start, Vector3dc dir, Vector3dc pos) {
		double wx = pos.x() - start.x();
		double wy = pos.y() - start.y();
		double wz = pos.z() - start.z();

		double dLenSq = dir.lengthSquared();

		if(dLenSq < EPSILON_SQ) {
			return Vector3d.lengthSquared(wx, wy, wz);
		}

		double dw = dir.x() * wx + dir.y() * wy + dir.z() * wz;
		return Vector3d.lengthSquared(wx, wy, wz) - dw / dLenSq * dw;
	}

	public static double projectTLinePoint(Vector3dc start, Vector3dc delta, Vector3dc pos) {
		double dLenSq = delta.lengthSquared();

		if(dLenSq < EPSILON_SQ) {
			return 0;
		}

		double wx = pos.x() - start.x();
		double wy = pos.y() - start.y();
		double wz = pos.z() - start.z();
		double dw = delta.x() * wx + delta.y() * wy + delta.z() * wz;
		return dw / dLenSq;
	}

	public static Vector3d projectLinePoint(Vector3dc start, Vector3dc dir, Vector3dc pos, Vector3d dest) {
		return start.fma(projectTLinePoint(start, dir, pos), dir, dest);
	}

	public static double intersectPlaneLine(Vector3dc point, Vector3dc normal, Vector3dc start, Vector3dc dir) {
		double nd = normal.dot(dir);

		if(Math.abs(nd) < EPSILON_SQ) {
			return Double.NaN;
		}

		double wx = point.x() - start.x();
		double wy = point.y() - start.y();
		double wz = point.z() - start.z();
		double wn = wx * normal.x() + wy * normal.y() + wz * normal.z();

		return wn / nd;
	}

	// Quaternions

	public static Quaterniond rot(Vector3dc xDir, Quaterniond dest) {
		return rot(xDir, false, dest);
	}

	private static Quaterniond rot(Vector3dc xDir, boolean mapZXY, Quaterniond dest) {
		double lenSq = xDir.lengthSquared();

		if(lenSq < EPSILON_SQ) {
			return dest.identity();
		}

		double lenInv = 1 / Math.sqrt(lenSq);
		double xx = xDir.x() * lenInv;
		double xy = xDir.y() * lenInv;
		double xz = xDir.z() * lenInv;

		double sign = Math.copySign(1, xz);
		double a = -1 / (sign + xz);
		double b = xx * xy * a;

		double yx = 1 + sign * xx * xx * a;
		double yy = sign * b;
		double yz = -sign * xx;

		double zx = b;
		double zy = sign + xy * xy * a;
		double zz = -xy;

		if(mapZXY) {
			return setRotationFromNormalized(
					zx, zy, zz,
					xx, xy, xz,
					yx, yy, yz,
					dest);
		}
		else {
			return setRotationFromNormalized(
					xx, xy, xz,
					yx, yy, yz,
					zx, zy, zz,
					dest);
		}
	}

	public static Quaterniond rot(Vector3dc xDir, Vector3dc yDir, Quaterniond dest) {
		if(yDir.lengthSquared() < EPSILON_SQ) {
			return rot(xDir, false, dest);
		}

		double xLenSq = xDir.lengthSquared();

		if(xLenSq < EPSILON_SQ) {
			return rot(yDir, true, dest);
		}

		double xLenInv = 1 / Math.sqrt(xLenSq);
		double xx = xDir.x() * xLenInv;
		double xy = xDir.y() * xLenInv;
		double xz = xDir.z() * xLenInv;

		double zx = xy * yDir.z() - xz * yDir.y();
		double zy = xz * yDir.x() - xx * yDir.z();
		double zz = xx * yDir.y() - xy * yDir.x();

		double zLenSq = Vector3d.lengthSquared(zx, zy, zz);

		if(zLenSq < EPSILON_SQ) {
			return rot(xDir, false, dest);
		}

		double zLenInv = 1 / Math.sqrt(zLenSq);
		zx *= zLenInv;
		zy *= zLenInv;
		zz *= zLenInv;

		double yx = zy * xz - zz * xy;
		double yy = zz * xx - zx * xz;
		double yz = zx * xy - zy * xx;

		return setRotationFromNormalized(
				xx, xy, xz,
				yx, yy, yz,
				zx, zy, zz,
				dest);
	}

	private static Quaterniond setRotationFromNormalized(double m00, double m01, double m02, double m10, double m11, double m12, double m20, double m21, double m22, Quaterniond dest) {
		double t;
		double tr = m00 + m11 + m22;
		if(tr >= 0) {
			t = Math.sqrt(tr + 1);
			dest.w = t * 0.5;
			t = 0.5 / t;
			dest.x = (m12 - m21) * t;
			dest.y = (m20 - m02) * t;
			dest.z = (m01 - m10) * t;
		}
		else if(m00 >= m11 && m00 >= m22) {
			t = Math.sqrt(m00 - (m11 + m22) + 1);
			dest.x = t * 0.5;
			t = 0.5 / t;
			dest.y = (m10 + m01) * t;
			dest.z = (m02 + m20) * t;
			dest.w = (m12 - m21) * t;
		}
		else if(m11 > m22) {
			t = Math.sqrt(m11 - (m22 + m00) + 1);
			dest.y = t * 0.5;
			t = 0.5 / t;
			dest.z = (m21 + m12) * t;
			dest.x = (m10 + m01) * t;
			dest.w = (m20 - m02) * t;
		}
		else {
			t = Math.sqrt(m22 - (m00 + m11) + 1);
			dest.z = t * 0.5;
			t = 0.5 / t;
			dest.x = (m02 + m20) * t;
			dest.y = (m21 + m12) * t;
			dest.w = (m01 - m10) * t;
		}
		return dest;
	}

	// Moment of Inertia

	public static double moment(MassData massData, Vector3dc axis) {
		double m = massData.getMass();

		if(m <= 0) {
			return 0;
		}

		double aLenSq = axis.lengthSquared();

		if(aLenSq < EPSILON_SQ) {
			return 0;
		}

		double aLenInv = 1 / Math.sqrt(aLenSq);
		double ax = axis.x() * aLenInv;
		double ay = axis.y() * aLenInv;
		double az = axis.z() * aLenInv;

		Matrix3dc I = massData.getInertiaTensor();
		return (I.m00() * ax + I.m10() * ay + I.m20() * az) * ax + (I.m01() * ax + I.m11() * ay + I.m21() * az) * ay + (I.m02() * ax + I.m12() * ay + I.m22() * az) * az;
	}

	public static double moment(MassData massData, Vector3dc pos, Vector3dc axis) {
		double m = massData.getMass();

		if(m <= 0) {
			return 0;
		}

		double aLenSq = axis.lengthSquared();

		if(aLenSq < EPSILON_SQ) {
			return 0;
		}

		double aLenInv = 1 / Math.sqrt(aLenSq);
		double ax = axis.x() * aLenInv;
		double ay = axis.y() * aLenInv;
		double az = axis.z() * aLenInv;

		Matrix3dc I = massData.getInertiaTensor();
		double i = (I.m00() * ax + I.m10() * ay + I.m20() * az) * ax + (I.m01() * ax + I.m11() * ay + I.m21() * az) * ay + (I.m02() * ax + I.m12() * ay + I.m22() * az) * az;

		Vector3dc cm = massData.getCenterOfMass();
		double rx = cm.x() - pos.x();
		double ry = cm.y() - pos.y();
		double rz = cm.z() - pos.z();
		double ra = rx * ax + ry * ay + rz * az;
		double dSq = (rx * rx + ry * ry + rz * rz) - (ra * ra);

		return i + (m * dSq);
	}

	// Bezier Curves

	public static BezierControlPoints controlPoints(BezierConnection curve) {
		double handleLength = curve.getHandleLength();
		Vector3d end1 = JOMLConversion.toJOML(curve.starts.getFirst());
		Vector3d end2 = JOMLConversion.toJOML(curve.starts.getSecond());
		Vector3d handle1 = JOMLConversion.toJOML(curve.axes.getFirst()).normalize(handleLength).add(end1);
		Vector3d handle2 = JOMLConversion.toJOML(curve.axes.getSecond()).normalize(handleLength).add(end2);
		return new BezierControlPoints(end1, end2, handle1, handle2);
	}

	public static BezierControlPoints cachedControlPoints(BezierConnection curve) {
		return ((BezierConnectionExtension)curve).simurail$controlPoints();
	}

	public static double segmentToCurveT(BezierControlPoints points, double segmentStartT, double segmentEndT, double segmentLinearT, int iterations) {
		double ft0x = bezierPosition(points, segmentStartT, 0);
		double ft0y = bezierPosition(points, segmentStartT, 1);
		double ft0z = bezierPosition(points, segmentStartT, 2);
		double ft1x = bezierPosition(points, segmentEndT, 0);
		double ft1y = bezierPosition(points, segmentEndT, 1);
		double ft1z = bezierPosition(points, segmentEndT, 2);
		double px = Mth.lerp(segmentLinearT, ft0x, ft1x);
		double py = Mth.lerp(segmentLinearT, ft0y, ft1y);
		double pz = Mth.lerp(segmentLinearT, ft0z, ft1z);

		double t = Mth.lerp(segmentLinearT, segmentStartT, segmentEndT);

		for(int i = 0; i < iterations; ++i) {
			double wtx = bezierPosition(points, t, 0) - px;
			double wty = bezierPosition(points, t, 1) - py;
			double wtz = bezierPosition(points, t, 2) - pz;
			double vtx = bezierVelocity(points, t, 0);
			double vty = bezierVelocity(points, t, 1);
			double vtz = bezierVelocity(points, t, 2);
			double atx = bezierAcceleration(points, t, 0);
			double aty = bezierAcceleration(points, t, 1);
			double atz = bezierAcceleration(points, t, 2);

			double d1d = wtx * vtx + wty * vty + wtz * vtz;
			double d2d = wtx * atx + wty * aty + wtz * atz + Vector3d.lengthSquared(vtx, vty, vtz);

			if(Math.abs(d2d) < EPSILON_SQ) {
				break;
			}

			double d = d1d / d2d;

			if(Math.abs(d) < EPSILON) {
				break;
			}

			t = Math.clamp(t - d, segmentStartT, segmentEndT);
		}

		return t;
	}

	public static double segmentToCurveT(BezierConnection curve, double segmentStartT, double segmentEndT, double segmentLinearT, int iterations) {
		return segmentToCurveT(cachedControlPoints(curve), segmentStartT, segmentEndT, segmentLinearT, iterations);
	}

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

	public static double bezierLength(BezierControlPoints points, double t0, double t1) {
		double s = 0;
		double h = (t1 - t0) * 0.5;
		double m = (t0 + t1) * 0.5;
		for(int i = 0; i < 7; i++) {
			double t = h * X7[i] + m;
			double vx = bezierVelocity(points, t, 0);
			double vy = bezierVelocity(points, t, 1);
			double vz = bezierVelocity(points, t, 2);
			s += W7[i] * Vector3d.length(vx, vy, vz);
		}
		return h * s;
	}

	public static double bezierLength(BezierConnection curve, double t0, double t1) {
		return bezierLength(cachedControlPoints(curve), t0, t1);
	}

	public static double bezierPosition(BezierControlPoints points, double t, int component) {
		double p01 = Mth.lerp(t, points.end1().get(component), points.handle1().get(component));
		double p12 = Mth.lerp(t, points.handle1().get(component), points.handle2().get(component));
		double p23 = Mth.lerp(t, points.handle2().get(component), points.end2().get(component));
		double p02 = Mth.lerp(t, p01, p12);
		double p13 = Mth.lerp(t, p12, p23);
		return Mth.lerp(t, p02, p13);
	}

	public static Vector3d bezierPosition(BezierControlPoints points, double t, Vector3d dest) {
		return dest.set(bezierPosition(points, t, 0), bezierPosition(points, t, 1), bezierPosition(points, t, 2));
	}

	public static Vector3d bezierPosition(BezierConnection curve, double t, Vector3d dest) {
		return bezierPosition(cachedControlPoints(curve), t, dest);
	}

	public static double bezierVelocity(BezierControlPoints points, double t, int component) {
		double q0 = 3 * (points.handle1().get(component) - points.end1().get(component));
		double q1 = 3 * (points.handle2().get(component) - points.handle1().get(component));
		double q2 = 3 * (points.end2().get(component) - points.handle2().get(component));
		double q01 = Mth.lerp(t, q0, q1);
		double q12 = Mth.lerp(t, q1, q2);
		return Mth.lerp(t, q01, q12);
	}

	public static Vector3d bezierVelocity(BezierControlPoints points, double t, Vector3d dest) {
		return dest.set(bezierVelocity(points, t, 0), bezierVelocity(points, t, 1), bezierVelocity(points, t, 2));
	}

	public static Vector3d bezierVelocity(BezierConnection curve, double t, Vector3d dest) {
		return bezierVelocity(cachedControlPoints(curve), t, dest);
	}

	public static double bezierAcceleration(BezierControlPoints points, double t, int component) {
		double q0 = 3 * (points.handle1().get(component) - points.end1().get(component));
		double q1 = 3 * (points.handle2().get(component) - points.handle1().get(component));
		double q2 = 3 * (points.end2().get(component) - points.handle2().get(component));
		double r0 = 2 * (q1 - q0);
		double r1 = 2 * (q2 - q1);
		return Mth.lerp(t, r0, r1);
	}

	public static Vector3d bezierCurvature(BezierControlPoints points, double t, Vector3d dest) {
		double vx = bezierVelocity(points, t, 0);
		double vy = bezierVelocity(points, t, 1);
		double vz = bezierVelocity(points, t, 2);
		double vLenSq = Vector3d.lengthSquared(vx, vy, vz);

		if(vLenSq < EPSILON_SQ) {
			return dest.zero();
		}

		double ax = bezierAcceleration(points, t, 0);
		double ay = bezierAcceleration(points, t, 1);
		double az = bezierAcceleration(points, t, 2);

		double vLenSqInv = 1 / vLenSq;
		double va = (vx * ax + vy * ay + vz * az) * vLenSqInv;
		double cx = (ax - vx * va) * vLenSqInv;
		double cy = (ay - vy * va) * vLenSqInv;
		double cz = (az - vz * va) * vLenSqInv;

		return dest.set(cx, cy, cz);
	}

	public static Vector3d bezierCurvature(BezierConnection curve, double t, Vector3d dest) {
		return bezierCurvature(cachedControlPoints(curve), t, dest);
	}
}
