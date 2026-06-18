package com.crystaelix.simurail.api.bogey.menu;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.ToIntFunction;

import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour.StepContext;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public non-sealed class BogeyDataNumericOption extends BogeyDataOption<Integer> {

	protected int min;
	protected int max;
	protected int step = 1;
	protected int shiftStep = 1;
	protected ToIntFunction<StepContext> stepFunction = ctx -> {
		int delta = ctx.shift ? shiftStep : step;
		int value = ctx.currentValue + (ctx.forward ? delta : -delta);
		return Math.clamp(value, min, max);
	};

	public BogeyDataNumericOption(Component displayName) {
		super(displayName);
		defaultValue = 0;
	}

	@Override
	public BogeyDataNumericOption defaultValue(Integer defaultValue) {
		super.defaultValue(defaultValue);
		return this;
	}

	@Override
	public BogeyDataNumericOption codec(BiConsumer<CompoundTag, Integer> valueWriter, Function<CompoundTag, Integer> valueReader) {
		super.codec(valueWriter, valueReader);
		return this;
	}

	public BogeyDataNumericOption range(int min, int max) {
		this.min = min;
		this.max = max;
		return this;
	}

	public BogeyDataNumericOption step(int step) {
		this.step = step;
		return this;
	}

	public BogeyDataNumericOption shiftStep(int shiftStep) {
		this.shiftStep = shiftStep;
		return this;
	}

	public BogeyDataNumericOption stepFunction(ToIntFunction<StepContext> step) {
		Objects.requireNonNull(step, "Cannot set stepFunction to null");
		this.stepFunction = step;
		return this;
	}

	public int min() {
		return min;
	}

	public int max() {
		return max;
	}

	public int applyStep(StepContext context) {
		return stepFunction.applyAsInt(context);
	}
}
