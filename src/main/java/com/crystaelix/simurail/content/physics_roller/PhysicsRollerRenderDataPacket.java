package com.crystaelix.simurail.content.physics_roller;

import com.crystaelix.simurail.Simurail;

import foundry.veil.api.network.handler.ClientPacketContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;

public record PhysicsRollerRenderDataPacket(BlockPos pos, float visualSpeed) implements CustomPacketPayload {

	public static final Type<PhysicsRollerRenderDataPacket> TYPE = new Type<>(Simurail.id("physics_roller_render_data"));
	public static final StreamCodec<ByteBuf, PhysicsRollerRenderDataPacket> CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, PhysicsRollerRenderDataPacket::pos,
			ByteBufCodecs.FLOAT, PhysicsRollerRenderDataPacket::visualSpeed,
			PhysicsRollerRenderDataPacket::new);

	public PhysicsRollerRenderDataPacket(PhysicsRollerBlockEntity roller) {
		this(roller.getBlockPos(), roller.visualSpeed);
	}

	@Override
	public Type<PhysicsRollerRenderDataPacket> type() {
		return TYPE;
	}

	public void handle(ClientPacketContext context) {
		Level level = context.level();
		if(level.getBlockEntity(pos) instanceof PhysicsRollerBlockEntity roller) {
			roller.visualSpeed = visualSpeed;
		}
	}
}
