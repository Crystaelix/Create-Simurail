package com.crystaelix.simurail.api.network;

import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import com.simibubi.create.AllBogeyStyles;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeySizes.BogeySize;
import com.simibubi.create.content.trains.bogey.BogeyStyle;

import dev.ryanhcode.sable.util.SableBufferUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class SimurailStreamCodecs {

	public static final StreamCodec<ByteBuf, Vector3dc> VECTOR3D = StreamCodec.of(
			SableBufferUtils::write,
			b -> SableBufferUtils.read(b, new Vector3d()));
	public static final StreamCodec<ByteBuf, Quaterniondc> QUATERNIOND = StreamCodec.of(
			SableBufferUtils::write,
			b -> SableBufferUtils.read(b, new Quaterniond()));

	public static final StreamCodec<ByteBuf, BogeyStyle> BOGEY_STYLE = ResourceLocation.STREAM_CODEC.map(
			AllBogeyStyles.BOGEY_STYLES::get, s -> s.id);
	public static final StreamCodec<ByteBuf, BogeySize> BOGEY_SIZE = ResourceLocation.STREAM_CODEC.map(
			BogeySizes.all()::get, BogeySize::id);
}
