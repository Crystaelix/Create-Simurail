package com.crystaelix.simurail.content.track;

import java.util.Objects;

import com.simibubi.create.content.trains.track.BezierConnection;

import it.unimi.dsi.fastutil.Hash;

public enum BezierHashStrategy implements Hash.Strategy<BezierConnection> {
	INSTANCE;

	@Override
	public int hashCode(BezierConnection o) {
		return o == null ? 0 : Objects.hash(o.bePositions, o.starts, o.axes, o.normals, o.smoothing);
	}

	@Override
	public boolean equals(BezierConnection a, BezierConnection b) {
		if(a == b) {
			return true;
		}
		if(a == null || b == null) {
			return false;
		}
		return a.bePositions.equals(b.bePositions) &&
				a.starts.equals(b.starts) &&
				a.axes.equals(b.axes) &&
				a.normals.equals(b.normals) &&
				Objects.equals(a.smoothing, b.smoothing);
	}
}
