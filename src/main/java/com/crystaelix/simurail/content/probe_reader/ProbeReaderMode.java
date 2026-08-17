package com.crystaelix.simurail.content.probe_reader;

import java.util.Locale;
import java.util.function.IntFunction;

import com.crystaelix.simurail.content.bogey.PhysicsBogeyControlMode;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.ByIdMap.OutOfBoundsStrategy;

public enum ProbeReaderMode {
	OCCUPIED_SIGNAL,
	ALIGNED_SIGNAL,
	OPPOSITE_SIGNAL,
	SIGNAL,
	STATION,
	POWERED_STATION,
	DISCONTINUITY;

	public static final IntFunction<ProbeReaderMode> BY_ID = ByIdMap.continuous(ProbeReaderMode::ordinal, values(), OutOfBoundsStrategy.ZERO);
	public static final StreamCodec<ByteBuf, ProbeReaderMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, ProbeReaderMode::ordinal);

	@Override
	public String toString() {
		return name().toLowerCase(Locale.ROOT);
	}
}
