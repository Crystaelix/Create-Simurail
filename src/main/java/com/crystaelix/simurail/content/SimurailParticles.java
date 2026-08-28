package com.crystaelix.simurail.content;

import java.util.function.Supplier;

import com.crystaelix.simurail.Simurail;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SimurailParticles {

	public static final DeferredRegister<ParticleType<?>> REGISTRAR = DeferredRegister.create(Registries.PARTICLE_TYPE, Simurail.MOD_ID);

	public static final Supplier<SimpleParticleType> PHYSICS_BOGEY_WHEEL_SPARK = particle("physics_bogey_wheel_spark");

	public static void register(IEventBus modEventBus) {
		REGISTRAR.register(modEventBus);
	}

	private static DeferredHolder<ParticleType<?>, SimpleParticleType> particle(String name) {
		return REGISTRAR.register(name, () -> new SimpleParticleType(true));
	}
}
