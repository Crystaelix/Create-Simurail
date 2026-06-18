package com.crystaelix.simurail.api.bogey.menu;

import java.util.List;

import net.minecraft.network.chat.Component;

public abstract sealed class BogeyCategory<T extends BogeyMenuItem> extends BogeyMenuItem permits BogeyParentCategory, BogeyEntryCategory {

	private final List<T> children;
	private List<T> invertibleChildren;

	public BogeyCategory(Component displayName, List<T> children) {
		super(displayName);
		this.children = children.stream().distinct().toList();
	}

	public List<T> children() {
		return children;
	}

	public List<T> children(boolean inverted) {
		if(invertibleChildren == null) {
			invertibleChildren = children().stream().filter(BogeyMenuItem::containsInvertible).toList();
		}
		return inverted ? invertibleChildren : children();
	}

	public boolean isDynamic() {
		return false;
	}

	@Override
	public boolean containsInvertible() {
		return children().stream().anyMatch(BogeyMenuItem::containsInvertible);
	}
}
