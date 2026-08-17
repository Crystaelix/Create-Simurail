package com.crystaelix.simurail.content.bogey;

import com.crystaelix.simurail.Simurail;

import foundry.veil.api.network.handler.ServerPacketContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public record PhysicsBogeyOptionsPacket(PhysicsBogeyOptions options) implements CustomPacketPayload {

	public static final Type<PhysicsBogeyOptionsPacket> TYPE = new Type<>(Simurail.id("physics_bogey_options"));
	public static final StreamCodec<ByteBuf, PhysicsBogeyOptionsPacket> CODEC =
			PhysicsBogeyOptions.STREAM_CODEC.map(PhysicsBogeyOptionsPacket::new, PhysicsBogeyOptionsPacket::options);

	@Override
	public Type<PhysicsBogeyOptionsPacket> type() {
		return TYPE;
	}

	public void handle(ServerPacketContext context) {
		if(context.player().containerMenu instanceof PhysicsBogeyMenu menu && context.level().getBlockEntity(menu.pos) instanceof PhysicsBogeyBlockEntity bogey) {
			bogey.setOptions(options);
		}
	}
}
