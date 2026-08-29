package com.crystaelix.simurail.content;

import com.crystaelix.simurail.content.bogey.PhysicsBogeyWheelSparkParticle;

import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

public class SimurailParticleProviders {

	public static void register(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(SimurailParticles.PHYSICS_BOGEY_WHEEL_SPARK.get(), PhysicsBogeyWheelSparkParticle.Provider::new);
	}
}
