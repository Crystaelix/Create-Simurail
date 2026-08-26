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

public record PhysicsBogeyRenderDataPacket(BlockPos pos, Vector3dc pivotOffset, Quaterniondc pivotRot, float visualSpeed, float slipSpeed, float movementSpeed) implements CustomPacketPayload {

	public static final Type<PhysicsBogeyRenderDataPacket> TYPE = new Type<>(Simurail.id("physics_bogey_render_data"));
	public static final StreamCodec<ByteBuf, PhysicsBogeyRenderDataPacket> CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, PhysicsBogeyRenderDataPacket::pos,
			SimurailStreamCodecs.VECTOR3D_F, PhysicsBogeyRenderDataPacket::pivotOffset,
			SimurailStreamCodecs.QUATERNIOND_F, PhysicsBogeyRenderDataPacket::pivotRot,
			ByteBufCodecs.FLOAT, PhysicsBogeyRenderDataPacket::visualSpeed,
			ByteBufCodecs.FLOAT, PhysicsBogeyRenderDataPacket::slipSpeed,
			ByteBufCodecs.FLOAT, PhysicsBogeyRenderDataPacket::movementSpeed,
			PhysicsBogeyRenderDataPacket::new);

	public PhysicsBogeyRenderDataPacket(PhysicsBogeyBlockEntity bogey) {
		this(bogey.getBlockPos(), bogey.localPivotOffset, bogey.localPivotRot, (float)bogey.visualSpeed, (float)bogey.slipSpeed, (float)bogey.getMovementSpeed());
	}

	@Override
	public Type<PhysicsBogeyRenderDataPacket> type() {
		return TYPE;
	}

	public void handle(ClientPacketContext context) {
		Level level = context.level();
		if(level.getBlockEntity(pos) instanceof PhysicsBogeyBlockEntity bogey) {
			bogey.updateRenderData(pivotOffset, pivotRot, visualSpeed, slipSpeed, movementSpeed);
		}
	}
}
