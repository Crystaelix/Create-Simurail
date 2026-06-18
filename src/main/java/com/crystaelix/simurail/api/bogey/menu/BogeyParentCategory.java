package com.crystaelix.simurail.api.bogey.menu;

import java.util.List;

import net.minecraft.network.chat.Component;

public non-sealed class BogeyParentCategory extends BogeyCategory<BogeyCategory<?>> {

	public BogeyParentCategory(Component displayName, List<BogeyCategory<?>> children) {
		super(displayName, children);
	}
}
