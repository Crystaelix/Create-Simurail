package com.crystaelix.simurail.api.math;

import org.joml.Quaterniondc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public class MovingQuaternionfLerp {

	protected final int size;
	protected final int tailSize;

	protected int index = 0;
	protected final Quaternionf[] qs;

	protected final float[] tailWeights;

	protected MovingQuaternionfLerp(int size, int tailSize) {
		if(size < 2) {
			throw new IllegalArgumentException("size must be >= 2");
		}
		if(size < tailSize + 1) {
			throw new IllegalArgumentException("size must be >= tailSize + 1");
		}
		this.size = size;
		this.tailSize = tailSize;

		qs = new Quaternionf[size];
		for(int i = 0; i < size; ++i) {
			qs[i] = new Quaternionf();
		}

		tailWeights = new float[tailSize];
	}

	public static MovingQuaternionfLerp of(int size) {
		return new MovingQuaternionfLerp(size, 1);
	}

	public static MovingQuaternionfLerp of(int size, int tailSize) {
		return new MovingQuaternionfLerp(size, tailSize);
	}

	public static MovingQuaternionfLerp average(int size) {
		return new MovingQuaternionfLerp(size, 0);
	}

	public MovingQuaternionfLerp initialize(Quaternionfc q) {
		for(int i = 0; i < size; ++i) {
			qs[i].set(q);
		}
		return this;
	}

	public MovingQuaternionfLerp initialize(Quaterniondc q) {
		for(int i = 0; i < size; ++i) {
			qs[i].set(q);
		}
		return this;
	}

	public MovingQuaternionfLerp step() {
		int next = (index + 1) % size;
		qs[next].set(qs[index]);
		index = next;
		return this;
	}

	public MovingQuaternionfLerp step(Quaternionfc q) {
		int next = (index + 1) % size;
		qs[next].set(q);
		index = next;
		return this;
	}

	public MovingQuaternionfLerp step(Quaterniondc q) {
		int next = (index + 1) % size;
		qs[next].set(q);
		index = next;
		return this;
	}

	public Quaternionf nlerp(float t, Quaternionf dest) {
		for(int i = 0; i < tailSize; ++i) {
			tailWeights[i] = 1 + (i - t) / tailSize;
		}
		int next = (index + 1) % size;
		dest.set(qs[next]);
		float w = tailSize == 0 ? 1 : tailWeights[0];
		for(int i = 1; i < size; ++i) {
			int j = (next + i) % size;
			float w0 = w;
			float w1 = i < tailSize ? tailWeights[i] : i == size - 1 ? t : 1;
			float rw1 = w1 / (w0 + w1);
			w += w1;
			dest.nlerp(qs[j], rw1);
		}
		return dest;
	}

	public Quaternionf nlerp(float t) {
		return nlerp(t, new Quaternionf());
	}

	public Quaternionf nlerp(Quaternionf dest) {
		return nlerp(1, dest);
	}

	public Quaternionf nlerp() {
		return nlerp(1);
	}
}
