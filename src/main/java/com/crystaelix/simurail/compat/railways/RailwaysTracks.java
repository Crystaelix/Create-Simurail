package com.crystaelix.simurail.compat.railways;

import com.crystaelix.simurail.api.track.TrackTypeEntries;
import com.crystaelix.simurail.api.track.TrackTypeEntry;
import com.crystaelix.simurail.api.track.TrackTypeOverrides;
import com.crystaelix.simurail.config.SimurailConfig;
import com.crystaelix.simurail.content.SimurailTracks;
import com.railwayteam.railways.registry.CRBlocks;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.railwayteam.railways.registry.CRTrackMaterials.CRTrackType;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;

import net.minecraft.network.chat.Component;

public class RailwaysTracks {

	public static final double MONORAIL_WIDTH = 1;
	public static final double MONORAIL_HEIGHT = 0.5;
	public static final double NARROW_WIDTH = 8.45 * 2 / 16;
	public static final double NARROW_HEIGHT = 5.48125 / 16;
	public static final double WIDE_WIDTH = 23.45 * 2 / 16;
	public static final double WIDE_HEIGHT = 5.48125 / 16;

	public static final TrackTypeEntry
	MONORAIL = new TrackTypeEntry(
			CRTrackType.MONORAIL,
			Component.translatable("simurail_track_type.railways.monorail"),
			Component.translatable("simurail_track_type.railways.monorail.short"),
			true,
			MONORAIL_WIDTH,
			MONORAIL_HEIGHT,
			SimurailConfig.server().compat.monorailLateralMaxSpeedFactor::get,
			SimurailConfig.server().compat.monorailVerticalMaxSpeedFactor::get,
			SimurailConfig.server().compat.monorailAdhesionFactor::get,
			CRBlocks.MONORAIL_TRACK::getDefaultState),
	NARROW = new TrackTypeEntry(
			CRTrackType.NARROW_GAUGE,
			Component.translatable("simurail_track_type.railways.narrow"),
			Component.translatable("simurail_track_type.railways.narrow.short"),
			false,
			NARROW_WIDTH,
			NARROW_HEIGHT,
			SimurailConfig.server().compat.narrowLateralMaxSpeedFactor::get,
			SimurailConfig.server().compat.narrowVerticalMaxSpeedFactor::get,
			SimurailConfig.server().compat.narrowAdhesionFactor::get,
			() -> CRTrackMaterials.NARROW_GAUGE_ANDESITE.getBlockSupplier().get().defaultBlockState()),
	WIDE = new TrackTypeEntry(
			CRTrackType.WIDE_GAUGE,
			Component.translatable("simurail_track_type.railways.wide"),
			Component.translatable("simurail_track_type.railways.wide.short"),
			false,
			WIDE_WIDTH,
			WIDE_HEIGHT,
			SimurailConfig.server().compat.wideLateralMaxSpeedFactor::get,
			SimurailConfig.server().compat.wideVerticalMaxSpeedFactor::get,
			SimurailConfig.server().compat.wideAdhesionFactor::get,
			() -> CRTrackMaterials.WIDE_GAUGE_ANDESITE.getBlockSupplier().get().defaultBlockState());

	public static void register() {
		TrackTypeEntries.addEntry(MONORAIL);
		TrackTypeEntries.addEntry(NARROW);
		TrackTypeEntries.addEntry(WIDE);

		TrackTypeOverrides.setOverride(CRTrackMaterials.PHANTOM, TrackType.STANDARD);
		TrackTypeOverrides.setUniversal(CRTrackType.UNIVERSAL);
	}
}
