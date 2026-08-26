package com.crystaelix.simurail.api.bogey;

import java.util.Set;

import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public final class BogeyPropertyOverrides {

	static final Object2DoubleMap<BogeyType> WHEEL_SPACING_OVERRIDE = new Object2DoubleOpenHashMap<>();
	static final Object2DoubleMap<BogeyType> WHEEL_RADIUS_OVERRIDE = new Object2DoubleOpenHashMap<>();
	static final Object2DoubleMap<BogeyType> AXLE_SPACING_OVERRIDE = new Object2DoubleOpenHashMap<>();
	static final Object2IntMap<BogeyType> AXLE_COUNT_OVERRIDE = new Object2IntOpenHashMap<>();
	static final Object2ObjectMap<BogeyType, BogeyAxlePositions> AXLE_POSITIONS_OVERRIDE = new Object2ObjectOpenHashMap<>();
	static final Object2DoubleMap<BogeyType> WHEEL_GAUGE_OVERRIDE = new Object2DoubleOpenHashMap<>();
	static final Object2DoubleMap<BogeyType> WHEEL_WIDTH_OVERRIDE = new Object2DoubleOpenHashMap<>();
	static final Object2DoubleMap<BogeyType> WHEEL_CENTER_HEIGHT_OVERRIDE = new Object2DoubleOpenHashMap<>();
	static final Object2DoubleMap<BogeyType> WHEEL_OUTER_RADIUS_OVERRIDE = new Object2DoubleOpenHashMap<>();
	static final Object2ObjectMap<BogeyType, Set<TrackType>> TRACK_TYPES_OVERRIDE = new Object2ObjectOpenHashMap<>();
	static final Object2BooleanMap<BogeyType> GROUND_DRIVABLE_OVERRIDE = new Object2BooleanOpenHashMap<>();

	public static void setWheelSpacingOverride(BogeyType type, double wheelSpacing) {
		WHEEL_SPACING_OVERRIDE.put(type, wheelSpacing);
	}

	public static void setWheelRadiusOverride(BogeyType type, double wheelRadius) {
		WHEEL_RADIUS_OVERRIDE.put(type, wheelRadius);
	}

	/**
	 * Outermost wheelsets position.
	 */
	public static void setAxleSpacingOverride(BogeyType type, double axleSpacing) {
		AXLE_SPACING_OVERRIDE.put(type, axleSpacing);
	}

	public static void setAxleCountOverride(BogeyType type, int axleCount) {
		AXLE_COUNT_OVERRIDE.put(type, axleCount);
	}

	/**
	 * Position where the wheelsets of the bogey are drawn along it, for a style that does not simply spread
	 * {@link #setAxleCountOverride} evenly over {@link #setAxleSpacingOverride}.
	 */
	public static void setAxlePositionsOverride(BogeyType type, BogeyAxlePositions axlePositions) {
		AXLE_POSITIONS_OVERRIDE.put(type, axlePositions);
	}

	public static void setAxlePositionsOverride(BogeyType type, double... axlePositions) {
		AXLE_POSITIONS_OVERRIDE.put(type, extra -> axlePositions);
	}

	public static void setWheelGaugeOverride(BogeyType type, double wheelGauge) {
		WHEEL_GAUGE_OVERRIDE.put(type, wheelGauge);
	}

	public static void setWheelWidthOverride(BogeyType type, double wheelWidth) {
		WHEEL_WIDTH_OVERRIDE.put(type, wheelWidth);
	}

	public static void setWheelCenterHeightOverride(BogeyType type, double wheelCenterHeight) {
		WHEEL_CENTER_HEIGHT_OVERRIDE.put(type, wheelCenterHeight);
	}

	public static void setWheelOuterRadiusOverride(BogeyType type, double wheelOuterRadius) {
		WHEEL_OUTER_RADIUS_OVERRIDE.put(type, wheelOuterRadius);
	}

	public static void setTrackTypesOverride(BogeyType type, Set<TrackType> trackTypes) {
		TRACK_TYPES_OVERRIDE.put(type, trackTypes);
	}

	public static void setGroundDrivableOverride(BogeyType type, boolean groundDrivable) {
		GROUND_DRIVABLE_OVERRIDE.put(type, groundDrivable);
	}
}
