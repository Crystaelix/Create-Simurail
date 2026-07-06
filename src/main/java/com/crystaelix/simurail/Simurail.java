package com.crystaelix.simurail;

import com.crystaelix.simurail.compat.SimurailCompat;
import com.crystaelix.simurail.compat.computercraft.SimurailComputerCraftProxy;
import com.crystaelix.simurail.compat.create_bb.SimurailBlocksBogiesCompat;
import com.crystaelix.simurail.compat.electroenergetics.SimurailElectroEnergeticsCompat;
import com.crystaelix.simurail.compat.railways.SimurailRailwaysCompat;
import com.crystaelix.simurail.config.SimurailConfig;
import com.crystaelix.simurail.content.SimurailBlockEntities;
import com.crystaelix.simurail.content.SimurailBlocks;
import com.crystaelix.simurail.content.SimurailBogeys;
import com.crystaelix.simurail.content.SimurailCouplers;
import com.crystaelix.simurail.content.SimurailForceGroups;
import com.crystaelix.simurail.content.SimurailItems;
import com.crystaelix.simurail.content.SimurailMenus;
import com.crystaelix.simurail.content.SimurailPackets;
import com.crystaelix.simurail.content.SimurailSoundEvents;
import com.crystaelix.simurail.content.SimurailTracks;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import dev.simulated_team.simulated.registrate.SimulatedRegistrate;
import net.createmod.catnip.config.ConfigBase;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(Simurail.MOD_ID)
public class Simurail {

	public static final String MOD_ID = "simurail";
	private static final NonNullSupplier<CreateRegistrate> REGISTRATE = NonNullSupplier.
			lazy(() -> (SimulatedRegistrate)new SimulatedRegistrate(id(MOD_ID), MOD_ID).defaultCreativeTab((ResourceKey<CreativeModeTab>)null));

	public Simurail(IEventBus modEventBus, ModContainer modContainer) {
		modEventBus.register(this);
		registrate().registerEventListeners(modEventBus);

		SimurailBlocks.register();
		SimurailItems.register();
		SimurailBlockEntities.register();
		SimurailMenus.register();
		SimurailSoundEvents.register(modEventBus);
		SimurailForceGroups.register(modEventBus);
		SimurailPackets.register();

		SimurailConfig.register(modContainer);

		SimurailCompat.ELECTROENERGETICS.ifLoaded(() -> () -> SimurailElectroEnergeticsCompat.onConstruct(modEventBus));
		SimurailComputerCraftProxy.register();
	}

	@SubscribeEvent
	public void onCommonSetup(FMLCommonSetupEvent event) {
		SimurailBogeys.register();
		SimurailTracks.register();
		SimurailCouplers.register();

		event.enqueueWork(() -> {
			SimurailCompat.BLOCKSBOGIES.ifLoaded(() -> () -> SimurailBlocksBogiesCompat.onCommonSetupLate());
			SimurailCompat.RAILWAYS.ifLoaded(() -> () -> SimurailRailwaysCompat.onCommonSetupLate());
		});
	}

	@SubscribeEvent
	public void onConfigLoad(ModConfigEvent.Loading event) {
		for(ConfigBase config : SimurailConfig.CONFIGS.values()) {
			if(config.specification == event.getConfig().getSpec()) {
				config.onLoad();
			}
		}
	}

	@SubscribeEvent
	public void onConfigReload(ModConfigEvent.Reloading event) {
		for(ConfigBase config : SimurailConfig.CONFIGS.values()) {
			if(config.specification == event.getConfig().getSpec()) {
				config.onReload();
			}
		}
	}

	public static CreateRegistrate registrate() {
		return REGISTRATE.get();
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.tryBuild(MOD_ID, path);
	}
}
