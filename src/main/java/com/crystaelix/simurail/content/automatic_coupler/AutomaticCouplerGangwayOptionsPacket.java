package com.crystaelix.simurail.content.automatic_coupler;

import com.crystaelix.simurail.Simurail;
import com.crystaelix.simurail.content.gangway_frame.GangwayFrameBlockShape;

import foundry.veil.api.network.handler.ServerPacketContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;


public record AutomaticCouplerGangwayOptionsPacket(BlockPos pos, GangwayFrameBlockShape shape, float restLength) implements CustomPacketPayload {

	public static final Type<AutomaticCouplerGangwayOptionsPacket> TYPE = new Type<>(Simurail.id("automatic_coupler_gangway_options"));
	public static final StreamCodec<ByteBuf, AutomaticCouplerGangwayOptionsPacket> CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, AutomaticCouplerGangwayOptionsPacket::pos,
			GangwayFrameBlockShape.STREAM_CODEC, AutomaticCouplerGangwayOptionsPacket::shape,
			ByteBufCodecs.FLOAT, AutomaticCouplerGangwayOptionsPacket::restLength,
			AutomaticCouplerGangwayOptionsPacket::new);

	@Override
	public Type<AutomaticCouplerGangwayOptionsPacket> type() {
		return TYPE;
	}

	public void handle(ServerPacketContext context) {
		Level level = context.level();
		if(level.getBlockEntity(pos) instanceof AutomaticCouplerBlockEntity coupler) {
			coupler.gangwayRestLength = restLength;
			coupler.setChanged();
			coupler.sendData();
			if(GangwayFrameBlockShape.COUPLER.contains(shape)) {
				level.setBlock(pos, coupler.getBlockState().setValue(AutomaticCouplerBlock.GANGWAY_SHAPE, shape), Block.UPDATE_ALL);
			}
		}
	}
}
