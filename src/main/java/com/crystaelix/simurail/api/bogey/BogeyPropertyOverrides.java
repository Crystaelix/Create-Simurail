package com.crystaelix.simurail.api.bogey;

import java.util.Set;

import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public final class BogeyPropertyOverrides {

	static final Object2DoubleMap<BogeyType> WHEEL_SPACING_OVERRIDE = new Object2DoubleOpenHashMap<>();
	static final Object2DoubleMap<BogeyType> WHEEL_RADIUS_OVERRIDE = new Object2DoubleOpenHashMap<>();
	static final Object2ObjectMap<BogeyType, Set<TrackType>> TRACK_TYPES_OVERRIDE = new Object2ObjectOpenHashMap<>();
	static final Object2BooleanMap<BogeyType> GROUND_DRIVABLE_OVERRIDE = new Object2BooleanOpenHashMap<>();

	public static void setWheelSpacingOverride(BogeyType type, double wheelSpacing) {
		WHEEL_SPACING_OVERRIDE.put(type, wheelSpacing);
	}

	public static void setWheelRadiusOverride(BogeyType type, double wheelRadius) {
		WHEEL_RADIUS_OVERRIDE.put(type, wheelRadius);
	}

	public static void setTrackTypesOverride(BogeyType type, Set<TrackType> trackTypes) {
		TRACK_TYPES_OVERRIDE.put(type, trackTypes);
	}

	public static void setGroundDrivableOverride(BogeyType type, boolean groundDrivable) {
		GROUND_DRIVABLE_OVERRIDE.put(type, groundDrivable);
	}
}
