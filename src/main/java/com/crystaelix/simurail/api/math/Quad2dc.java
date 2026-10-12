package com.crystaelix.simurail.api.math;

import org.joml.Vector2dc;

public interface Quad2dc {

	Vector2dc v0();

	Vector2dc v1();

	Vector2dc v2();

	Vector2dc v3();

	Quad2d add(Vector2dc vec, Quad2d dest);

	Quad2d add(double x, double y, Quad2d dest);

	Quad2d sub(Vector2dc vec, Quad2d dest);

	Quad2d sub(double x, double y, Quad2d dest);
}
