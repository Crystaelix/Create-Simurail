package com.crystaelix.simurail.content.probe_reader;

import com.crystaelix.simurail.Simurail;

import foundry.veil.api.network.handler.ServerPacketContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;


public record ProbeReaderOptionsPacket(ProbeReaderOptions options) implements CustomPacketPayload {

	public static final Type<ProbeReaderOptionsPacket> TYPE = new Type<>(Simurail.id("probe_reader_options"));
	public static final StreamCodec<ByteBuf, ProbeReaderOptionsPacket> CODEC = ProbeReaderOptions.STREAM_CODEC.map(ProbeReaderOptionsPacket::new, ProbeReaderOptionsPacket::options);

	@Override
	public Type<ProbeReaderOptionsPacket> type() {
		return TYPE;
	}

	public void handle(ServerPacketContext context) {
		if(context.player().containerMenu instanceof ProbeReaderMenu menu) {
			Level level = context.server().getLevel(menu.dimension);
			if(level != null && level.getBlockEntity(menu.pos) instanceof ProbeReaderBlockEntity reader) {
				reader.setOptions(options);
			}
		}
	}
}
