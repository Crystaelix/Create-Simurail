package com.crystaelix.simurail.content.bogey;

import java.util.function.Consumer;

import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

import com.crystaelix.simurail.api.bogey.BogeyRenderedType;
import com.crystaelix.simurail.api.math.SimurailMath;
import com.crystaelix.simurail.api.math.SimurailMathf;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.ShaftVisual;
import com.simibubi.create.content.trains.bogey.BogeyVisual;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

public class PhysicsBogeyVisual extends ShaftVisual<PhysicsBogeyBlockEntity> implements SimpleDynamicVisual {

	private BogeyRenderedType type;

	private BogeyVisual pivot;
	private TransformedInstance frontHead;
	private TransformedInstance backHead;
	private TransformedInstance[] frontCable = new TransformedInstance[8];
	private TransformedInstance[] backCable = new TransformedInstance[8];

	public PhysicsBogeyVisual(VisualizationContext context, PhysicsBogeyBlockEntity blockEntity, float partialTick) {
		super(context, blockEntity, partialTick);
	}

	@Override
	public void beginFrame(DynamicVisual.Context context) {
		BogeyRenderedType type = blockEntity.options.type;

		if(!type.equals(this.type)) {
			if(pivot != null) {
				pivot.delete();
				pivot = null;
			}
			this.type = type;
			pivot = type.style().createVisual(type.size(), visualizationContext, context.partialTick(), false);
			pivot.updateLight(computePackedLight());
		}

		float partialTick = context.partialTick();
		BlockPos visualPos = getVisualPosition();

		blockEntity.getRenderPivotOffset(partialTick, pivotOffset);
		blockEntity.getRenderPivotRot(partialTick, pivotRot);

		poseStack.pushPose();
		poseStack.translate(visualPos.getX(), visualPos.getY(), visualPos.getZ());
		poseStack.translate(0.5F, 0.5F, 0.5F);
		poseStack.translate(pivotOffset.x, pivotOffset.y, pivotOffset.z);
		poseStack.mulPose(pivotRot);
		poseStack.translate(0, (blockEntity.isInverted() ? 1 : -1) * blockEntity.options.getAxleOffset(), 0);
		poseStack.last().pose().rotate(SimurailMathf.ROT_ZNYPXP);
		poseStack.last().normal().rotate(SimurailMathf.ROT_ZNYPXP);
		poseStack.translate(0, -1.5F - 0.0078125F, 0);
		pivot.update(blockEntity.getBogeyData(), blockEntity.getWheelAngle(partialTick), poseStack);
		poseStack.popPose();

		ClientSubLevel selfSubLevel = Sable.HELPER.getContainingClient(blockEntity);

		if(blockEntity.options.renderFrontConnector && blockEntity.connectionFront != null && level.getBlockEntity(blockEntity.connectionFront) instanceof PhysicsBogeyBlockEntity other) {
			pivotRot.transform(blockEntity.getConnectorAnchorOffset(partialTick, true, anchorOffset)).add(pivotOffset);
			other.getRenderPivotOffset(partialTick, otherPivotOffset);
			other.getRenderPivotRot(partialTick, otherPivotRot);
			otherPivotRot.transform(other.getConnectorAnchorOffset(partialTick, blockEntity.connectionFrontToFront, otherAnchorOffset)).add(otherPivotOffset);

			ClientSubLevel otherSubLevel = Sable.HELPER.getContainingClient(other);
			if(selfSubLevel != otherSubLevel) {
				Pose3dc selfPose = selfSubLevel == null ? SimurailMath.POSE_I : selfSubLevel.renderPose(partialTick);
				Pose3dc otherPose = otherSubLevel == null ? SimurailMath.POSE_I : otherSubLevel.renderPose(partialTick);
				selfPose.transformPositionInverse(otherPose.transformPosition(other.localCenter, otherOffset)).sub(blockEntity.localCenter);
				selfPose.orientation().transformInverse(otherPose.orientation().transform(otherAnchorOffset));
			}
			else {
				otherOffset.set(other.localCenter).sub(blockEntity.localCenter);
			}

			otherAnchorOffset.add((float)otherOffset.x, (float)otherOffset.y, (float)otherOffset.z);

			float diffX = otherAnchorOffset.x - anchorOffset.x;
			float diffY = otherAnchorOffset.y - anchorOffset.y;
			float diffZ = otherAnchorOffset.z - anchorOffset.z;

			float yRot = (float)Math.atan2(diffX, diffZ);
			float xRot = (float)Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ));

