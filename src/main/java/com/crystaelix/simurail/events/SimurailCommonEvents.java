package com.crystaelix.simurail.events;

import com.crystaelix.simurail.content.probe_reader.ProbeReaderBlockItem;
import com.crystaelix.simurail.content.remote_controller.RemoteControllerBlockItem;
import com.crystaelix.simurail.content.track.CurvedTrackSegmentCache;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

@EventBusSubscriber
public class SimurailCommonEvents {

	@SubscribeEvent
	public static void onServerStopped(ServerStoppedEvent event) {
		CurvedTrackSegmentCache.removeCaches();
	}

	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		ItemStack stack = event.getItemStack();
		Item item = stack.getItem();
		if(item instanceof ProbeReaderBlockItem || item instanceof RemoteControllerBlockItem) {
			BlockItem blockItem = (BlockItem)item;
			if(!event.getLevel().getBlockState(event.getPos()).is(blockItem.getBlock())) {
				event.setUseBlock(TriState.FALSE);
			}
		}
	}
}
