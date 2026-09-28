package com.crystaelix.simurail.content.track_switch;

import org.joml.Vector3d;

import com.crystaelix.simurail.api.math.SimurailMath;
import com.crystaelix.simurail.content.SimurailPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour.RenderedTrackOverlayType;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;

import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class TrackSwitchRenderer extends SmartBlockEntityRenderer<TrackSwitchBlockEntity> {

	public TrackSwitchRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected void renderSafe(TrackSwitchBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
		super.renderSafe(be, partialTick, poseStack, bufferSource, light, overlay);
		Level level = be.getLevel();
		if(VisualizationManager.supportsVisualization(level)) {
			return;
		}

		BlockPos pos = be.getBlockPos();
		TrackTargetingBehaviour<TrackSwitch> target = be.edgePoint;
		BlockPos targetPosition = target.getGlobalPosition();
		BlockState trackState = level.getBlockState(targetPosition);
		Block block = trackState.getBlock();
		VertexConsumer vb = bufferSource.getBuffer(RenderType.solid());

		if(block instanceof ITrackBlock trackBlock) {
			PartialModel model = getOverlayModel(be);
			SuperByteBuffer handle = CachedBuffers.partial(model, trackState).translate(targetPosition.subtract(pos));
			trackBlock.prepareTrackOverlay(handle, level, targetPosition, trackState, target.getTargetBezier(), target.getTargetDirection(), RenderedTrackOverlayType.SIGNAL);
			handle.light(light).overlay(overlay).renderInto(poseStack, vb);
		}

		BlockState state = be.getBlockState();
		be.trackRot.transform(SimurailMath.DIR_XP, trackDir);
		float trackAngle = (float)Math.atan2(-trackDir.z, trackDir.x);
		float signAngle = be.lerpedAngle.getValue(partialTick);

		CachedBuffers.partial(SimurailPartialModels.TRACK_SWITCH_SIGN, state).
		translate(0.5, 0.75, 0.5).
		rotateY(trackAngle).
		rotateX(signAngle).
		light(light).overlay(overlay).
		renderInto(poseStack, vb);
	}

	protected static PartialModel getOverlayModel(TrackSwitchBlockEntity be) {
		if(be.hasStraightExit() && be.hasLeftExit() && be.hasRightExit()) {
			return switch(be.state) {
			case LEFT -> SimurailPartialModels.TRACK_SWITCH_3WAY_LEFT;
			case RIGHT -> SimurailPartialModels.TRACK_SWITCH_3WAY_RIGHT;
			case null, default -> SimurailPartialModels.TRACK_SWITCH_3WAY_STRAIGHT;
			};
		}
		else if(be.hasStraightExit() && be.hasLeftExit()) {
			return be.state == TrackSwitchState.LEFT ?
					SimurailPartialModels.TRACK_SWITCH_LEFT_TURN :
						SimurailPartialModels.TRACK_SWITCH_LEFT_STRAIGHT;
		}
		else if(be.hasStraightExit() && be.hasRightExit()) {
			return be.state == TrackSwitchState.RIGHT ?
					SimurailPartialModels.TRACK_SWITCH_RIGHT_TURN :
						SimurailPartialModels.TRACK_SWITCH_RIGHT_STRAIGHT;
		}
		else if(be.hasLeftExit() && be.hasRightExit()) {
			return be.state == TrackSwitchState.LEFT ?
					SimurailPartialModels.TRACK_SWITCH_WYE_LEFT :
						SimurailPartialModels.TRACK_SWITCH_WYE_RIGHT;
		}
		return SimurailPartialModels.TRACK_SWITCH_NONE;
	}

	private final Vector3d trackDir = new Vector3d();
}
