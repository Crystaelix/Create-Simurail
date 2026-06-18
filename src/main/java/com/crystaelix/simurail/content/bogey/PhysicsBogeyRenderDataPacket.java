package com.crystaelix.simurail.content.bogey;

import org.joml.Quaterniondc;
import org.joml.Vector3dc;

import com.crystaelix.simurail.Simurail;
import com.crystaelix.simurail.api.network.SimurailStreamCodecs;

import foundry.veil.api.network.handler.ClientPacketContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;

public record PhysicsBogeyRenderDataPacket(BlockPos pos, Vector3dc pivotOffset, Quaterniondc pivotRot, double visualSpeed) implements CustomPacketPayload {

	public static final Type<PhysicsBogeyRenderDataPacket> TYPE = new Type<>(Simurail.id("physics_bogey_render_data"));
	public static final StreamCodec<ByteBuf, PhysicsBogeyRenderDataPacket> CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, PhysicsBogeyRenderDataPacket::pos,
			SimurailStreamCodecs.VECTOR3D, PhysicsBogeyRenderDataPacket::pivotOffset,
			SimurailStreamCodecs.QUATERNIOND, PhysicsBogeyRenderDataPacket::pivotRot,
			ByteBufCodecs.DOUBLE, PhysicsBogeyRenderDataPacket::visualSpeed,
			PhysicsBogeyRenderDataPacket::new);

	public PhysicsBogeyRenderDataPacket(PhysicsBogeyBlockEntity bogey) {
		this(bogey.getBlockPos(), bogey.localPivotOffset, bogey.localPivotRot, bogey.visualSpeed);
	}

	@Override
	public Type<PhysicsBogeyRenderDataPacket> type() {
		return TYPE;
	}

	public void handle(ClientPacketContext context) {
		Level level = context.level();
		if(level.getBlockEntity(pos) instanceof PhysicsBogeyBlockEntity bogey) {
			bogey.updateRenderData(pivotOffset, pivotRot, visualSpeed);
		}
	}
}
