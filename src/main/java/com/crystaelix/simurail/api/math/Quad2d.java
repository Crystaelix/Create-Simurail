package com.crystaelix.simurail.api.math;

import java.util.Objects;

import org.joml.Vector2d;
import org.joml.Vector2dc;

public class Quad2d implements Quad2dc {

	public final Vector2d v0;
	public final Vector2d v1;
	public final Vector2d v2;
	public final Vector2d v3;

	public Quad2d() {
		this.v0 = new Vector2d();
		this.v1 = new Vector2d();
		this.v2 = new Vector2d();
		this.v3 = new Vector2d();
	}

	public Quad2d(Vector2dc v0, Vector2dc v1, Vector2dc v2, Vector2dc v3) {
		this.v0 = new Vector2d(v0);
		this.v1 = new Vector2d(v1);
		this.v2 = new Vector2d(v2);
		this.v3 = new Vector2d(v3);
	}

	@Override
	public Vector2d v0() {
		return v0;
	}

	@Override
	public Vector2d v1() {
		return v1;
	}

	@Override
	public Vector2d v2() {
		return v2;
	}

	@Override
	public Vector2d v3() {
		return v3;
	}

	public Quad2d set(Quad2dc quad) {
		v0.set(quad.v0());
		v1.set(quad.v1());
		v2.set(quad.v2());
		v3.set(quad.v3());
		return this;
	}

	public Quad2d v0(Vector2dc v0) {
		this.v0.set(v0);
		return this;
	}

	public Quad2d v1(Vector2dc v1) {
		this.v1.set(v1);
		return this;
	}

	public Quad2d v2(Vector2dc v2) {
		this.v2.set(v2);
		return this;
	}

	public Quad2d v3(Vector2dc v3) {
		this.v3.set(v3);
		return this;
	}

	@Override
	public Quad2d add(Vector2dc vec, Quad2d dest) {
		v0.add(vec, dest.v0);
		v1.add(vec, dest.v1);
		v2.add(vec, dest.v2);
		v3.add(vec, dest.v3);
		return dest;
	}

	public Quad2d add(Vector2dc vec) {
		v0.add(vec);
		v1.add(vec);
		v2.add(vec);
		v3.add(vec);
		return this;
	}

	@Override
	public Quad2d add(double x, double y, Quad2d dest) {
		v0.add(x, y, dest.v0);
		v1.add(x, y, dest.v1);
		v2.add(x, y, dest.v2);
		v3.add(x, y, dest.v3);
		return dest;
	}

	public Quad2d add(double x, double y) {
		v0.add(x, y);
		v1.add(x, y);
		v2.add(x, y);
		v3.add(x, y);
		return this;
	}

	@Override
	public Quad2d sub(Vector2dc vec, Quad2d dest) {
		v0.sub(vec, dest.v0);
		v1.sub(vec, dest.v1);
		v2.sub(vec, dest.v2);
		v3.sub(vec, dest.v3);
		return dest;
	}

	public Quad2d sub(Vector2dc vec) {
		v0.sub(vec);
		v1.sub(vec);
		v2.sub(vec);
		v3.sub(vec);
		return this;
	}

	@Override
	public Quad2d sub(double x, double y, Quad2d dest) {
		v0.sub(x, y, dest.v0);
		v1.sub(x, y, dest.v1);
		v2.sub(x, y, dest.v2);
		v3.sub(x, y, dest.v3);
		return dest;
	}

	public Quad2d sub(double x, double y) {
		v0.sub(x, y);
		v1.sub(x, y);
		v2.sub(x, y);
		v3.sub(x, y);
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
		if(obj instanceof Quad2dc other) {
			return Objects.equals(v0, other.v0()) &&
					Objects.equals(v1, other.v1()) &&
					Objects.equals(v2, other.v2()) &&
					Objects.equals(v3, other.v3());
		}
		return false;
	}
}