			if(frontHead == null) {
				frontHead = instancerProvider().
						instancer(InstanceTypes.TRANSFORMED, Models.partial(AllPartialModels.TRAIN_COUPLING_HEAD)).
						createInstance();
				relight(frontHead);
			}

			frontHead.setIdentityTransform().
			translate(visualPos).
			translate(0.5F, 0.5F, 0.5F).
			translate(anchorOffset).
			rotateY(Mth.PI + yRot).rotateX(xRot).
			setChanged();

			if(blockEntity.connectionFrontToFront ? other.options.renderFrontConnector : other.options.renderBackConnector) {
				float length = anchorOffset.distance(otherAnchorOffset) * 0.5F - 0.1875F + 0.0078125F;
				float scale = (length * 4) / 8;

				for(int i = 0; i < 8; ++i) {
					if(frontCable[i] == null) {
						frontCable[i] = instancerProvider().
								instancer(InstanceTypes.TRANSFORMED, Models.partial(AllPartialModels.TRAIN_COUPLING_CABLE)).
								createInstance();
						relight(frontCable[i]);
					}

					frontCable[i].setIdentityTransform().
					translate(visualPos).
					translate(0.5F, 0.5F, 0.5F).
					translate(anchorOffset).
					rotateY(yRot).rotateX(-xRot).
					translate(0, 0, 0.1875F).
					scale(0.5F, 0.5F, scale).
					translate(0, 0, 0.125F + i * 0.25F).
					setChanged();
				}
			}
			else for(int i = 0; i < 8; ++i) {
				if(frontCable[i] != null) {
					frontCable[i].delete();
					frontCable[i] = null;
				}
			}
		}
		else {
			if(frontHead != null) {
				frontHead.delete();
				frontHead = null;
			}
			for(int i = 0; i < 8; ++i) {
				if(frontCable[i] != null) {
					frontCable[i].delete();
					frontCable[i] = null;
				}
			}
		}

		if(blockEntity.options.renderBackConnector && blockEntity.connectionBack != null && level.getBlockEntity(blockEntity.connectionBack) instanceof PhysicsBogeyBlockEntity other) {
			pivotRot.transform(blockEntity.getConnectorAnchorOffset(partialTick, false, anchorOffset)).add(pivotOffset);
			other.getRenderPivotOffset(partialTick, otherPivotOffset);
			other.getRenderPivotRot(partialTick, otherPivotRot);
			otherPivotRot.transform(other.getConnectorAnchorOffset(partialTick, blockEntity.connectionBackToFront, otherAnchorOffset)).add(otherPivotOffset);

			ClientSubLevel otherSubLevel = Sable.HELPER.getContainingClient(other);
			if(selfSubLevel != otherSubLevel) {
				Pose3dc selfPose = selfSubLevel == null ? SimurailMath.POSE_I : selfSubLevel.renderPose(partialTick);
				Pose3dc otherPose = otherSubLevel == null ? SimurailMath.POSE_I : otherSubLevel.renderPose(partialTick);
				selfPose.transformPositionInverse(otherPose.transformPosition(other.localCenter, otherOffset)).sub(blockEntity.localCenter);
				selfPose.orientation().transformInverse(otherPose.orientation().transform(otherAnchorOffset));
			}
			else {
				otherOffset.set(other.localCenter).sub(blockEntity.localCenter);
			}

			otherAnchorOffset.add((float)otherOffset.x, (float)otherOffset.y, (float)otherOffset.z);

			float diffX = otherAnchorOffset.x - anchorOffset.x;
			float diffY = otherAnchorOffset.y - anchorOffset.y;
			float diffZ = otherAnchorOffset.z - anchorOffset.z;

			float yRot = (float)Math.atan2(diffX, diffZ);
			float xRot = (float)Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ));

			if(backHead == null) {
				backHead = instancerProvider().
						instancer(InstanceTypes.TRANSFORMED, Models.partial(AllPartialModels.TRAIN_COUPLING_HEAD)).
						createInstance();
				relight(backHead);
			}

			backHead.setIdentityTransform().
			translate(visualPos).
			translate(0.5F, 0.5F, 0.5F).
			translate(anchorOffset).
			rotateY(Mth.PI + yRot).rotateX(xRot).
			setChanged();

			if(blockEntity.connectionBackToFront ? other.options.renderFrontConnector : other.options.renderBackConnector) {
				float length = anchorOffset.distance(otherAnchorOffset) * 0.5F - 0.1875F + 0.0078125F;
				float scale = (length * 4) / 8;

				for(int i = 0; i < 8; ++i) {
					if(backCable[i] == null) {
						backCable[i] = instancerProvider().
								instancer(InstanceTypes.TRANSFORMED, Models.partial(AllPartialModels.TRAIN_COUPLING_CABLE)).
								createInstance();
						relight(frontCable[i]);
					}

					backCable[i].setIdentityTransform().
					translate(visualPos).
					translate(0.5F, 0.5F, 0.5F).
					translate(anchorOffset).
					rotateY(yRot).rotateX(-xRot).
					translate(0, 0, 0.1875F).
					scale(0.5F, 0.5F, scale).
					translate(0, 0, 0.125F + i * 0.25F).
					setChanged();
				}
			}
			else for(int i = 0; i < 8; ++i) {
				if(backCable[i] != null) {
					backCable[i].delete();
					backCable[i] = null;
				}
			}
		}
		else {
			if(backHead != null) {
				backHead.delete();
				backHead = null;
			}
			for(int i = 0; i < 8; ++i) {
				if(backCable[i] != null) {
					backCable[i].delete();
					backCable[i] = null;
				}
			}
		}
	}

	@Override
	public void update(float partialTick) {
		super.update(partialTick);
	}

	@Override
	public void updateLight(float partialTick) {
		super.updateLight(partialTick);
		if(pivot != null) {
			pivot.updateLight(computePackedLight());
		}
		relight(frontHead, backHead);
		relight(frontCable);
		relight(backCable);
	}

	@Override
	protected void _delete() {
		super._delete();
		if(pivot != null) {
			pivot.delete();
		}
		if(frontHead != null) {
			frontHead.delete();
		}
		if(backHead != null) {
			backHead.delete();
		}
		for(int i = 0; i < 8; ++i) {
			if(frontCable[i] != null) {
				frontCable[i].delete();
			}
			if(backCable[i] != null) {
				backCable[i].delete();
			}
		}
	}

	@Override
	public void collectCrumblingInstances(Consumer<Instance> consumer) {
		super.collectCrumblingInstances(consumer);
		if(pivot != null) {
			pivot.collectCrumblingInstances(consumer);
		}
		consumer.accept(frontHead);
		consumer.accept(backHead);
		for(int i = 0; i < 8; ++i) {
			consumer.accept(frontCable[i]);
			consumer.accept(backCable[i]);
		}
	}

	private final PoseStack poseStack = new PoseStack();

	private final Vector3f pivotOffset = new Vector3f();
	private final Quaternionf pivotRot = new Quaternionf();
	private final Vector3f anchorOffset = new Vector3f();

	private final Vector3d otherOffset = new Vector3d();
	private final Vector3f otherPivotOffset = new Vector3f();
	private final Quaternionf otherPivotRot = new Quaternionf();
	private final Vector3f otherAnchorOffset = new Vector3f();
}
