package com.crystaelix.simurail.api.math;

import org.joml.Vector3dc;

public record BezierControlPoints(Vector3dc end1, Vector3dc end2, Vector3dc handle1, Vector3dc handle2) {
}
