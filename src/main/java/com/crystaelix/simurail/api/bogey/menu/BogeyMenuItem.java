package com.crystaelix.simurail.api.bogey.menu;

import net.minecraft.network.chat.Component;

public abstract sealed class BogeyMenuItem permits BogeyCategory, BogeyEntry {

	private final Component displayName;

	public BogeyMenuItem(Component displayName) {
		this.displayName = displayName;
	}

	public Component displayName() {
		return displayName;
	}

	public abstract boolean containsInvertible();
}
