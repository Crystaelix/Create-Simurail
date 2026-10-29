package com.crystaelix.simurail.content.track;

import java.util.Objects;

import org.joml.Vector3dc;

import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.content.trains.track.TrackMaterial;

import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.util.SableNBTUtils;
import net.createmod.catnip.data.Couple;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public final class StraightTrackSegment extends TrackSegment {

	static {
		READERS.put("straight", StraightTrackSegment::read);
	}

	final TrackNodeLocation trackStart;
	final TrackNodeLocation trackEnd;
	final double startOffset;
	final double endOffset;

	public StraightTrackSegment(TrackNodeLocation trackStart, TrackNodeLocation trackEnd, double startOffset, double endOffset, Vector3dc normal, TrackMaterial material) {
		super(trackStart.dimension,
				JOMLConversion.toJOML(trackStart.getLocation()).add(0, startOffset, 0),
				JOMLConversion.toJOML(trackEnd.getLocation()).add(0, endOffset, 0),
				normal,
				material);
		this.trackStart = trackStart;
		this.trackEnd = trackEnd;
		this.startOffset = startOffset;
		this.endOffset = endOffset;
	}

	@Override
	public String type() {
		return "straight";
	}

	@Override
	public StraightTrackSegment reverse() {
		return new StraightTrackSegment(trackEnd, trackStart, endOffset, startOffset, normal, material);
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
	protected CompoundTag write(CompoundTag tag) {
		tag.put("start", trackStart.write(null));
		tag.put("end", trackEnd.write(null));
		tag.put("normal", SableNBTUtils.writeVector3d(normal));
		tag.putString("material", material().id.toString());
		return tag;
	}

	public static StraightTrackSegment read(ResourceKey<Level> dimension, CompoundTag tag) {
		TrackNodeLocation trackStart = TrackNodeLocation.read(tag.getCompound("start"), null);
		TrackNodeLocation trackEnd = TrackNodeLocation.read(tag.getCompound("end"), null);
		double startOffset = tag.getDouble("start_offset");
		double endOffset = tag.getDouble("end_offset");
		Vector3dc trackNormal = SableNBTUtils.readVector3d(tag.getCompound("normal"));
		TrackMaterial material = TrackMaterial.deserialize(tag.getString("material"));
		trackStart.dimension = dimension;
		trackEnd.dimension = dimension;
		return new StraightTrackSegment(trackStart, trackEnd, startOffset, endOffset, trackNormal, material);
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
