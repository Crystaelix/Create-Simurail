package com.crystaelix.simurail.content;

import com.crystaelix.simurail.Simurail;
import com.crystaelix.simurail.content.automatic_coupler.AutomaticCouplerBaseScreen;
import com.crystaelix.simurail.content.automatic_coupler.AutomaticCouplerMenu;
import com.crystaelix.simurail.content.bogey.PhysicsBogeyBaseScreen;
import com.crystaelix.simurail.content.bogey.PhysicsBogeyMenu;
import com.crystaelix.simurail.content.gangway_frame.GangwayFrameMenu;
import com.crystaelix.simurail.content.gangway_frame.GangwayFrameScreen;
import com.crystaelix.simurail.content.probe_reader.ProbeReaderMenu;
import com.crystaelix.simurail.content.probe_reader.ProbeReaderScreen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.MenuEntry;

public class SimurailMenus {

	private static final CreateRegistrate REGISTRATE = Simurail.registrate();

	public static final MenuEntry<PhysicsBogeyMenu> PHYSICS_BOGEY = REGISTRATE.
			menu("physics_bogey", PhysicsBogeyMenu::new, () -> PhysicsBogeyBaseScreen::create).
			register();
	public static final MenuEntry<AutomaticCouplerMenu> AUTOMATIC_COUPLER = REGISTRATE.
			menu("automatic_coupler", AutomaticCouplerMenu::new, () -> AutomaticCouplerBaseScreen::create).
			register();
	public static final MenuEntry<GangwayFrameMenu> GANGWAY_FRAME = REGISTRATE.
			menu("gangway_frame", GangwayFrameMenu::new, () -> GangwayFrameScreen::new).
			register();
	public static final MenuEntry<ProbeReaderMenu> PROBE_READER = REGISTRATE.
			menu("probe_reader", ProbeReaderMenu::new, () -> ProbeReaderScreen::new).
			register();

	public static void register() {
	}
}
