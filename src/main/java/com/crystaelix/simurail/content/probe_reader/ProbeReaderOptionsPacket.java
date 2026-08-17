package com.crystaelix.simurail.content.probe_reader;

import com.crystaelix.simurail.Simurail;

import foundry.veil.api.network.handler.ServerPacketContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;


public record ProbeReaderOptionsPacket(ProbeReaderOptions options) implements CustomPacketPayload {

	public static final Type<ProbeReaderOptionsPacket> TYPE = new Type<>(Simurail.id("probe_reader_options"));
	public static final StreamCodec<ByteBuf, ProbeReaderOptionsPacket> CODEC = ProbeReaderOptions.STREAM_CODEC.map(ProbeReaderOptionsPacket::new, ProbeReaderOptionsPacket::options);

	@Override
	public Type<ProbeReaderOptionsPacket> type() {
		return TYPE;
	}

	public void handle(ServerPacketContext context) {
		if(context.player().containerMenu instanceof ProbeReaderMenu menu && context.level().getBlockEntity(menu.pos) instanceof ProbeReaderBlockEntity reader) {
			reader.setOptions(options);
		}
	}
}
