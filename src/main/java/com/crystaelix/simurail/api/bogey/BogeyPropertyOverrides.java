package com.crystaelix.simurail.api.bogey;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundTag;

public final class BogeyPropertyOverrides {

	static final Object2ObjectMap<BogeyType, ToIntFunction<CompoundTag>> LOGICAL_AXLE_SPACING_OVERRIDE = new Object2ObjectOpenHashMap<>();
	static final Object2ObjectMap<BogeyType, ToDoubleFunction<CompoundTag>> VISUAL_AXLE_SPACING_OVERRIDE = new Object2ObjectOpenHashMap<>();
	static final Object2ObjectMap<BogeyType, ToIntFunction<CompoundTag>> AXLE_COUNT_OVERRIDE = new Object2ObjectOpenHashMap<>();
	static final Object2ObjectMap<BogeyType, Function<CompoundTag, double[]>> AXLE_POSITIONS_OVERRIDE = new Object2ObjectOpenHashMap<>();
	static final Object2ObjectMap<BogeyType, ToDoubleFunction<CompoundTag>> WHEEL_RADIUS_OVERRIDE = new Object2ObjectOpenHashMap<>();
	static final Object2ObjectMap<BogeyType, ConnectorAnchorOffsetOverride> CONNECTOR_ANCHOR_OFFSET_OVERRIDE = new Object2ObjectOpenHashMap<>();
	static final Object2ObjectMap<BogeyType, Set<TrackType>> TRACK_TYPES_OVERRIDE = new Object2ObjectOpenHashMap<>();
	static final Object2DoubleMap<BogeyType> TRACK_WIDTH_OVERRIDE = new Object2DoubleOpenHashMap<>();
	static final Object2DoubleMap<BogeyType> TRACK_HEIGHT_OVERRIDE = new Object2DoubleOpenHashMap<>();
	static final Object2ObjectMap<BogeyType, Predicate<CompoundTag>> GROUND_DRIVABLE_OVERRIDE = new Object2ObjectOpenHashMap<>();

	public static void setLogicalAxleSpacingOverride(BogeyType type, ToIntFunction<CompoundTag> logicalAxleSpacing) {
		LOGICAL_AXLE_SPACING_OVERRIDE.put(type, clampMin(logicalAxleSpacing, 1));
	}

	public static void setLogicalAxleSpacingOverride(BogeyType type, int logicalAxleSpacing) {
		int v = Math.max(logicalAxleSpacing, 1);
		LOGICAL_AXLE_SPACING_OVERRIDE.put(type, $ -> v);
	}

	public static void setVisualAxleSpacingOverride(BogeyType type, ToDoubleFunction<CompoundTag> visualAxleSpacing) {
		VISUAL_AXLE_SPACING_OVERRIDE.put(type, clampMin(visualAxleSpacing, 0));
	}

	public static void setVisualAxleSpacingOverride(BogeyType type, double visualAxleSpacing) {
		double v = Math.max(visualAxleSpacing, 0);
		VISUAL_AXLE_SPACING_OVERRIDE.put(type, $ -> v);
	}

	public static void setAxleSpacingOverride(BogeyType type, ToDoubleFunction<CompoundTag> axleSpacing) {
		setLogicalAxleSpacingOverride(type, roundToInt(axleSpacing));
		setVisualAxleSpacingOverride(type, axleSpacing);
	}

	public static void setAxleSpacingOverride(BogeyType type, double axleSpacing) {
		setLogicalAxleSpacingOverride(type, (int)Math.round(axleSpacing));
		setVisualAxleSpacingOverride(type, axleSpacing);
	}

	public static void setAxleCountOverride(BogeyType type, ToIntFunction<CompoundTag> axleCount) {
		AXLE_COUNT_OVERRIDE.put(type, clampMin(axleCount, 0));
	}

	public static void setAxleCountOverride(BogeyType type, int axleCount) {
		int v = Math.max(axleCount, 0);
		AXLE_COUNT_OVERRIDE.put(type, $ -> v);
	}

