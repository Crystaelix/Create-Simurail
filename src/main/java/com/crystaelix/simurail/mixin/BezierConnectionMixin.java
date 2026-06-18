package com.crystaelix.simurail.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.crystaelix.simurail.api.extension.BezierConnectionExtension;
import com.crystaelix.simurail.api.math.BezierControlPoints;
import com.crystaelix.simurail.api.math.SimurailMath;
import com.simibubi.create.content.trains.track.BezierConnection;

@Mixin(BezierConnection.class)
public abstract class BezierConnectionMixin implements BezierConnectionExtension {

	@Shadow
	public abstract double getHandleLength();

	@Unique
	private BezierControlPoints controlPoints;
	@Unique
	private double quadratureLength;

	@Override
	public BezierControlPoints simurail$controlPoints() {
		if(controlPoints == null) {
			controlPoints = SimurailMath.controlPoints(BezierConnection.class.cast(this));
		}
		return controlPoints;
	}

	@Override
	public double simurail$quadratureLength() {
		if(quadratureLength == 0) {
			quadratureLength = SimurailMath.bezierLength(simurail$controlPoints(), 0, 1);
		}
		return 0;
	}
}
