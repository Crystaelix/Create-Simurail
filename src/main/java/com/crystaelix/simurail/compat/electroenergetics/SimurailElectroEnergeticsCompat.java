package com.crystaelix.simurail.compat.electroenergetics;

import net.neoforged.bus.api.IEventBus;

public class SimurailElectroEnergeticsCompat {

	public static void onConstruct(IEventBus modEventBus) {
		SimurailDeviceTypes.register(modEventBus);
	}
}
