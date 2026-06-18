package com.crystaelix.simurail.content;

import java.util.function.Supplier;

import com.crystaelix.simurail.Simurail;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SimurailSoundEvents {

	public static final DeferredRegister<SoundEvent> REGISTRAR = DeferredRegister.create(Registries.SOUND_EVENT, Simurail.MOD_ID);

	public static final Supplier<SoundEvent>
	COUPLER_CONNECT = sound("block.coupler.connect"),
	COUPLER_DISCONNECT = sound("block.coupler.disconnect");

	public static void register(IEventBus modEventBus) {
		REGISTRAR.register(modEventBus);
	}

	private static DeferredHolder<SoundEvent, SoundEvent> sound(String name) {
		return REGISTRAR.register(name, () -> SoundEvent.createVariableRangeEvent(Simurail.id(name)));
	}
}
