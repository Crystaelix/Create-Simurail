package com.crystaelix.simurail.content.physics_roller;

import com.crystaelix.simurail.content.SimurailPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;

import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class PhysicsRollerRenderer extends SmartBlockEntityRenderer<PhysicsRollerBlockEntity> {

	public PhysicsRollerRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected void renderSafe(PhysicsRollerBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
		super.renderSafe(be, partialTick, poseStack, buffer, light, overlay);
		Level level = be.getLevel();
		if(VisualizationManager.supportsVisualization(level)) {
			return;
		}

		BlockState state = be.getBlockState();
		Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
		VertexConsumer vb = buffer.getBuffer(RenderType.cutoutMipped());

		float time = (AnimationTickHolder.getTicks(level) + partialTick) / 20;
		float yAngle = AngleHelper.horizontalAngle(facing);
		float xAngle = (time * be.getAnimatedSpeed()) % 360;

		CachedBuffers.partial(SimurailPartialModels.PHYSICS_ROLLER_WHEEL, state).
		rotateYCenteredDegrees(yAngle).
		translate(0, -0.25, 1.0625).
		rotateXDegrees(-xAngle).
		translate(0, -0.5, 0.5).
		rotateYDegrees(90).
		light(light).
		renderInto(poseStack, vb);

		CachedBuffers.partial(SimurailPartialModels.PHYSICS_ROLLER_FRAME, state).
		rotateYCenteredDegrees(yAngle + 180).
		light(light).
		renderInto(poseStack, vb);
	}
}
