package com.crystaelix.simurail.api.bogey.menu;

import java.util.List;

import com.crystaelix.simurail.api.bogey.BogeyType;

import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public non-sealed class BogeyEntry extends BogeyMenuItem {

	private final ResourceLocation id;
	private final BogeyType type;
	private final List<BogeyDataOption<?>> options;

	public BogeyEntry(ResourceLocation id, Component displayName, BogeyType type, List<BogeyDataOption<?>> options) {
		super(displayName);
		this.id = id;
		this.type = type;
		this.options = options;
	}

	public BogeyEntry(ResourceLocation id, Component displayName, BogeyType type, BogeyDataOption<?>... options) {
		this(id, displayName, type, List.of(options));
	}

	public BogeyEntry(ResourceLocation id, Component displayName, BogeyType type) {
		this(id, displayName, type, List.of());
	}

	public BogeyEntry(ResourceLocation id, BogeyType type, List<BogeyDataOption<?>> options) {
		this(id, Component.translatable(Util.makeDescriptionId("simurail_bogey_entry", id)), type, options);
	}

	public BogeyEntry(ResourceLocation id, BogeyType type, BogeyDataOption<?>... options) {
		this(id, type, List.of(options));
	}

	public BogeyEntry(ResourceLocation id, BogeyType type) {
		this(id, type, List.of());
	}

	public ResourceLocation id() {
		return id;
	}

	public BogeyType type() {
		return type;
	}

	public List<BogeyDataOption<?>> options() {
		return options;
	}

	@Override
	public boolean containsInvertible() {
		return type.invertible();
	}
}
