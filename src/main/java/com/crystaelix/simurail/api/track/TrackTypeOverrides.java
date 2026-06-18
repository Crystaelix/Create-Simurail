package com.crystaelix.simurail.api.track;

import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;

public class TrackTypeOverrides {

	private static final Object2ObjectMap<TrackMaterial, TrackType> TRACK_TYPE_OVERRIDE = new Object2ObjectOpenHashMap<>();
	private static final ObjectSet<TrackType> UNIVERSAL_TRACK_TYPES = new ObjectOpenHashSet<>();

	public static void setOverride(TrackMaterial material, TrackType type) {
		TRACK_TYPE_OVERRIDE.put(material, type);
	}

	public static void setUniversal(TrackType type) {
		UNIVERSAL_TRACK_TYPES.add(type);
	}

	public static TrackType getTrackType(TrackMaterial material) {
		if(TRACK_TYPE_OVERRIDE.containsKey(material)) {
			return TRACK_TYPE_OVERRIDE.get(material);
		}
		return material.trackType;
	}

	public static boolean isUniversal(TrackType type) {
		return UNIVERSAL_TRACK_TYPES.contains(type);
	}
}
