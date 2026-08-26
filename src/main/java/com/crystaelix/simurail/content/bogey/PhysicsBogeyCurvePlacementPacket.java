package com.crystaelix.simurail.content.bogey;

import org.joml.Quaterniond;
import org.joml.Vector3d;

import com.crystaelix.simurail.Simurail;
import com.crystaelix.simurail.api.bogey.BogeyType;
import com.crystaelix.simurail.api.math.CubicBezier3dc;
import com.crystaelix.simurail.api.math.SimurailMath;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.content.trains.track.TrackBlockEntity;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.SubLevel;
import foundry.veil.api.network.handler.ServerPacketContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;

public record PhysicsBogeyCurvePlacementPacket(BlockPos curveStart, BezierTrackPointLocation curveLocation) implements CustomPacketPayload {

	public static final Type<PhysicsBogeyCurvePlacementPacket> TYPE = new Type<>(Simurail.id("physics_bogey_curve_placement"));
	public static final StreamCodec<ByteBuf, PhysicsBogeyCurvePlacementPacket> CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, PhysicsBogeyCurvePlacementPacket::curveStart,
			BezierTrackPointLocation.STREAM_CODEC, PhysicsBogeyCurvePlacementPacket::curveLocation,
			PhysicsBogeyCurvePlacementPacket::new);

	@Override
	public Type<PhysicsBogeyCurvePlacementPacket> type() {
		return TYPE;
	}

	public void handle(ServerPacketContext context) {
		Level level = context.level();
		ServerPlayer player = context.player();
		ItemStack stack = player.getMainHandItem();
		if(level.getBlockEntity(curveStart) instanceof TrackBlockEntity track && stack.getItem() instanceof PhysicsBogeyBlockItem bogeyItem) {
			BlockState state = bogeyItem.defaultBlockState();
			BezierConnection connection = track.getConnections().get(curveLocation.curveTarget());
			if(state != null && connection != null && BogeyType.hasDefault(connection.getMaterial().trackType, state.getValue(BlockStateProperties.INVERTED))) {
				state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST);
				boolean inverted = state.getValue(BlockStateProperties.INVERTED);
				CubicBezier3dc curve = SimurailMath.cachedControlPoints(connection);

				Vector3d lookDirection = JOMLConversion.toJOML(player.getLookAngle());
				SubLevel subLevel = Sable.HELPER.getContaining(level, curveStart);
				if(subLevel != null) lookDirection = subLevel.logicalPose().transformNormalInverse(lookDirection);

				int segment = curveLocation.segment();
				double t = connection.getSegmentT(segment + 1); 
				Vector3d trackDirection = curve.velocity(t, new Vector3d()).normalize();
				if(lookDirection.dot(trackDirection) < 0) trackDirection.mul(-1);
				Vector3d trackNormal = JOMLConversion.toJOML(connection.getNormal(t));

				double offset = inverted ? -0.625 : 1.625;
				Vector3d position = curve.position(t, new Vector3d()).fma(offset, trackNormal);
				Quaterniond orientation = SimurailMath.rot(trackDirection, trackNormal, new Quaterniond());

				bogeyItem.placeSubLevel(level, position, orientation, player, stack, state, connection.getMaterial().trackType);

				SoundType soundType = state.getSoundType();
				level.playSound(player, position.x, position.y, position.z, soundType.getPlaceSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1) / 2, soundType.getPitch() * 0.8F);
				level.gameEvent(GameEvent.BLOCK_PLACE, JOMLConversion.toMojang(position), GameEvent.Context.of(player, state));
				stack.consume(1, player);
			}
		}
	}
}
