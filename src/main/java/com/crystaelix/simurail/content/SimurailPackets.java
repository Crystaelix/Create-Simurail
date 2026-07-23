package com.crystaelix.simurail.content;

import com.crystaelix.simurail.Simurail;
import com.crystaelix.simurail.content.automatic_coupler.AutomaticCouplerGangwayOptionsPacket;
import com.crystaelix.simurail.content.bogey.PhysicsBogeyOptionsPacket;
import com.crystaelix.simurail.content.bogey.PhysicsBogeyRenderDataPacket;
import com.crystaelix.simurail.content.connector.ConnectorConnectPacket;
import com.crystaelix.simurail.content.gangway_frame.GangwayFrameOptionsPacket;

import foundry.veil.api.network.VeilPacketManager;

public class SimurailPackets {

	private static final VeilPacketManager INSTANCE = VeilPacketManager.create(Simurail.MOD_ID, "0.1");

	public static void register() {
		INSTANCE.registerClientbound(PhysicsBogeyRenderDataPacket.TYPE, PhysicsBogeyRenderDataPacket.CODEC, PhysicsBogeyRenderDataPacket::handle);

		INSTANCE.registerServerbound(PhysicsBogeyOptionsPacket.TYPE, PhysicsBogeyOptionsPacket.CODEC, PhysicsBogeyOptionsPacket::handle);
		INSTANCE.registerServerbound(AutomaticCouplerGangwayOptionsPacket.TYPE, AutomaticCouplerGangwayOptionsPacket.CODEC, AutomaticCouplerGangwayOptionsPacket::handle);
		INSTANCE.registerServerbound(GangwayFrameOptionsPacket.TYPE, GangwayFrameOptionsPacket.CODEC, GangwayFrameOptionsPacket::handle);
		INSTANCE.registerServerbound(ConnectorConnectPacket.TYPE, ConnectorConnectPacket.CODEC, ConnectorConnectPacket::handle);
	}
}
