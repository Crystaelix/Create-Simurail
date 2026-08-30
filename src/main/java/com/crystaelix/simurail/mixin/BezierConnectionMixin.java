package com.crystaelix.simurail.mixin;

import java.util.concurrent.atomic.AtomicReference;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.crystaelix.simurail.api.extension.BezierConnectionExtension;
import com.crystaelix.simurail.api.math.CubicBezier3dc;
import com.crystaelix.simurail.api.math.SimurailMath;
import com.simibubi.create.content.trains.track.BezierConnection;

@Mixin(BezierConnection.class)
public abstract class BezierConnectionMixin implements BezierConnectionExtension {

	@Shadow
	public abstract double getHandleLength();

	@Shadow
	@Final
	private AtomicReference<Object> lazyRuntime;

	@Unique
	private volatile CubicBezier3dc controlPoints;
	@Unique
	private volatile double quadratureLength;

	@Override
	public CubicBezier3dc simurail$controlPoints() {
		CubicBezier3dc points = controlPoints;
		if(points == null) {
			points = SimurailMath.controlPoints(BezierConnection.class.cast(this));
			controlPoints = points;
		}
		return points;
	}

	@Override
	public double simurail$quadratureLength() {
		double length = quadratureLength;
		if(length == 0) {
			length = simurail$controlPoints().length(0, 1);
			quadratureLength = length;
		}
		return length;
	}

	@Override
	public void simurail$invalidateCurve() {
		lazyRuntime.set(null);
		controlPoints = null;
		quadratureLength = 0;
	}
}
