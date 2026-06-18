package com.crystaelix.simurail.api.bogey.menu;

import net.minecraft.nbt.CompoundTag;

public class BogeyDataOptionValue<T> {

	protected final BogeyDataOption<T> option;
	protected T value;

	public BogeyDataOptionValue(BogeyDataOption<T> option) {
		this.option = option;
		this.value = option.defaultValue();
	}

	public BogeyDataOptionValue(BogeyDataOption<T> option, T value) {
		this.option = option;
		this.value = value;
	}

	public BogeyDataOptionValue(BogeyDataOption<T> option, CompoundTag extra) {
		this.option = option;
		read(extra);
	}

	public BogeyDataOption<T> option() {
		return option;
	}

	public T value() {
		return value;
	}

	public BogeyDataOptionValue<T> value(T value) {
		this.value = value;
		return this;
	}

	public CompoundTag write(CompoundTag extra) {
		option.writeValue(extra, value);
		return extra;
	}

	public BogeyDataOptionValue<T> read(CompoundTag extra) {
		value = option.readValue(extra);
		return this;
	}
}
