package com.crystaelix.simurail.compat.imm_ptl;

import com.crystaelix.simurail.compat.SimurailCompat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import qouteall.imm_ptl.core.ClientWorldLoader;

public class ClientLevels {

	public static ClientLevel getLevel(ResourceKey<Level> dimension) {
		if(SimurailCompat.IMMERSIVEPORTALS.isLoaded()) {
			ClientLevel level = ClientWorldLoader.getOptionalWorld(dimension);
			if(level != null) {
				return level;
			}
		}
		return Minecraft.getInstance().level;
	}
}
