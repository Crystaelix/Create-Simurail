package com.crystaelix.simurail;

import com.crystaelix.simurail.content.SimurailInteractCallbacks;
import com.crystaelix.simurail.content.SimurailParticleTypes;
import com.crystaelix.simurail.content.SimurailPartialModels;
import com.crystaelix.simurail.ponder.SimurailPonderPlugin;

import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = Simurail.MOD_ID, dist = Dist.CLIENT)
public class SimurailClient {

	public SimurailClient(IEventBus modEventBus, ModContainer modContainer) {
		modEventBus.register(this);
		modContainer.registerExtensionPoint(IConfigScreenFactory.class, (c, l) -> new BaseConfigScreen(l, Simurail.MOD_ID));
		PonderIndex.addPlugin(new SimurailPonderPlugin());
	}

	@SubscribeEvent
	public void onClientSetup(FMLCommonSetupEvent event) {
		SimurailPartialModels.register();
		SimurailInteractCallbacks.register();
	}

	@SubscribeEvent
	public void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
		SimurailParticleTypes.registerProviders(event);
	}
}
