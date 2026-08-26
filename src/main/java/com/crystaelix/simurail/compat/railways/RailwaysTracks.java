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

	public static final double RAIL_HEIGHT = SimurailTracks.STANDARD_RAIL_HEIGHT;

	public static final double NARROW_GAUGE = SimurailTracks.STANDARD_GAUGE - 0.875;
	public static final double WIDE_GAUGE = SimurailTracks.STANDARD_GAUGE + 1;
	public static final double MONORAIL_GAUGE = 1;

	public static final TrackTypeEntry
	MONORAIL = new TrackTypeEntry(
			CRTrackType.MONORAIL,
			Component.translatable("simurail_track_type.railways.monorail"),
			Component.translatable("simurail_track_type.railways.monorail.short"),
			true,
			MONORAIL_GAUGE,
			RAIL_HEIGHT,
			SimurailConfig.server().compat.axleMonorailLateralMaxSpeedFactor::get,
			SimurailConfig.server().compat.axleMonorailVerticalMaxSpeedFactor::get,
			CRBlocks.MONORAIL_TRACK::getDefaultState),
	NARROW = new TrackTypeEntry(
			CRTrackType.NARROW_GAUGE,
			Component.translatable("simurail_track_type.railways.narrow"),
			Component.translatable("simurail_track_type.railways.narrow.short"),
			false,
			NARROW_GAUGE,
			RAIL_HEIGHT,
			SimurailConfig.server().compat.axleNarrowLateralMaxSpeedFactor::get,
			SimurailConfig.server().compat.axleNarrowVerticalMaxSpeedFactor::get,
			() -> CRTrackMaterials.NARROW_GAUGE_ANDESITE.getBlockSupplier().get().defaultBlockState()),
	WIDE = new TrackTypeEntry(
			CRTrackType.WIDE_GAUGE,
			Component.translatable("simurail_track_type.railways.wide"),
			Component.translatable("simurail_track_type.railways.wide.short"),
			false,
			WIDE_GAUGE,
			RAIL_HEIGHT,
			SimurailConfig.server().compat.axleWideLateralMaxSpeedFactor::get,
			SimurailConfig.server().compat.axleWideVerticalMaxSpeedFactor::get,
			() -> CRTrackMaterials.WIDE_GAUGE_ANDESITE.getBlockSupplier().get().defaultBlockState());

	public static void register() {
		TrackTypeEntries.addEntry(MONORAIL);
		TrackTypeEntries.addEntry(NARROW);
		TrackTypeEntries.addEntry(WIDE);

		TrackTypeOverrides.setOverride(CRTrackMaterials.PHANTOM, TrackType.STANDARD);
		TrackTypeOverrides.setUniversal(CRTrackType.UNIVERSAL);
	}
}
