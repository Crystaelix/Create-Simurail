package com.crystaelix.simurail.content.bogey;

import com.crystaelix.simurail.Simurail;

import foundry.veil.api.network.handler.ServerPacketContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;

public record PhysicsBogeySetOptionsPacket(BlockPos pos, PhysicsBogeyOptions options) implements CustomPacketPayload {

	public static final Type<PhysicsBogeySetOptionsPacket> TYPE = new Type<>(Simurail.id("physics_bogey_options"));
	public static final StreamCodec<ByteBuf, PhysicsBogeySetOptionsPacket> CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, PhysicsBogeySetOptionsPacket::pos,
			PhysicsBogeyOptions.STREAM_CODEC, PhysicsBogeySetOptionsPacket::options,
			PhysicsBogeySetOptionsPacket::new);

	@Override
	public Type<PhysicsBogeySetOptionsPacket> type() {
		return TYPE;
	}

	public void handle(ServerPacketContext context) {
		Level level = context.level();
		if(level.getBlockEntity(pos) instanceof PhysicsBogeyBlockEntity bogey) {
			bogey.setOptions(options);
		}
	}
}
