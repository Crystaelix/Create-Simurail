package com.crystaelix.simurail.config;

import java.util.EnumMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import net.createmod.catnip.config.ConfigBase;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class SimurailConfig {

	public static final Map<ModConfig.Type, ConfigBase> CONFIGS = new EnumMap<>(ModConfig.Type.class);

	//public static final SimurailCommonConfig COMMON;
	public static final SimurailServerConfig SERVER = new SimurailServerConfig();
	public static final SimurailClientConfig CLIENT = new SimurailClientConfig();

	public static ConfigBase byType(ModConfig.Type type) {
		return CONFIGS.get(type);
	}

	private static <T extends ConfigBase> T register(T config, ModConfig.Type side) {
		Pair<T, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(builder -> {
			config.registerAll(builder);
			return config;
		});
		config.specification = specPair.getRight();
		CONFIGS.put(side, config);
		return config;
	}

	public static void register(ModContainer container) {
		register(SERVER, ModConfig.Type.SERVER);


		for(Map.Entry<ModConfig.Type, ConfigBase> pair : CONFIGS.entrySet()) {
			container.registerConfig(pair.getKey(), pair.getValue().specification);
		}
	}
}
