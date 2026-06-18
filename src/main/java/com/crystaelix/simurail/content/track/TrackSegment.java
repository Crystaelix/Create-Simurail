package com.crystaelix.simurail.content.track;

import org.joml.Vector3d;
import org.joml.Vector3dc;

import com.crystaelix.simurail.api.math.Basis3d;
import com.crystaelix.simurail.api.math.Basis3dc;
import com.crystaelix.simurail.api.math.Frame3d;
import com.crystaelix.simurail.api.math.SimurailMath;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackMaterial;

import net.createmod.catnip.data.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public sealed abstract class TrackSegment permits StraightTrackSegment, CurvedTrackSegment {

	final ResourceKey<Level> dimension;
	final Vector3dc start;
	final Vector3dc end;
	final TrackMaterial material;

	final Vector3dc delta;
	final Basis3dc basis;

	public TrackSegment(ResourceKey<Level> dimension, Vector3dc start, Vector3dc end, TrackMaterial material) {
		this.dimension = dimension;
		this.start = start;
		this.end = end;
		this.material = material;

		delta = new Vector3d(end).sub(start);
		basis = new Basis3d().orthogonalized(delta, SimurailMath.DIR_YP);
	}

	public ResourceKey<Level> dimension() {
		return dimension;
	}

	public Vector3dc start() {
		return start;
	}

	public Vector3dc end() {
		return end;
	}

	public Vector3dc delta() {
		return delta;
	}

	public Basis3dc basis() {
		return basis;
	}

	public Frame3d frame(double t, Frame3d dest) {
		dest.basis(basis);
		start().lerp(end(), t, dest.position);
		return dest;
	}

	public Vector3d curvature(double t, Vector3d dest) {
		return dest.zero();
	}

	public double lengthSquared() {
		return delta().lengthSquared();
	}

	public double length() {
		return delta().length();
	}

	public double distanceSquared(Vector3dc localPosition) {
		return SimurailMath.distanceSquaredLinePoint(start(), delta(), localPosition);
	}

	public double projectT(Vector3dc localPosition) {
		return SimurailMath.projectTLinePoint(start(), delta(), localPosition);
	}

	public double verticalDistance(Vector3dc localPosition) {
		return SimurailMath.projectTLinePoint(start(), basis().vertical(), localPosition);
	}

	public double lateralDistance(Vector3dc localPosition) {
		return SimurailMath.projectTLinePoint(start(), basis().lateral(), localPosition);
	}

	public TrackMaterial material() {
		return material;
	}

	public abstract TrackNodeLocation edgeStart();

	public abstract TrackNodeLocation edgeEnd();

	public Pair<TrackGraph, TrackEdge> graphConnection() {
		for(TrackGraph graph : Create.RAILWAYS.getGraphs(null, edgeStart())) {
			TrackEdge edge = graphEdge(graph);
			if(edge != null) {
				return Pair.of(graph, edge);
			}
		}
		return null;
	}

	public abstract TrackEdge graphEdge(TrackGraph graph);

	public boolean inLineRange(Vector3dc localPosition, Vector3dc localVertical, boolean inverted) {
		return inLineRange(start(), basis().vertical(), basis().lateral(), localPosition, localVertical, inverted);
	}

	public boolean inProjectionRange(Vector3dc localPosition, Vector3dc localVertical) {
		return inProjectionRange(start(), delta(), basis().vertical(), localPosition, localVertical);
	}

	public boolean inSegmentRange(Vector3dc localPosition, Vector3dc localVertical, boolean inverted) {
		return inSegmentRange(start(), delta(), basis().vertical(), basis().lateral(), localPosition, localVertical, inverted);
	}

	public static boolean inLineRange(Vector3dc segmentStart, Vector3dc segmentVertical, Vector3dc segmentLateral, Vector3dc localPosition, Vector3dc localVertical, boolean inverted) {
		double vCos = segmentVertical.dot(localVertical);
		if(vCos < 0.5) {
			return false;
		}
		double vDist = SimurailMath.projectTLinePoint(segmentStart, segmentVertical, localPosition);
		if(inverted ? (vDist < -0.4375 || vDist > 1.5) : (vDist < -1.5 || vDist > 0.375)) {
			return false;
		}
		double lDist = SimurailMath.projectTLinePoint(segmentStart, segmentLateral, localPosition);
		if(lDist < -0.5 || lDist > 0.5) {
			return false;
		}
		return true;
	}

	public static boolean inProjectionRange(Vector3dc segmentStart, Vector3dc segmentDelta, Vector3dc segmentVertical, Vector3dc localPosition, Vector3dc localVertical) {
		double t = SimurailMath.intersectPlaneLine(localPosition, localVertical, segmentStart, segmentVertical);
		if(Double.isNaN(t)) {
			return false;
		}
		Vector3d i = new Vector3d(localPosition).fma(t, localVertical);
		double projT = SimurailMath.projectTLinePoint(segmentStart, segmentDelta, i);
		return projT >= -1/32D && projT <= 1 + 1/32D;
	}

	public static boolean inSegmentRange(Vector3dc segmentStart, Vector3dc segmentDelta, Vector3dc segmentVertical, Vector3dc segmentLateral, Vector3dc localPosition, Vector3dc localVertical, boolean inverted) {
		return inProjectionRange(segmentStart, segmentDelta, segmentVertical, localPosition, localVertical) &&
				inLineRange(segmentStart, segmentVertical, segmentLateral, localPosition, localVertical, inverted);
	}

	public CompoundTag write() {
		CompoundTag tag = new CompoundTag();
		switch(this) {
		case StraightTrackSegment straight -> {
			tag.putString("type", "straight");
			tag.put("start", straight.edgeStart().write(null));
			tag.put("end", straight.edgeEnd().write(null));
			tag.putString("material", material().id.toString());
		}
		case CurvedTrackSegment curve -> {
			tag.putString("type", "curved");
			tag.put("curve", curve.curve().write(BlockPos.ZERO));
			tag.putInt("segment", curve.segment());
		}
		}
		tag.putString("dimension", dimension().location().toString());
		return tag;
	}

	public static TrackSegment read(CompoundTag tag) {
		String type = tag.getString("type");
		ResourceLocation dimensionKey = ResourceLocation.tryParse(tag.getString("dimension"));
		ResourceKey<Level> dimension = dimensionKey == null ? Level.OVERWORLD : ResourceKey.create(Registries.DIMENSION, dimensionKey);
		switch(type) {
		case "straight" -> {
			TrackNodeLocation trackStart = TrackNodeLocation.read(tag.getCompound("start"), null);
			TrackNodeLocation trackEnd = TrackNodeLocation.read(tag.getCompound("end"), null);
			TrackMaterial material = TrackMaterial.deserialize(tag.getString("material"));
			trackStart.dimension = dimension;
			trackEnd.dimension = dimension;
			return new StraightTrackSegment(trackStart, trackEnd, material);
		}
		case "curved" -> {
			BezierConnection curve = new BezierConnection(tag.getCompound("curve"), BlockPos.ZERO);
			int segment = tag.getInt("segment");
			return new CurvedTrackSegment(dimension, curve, segment);
		}
		}
		return null;
	}
}
