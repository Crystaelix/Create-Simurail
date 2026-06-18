package com.crystaelix.simurail.content.automatic_coupler;

import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

import com.crystaelix.simurail.api.math.SimurailMath;
import com.crystaelix.simurail.api.math.SimurailMathf;
import com.crystaelix.simurail.content.SimurailPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;

import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class AutomaticCouplerRenderer extends SafeBlockEntityRenderer<AutomaticCouplerBlockEntity> {

	public AutomaticCouplerRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	protected void renderSafe(AutomaticCouplerBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
		Level level = be.getLevel();

		if(VisualizationManager.supportsVisualization(level)) {
			return;
		}

		couplerOffset.set(be.getDirection()).mul(-0.4375F);

		boolean hasPartner;
		be.getJointPosition(jointPos);
		if(be.partnerPos != null && level.getBlockEntity(be.partnerPos) instanceof AutomaticCouplerBlockEntity partner) {
			hasPartner = true;
			ClientSubLevel selfSubLevel = Sable.HELPER.getContainingClient(be);
			ClientSubLevel partnerSubLevel = Sable.HELPER.getContainingClient(partner);
			Pose3dc selfPose = selfSubLevel == null ? SimurailMath.POSE_I : selfSubLevel.renderPose(partialTick);
			Pose3dc partnerPose = partnerSubLevel == null ? SimurailMath.POSE_I : partnerSubLevel.renderPose(partialTick);
			selfPose.transformPositionInverse(partnerPose.transformPosition(partner.getJointPosition(targetPos)));
			selfPose.orientation().transformInverse(partnerPose.orientation().transform(SimurailMathf.DIR_YP, targetVert));
		}
		else {
			hasPartner = false;
			be.getEndPosition(targetPos);
		}
		couplerDir.set(
				targetPos.x - jointPos.x,
				targetPos.y - jointPos.y,
				targetPos.z - jointPos.z).normalize();
		SimurailMathf.rot(couplerDir, SimurailMathf.DIR_YP, couplerRot);
		if(hasPartner) {
			SimurailMathf.rot(couplerDir, targetVert, targetRot);
			couplerRot.nlerp(targetRot, 0.5F);
		}

		BlockState air = Blocks.AIR.defaultBlockState();
		VertexConsumer vb = bufferSource.getBuffer(RenderType.solid());

		poseStack.pushPose();
		poseStack.translate(0.5F, 0.5F, 0.5F);

		PartialModel model = be.isShort ? SimurailPartialModels.COUPLER_BAR_SHORT : SimurailPartialModels.COUPLER_BAR;
		CachedBuffers.partial(model, air).
		translate(couplerOffset).
		rotate(couplerRot).
		light(light).overlay(overlay).
		renderInto(poseStack, vb);

		CachedBuffers.partial(PartialModel.of(be.type.modelId()), air).
		translate(couplerOffset).
		rotate(couplerRot).
		translate(be.getLength(), 0, 0).
		light(light).overlay(overlay).
		renderInto(poseStack, vb);

		poseStack.popPose();
	}

	private final Vector3f couplerOffset = new Vector3f();
	private final Vector3d jointPos = new Vector3d();
	private final Vector3d targetPos = new Vector3d();
	private final Vector3f targetVert = new Vector3f();
	private final Vector3f couplerDir = new Vector3f();
	private final Quaternionf targetRot = new Quaternionf();
	private final Quaternionf couplerRot = new Quaternionf();
}
