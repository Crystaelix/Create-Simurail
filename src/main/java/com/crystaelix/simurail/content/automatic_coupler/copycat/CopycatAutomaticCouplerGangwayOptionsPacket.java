package com.crystaelix.simurail.content.automatic_coupler.copycat;

import com.crystaelix.simurail.Simurail;

import foundry.veil.api.network.handler.ServerPacketContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;


public record CopycatAutomaticCouplerGangwayOptionsPacket(ResourceKey<Level> dimension, BlockPos pos, float restLength) implements CustomPacketPayload {

	public static final Type<CopycatAutomaticCouplerGangwayOptionsPacket> TYPE = new Type<>(Simurail.id("copycat_automatic_coupler_gangway_options"));
	public static final StreamCodec<ByteBuf, CopycatAutomaticCouplerGangwayOptionsPacket> CODEC = StreamCodec.composite(
			ResourceKey.streamCodec(Registries.DIMENSION), CopycatAutomaticCouplerGangwayOptionsPacket::dimension,
			BlockPos.STREAM_CODEC, CopycatAutomaticCouplerGangwayOptionsPacket::pos,
			ByteBufCodecs.FLOAT, CopycatAutomaticCouplerGangwayOptionsPacket::restLength,
			CopycatAutomaticCouplerGangwayOptionsPacket::new);

	@Override
	public Type<CopycatAutomaticCouplerGangwayOptionsPacket> type() {
		return TYPE;
	}

	public void handle(ServerPacketContext context) {
		Level level = context.server().getLevel(dimension);
		if(level != null && level.getBlockEntity(pos) instanceof CopycatAutomaticCouplerBlockEntity coupler) {
			coupler.gangwayRestLength = restLength;
			coupler.setChanged();
			coupler.sendData();
		}
	}
}
