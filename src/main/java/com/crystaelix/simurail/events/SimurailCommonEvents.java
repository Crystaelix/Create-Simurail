package com.crystaelix.simurail.events;

import com.crystaelix.simurail.Simurail;
import com.crystaelix.simurail.compat.create_bb.SimurailBlocksBogiesCompat;
import com.crystaelix.simurail.compat.railways.SimurailRailwaysCompat;
import com.crystaelix.simurail.content.SimurailBogeys;
import com.crystaelix.simurail.content.track.CurvedTrackSegmentCache;

import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

@EventBusSubscriber
public class SimurailCommonEvents {

	@SubscribeEvent
	public static void onLevelUnload(LevelEvent.Unload event) {
		if(event.getLevel() instanceof Level level) {
			CurvedTrackSegmentCache.removeCache(level.dimension());
		}
	}
}
