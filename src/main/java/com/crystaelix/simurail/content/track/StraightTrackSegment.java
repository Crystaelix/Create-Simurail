package com.crystaelix.simurail.content.track;

import java.util.Objects;

import org.joml.Vector3d;
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
import net.minecraft.world.phys.Vec3;

public final class StraightTrackSegment extends TrackSegment {

	static {
		READERS.put("straight", StraightTrackSegment::read);
	}

	final TrackNodeLocation trackStart;
	final TrackNodeLocation trackEnd;
	final double startOffset;
	final double endOffset;
	final boolean startPortal;
	final boolean endPortal;
	
	private static Vector3dc position(TrackNodeLocation trackStart, TrackNodeLocation trackEnd, double offset, boolean portal) {
		Vector3d start = JOMLConversion.toJOML(trackStart.getLocation());
		if(!portal) {
			return start.add(0, offset, 0);
		}
		Vec3 end = trackEnd.getLocation();
		Vector3d dir = new Vector3d(start.x - end.x, start.y - end.y, start.z - end.z).normalize();
		return start.add(0, offset, 0).add(dir);
	}

	public StraightTrackSegment(TrackNodeLocation trackStart, TrackNodeLocation trackEnd, double startOffset, double endOffset, boolean startPortal, boolean endPortal, Vector3dc normal, TrackMaterial material) {
		super(trackStart.dimension,
				position(trackStart, trackEnd, startOffset, startPortal),
				position(trackEnd, trackStart,  endOffset, endPortal),
				normal,
				material);
		this.trackStart = trackStart;
		this.trackEnd = trackEnd;
		this.startOffset = startOffset;
		this.endOffset = endOffset;
		this.startPortal = startPortal;
		this.endPortal = endPortal;
	}

	@Override
	public String type() {
		return "straight";
	}

	@Override
	public StraightTrackSegment reverse() {
		return new StraightTrackSegment(trackEnd, trackStart, endOffset, startOffset, endPortal, startPortal, normal, material);
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
	public TrackSegment next(boolean reverse) {
		return null;
	}

	@Override
	public CompoundTag write(CompoundTag tag) {
		tag.put("start", trackStart.write(null));
		tag.put("end", trackEnd.write(null));
		tag.putDouble("start_offset", startOffset);
		tag.putDouble("end_offset", endOffset);
		tag.putBoolean("start_portal", startPortal);
		tag.putBoolean("end_portal", endPortal);
		tag.put("normal", SableNBTUtils.writeVector3d(normal));
		tag.putString("material", material().id.toString());
		return tag;
	}

	public static StraightTrackSegment read(ResourceKey<Level> dimension, CompoundTag tag) {
		TrackNodeLocation trackStart = TrackNodeLocation.read(tag.getCompound("start"), null);
		TrackNodeLocation trackEnd = TrackNodeLocation.read(tag.getCompound("end"), null);
		double startOffset = tag.getDouble("start_offset");
		double endOffset = tag.getDouble("end_offset");
		boolean startPortal = tag.getBoolean("start_portal");
		boolean endPortal = tag.getBoolean("end_portal");
		Vector3dc trackNormal = SableNBTUtils.readVector3d(tag.getCompound("normal"));
		TrackMaterial material = TrackMaterial.deserialize(tag.getString("material"));
		trackStart.dimension = dimension;
		trackEnd.dimension = dimension;
		return new StraightTrackSegment(trackStart, trackEnd, startOffset, endOffset, startPortal, endPortal, trackNormal, material);
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
