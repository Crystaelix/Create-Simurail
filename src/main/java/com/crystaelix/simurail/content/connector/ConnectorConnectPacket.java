package com.crystaelix.simurail.content.connector;

import com.crystaelix.simurail.Simurail;

import foundry.veil.api.network.handler.ServerPacketContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ConnectorConnectPacket(BlockPos pos1, boolean front1, BlockPos pos2, boolean front2) implements CustomPacketPayload {

	public static final Type<ConnectorConnectPacket> TYPE = new Type<>(Simurail.id("connector_connect"));
	public static final StreamCodec<ByteBuf, ConnectorConnectPacket> CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, ConnectorConnectPacket::pos1,
			ByteBufCodecs.BOOL, ConnectorConnectPacket::front1,
			BlockPos.STREAM_CODEC, ConnectorConnectPacket::pos2,
			ByteBufCodecs.BOOL, ConnectorConnectPacket::front2,
			ConnectorConnectPacket::new);

	@Override
	public Type<ConnectorConnectPacket> type() {
		return TYPE;
	}

	public void handle(ServerPacketContext context) {
		if(context.level().getBlockEntity(pos1) instanceof ConnectorConnectable conn1) {
			if(pos1.equals(pos2)) {
				if(front1 == front2) {
					conn1.disconnect(front1);
				}
			}
			else if(context.level().getBlockEntity(pos2) instanceof ConnectorConnectable conn2) {
				conn1.connect(front1, conn2, front2);
			}
		}
	}
}
