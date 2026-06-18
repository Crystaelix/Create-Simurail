package com.crystaelix.simurail.content.track;

import java.util.Objects;

import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.content.trains.track.TrackMaterial;

import dev.ryanhcode.sable.companion.math.JOMLConversion;
import net.createmod.catnip.data.Couple;

public final class StraightTrackSegment extends TrackSegment {

	final TrackNodeLocation trackStart;
	final TrackNodeLocation trackEnd;

	public StraightTrackSegment(TrackNodeLocation trackStart, TrackNodeLocation trackEnd, TrackMaterial material) {
		super(trackStart.dimension,
				JOMLConversion.toJOML(trackStart.getLocation()),
				JOMLConversion.toJOML(trackEnd.getLocation()),
				material);
		this.trackStart = trackStart;
		this.trackEnd = trackEnd;
	}

	@Override
	public TrackNodeLocation edgeStart() {
		return trackStart;
	}

	@Override
	public TrackNodeLocation edgeEnd() {
		return trackEnd;
	}

	@Override
	public TrackEdge graphEdge(TrackGraph graph) {
		TrackNode startNode = graph.locateNode(trackStart);
		TrackNode endNode = graph.locateNode(trackEnd);
		if(startNode != null && endNode != null) {
			TrackEdge edge = graph.getConnection(Couple.create(startNode, endNode));
			if(edge != null && !edge.isTurn()) {
				return edge;
			}
		}
		return null;
	}

	@Override
	public int hashCode() {
		return Objects.hash(start, end, material);
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj instanceof StraightTrackSegment other) {
			return start.equals(other.start) &&
					end.equals(other.end) &&
					material == other.material;
		}
		return false;
	}
}
