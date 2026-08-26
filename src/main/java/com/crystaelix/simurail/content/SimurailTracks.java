package com.crystaelix.simurail.content;

import com.crystaelix.simurail.api.track.TrackTypeEntries;
import com.crystaelix.simurail.api.track.TrackTypeEntry;
import com.crystaelix.simurail.config.SimurailConfig;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;

import net.minecraft.network.chat.Component;

public class SimurailTracks {

	// +/-0.965 from track center line
	public static final double STANDARD_GAUGE = 1.93;
	// Height of the rail head, where wheels' bottom rests: rails sit 3/16 above the
	// node, and the 0.2836-tall rail model is drawn from 2/16 + 1/256 below that
	public static final double STANDARD_RAIL_HEIGHT = 3 / 16D + 0.2836 - (2 / 16D + 1 / 256D);

	public static final TrackTypeEntry
	STANDARD = new TrackTypeEntry(
			TrackType.STANDARD,
			Component.translatable("simurail_track_type.create.standard"),
			Component.translatable("simurail_track_type.create.standard.short"),
			false,
			STANDARD_GAUGE,
			STANDARD_RAIL_HEIGHT,
			SimurailConfig.server().physics.axleStandardLateralMaxSpeedFactor::get,
			SimurailConfig.server().physics.axleStandardVerticalMaxSpeedFactor::get,
			AllBlocks.TRACK::getDefaultState);

	public static void register() {
		TrackTypeEntries.addEntry(STANDARD);
	}
}
