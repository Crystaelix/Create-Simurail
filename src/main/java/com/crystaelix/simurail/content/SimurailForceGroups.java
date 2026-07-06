package com.crystaelix.simurail.content;

import java.util.function.Supplier;

import com.crystaelix.simurail.Simurail;

import dev.ryanhcode.sable.api.physics.force.ForceGroup;
import dev.ryanhcode.sable.api.physics.force.ForceGroups;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SimurailForceGroups {

	public static final DeferredRegister<ForceGroup> REGISTRAR = DeferredRegister.create(ForceGroups.REGISTRY_KEY, Simurail.MOD_ID);

	public static final Supplier<ForceGroup>
	TRACTION = group("traction", 0xE0B359, true),
	BRAKE = group("brake", 0x5757DB, true);

	public static void register(IEventBus modEventBus) {
		REGISTRAR.register(modEventBus);
	}

	private static DeferredHolder<ForceGroup, ForceGroup> group(String name, int color, boolean defaultDisplayed) {
		return REGISTRAR.register(name, () -> new ForceGroup(Component.translatable("force_group.simurail." + name), null, color, defaultDisplayed));
	}
}
