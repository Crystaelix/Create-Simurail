package com.crystaelix.simurail.api.coupler;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;

public class CouplerTypeRegistry {

	private static final Map<ResourceLocation, CouplerType> REGISTRY = new HashMap<>();
	private static final CrudeIncrementalIntIdentityHashBiMap<CouplerType> IDS = CrudeIncrementalIntIdentityHashBiMap.create(4);

	public static boolean register(CouplerType type) {
		if(REGISTRY.containsKey(type.id())) {
			return false;
		}
		synchronized(IDS) {
			REGISTRY.put(type.id(), type);
			IDS.add(type);
		}
		return true;
	}

	public static CouplerType get(ResourceLocation id) {
		return REGISTRY.get(id);
	}

	public static CouplerType next(CouplerType type) {
		synchronized(IDS) {
			int toGet = Math.floorMod(IDS.getId(type) + 1, REGISTRY.size());
			return IDS.byId(toGet);
		}
	}
}
