package com.crystaelix.simurail.content.steering_connector;

import com.crystaelix.simurail.Simurail;

import foundry.veil.api.network.handler.ServerPacketContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SteeringConnectPacket(BlockPos pos1, boolean front1, BlockPos pos2, boolean front2) implements CustomPacketPayload {

	public static final Type<SteeringConnectPacket> TYPE = new Type<>(Simurail.id("physics_bogey_connect"));
	public static final StreamCodec<ByteBuf, SteeringConnectPacket> CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, SteeringConnectPacket::pos1,
			ByteBufCodecs.BOOL, SteeringConnectPacket::front1,
			BlockPos.STREAM_CODEC, SteeringConnectPacket::pos2,
			ByteBufCodecs.BOOL, SteeringConnectPacket::front2,
			SteeringConnectPacket::new);

	@Override
	public Type<SteeringConnectPacket> type() {
		return TYPE;
	}

	public void handle(ServerPacketContext context) {
		if(context.level().getBlockEntity(pos1) instanceof SteeringConnectable conn1) {
			if(pos1.equals(pos2)) {
				if(front1 == front2) {
					conn1.disconnectSteering(front1);
				}
			}
			else if(context.level().getBlockEntity(pos2) instanceof SteeringConnectable conn2) {
				conn1.connectSteering(front1, conn2, front2);
			}
		}
	}
}
