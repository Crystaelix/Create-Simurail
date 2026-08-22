package com.crystaelix.simurail.content.remote_controller;

import com.crystaelix.simurail.Simurail;

import foundry.veil.api.network.handler.ServerPacketContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;


public record RemoteControllerModePacket(RemoteControllerMode mode) implements CustomPacketPayload {

	public static final Type<RemoteControllerModePacket> TYPE = new Type<>(Simurail.id("remote_controller_mode"));
	public static final StreamCodec<ByteBuf, RemoteControllerModePacket> CODEC = RemoteControllerMode.STREAM_CODEC.map(RemoteControllerModePacket::new, RemoteControllerModePacket::mode);

	@Override
	public Type<RemoteControllerModePacket> type() {
		return TYPE;
	}

	public void handle(ServerPacketContext context) {
		if(context.player().containerMenu instanceof RemoteControllerMenu menu && context.level().getBlockEntity(menu.pos) instanceof RemoteControllerBlockEntity controller) {
			controller.setMode(mode);
		}
	}
}
