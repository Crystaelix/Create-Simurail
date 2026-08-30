package com.crystaelix.simurail.api.extension;

import com.crystaelix.simurail.api.math.CubicBezier3dc;

public interface BezierConnectionExtension {

	CubicBezier3dc simurail$controlPoints();

	double simurail$quadratureLength();

	void simurail$invalidateCurve();
}
