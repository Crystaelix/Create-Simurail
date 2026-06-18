package com.crystaelix.simurail.api.bogey.menu;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public abstract sealed class BogeyDataOption<T> permits BogeyDataSelectionOption, BogeyDataNumericOption, BogeyDataTextOption {

	protected final Component displayName;
	protected BiConsumer<CompoundTag, T> valueWriter = null;
	protected Function<CompoundTag, T> valueReader = null;
	protected T defaultValue;

	public BogeyDataOption(Component displayName) {
		this.displayName = displayName;
	}

	public Component displayName() {
		return displayName;
	}

	public T defaultValue() {
		return defaultValue;
	}

	public BogeyDataOption<T> defaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}

	public BogeyDataOption<T> codec(BiConsumer<CompoundTag, T> valueWriter, Function<CompoundTag, T> valueReader) {
		Objects.requireNonNull(valueWriter, "Cannot set valueWriter to null");
		Objects.requireNonNull(valueReader, "Cannot set valueReader to null");
		this.valueWriter = valueWriter;
		this.valueReader = valueReader;
		return this;
	}

	public CompoundTag writeValue(CompoundTag tag, T value) {
		Objects.requireNonNull(valueWriter, "valueWriter not set");
		valueWriter.accept(tag, value);
		return tag;
	}

	public T readValue(CompoundTag tag) {
		Objects.requireNonNull(valueReader, "valueReader not set");
		return valueReader.apply(tag);
	}

	public BogeyDataOptionValue<T> readToValue(CompoundTag tag) {
		BogeyDataOptionValue<T> value = new BogeyDataOptionValue<>(this);
		value.value(readValue(tag));
		return value;
	}
}
