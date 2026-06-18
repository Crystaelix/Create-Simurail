package com.crystaelix.simurail.content.bogey;

import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

public class PhysicsBogeyScreenOpener {

	public static void displayScreen(PhysicsBogeyBlockEntity be, Player player) {
		if(player instanceof LocalPlayer) {
			ScreenOpener.open(player.isSecondaryUseActive() ? new PhysicsBogeyMenuScreen(be) : new PhysicsBogeyOptionsScreen(be));
		}
	}
}
