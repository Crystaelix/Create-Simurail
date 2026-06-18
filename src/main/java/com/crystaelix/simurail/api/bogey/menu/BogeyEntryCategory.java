package com.crystaelix.simurail.api.bogey.menu;

import java.util.List;

import net.minecraft.network.chat.Component;

public non-sealed class BogeyEntryCategory extends BogeyCategory<BogeyEntry> {

	public BogeyEntryCategory(Component displayName, List<BogeyEntry> children) {
		super(displayName, children);
	}
}
