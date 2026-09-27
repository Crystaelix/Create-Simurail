package com.crystaelix.simurail.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.crystaelix.simurail.content.SimurailEdgePoints;
import com.crystaelix.simurail.content.SimurailPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour.RenderedTrackOverlayType;
import com.simibubi.create.content.trains.track.TrackTargetingClient;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.createmod.catnip.render.SuperRenderTypeBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

@Mixin(TrackTargetingClient.class)
public abstract class TrackTargetingClientMixin {

	@Shadow
	static EdgePointType<?> lastType;
	@Shadow
	static BlockPos lastHovered;
	@Shadow
	static boolean lastDirection;
	@Shadow
	static BezierTrackPointLocation lastHoveredBezierSegment;

	@Inject(method = "render", at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lcom/simibubi/create/content/trains/track/TrackTargetingClient;lastType:Lcom/simibubi/create/content/trains/graph/EdgePointType;", ordinal = 0), cancellable = true)
	private static void simurail$renderCustom(PoseStack poseStack, SuperRenderTypeBuffer buffer, Vec3 camera, CallbackInfo ci) {
		if(lastType == SimurailEdgePoints.TRACK_SWITCH) {
			ci.cancel();

			Minecraft mc = Minecraft.getInstance();
			Level level = mc.level;
			BlockPos pos = lastHovered;
			BezierTrackPointLocation loc = lastHoveredBezierSegment;

			if(loc != null && level.getBlockEntity(lastHovered) instanceof TrackBlockEntity track) {
				BezierConnection curve = track.getConnections().get(loc.curveTarget());
				if(curve == null || (lastDirection ? loc.segment() >= 3 : loc.segment() <= curve.getSegmentCount() - 4)) {
					return;
				}
			}

			int light = LevelRenderer.getLightColor(level, pos);
			Direction.AxisDirection direction = lastDirection ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;
			BlockState trackState = level.getBlockState(pos);
			Block block = trackState.getBlock();

			if(block instanceof ITrackBlock trackBlock) {
				VertexConsumer vb = buffer.getBuffer(RenderType.solid());
				PartialModel model = SimurailPartialModels.TRACK_SWITCH_NONE;
				SuperByteBuffer handle = CachedBuffers.partial(model, trackState).translate(Vec3.atLowerCornerOf(pos).subtract(camera));
				trackBlock.prepareTrackOverlay(handle, level, pos, trackState, loc, direction, RenderedTrackOverlayType.SIGNAL);
				handle.
				translate(0.5, 0, 0.5).
				scale(1.0625F).
				translate(-0.5, 0, -0.5).
				light(light).
				renderInto(poseStack, vb);
			}
		}
	}
}
