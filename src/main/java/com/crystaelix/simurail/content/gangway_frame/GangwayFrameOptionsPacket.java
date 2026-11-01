package com.crystaelix.simurail.content.gangway_frame;

import com.crystaelix.simurail.Simurail;
import com.crystaelix.simurail.content.gangway_frame.copycat.CopycatGangwayFrameBlock;
import com.crystaelix.simurail.content.gangway_frame.copycat.CopycatGangwayFrameBlockEntity;

import foundry.veil.api.network.handler.ServerPacketContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public record GangwayFrameOptionsPacket(ResourceKey<Level> dimension, BlockPos pos, GangwayFrameBlockShape shape, float restLength) implements CustomPacketPayload {

	public static final Type<GangwayFrameOptionsPacket> TYPE = new Type<>(Simurail.id("gangway_frame_options"));
	public static final StreamCodec<ByteBuf, GangwayFrameOptionsPacket> CODEC = StreamCodec.composite(
			ResourceKey.streamCodec(Registries.DIMENSION), GangwayFrameOptionsPacket::dimension,
			BlockPos.STREAM_CODEC, GangwayFrameOptionsPacket::pos,
			GangwayFrameBlockShape.STREAM_CODEC, GangwayFrameOptionsPacket::shape,
			ByteBufCodecs.FLOAT, GangwayFrameOptionsPacket::restLength,
			GangwayFrameOptionsPacket::new);

	@Override
	public Type<GangwayFrameOptionsPacket> type() {
		return TYPE;
	}

	public void handle(ServerPacketContext context) {
		Level level = context.server().getLevel(dimension);
		if(level != null) {
			switch(level.getBlockEntity(pos)) {
			case GangwayFrameBlockEntity gangway -> {
				gangway.restLength = restLength;
				gangway.setChanged();
				gangway.sendData();
				if(shape != GangwayFrameBlockShape.NONE) {
					level.setBlock(pos, gangway.getBlockState().setValue(GangwayFrameBlock.SHAPE, shape), Block.UPDATE_ALL);
				}
			}
			case CopycatGangwayFrameBlockEntity copycat -> {
				copycat.restLength = restLength;
				copycat.setChanged();
				copycat.sendData();
				if(shape != GangwayFrameBlockShape.NONE) {
					level.setBlock(pos, copycat.getBlockState().setValue(CopycatGangwayFrameBlock.SHAPE, shape), Block.UPDATE_ALL);
				}
			}
			case null, default -> {}
			}
		}
	}
}
