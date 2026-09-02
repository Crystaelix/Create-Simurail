package com.crystaelix.simurail.content.physics_roller;

import com.crystaelix.simurail.content.SimurailPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;

import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class PhysicsRollerRenderer extends SmartBlockEntityRenderer<PhysicsRollerBlockEntity> {

	public PhysicsRollerRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected void renderSafe(PhysicsRollerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

		BlockState state = be.getBlockState();
		Direction facing = state.getValue(PhysicsRollerBlock.FACING);
		VertexConsumer vc = buffer.getBuffer(RenderType.cutoutMipped());

		ms.pushPose();
		ms.translate(0, -0.25, 0);
		SuperByteBuffer wheel = CachedBuffers.partial(SimurailPartialModels.PHYSICS_ROLLER_WHEEL, state);
		wheel.translate(Vec3.atLowerCornerOf(facing.getNormal()).scale(17 / 16F));
		HarvesterRenderer.transform(be.getLevel(), facing, wheel, be.getAnimatedSpeed(), Vec3.ZERO);
		wheel.translate(0, -0.5, 0.5).
		rotateYDegrees(90).
		light(light).
		renderInto(ms, vc);
		ms.popPose();

		CachedBuffers.partial(SimurailPartialModels.PHYSICS_ROLLER_FRAME, state).
		rotateCentered(AngleHelper.rad(AngleHelper.horizontalAngle(facing) + 180), Direction.UP).
		light(light).
		renderInto(ms, vc);
	}
}