	public static void setAxlePositionsOverride(BogeyType type, Function<CompoundTag, double[]> axlePositions) {
		AXLE_POSITIONS_OVERRIDE.put(type, axlePositions);
	}

	public static void setAxlePositionsOverride(BogeyType type, double... axlePositions) {
		AXLE_POSITIONS_OVERRIDE.put(type, $ -> axlePositions);
	}

	public static void setWheelRadiusOverride(BogeyType type, ToDoubleFunction<CompoundTag> wheelRadius) {
		WHEEL_RADIUS_OVERRIDE.put(type, clampMin(wheelRadius, 0));
	}

	public static void setWheelRadiusOverride(BogeyType type, double wheelRadius) {
		double v = Math.max(wheelRadius, 0);
		WHEEL_RADIUS_OVERRIDE.put(type, $ -> v);
	}

	public static void setConnectorAnchorOffsetOverride(BogeyType type, ConnectorAnchorOffsetOverride connectorAnchorOffset) {
		CONNECTOR_ANCHOR_OFFSET_OVERRIDE.put(type, connectorAnchorOffset);
	}

	public static void setConnectorAnchorOffsetOverride(BogeyType type, BiFunction<CompoundTag, Vector3f, Vector3f> normalOffset, BiFunction<CompoundTag, Vector3f, Vector3f> invertedOffset) {
		CONNECTOR_ANCHOR_OFFSET_OVERRIDE.put(type, (inverted, tag, dest) -> (inverted ? invertedOffset : normalOffset).apply(tag, dest));
	}

	public static void setConnectorAnchorOffsetOverride(BogeyType type, BiFunction<CompoundTag, Vector3f, Vector3f> connectorAnchorOffset) {
		CONNECTOR_ANCHOR_OFFSET_OVERRIDE.put(type, ($, tag, dest) -> connectorAnchorOffset.apply(tag, dest));
	}

	public static void setConnectorAnchorOffsetOverride(BogeyType type, Vector3fc normalOffset, Vector3fc invertedOffset) {
		CONNECTOR_ANCHOR_OFFSET_OVERRIDE.put(type, (inverted, $, dest) -> dest.set(inverted ? invertedOffset : normalOffset));
	}

	public static void setConnectorAnchorOffsetOverride(BogeyType type, Vector3fc connectorAnchorOffset) {
		CONNECTOR_ANCHOR_OFFSET_OVERRIDE.put(type, ($1, $2, dest) -> dest.set(connectorAnchorOffset));
	}

	public static void setTrackTypesOverride(BogeyType type, Set<TrackType> trackTypes) {
		TRACK_TYPES_OVERRIDE.put(type, trackTypes);
	}

	public static void setTrackWidthOverride(BogeyType type, double trackWidth) {
		TRACK_WIDTH_OVERRIDE.put(type, Math.max(trackWidth, 0));
	}

	public static void setTrackHeightOverride(BogeyType type, double trackHeight) {
		TRACK_HEIGHT_OVERRIDE.put(type, trackHeight);
	}

	public static void setGroundDrivableOverride(BogeyType type, Predicate<CompoundTag> groundDrivable) {
		GROUND_DRIVABLE_OVERRIDE.put(type, groundDrivable);
	}

	public static void setGroundDrivableOverride(BogeyType type, boolean groundDrivable) {
		GROUND_DRIVABLE_OVERRIDE.put(type, $ -> groundDrivable);
	}

	private static <T> ToIntFunction<T> clampMin(ToIntFunction<T> function, int min) {
		return t -> Math.max(function.applyAsInt(t), min);
	}

	private static <T> ToDoubleFunction<T> clampMin(ToDoubleFunction<T> function, double min) {
		return t -> Math.max(function.applyAsDouble(t), min);
	}

	private static <T> ToIntFunction<T> roundToInt(ToDoubleFunction<T> function) {
		return t -> (int)Math.round(function.applyAsDouble(t));
	}
}
