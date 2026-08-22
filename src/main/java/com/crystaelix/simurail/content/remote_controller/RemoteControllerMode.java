package com.crystaelix.simurail.content.remote_controller;

import java.util.Locale;
import java.util.function.IntFunction;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.ByIdMap.OutOfBoundsStrategy;

public enum RemoteControllerMode {
	BRAKING,
	BRAKING_INVERTED,
	STEERING_LEFT,
	STEERING_RIGHT,
	VERTICAL_MOVEMENT,
	ENABLED;

	public static final IntFunction<RemoteControllerMode> BY_ID = ByIdMap.continuous(RemoteControllerMode::ordinal, values(), OutOfBoundsStrategy.ZERO);
	public static final StreamCodec<ByteBuf, RemoteControllerMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, RemoteControllerMode::ordinal);

	@Override
	public String toString() {
		return name().toLowerCase(Locale.ROOT);
	}
}
