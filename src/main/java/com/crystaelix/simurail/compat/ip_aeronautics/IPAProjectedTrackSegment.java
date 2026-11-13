package com.crystaelix.simurail.compat.ip_aeronautics;

import java.util.Objects;

import com.crystaelix.simurail.api.track.TrackSegment;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import qouteall.imm_ptl.core.compat.sable_straddle.IPPortalIsometry;

public final class IPAProjectedTrackSegment extends TrackSegment {

	final IPPortalIsometry isometry;
	final TrackSegment segment;

	public IPAProjectedTrackSegment(ResourceKey<Level> dimension, IPPortalIsometry isometry, TrackSegment segment) {
		super(dimension,
				IPAPortalUtil.transformPositionInverse(segment.start(), isometry),
				IPAPortalUtil.transformPositionInverse(segment.end(), isometry),
				IPAPortalUtil.transformNormalInverse(segment.normal(), isometry),
				segment.material());
		this.isometry = isometry;
		this.segment = segment;
	}

	@Override
	public String type() {
		return "ipa_projected";
	}

	@Override
	public IPAProjectedTrackSegment reverse() {
		return new IPAProjectedTrackSegment(dimension, isometry, segment.reverse());
	}

	@Override
	public TrackNodeLocation edgeStart() {
		return segment.edgeStart();
	}

	@Override
	public TrackNodeLocation edgeEnd() {
		return segment.edgeEnd();
	}

	@Override
	public TrackEdge graphEdge(TrackGraph graph) {
		return segment.graphEdge(graph);
	}

	@Override
	public TrackSegment next(boolean reverse) {
		TrackSegment next = segment.next(reverse);
		return next == null ? null : new IPAProjectedTrackSegment(dimension, isometry, next);
	}

	@Override
	public TrackSegment next(TrackGraph graph, TrackEdge edge, boolean reverse) {
		TrackSegment next = segment.next(graph, edge, reverse);
		return next == null ? null : new IPAProjectedTrackSegment(dimension, isometry, next);
	}

	@Override
	public CompoundTag write(CompoundTag tag) {
		tag.put("isometry", IPAPortalUtil.writeIsometry(isometry));
		tag.put("segment", segment.write());
		return tag;
	}

	public static IPAProjectedTrackSegment read(ResourceKey<Level> dimension, CompoundTag tag) {
		IPPortalIsometry isometry = IPAPortalUtil.readIsometry(tag.getCompound("isometry"));
		TrackSegment segment = TrackSegment.read(tag.getCompound("segment"));
		return new IPAProjectedTrackSegment(dimension, isometry, segment);
	}

	@Override
	public int hashCode() {
		return Objects.hash(dimension, isometry, segment);
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj instanceof IPAProjectedTrackSegment other) {
			return dimension.equals(other.dimension) &&
					isometry.equals(other.isometry) &&
					segment.equals(other.segment);
		}
		return false;
	}
}
