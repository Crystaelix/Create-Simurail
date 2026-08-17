package com.crystaelix.simurail.events;

import com.crystaelix.simurail.content.probe_reader.ProbeReaderBlockItem;
import com.crystaelix.simurail.content.track.CurvedTrackSegmentCache;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

@EventBusSubscriber
public class SimurailCommonEvents {

	@SubscribeEvent
	public static void onLevelUnload(LevelEvent.Unload event) {
		if(event.getLevel() instanceof Level level) {
			CurvedTrackSegmentCache.removeCache(level.dimension());
		}
	}

	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		ItemStack usedItem = event.getItemStack();
		if(usedItem.getItem() instanceof ProbeReaderBlockItem) {
			BlockItem blockItem = (BlockItem)usedItem.getItem();
			if(!event.getLevel().getBlockState(event.getPos()).is(blockItem.getBlock())) {
				event.setUseBlock(TriState.FALSE);
			}
		}
	}
}
