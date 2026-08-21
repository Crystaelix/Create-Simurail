package com.crystaelix.simurail.events;

import com.crystaelix.simurail.content.probe_reader.ProbeReaderOutline;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(Dist.CLIENT)
public class SimurailClientEvents {

	@SubscribeEvent
	public static void onClientTickPre(ClientTickEvent.Pre event) {
		onClientTick(true);
	}

	@SubscribeEvent
	public static void onClientTickPost(ClientTickEvent.Post event) {
		onClientTick(false);
	}

	public static void onClientTick(boolean pre) {
		if(!isGameActive()) {
			return;
		}
		if(pre) {
			return;
		}
		ProbeReaderOutline.clientTick();
	}

	protected static boolean isGameActive() {
		return !(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null);
	}
}
