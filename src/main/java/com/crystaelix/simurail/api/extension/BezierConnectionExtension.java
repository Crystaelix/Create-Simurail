package com.crystaelix.simurail.api.extension;

import com.crystaelix.simurail.api.math.BezierControlPoints;

public interface BezierConnectionExtension {

	BezierControlPoints simurail$controlPoints();

	double simurail$quadratureLength();
}
