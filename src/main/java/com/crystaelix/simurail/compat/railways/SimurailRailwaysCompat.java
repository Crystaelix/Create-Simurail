package com.crystaelix.simurail.compat.railways;

import com.crystaelix.simurail.api.track.TrackTypeOverrides;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.railwayteam.railways.registry.CRTrackMaterials.CRTrackType;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;

public class SimurailRailwaysCompat {

	public static void onCommonSetupLate() {
		RailwaysBogeys.register();

		TrackTypeOverrides.setOverride(CRTrackMaterials.PHANTOM, TrackType.STANDARD);
		TrackTypeOverrides.setUniversal(CRTrackType.UNIVERSAL);
	}
}
