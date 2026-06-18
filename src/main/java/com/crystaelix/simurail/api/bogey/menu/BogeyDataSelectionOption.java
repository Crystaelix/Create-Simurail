package com.crystaelix.simurail.api.bogey.menu;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public non-sealed class BogeyDataSelectionOption extends BogeyDataOption<Integer> {

	protected List<Component> options = List.of();

	public BogeyDataSelectionOption(Component displayName) {
		super(displayName);
		defaultValue = 0;
	}

	@Override
	public BogeyDataSelectionOption defaultValue(Integer defaultValue) {
		super.defaultValue(defaultValue);
		return this;
	}

	@Override
	public BogeyDataSelectionOption codec(BiConsumer<CompoundTag, Integer> valueWriter, Function<CompoundTag, Integer> valueReader) {
		super.codec(valueWriter, valueReader);
		return this;
	}

	public BogeyDataSelectionOption options(List<Component> options) {
		this.options = options == null ? List.of() : List.copyOf(options);
		return this;
	}

	public List<Component> options() {
		return options;
	}
}
