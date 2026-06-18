package com.crystaelix.simurail.content.bogey;

import java.util.Locale;
import java.util.function.IntFunction;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.ByIdMap.OutOfBoundsStrategy;

public enum PhysicsBogeyControlMode {
	BRAKING,
	BRAKING_INVERTED,
	STRENGTH,
	STRENGTH_INVERTED;

	public static final IntFunction<PhysicsBogeyControlMode> BY_ID = ByIdMap.continuous(PhysicsBogeyControlMode::ordinal, values(), OutOfBoundsStrategy.ZERO);
	public static final StreamCodec<ByteBuf, PhysicsBogeyControlMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, PhysicsBogeyControlMode::ordinal);

	@Override
	public String toString() {
		return name().toLowerCase(Locale.ROOT);
	}
}
