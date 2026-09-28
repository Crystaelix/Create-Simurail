package com.crystaelix.simurail.content;

import com.crystaelix.simurail.Simurail;
import com.crystaelix.simurail.content.track_switch.TrackSwitchDisplaySource;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class SimurailDisplaySources {

	private static final CreateRegistrate REGISTRATE = Simurail.registrate();

	public static final RegistryEntry<DisplaySource, TrackSwitchDisplaySource> TRACK_SWITCH = REGISTRATE.
			displaySource("track_switch", TrackSwitchDisplaySource::new).
			register();

	public static void register() {
	}
}
