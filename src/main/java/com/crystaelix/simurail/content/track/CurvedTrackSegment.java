package com.crystaelix.simurail.content.track;

import java.util.Objects;

import org.joml.Vector3d;

import com.crystaelix.simurail.api.math.Frame3d;
import com.crystaelix.simurail.api.math.SimurailMath;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.content.trains.track.BezierConnection;

import dev.ryanhcode.sable.companion.math.JOMLConversion;
import net.createmod.catnip.data.Couple;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public final class CurvedTrackSegment extends TrackSegment {

	final BezierConnection curve;
	final int segment;

	final TrackNodeLocation curveStart;
	final TrackNodeLocation curveEnd;

	public static double segmentT(double segment, BezierConnection curve) {
		return segment / curve.getSegmentCount();
	}

	private static Vector3d curvePosition(BezierConnection curve, double t) {
		return SimurailMath.position(curve, t, new Vector3d());
	}

	private static Vector3d curveNormal(BezierConnection curve, double t) {
		Vector3d direction = SimurailMath.velocity(curve, t, new Vector3d());
		Vector3d vertical = SimurailMath.slerp(
				JOMLConversion.toJOML(curve.normals.getFirst()),
				JOMLConversion.toJOML(curve.normals.getSecond()), t, new Vector3d());
		Vector3d lateral = direction.cross(vertical, new Vector3d());
		return lateral.cross(direction, vertical).normalize();
	}

	public CurvedTrackSegment(ResourceKey<Level> dimension, BezierConnection curve, int segment) {
		super(dimension,
				curvePosition(curve, segmentT(segment, curve)),
				curvePosition(curve, segmentT(segment + 1, curve)),
				curveNormal(curve, segmentT(segment + 0.5, curve)),
				curve.getMaterial());
		this.curve = curve;
		this.segment = segment;

		curveStart = new TrackNodeLocation(curve.starts.getFirst()).in(dimension);
		curveStart.yOffsetPixels = curve.yOffsetAt(curve.starts.getFirst());
		curveEnd = new TrackNodeLocation(curve.starts.getSecond()).in(dimension);
		curveEnd.yOffsetPixels = curve.yOffsetAt(curve.starts.getSecond());
	}

	public BezierConnection curve() {
		return curve;
	}

	public int segment() {
		return segment;
	}

	public double curveT(double t) {
		int iterations = 2;
		return SimurailMath.segmentToCurveT(curve, segmentT(segment, curve), segmentT(segment + 1, curve), t, iterations);
	}

	@Override
	public CurvedTrackSegment reverse() {
		return new CurvedTrackSegment(dimension, curve.secondary(), curve.getSegmentCount() - segment - 1);
	}

	@Override
	public Frame3d frame(double t, Frame3d dest) {
		double curveT = curveT(t);

		SimurailMath.velocity(curve, curveT, dest.direction);

		Vector3d normal1 = JOMLConversion.toJOML(curve.normals.getFirst());
		Vector3d normal2 = JOMLConversion.toJOML(curve.normals.getSecond());
		SimurailMath.slerp(normal1, normal2, curveT, dest.vertical);

		dest.direction.cross(dest.vertical, dest.lateral);
		dest.lateral.cross(dest.direction, dest.vertical);

		SimurailMath.position(curve, curveT, dest.position);

		return dest.normalize();
	}

	@Override
	public Vector3d curvature(double t, Vector3d dest) {
		return SimurailMath.curvature(curve, curveT(t), dest);
	}

	@Override
	public TrackNodeLocation edgeStart() {
		return curveStart;
	}

	@Override
	public TrackNodeLocation edgeEnd() {
		return curveEnd;
	}

	@Override
	public TrackEdge graphEdge(TrackGraph graph) {
		TrackNode startNode = graph.locateNode(curveStart);
		TrackNode endNode = graph.locateNode(curveEnd);
		if(startNode != null && endNode != null) {
			TrackEdge edge = graph.getConnection(Couple.create(startNode, endNode));
			if(edge != null && edge.isTurn() && BezierHashStrategy.INSTANCE.equals(curve, edge.getTurn())) {
				return edge;
			}
		}
		return null;
	}

	public CurvedTrackSegment next(boolean reverse) {
		int nextSegment = segment + (reverse ? -1 : 1);
		if(nextSegment < 0 || nextSegment >= curve.getSegmentCount()) {
			return null;
		}
		return new CurvedTrackSegment(dimension, curve, nextSegment);
	}

	@Override
	public int hashCode() {
		return Objects.hash(dimension, BezierHashStrategy.INSTANCE.hashCode(curve), segment);
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj instanceof CurvedTrackSegment other) {
			return dimension.equals(other.dimension) &&
					BezierHashStrategy.INSTANCE.equals(curve, other.curve) &&
					segment == other.segment &&
					material == other.material;
		}
		return false;
	}
}
