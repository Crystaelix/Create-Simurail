package com.crystaelix.simurail.content;

import java.util.List;

import com.crystaelix.simurail.api.bogey.BogeyType;
import com.crystaelix.simurail.api.bogey.menu.BogeyEntry;
import com.crystaelix.simurail.api.bogey.menu.BogeyEntryCategory;
import com.crystaelix.simurail.api.bogey.menu.BogeyMenuManager;
import com.crystaelix.simurail.api.bogey.menu.TrackTypeEntry;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllBogeyStyles;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;

import net.minecraft.network.chat.Component;

public class SimurailBogeys {

	public static final BogeyEntry
	SMALL = new BogeyEntry(
			Create.asResource("standard/small"),
			new BogeyType(AllBogeyStyles.STANDARD, BogeySizes.SMALL)),
	LARGE = new BogeyEntry(
			Create.asResource("standard/large"),
			new BogeyType(AllBogeyStyles.STANDARD, BogeySizes.LARGE));

	public static final BogeyEntryCategory CREATE = new BogeyEntryCategory(
			Component.translatable("itemGroup.create.base"),
			List.of(SMALL, LARGE));

	public static final TrackTypeEntry
	STANDARD = new TrackTypeEntry(
			TrackType.STANDARD,
			Component.translatable("simurail_track_type.create.standard"),
			Component.translatable("simurail_track_type.create.standard.short"),
			false,
			AllBlocks.TRACK::getDefaultState);

	public static void register() {
		BogeyMenuManager.addBogeyCategory(CREATE);
		BogeyMenuManager.addTrackTypeEntry(STANDARD);

		BogeyType.setDefault(TrackType.STANDARD, false, SMALL.type());
	}
}
