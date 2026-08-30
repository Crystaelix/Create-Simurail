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
import net.minecraft.world.phys.Vec3;

public final class CurvedTrackSegment extends TrackSegment {

	final BezierConnection curve;
	final int segment;
	final boolean reversed;

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

	// A curve's secondary resolves to a different shape than its primary, so always anchor on the primary
	private record Form(BezierConnection curve, int segment, boolean reversed) {

		static Form of(BezierConnection curve, int segment, boolean reversed) {
			if(curve.isPrimary()) {
				return new Form(curve, segment, reversed);
			}
			// Segment counts differ between the two, so remap through t
			BezierConnection primary = curve.secondary();
			int primaryCount = primary.getSegmentCount();
			double t = 1 - (segment + 0.5) / curve.getSegmentCount();
			int primarySegment = Math.clamp((int)(t * primaryCount), 0, Math.max(primaryCount - 1, 0));
			return new Form(primary, primarySegment, !reversed);
		}
	}

	public CurvedTrackSegment(ResourceKey<Level> dimension, BezierConnection curve, int segment) {
		this(dimension, curve, segment, false);
	}

	public CurvedTrackSegment(ResourceKey<Level> dimension, BezierConnection curve, int segment, boolean reversed) {
		this(dimension, Form.of(curve, segment, reversed));
	}

	private CurvedTrackSegment(ResourceKey<Level> dimension, Form form) {
		super(dimension,
				curvePosition(form.curve(), segmentT(form.reversed() ? form.segment() + 1 : form.segment(), form.curve())),
				curvePosition(form.curve(), segmentT(form.reversed() ? form.segment() : form.segment() + 1, form.curve())),
				curveNormal(form.curve(), segmentT(form.segment() + 0.5, form.curve())),
				form.curve().getMaterial());
		this.curve = form.curve();
		this.segment = form.segment();
		this.reversed = form.reversed();

		Vec3 startEnd = reversed ? curve.starts.getSecond() : curve.starts.getFirst();
		Vec3 endEnd = reversed ? curve.starts.getFirst() : curve.starts.getSecond();
		curveStart = new TrackNodeLocation(startEnd).in(dimension);
		curveStart.yOffsetPixels = curve.yOffsetAt(startEnd);
		curveEnd = new TrackNodeLocation(endEnd).in(dimension);
		curveEnd.yOffsetPixels = curve.yOffsetAt(endEnd);
	}

	public BezierConnection curve() {
		return curve;
	}

	public int segment() {
		return segment;
	}

	public boolean reversed() {
		return reversed;
	}

	public double curveT(double t) {
		int iterations = 2;
		return SimurailMath.segmentToCurveT(curve,
				segmentT(segment, curve), segmentT(segment + 1, curve),
				reversed ? 1 - t : t, iterations);
	}

	@Override
	public CurvedTrackSegment reverse() {
		return new CurvedTrackSegment(dimension, curve, segment, !reversed);
	}

	@Override
	public Frame3d frame(double t, Frame3d dest) {
		double curveT = curveT(t);

		SimurailMath.velocity(curve, curveT, dest.direction);
		if(reversed) {
			dest.direction.negate();
		}

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
			if(edge != null && edge.isTurn()) {
				BezierConnection turn = edge.getTurn();
				if(!turn.isPrimary()) {
					turn = turn.secondary();
				}
				if(BezierHashStrategy.INSTANCE.equals(curve, turn)) {
					return edge;
				}
			}
		}
		return null;
	}

	public CurvedTrackSegment next(boolean reverse) {
		int nextSegment = segment + (reverse != reversed ? -1 : 1);
		if(nextSegment < 0 || nextSegment >= curve.getSegmentCount()) {
			return null;
		}
		return new CurvedTrackSegment(dimension, curve, nextSegment, reversed);
	}

	@Override
	public int hashCode() {
		return Objects.hash(dimension, BezierHashStrategy.INSTANCE.hashCode(curve), segment, reversed);
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
					reversed == other.reversed &&
					material == other.material;
		}
		return false;
	}
}
