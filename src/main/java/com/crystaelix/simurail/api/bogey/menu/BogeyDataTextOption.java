package com.crystaelix.simurail.api.bogey.menu;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.common.base.Predicates;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public non-sealed class BogeyDataTextOption extends BogeyDataOption<String> {

	protected Predicate<String> validator = Predicates.alwaysTrue();

	public BogeyDataTextOption(Component displayName) {
		super(displayName);
		defaultValue = "";
	}

	@Override
	public BogeyDataTextOption defaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}

	@Override
	public BogeyDataTextOption codec(BiConsumer<CompoundTag, String> valueWriter, Function<CompoundTag, String> valueReader) {
		super.codec(valueWriter, valueReader);
		return this;
	}

	public BogeyDataTextOption validator(Predicate<String> validator) {
		Objects.requireNonNull(validator, "Cannot set validator to null");
		this.validator = validator;
		return this;
	}

	public boolean isValid(String value) {
		return validator.test(value);
	}
}
