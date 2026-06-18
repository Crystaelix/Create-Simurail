package com.crystaelix.simurail.api.math;

import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import net.minecraft.util.Mth;

public class SimurailMathf {

	public static final float SQRT_2 = Mth.SQRT_OF_TWO;
	public static final float EPSILON = 1E-5F;
	public static final float EPSILON_SQ = 1E-10F;

	public static final Vector3fc VEC_0 = new Vector3f();

	public static final Vector3fc DIR_XP = new Vector3f(1, 0, 0);
	public static final Vector3fc DIR_XN = new Vector3f(-1, 0, 0);
	public static final Vector3fc DIR_YP = new Vector3f(0, 1, 0);
	public static final Vector3fc DIR_YN = new Vector3f(0, -1, 0);
	public static final Vector3fc DIR_ZP = new Vector3f(0, 0, 1);
	public static final Vector3fc DIR_ZN = new Vector3f(0, 0, -1);

	public static final Quaternionfc ROT_I = new Quaternionf();
	public static final Quaternionfc ROT_XPYPZP = ROT_I;
	public static final Quaternionfc ROT_ZPYPXN = new Quaternionf(0, -SQRT_2/2, 0, SQRT_2/2);
	public static final Quaternionfc ROT_XNYPZN = new Quaternionf(0, 1, 0, 0);
	public static final Quaternionfc ROT_ZNYPXP = new Quaternionf(0, SQRT_2/2, 0, SQRT_2/2);

	// Vectors

	public static Vector3f slerp(Vector3fc v1, Vector3fc v2, float t, Vector3f dest) {
		float v1Len = v1.length();

		if(v1Len < EPSILON) {
			return dest.set(v2).mul(t);
		}

		float v2Len = v2.length();

		if(v2Len < EPSILON) {
			return dest.set(v1).mul(1 - t);
		}

		float v1LenInv = 1 / v1Len;
		float v1x = v1.x() * v1LenInv;
		float v1y = v1.y() * v1LenInv;
		float v1z = v1.z() * v1LenInv;

		float v2LenInv = 1 / v2Len;
		float v2x = v2.x() * v2LenInv;
		float v2y = v2.y() * v2LenInv;
		float v2z = v2.z() * v2LenInv;

		float cos = Math.clamp(v1x * v2x + v1y * v2y + v1z * v2z, -1, 1);

		if(cos > 0.9995 || cos < -0.9995) {
			return v1.lerp(v2, t, dest);
		}

		float angle = (float)Math.acos(cos);
		float sin = Mth.sin(angle);
		float s1 = Mth.sin((1 - t) * angle) / sin;
		float s2 = Mth.sin(t * angle) / sin;

		float len = Mth.lerp(t, v1Len, v2Len);
		float vx = (v1x * s1 + v2x * s2) * len;
		float vy = (v1y * s1 + v2y * s2) * len;
		float vz = (v1z * s1 + v2z * s2) * len;

		return dest.set(vx, vy, vz);
	}

	// Quaternions

	public static Quaternionf rot(Vector3fc xDir, Quaternionf dest) {
		return rot(xDir, false, dest);
	}

	private static Quaternionf rot(Vector3fc xDir, boolean mapZXY, Quaternionf dest) {
		float lenSq = xDir.lengthSquared();

		if(lenSq < EPSILON_SQ) {
			return dest.identity();
		}

		float lenInv = 1 / Mth.sqrt(lenSq);
		float xx = xDir.x() * lenInv;
		float xy = xDir.y() * lenInv;
		float xz = xDir.z() * lenInv;

		float sign = Math.copySign(1, xz);
		float a = -1 / (sign + xz);
		float b = xx * xy * a;

		float yx = 1 + sign * xx * xx * a;
		float yy = sign * b;
		float yz = -sign * xx;

		float zx = b;
		float zy = sign + xy * xy * a;
		float zz = -xy;

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

	public static Quaternionf rot(Vector3fc xDir, Vector3fc yDir, Quaternionf dest) {
		if(yDir.lengthSquared() < EPSILON_SQ) {
			return rot(xDir, false, dest);
		}

		float xLenSq = xDir.lengthSquared();

		if(xLenSq < EPSILON_SQ) {
			return rot(yDir, true, dest);
		}

		float xLenInv = 1 / Mth.sqrt(xLenSq);
		float xx = xDir.x() * xLenInv;
		float xy = xDir.y() * xLenInv;
		float xz = xDir.z() * xLenInv;

		float zx = xy * yDir.z() - xz * yDir.y();
		float zy = xz * yDir.x() - xx * yDir.z();
		float zz = xx * yDir.y() - xy * yDir.x();

		float zLenSq = Vector3f.lengthSquared(zx, zy, zz);

		if(zLenSq < EPSILON_SQ) {
			return rot(xDir, false, dest);
		}

		float zLenInv = 1 / Mth.sqrt(zLenSq);
		zx *= zLenInv;
		zy *= zLenInv;
		zz *= zLenInv;

		float yx = zy * xz - zz * xy;
		float yy = zz * xx - zx * xz;
		float yz = zx * xy - zy * xx;

		return setRotationFromNormalized(
				xx, xy, xz,
				yx, yy, yz,
				zx, zy, zz,
				dest);
	}

	private static Quaternionf setRotationFromNormalized(float m00, float m01, float m02, float m10, float m11, float m12, float m20, float m21, float m22, Quaternionf dest) {
		float t;
		float tr = m00 + m11 + m22;
		if(tr >= 0) {
			t = Mth.sqrt(tr + 1);
			dest.w = t * 0.5F;
			t = 0.5F / t;
			dest.x = (m12 - m21) * t;
			dest.y = (m20 - m02) * t;
			dest.z = (m01 - m10) * t;
		}
		else if(m00 >= m11 && m00 >= m22) {
			t = Mth.sqrt(m00 - (m11 + m22) + 1);
			dest.x = t * 0.5F;
			t = 0.5F / t;
			dest.y = (m10 + m01) * t;
			dest.z = (m02 + m20) * t;
			dest.w = (m12 - m21) * t;
		}
		else if(m11 > m22) {
			t = Mth.sqrt(m11 - (m22 + m00) + 1);
			dest.y = t * 0.5F;
			t = 0.5F / t;
			dest.z = (m21 + m12) * t;
			dest.x = (m10 + m01) * t;
			dest.w = (m20 - m02) * t;
		}
		else {
			t = Mth.sqrt(m22 - (m00 + m11) + 1);
			dest.z = t * 0.5F;
			t = 0.5F / t;
			dest.x = (m02 + m20) * t;
			dest.y = (m21 + m12) * t;
			dest.w = (m01 - m10) * t;
		}
		return dest;
	}
}
