package com.crystaelix.simurail.api.math;

import org.joml.Matrix3f;
import org.joml.Quaterniondc;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;

import dev.ryanhcode.sable.companion.math.Pose3dc;

public interface Basis3fc {

	Basis3fc I = new Basis3f();
	Basis3fc XPYPZP = I;
	Basis3fc ZPYPXN = new Basis3f(SimurailMathf.DIR_ZP, SimurailMathf.DIR_YP, SimurailMathf.DIR_XN);

	Vector3fc direction();

	Vector3fc vertical();

	Vector3fc lateral();

	Basis3f orthogonalize(Basis3f dest);

	Basis3f normalize(Basis3f dest);

	Basis3f transform(Quaternionfc quat, Basis3f dest);

	Basis3f transformInverse(Quaternionfc quat, Basis3f dest);

	Basis3f transform(Pose3dc pose, Basis3f dest);

	Basis3f transformInverse(Pose3dc pose, Basis3f dest);

	Basis3f transform(Quaterniondc quat, Basis3f dest);

	Basis3f transformInverse(Quaterniondc quat, Basis3f dest);

	Matrix3f matrix(Matrix3f dest);
}
