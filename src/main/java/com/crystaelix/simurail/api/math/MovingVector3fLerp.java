package com.crystaelix.simurail.api.math;

import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class MovingVector3fLerp {

	protected final int size;
	protected final int tailSize;

	protected int index = 0;
	protected final Vector3f[] vs;

	protected final float[] tailWeights;

	protected MovingVector3fLerp(int size, int tailSize) {
		if(size < 2) {
			throw new IllegalArgumentException("size must be >= 2");
		}
		if(size < tailSize + 1) {
			throw new IllegalArgumentException("size must be >= tailSize + 1");
		}
		this.size = size;
		this.tailSize = tailSize;

		vs = new Vector3f[size];
		for(int i = 0; i < size; ++i) {
			vs[i] = new Vector3f();
		}

		tailWeights = new float[tailSize];
	}

	public static MovingVector3fLerp of(int size) {
		return new MovingVector3fLerp(size, 1);
	}

	public static MovingVector3fLerp of(int size, int tailSize) {
		return new MovingVector3fLerp(size, tailSize);
	}

	public static MovingVector3fLerp average(int size) {
		return new MovingVector3fLerp(size, 0);
	}

	public MovingVector3fLerp initialize(Vector3fc v) {
		for(int i = 0; i < size; ++i) {
			vs[i].set(v);
		}
		return this;
	}

	public MovingVector3fLerp initialize(Vector3dc q) {
		for(int i = 0; i < size; ++i) {
			vs[i].set(q);
		}
		return this;
	}

	public MovingVector3fLerp step() {
		int next = (index + 1) % size;
		vs[next].set(vs[index]);
		index = next;
		return this;
	}

	public MovingVector3fLerp step(Vector3fc q) {
		int next = (index + 1) % size;
		vs[next].set(q);
		index = next;
		return this;
	}

	public MovingVector3fLerp step(Vector3dc q) {
		int next = (index + 1) % size;
		vs[next].set(q);
		index = next;
		return this;
	}

	public Vector3f lerp(float t, Vector3f dest) {
		for(int i = 0; i < tailSize; ++i) {
			tailWeights[i] = 1 + (i - t) / tailSize;
		}
		int next = (index + 1) % size;
		dest.set(vs[next]);
		float w = tailSize == 0 ? 1 : tailWeights[0];
		for(int i = 1; i < size; ++i) {
			int j = (next + i) % size;
			float w0 = w;
			float w1 = i < tailSize ? tailWeights[i] : i == size - 1 ? t : 1;
			float rw1 = w1 / (w0 + w1);
			w += w1;
			dest.lerp(vs[j], rw1);
		}
		return dest;
	}

	public Vector3f lerp(float t) {
		return lerp(t, new Vector3f());
	}

	public Vector3f lerp(Vector3f dest) {
		return lerp(1, dest);
	}

	public Vector3f lerp() {
		return lerp(1);
	}
}
