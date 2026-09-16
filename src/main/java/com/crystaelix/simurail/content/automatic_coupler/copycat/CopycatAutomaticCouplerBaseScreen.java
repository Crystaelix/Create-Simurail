package com.crystaelix.simurail.content.automatic_coupler.copycat;

import net.createmod.catnip.gui.AbstractSimiScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public abstract class CopycatAutomaticCouplerBaseScreen extends AbstractSimiScreen implements MenuAccess<CopycatAutomaticCouplerMenu> {

	protected CopycatAutomaticCouplerMenu menu;

	public CopycatAutomaticCouplerBaseScreen(CopycatAutomaticCouplerMenu menu, Component title) {
		super(title);
		this.menu = menu;
	}

	@Override
	public CopycatAutomaticCouplerMenu getMenu() {
		return menu;
	}

	public static CopycatAutomaticCouplerBaseScreen create(CopycatAutomaticCouplerMenu menu, Inventory inv, Component title) {
		//if(menu.gangway) {
		//	return new CopycatAutomaticCouplerGangwayScreen(menu, title);
		//}
		//else {
		//	return new CopycatAutomaticCouplerScreen(menu, title);
		//}
		return new CopycatAutomaticCouplerGangwayScreen(menu, title);
	}
}
