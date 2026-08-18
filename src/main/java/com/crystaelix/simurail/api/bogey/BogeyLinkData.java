package com.crystaelix.simurail.api.bogey;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

public record BogeyLinkData(BlockPos position, Direction direction, ResourceLocation dimension) {

	public static final Codec<BogeyLinkData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			BlockPos.CODEC.fieldOf("position").forGetter(BogeyLinkData::position),
			Direction.CODEC.fieldOf("direction").forGetter(BogeyLinkData::direction),
			ResourceLocation.CODEC.fieldOf("dimension").forGetter(BogeyLinkData::dimension)).
			apply(instance, BogeyLinkData::new));
	public static final StreamCodec<ByteBuf, BogeyLinkData> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, BogeyLinkData::position,
			Direction.STREAM_CODEC, BogeyLinkData::direction,
			ResourceLocation.STREAM_CODEC, BogeyLinkData::dimension,
			BogeyLinkData::new);
	
	public AABB getOutline() {
		return AABB.ofSize(
				position.getCenter().add(direction.getStepX() * 0.28125, 0, direction.getStepZ() * 0.28125),
				direction.getStepX() == 0 ? 1 : 0.4375, 1, direction.getStepZ() == 0 ? 1 : 0.4375);
	}
}
