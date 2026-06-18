package com.crystaelix.simurail.api.math;

import org.joml.Matrix3d;
import org.joml.Quaterniondc;
import org.joml.Vector3dc;

public interface Basis3dc {

	Basis3dc I = new Basis3d();
	Basis3dc XPYPZP = I;
	Basis3dc ZPYPXN = new Basis3d(SimurailMath.DIR_ZP, SimurailMath.DIR_YP, SimurailMath.DIR_XN);

	Vector3dc direction();

	Vector3dc vertical();

	Vector3dc lateral();

	Basis3d orthogonalize(Basis3d dest);

	Basis3d normalize(Basis3d dest);

	Basis3d transform(Quaterniondc quat, Basis3d dest);

	Basis3d transformInverse(Quaterniondc quat, Basis3d dest);

	Matrix3d matrix(Matrix3d dest);
}
