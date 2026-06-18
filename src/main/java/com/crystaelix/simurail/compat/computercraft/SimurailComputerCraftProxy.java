package com.crystaelix.simurail.compat.computercraft;

import com.crystaelix.simurail.compat.SimurailCompat;
import com.google.common.base.Function;
import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.compat.computercraft.FallbackComputerBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

public class SimurailComputerCraftProxy {

	private static Function<SmartBlockEntity, ? extends AbstractComputerBehaviour> fallbackFactory;
	private static Function<SmartBlockEntity, ? extends AbstractComputerBehaviour> computerFactory;

	public static void register() {
		fallbackFactory = FallbackComputerBehaviour::new;
		SimurailCompat.COMPUTERCRAFT.ifLoaded(() -> () -> registerWithDependency());
	}

	private static void registerWithDependency() {
		computerFactory = SimurailComputerBehaviour::new;
	}

	public static AbstractComputerBehaviour behaviour(SmartBlockEntity sbe) {
		return computerFactory == null ? fallbackFactory.apply(sbe) : computerFactory.apply(sbe);// 24 25 26
	}
}
