package com.crystaelix.simurail.content;

import java.util.List;

import com.crystaelix.simurail.api.bogey.BogeyPropertyOverrides;
import com.crystaelix.simurail.api.bogey.BogeyType;
import com.crystaelix.simurail.api.bogey.menu.BogeyEntry;
import com.crystaelix.simurail.api.bogey.menu.BogeyEntryCategory;
import com.crystaelix.simurail.api.bogey.menu.BogeyMenuManager;
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

	public static void register() {
		BogeyMenuManager.addBogeyCategory(CREATE);

		BogeyType.setDefault(TrackType.STANDARD, false, SMALL.type());

		BogeyPropertyOverrides.setWheelSpacingOverride(SMALL.type(), 2);
		BogeyPropertyOverrides.setWheelSpacingOverride(LARGE.type(), 1);

		BogeyPropertyOverrides.setAxleCountOverride(SMALL.type(), 2);
		BogeyPropertyOverrides.setAxleCountOverride(LARGE.type(), 1);

		// Models reach 2.375 and 2.125 across a 1.93 gauge; only the large needs the thinner wheel
		BogeyPropertyOverrides.setWheelWidthOverride(LARGE.type(), 3 / 16D);

		// Hardcoded: StandardBogeyRenderer's wheel heights are unrelated to wheel radius
		BogeyPropertyOverrides.setWheelCenterHeightOverride(SMALL.type(), 12 / 16D);
		BogeyPropertyOverrides.setWheelCenterHeightOverride(LARGE.type(), 16 / 16D);

		// Tread radii: mean of the eight sided flats and corners, short of the flange;
		// & the large sits inside its rolling radius
		BogeyPropertyOverrides.setWheelOuterRadiusOverride(SMALL.type(), 0.4033);
		BogeyPropertyOverrides.setWheelOuterRadiusOverride(LARGE.type(), 0.6508);
	}
}
