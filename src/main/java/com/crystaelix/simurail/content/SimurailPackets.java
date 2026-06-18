package com.crystaelix.simurail.content;

import com.crystaelix.simurail.Simurail;
import com.crystaelix.simurail.content.bogey.PhysicsBogeyRenderDataPacket;
import com.crystaelix.simurail.content.bogey.PhysicsBogeySetOptionsPacket;
import com.crystaelix.simurail.content.steering_connector.SteeringConnectPacket;

import foundry.veil.api.network.VeilPacketManager;

public class SimurailPackets {

	private static final VeilPacketManager INSTANCE = VeilPacketManager.create(Simurail.MOD_ID, "0.1");

	public static void register() {
		INSTANCE.registerClientbound(PhysicsBogeyRenderDataPacket.TYPE, PhysicsBogeyRenderDataPacket.CODEC, PhysicsBogeyRenderDataPacket::handle);

		INSTANCE.registerServerbound(PhysicsBogeySetOptionsPacket.TYPE, PhysicsBogeySetOptionsPacket.CODEC, PhysicsBogeySetOptionsPacket::handle);
		INSTANCE.registerServerbound(SteeringConnectPacket.TYPE, SteeringConnectPacket.CODEC, SteeringConnectPacket::handle);
	}
}
