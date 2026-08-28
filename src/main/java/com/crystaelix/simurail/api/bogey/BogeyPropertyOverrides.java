package com.crystaelix.simurail.api.bogey;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundTag;

public final class BogeyPropertyOverrides {

	static final Object2IntMap<BogeyType> LOGICAL_AXLE_SPACING_OVERRIDE = new Object2IntOpenHashMap<>();
	static final Object2IntMap<BogeyType> AXLE_COUNT_OVERRIDE = new Object2IntOpenHashMap<>();
	static final Object2DoubleMap<BogeyType> VISUAL_AXLE_SPACING_OVERRIDE = new Object2DoubleOpenHashMap<>();
	static final Map<BogeyType, Function<CompoundTag, double[]>> AXLE_POSITIONS_OVERRIDE = new HashMap<>();
	static final Object2DoubleMap<BogeyType> WHEEL_RADIUS_OVERRIDE = new Object2DoubleOpenHashMap<>();
	static final Object2DoubleMap<BogeyType> TRACK_WIDTH_OVERRIDE = new Object2DoubleOpenHashMap<>();
	static final Object2DoubleMap<BogeyType> TRACK_HEIGHT_OVERRIDE = new Object2DoubleOpenHashMap<>();
	static final Object2ObjectMap<BogeyType, Set<TrackType>> TRACK_TYPES_OVERRIDE = new Object2ObjectOpenHashMap<>();
	static final Object2BooleanMap<BogeyType> GROUND_DRIVABLE_OVERRIDE = new Object2BooleanOpenHashMap<>();

	public static void setLogicalAxleSpacingOverride(BogeyType type, int logicalAxleSpacing) {
		LOGICAL_AXLE_SPACING_OVERRIDE.put(type, Math.max(logicalAxleSpacing, 1));
	}

	public static void setAxleCountOverride(BogeyType type, int axleCount) {
		AXLE_COUNT_OVERRIDE.put(type, axleCount);
	}

	public static void setVisualAxleSpacingOverride(BogeyType type, double visualAxleSpacing) {
		VISUAL_AXLE_SPACING_OVERRIDE.put(type, visualAxleSpacing);
	}

	public static void setAxlePositionsOverride(BogeyType type, Function<CompoundTag, double[]> axlePositions) {
		AXLE_POSITIONS_OVERRIDE.put(type, axlePositions);
	}

	public static void setAxlePositionsOverride(BogeyType type, double... axlePositions) {
		AXLE_POSITIONS_OVERRIDE.put(type, $ -> axlePositions);
	}

	public static void setWheelRadiusOverride(BogeyType type, double wheelRadius) {
		WHEEL_RADIUS_OVERRIDE.put(type, wheelRadius);
	}

	public static void setTrackWidthOverride(BogeyType type, double trackWidth) {
		TRACK_WIDTH_OVERRIDE.put(type, trackWidth);
	}

	public static void setTrackHeightOverride(BogeyType type, double trackHeight) {
		TRACK_HEIGHT_OVERRIDE.put(type, trackHeight);
	}

	public static void setTrackTypesOverride(BogeyType type, Set<TrackType> trackTypes) {
		TRACK_TYPES_OVERRIDE.put(type, trackTypes);
	}

	public static void setGroundDrivableOverride(BogeyType type, boolean groundDrivable) {
		GROUND_DRIVABLE_OVERRIDE.put(type, groundDrivable);
	}
}
