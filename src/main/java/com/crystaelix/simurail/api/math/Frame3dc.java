package com.crystaelix.simurail.api.math;

import org.joml.Matrix3d;
import org.joml.Quaterniond;
import org.joml.Vector3dc;

import dev.ryanhcode.sable.companion.math.Pose3dc;

public interface Frame3dc {

	Vector3dc direction();

	Vector3dc vertical();

	Vector3dc lateral();

	Vector3dc position();

	Frame3d basis(Basis3dc basis, Frame3d dest);

	Frame3d orthogonalize(Frame3d dest);

	Frame3d normalize(Frame3d dest);

	Frame3d transform(Pose3dc pose, Frame3d dest);

	Frame3d transformInverse(Pose3dc pose, Frame3d dest);

	Matrix3d matrix(Matrix3d dest);

	Quaterniond orientation(Quaterniond dest);
}
