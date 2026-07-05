package com.crystaelix.simurail.content.bogey;

import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

import com.crystaelix.simurail.api.bogey.BogeyRenderedType;
import com.crystaelix.simurail.api.math.SimurailMath;
import com.crystaelix.simurail.api.math.SimurailMathf;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;

import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class PhysicsBogeyRenderer extends KineticBlockEntityRenderer<PhysicsBogeyBlockEntity> {

	public PhysicsBogeyRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected void renderSafe(PhysicsBogeyBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
		super.renderSafe(be, partialTick, poseStack, bufferSource, light, overlay);
		Level level = be.getLevel();
		if(VisualizationManager.supportsVisualization(level)) {
			return;
		}

		be.getRenderPivotOffset(partialTick, pivotOffset);
		be.getRenderPivotRot(partialTick, pivotRot);

		poseStack.pushPose();
		poseStack.translate(0.5F, 0.5F, 0.5F);

		poseStack.pushPose();
		poseStack.translate(pivotOffset.x, pivotOffset.y, pivotOffset.z);
		poseStack.mulPose(pivotRot);
		poseStack.translate(0, (be.isInverted() ? 1 : -1) * be.options.getAxleOffset(), 0);
		poseStack.last().pose().rotate(SimurailMathf.ROT_ZNYPXP);
		poseStack.last().normal().rotate(SimurailMathf.ROT_ZNYPXP);
		BogeyRenderedType type = be.options.type;
		type.style().render(type.size(), partialTick, poseStack, bufferSource, light, overlay, be.getWheelAngle(partialTick), be.getBogeyData(), false);
		poseStack.popPose();

		BlockState air = Blocks.AIR.defaultBlockState();
		VertexConsumer vb = bufferSource.getBuffer(RenderType.solid());

		if(be.options.renderFrontConnector && be.connectionFront != null && level.getBlockEntity(be.connectionFront) instanceof PhysicsBogeyBlockEntity other) {
			poseStack.pushPose();

			pivotRot.transform(be.getConnectorAnchorOffset(partialTick, true, anchorOffset)).add(pivotOffset);
			other.getRenderPivotOffset(partialTick, otherPivotOffset);
			other.getRenderPivotRot(partialTick, otherPivotRot);
			otherPivotRot.transform(other.getConnectorAnchorOffset(partialTick, be.connectionFrontToFront, otherAnchorOffset)).add(otherPivotOffset);

			ClientSubLevel selfSubLevel = Sable.HELPER.getContainingClient(be);
			ClientSubLevel otherSubLevel = Sable.HELPER.getContainingClient(other);

			if(selfSubLevel != otherSubLevel) {
				Pose3dc selfPose = selfSubLevel == null ? SimurailMath.POSE_I : selfSubLevel.renderPose(partialTick);
				Pose3dc otherPose = otherSubLevel == null ? SimurailMath.POSE_I : otherSubLevel.renderPose(partialTick);
				selfPose.transformPositionInverse(otherPose.transformPosition(other.localCenter, otherOffset)).sub(be.localCenter);
				selfPose.orientation().transformInverse(otherPose.orientation().transform(otherAnchorOffset));
			}
			else {
				otherOffset.set(other.localCenter).sub(be.localCenter);
			}

			otherAnchorOffset.add((float)otherOffset.x, (float)otherOffset.y, (float)otherOffset.z);

			float diffX = otherAnchorOffset.x - anchorOffset.x;
			float diffY = otherAnchorOffset.y - anchorOffset.y;
			float diffZ = otherAnchorOffset.z - anchorOffset.z;

			float yRot = (float)Math.atan2(diffX, diffZ);
			float xRot = (float)Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ));

			poseStack.translate(anchorOffset.x, anchorOffset.y, anchorOffset.z);

			CachedBuffers.partial(AllPartialModels.TRAIN_COUPLING_HEAD, air).
			rotateY(Mth.PI + yRot).rotateX(xRot).
			light(light).overlay(overlay).
			renderInto(poseStack, vb);

			if(be.connectionFrontToFront ? other.options.renderFrontConnector : other.options.renderBackConnector) {
				float length = anchorOffset.distance(otherAnchorOffset) * 0.5F - 0.1875F + 0.0078125F;
				float scale = (length * 4) / 8;

				for(int j = 0; j < 8; j++) {
					CachedBuffers.partial(AllPartialModels.TRAIN_COUPLING_CABLE, air).
					rotateY(yRot).rotateX(-xRot).
					translate(0, 0, 0.1875F).
					scale(0.5F, 0.5F, scale).
					translate(0, 0, 0.125F + j * 0.25F).
					light(light).overlay(overlay).
					renderInto(poseStack, vb);
				}
			}

			poseStack.popPose();
		}

		if(be.options.renderBackConnector && be.connectionBack != null && level.getBlockEntity(be.connectionBack) instanceof PhysicsBogeyBlockEntity other) {
			poseStack.pushPose();

			pivotRot.transform(be.getConnectorAnchorOffset(partialTick, false, anchorOffset)).add(pivotOffset);
			other.getRenderPivotOffset(partialTick, otherPivotOffset);
			other.getRenderPivotRot(partialTick, otherPivotRot);
			otherPivotRot.transform(other.getConnectorAnchorOffset(partialTick, be.connectionBackToFront, otherAnchorOffset)).add(otherPivotOffset);

			ClientSubLevel selfSubLevel = Sable.HELPER.getContainingClient(be);
			ClientSubLevel otherSubLevel = Sable.HELPER.getContainingClient(other);

			if(selfSubLevel != otherSubLevel) {
				Pose3dc selfPose = selfSubLevel == null ? SimurailMath.POSE_I : selfSubLevel.renderPose(partialTick);
				Pose3dc otherPose = otherSubLevel == null ? SimurailMath.POSE_I : otherSubLevel.renderPose(partialTick);
				selfPose.transformPositionInverse(otherPose.transformPosition(other.localCenter, otherOffset)).sub(be.localCenter);
				selfPose.orientation().transformInverse(otherPose.orientation().transform(otherAnchorOffset));
			}
			else {
				otherOffset.set(other.localCenter).sub(be.localCenter);
			}

			otherAnchorOffset.add((float)otherOffset.x(), (float)otherOffset.y(), (float)otherOffset.z());

			float diffX = otherAnchorOffset.x - anchorOffset.x;
			float diffY = otherAnchorOffset.y - anchorOffset.y;
			float diffZ = otherAnchorOffset.z - anchorOffset.z;

			float yRot = (float)Math.atan2(diffX, diffZ);
			float xRot = (float)Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ));

			poseStack.translate(anchorOffset.x, anchorOffset.y, anchorOffset.z);

			CachedBuffers.partial(AllPartialModels.TRAIN_COUPLING_HEAD, air).
			rotateY(Mth.PI + yRot).rotateX(xRot).
			light(light).overlay(overlay).
			renderInto(poseStack, vb);

			if(be.connectionBackToFront ? other.options.renderFrontConnector : other.options.renderBackConnector) {
				float length = anchorOffset.distance(otherAnchorOffset) * 0.5F - 0.1875F + 0.0078125F;
				float scale = (length * 4) / 8;

				for(int j = 0; j < 8; j++) {
					CachedBuffers.partial(AllPartialModels.TRAIN_COUPLING_CABLE, air).
					rotateY(yRot).rotateX(-xRot).
					translate(0, 0, 0.1875F).
					scale(0.5F, 0.5F, scale).
					translate(0, 0, 0.125F + j * 0.25F).
					light(light).overlay(overlay).
					renderInto(poseStack, vb);
				}
			}

			poseStack.popPose();
		}

		poseStack.popPose();
	}

	@Override
	protected BlockState getRenderedBlockState(PhysicsBogeyBlockEntity be) {
		return shaft(getRotationAxisOf(be));
	}

	private final Vector3f pivotOffset = new Vector3f();
	private final Quaternionf pivotRot = new Quaternionf();
	private final Vector3f anchorOffset = new Vector3f();

	private final Vector3d otherOffset = new Vector3d();
	private final Vector3f otherPivotOffset = new Vector3f();
	private final Quaternionf otherPivotRot = new Quaternionf();
	private final Vector3f otherAnchorOffset = new Vector3f();
}
