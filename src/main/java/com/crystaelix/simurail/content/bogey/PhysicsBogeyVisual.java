package com.crystaelix.simurail.content.bogey;

import java.util.function.Consumer;

import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

import com.crystaelix.simurail.api.bogey.BogeySubtype;
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
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.minecraft.util.Mth;

public class PhysicsBogeyVisual extends ShaftVisual<PhysicsBogeyBlockEntity> implements SimpleDynamicVisual {

	private BogeySubtype type;

	private BogeyVisual pivot;
	private TransformedInstance frontHead;
	private TransformedInstance backHead;
	private TransformedInstance[] frontCable = new TransformedInstance[8];
	private TransformedInstance[] backCable = new TransformedInstance[8];

	public PhysicsBogeyVisual(VisualizationContext context, PhysicsBogeyBlockEntity blockEntity, float partialTick) {
		super(context, blockEntity, partialTick);
		if(blockEntity.isUnpowered()) {
			rotatingModel.delete();
		}

		frontHead = instancerProvider().
				instancer(InstanceTypes.TRANSFORMED, Models.partial(AllPartialModels.TRAIN_COUPLING_HEAD)).
				createInstance();
		backHead = instancerProvider().
				instancer(InstanceTypes.TRANSFORMED, Models.partial(AllPartialModels.TRAIN_COUPLING_HEAD)).
				createInstance();

		instancerProvider().
		instancer(InstanceTypes.TRANSFORMED, Models.partial(AllPartialModels.TRAIN_COUPLING_CABLE)).
		createInstances(frontCable);
		instancerProvider().
		instancer(InstanceTypes.TRANSFORMED, Models.partial(AllPartialModels.TRAIN_COUPLING_CABLE)).
		createInstances(backCable);
	}

	@Override
	public void beginFrame(DynamicVisual.Context context) {
		BogeySubtype type = blockEntity.options.type;

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

		blockEntity.getRenderPivotOffset(partialTick, pivotOffset);
		blockEntity.getRenderPivotRot(partialTick, pivotRot);

		transformStack.pushPose();
		transformStack.
		translate(visualPos).
		center().
		translate(pivotOffset).
		rotate(pivotRot).
		translate(0, (blockEntity.isInverted() ? 1 : -1) * blockEntity.options.getAxleOffset(), 0).
		rotate(SimurailMathf.ROT_ZNYPXP).
		translate(0, -1.5F - 0.0078125F, 0);
		pivot.update(blockEntity.getBogeyData(), blockEntity.getWheelAngle(partialTick), transformStack.unwrap());
		transformStack.popPose();

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

			frontHead.setVisible(true);
			frontHead.setIdentityTransform().
			translate(visualPos).
			center().
			translate(anchorOffset).
			rotateY(Mth.PI + yRot).rotateX(xRot).
			setChanged();

			if(blockEntity.connectionFrontToFront ? other.options.renderFrontConnector : other.options.renderBackConnector) {
				float length = anchorOffset.distance(otherAnchorOffset) * 0.5F - 0.1875F + 0.0078125F;
				float scale = (length * 4) / 8;

				for(int i = 0; i < 8; ++i) {
					frontCable[i].setVisible(true);
					frontCable[i].setIdentityTransform().
					translate(visualPos).
					center().
					translate(anchorOffset).
					rotateY(yRot).rotateX(-xRot).
					translate(0, 0, 0.1875F).
					scale(0.5F, 0.5F, scale).
					translate(0, 0, 0.125F + i * 0.25F).
					setChanged();
				}
			}
			else for(int i = 0; i < 8; ++i) {
				frontCable[i].setVisible(false);
			}
		}
		else {
			frontHead.setVisible(false);
			for(int i = 0; i < 8; ++i) {
				frontCable[i].setVisible(false);
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

			backHead.setVisible(true);
			backHead.setIdentityTransform().
			translate(visualPos).
			center().
			translate(anchorOffset).
			rotateY(Mth.PI + yRot).rotateX(xRot).
			setChanged();

			if(blockEntity.connectionBackToFront ? other.options.renderFrontConnector : other.options.renderBackConnector) {
				float length = anchorOffset.distance(otherAnchorOffset) * 0.5F - 0.1875F + 0.0078125F;
				float scale = (length * 4) / 8;

				for(int i = 0; i < 8; ++i) {
					backCable[i].setVisible(true);
					backCable[i].setIdentityTransform().
					translate(visualPos).
					center().
					translate(anchorOffset).
					rotateY(yRot).rotateX(-xRot).
					translate(0, 0, 0.1875F).
					scale(0.5F, 0.5F, scale).
					translate(0, 0, 0.125F + i * 0.25F).
					setChanged();
				}
			}
			else for(int i = 0; i < 8; ++i) {
				backCable[i].setVisible(false);
			}
		}
		else {
			backHead.setVisible(false);
			for(int i = 0; i < 8; ++i) {
				backCable[i].setVisible(false);
			}
		}
	}

	@Override
	public void update(float partialTick) {
		if(!blockEntity.isUnpowered()) {
			super.update(partialTick);
		}
	}

	@Override
	public void updateLight(float partialTick) {
		if(!blockEntity.isUnpowered()) {
			super.updateLight(partialTick);
		}
		if(pivot != null) {
			pivot.updateLight(computePackedLight());
		}
		relight(frontHead, backHead);
		relight(frontCable);
		relight(backCable);
	}

	@Override
	protected void _delete() {
		if(!blockEntity.isUnpowered()) {
			super._delete();
		}
		if(pivot != null) {
			pivot.delete();
		}
		frontHead.delete();
		backHead.delete();
		for(int i = 0; i < 8; ++i) {
			frontCable[i].delete();
			backCable[i].delete();
		}
	}

	@Override
	public void collectCrumblingInstances(Consumer<Instance> consumer) {
		if(!blockEntity.isUnpowered()) {
			super.collectCrumblingInstances(consumer);
		}
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

	private final PoseTransformStack transformStack = TransformStack.of(new PoseStack());

	private final Vector3f pivotOffset = new Vector3f();
	private final Quaternionf pivotRot = new Quaternionf();
	private final Vector3f anchorOffset = new Vector3f();

	private final Vector3d otherOffset = new Vector3d();
	private final Vector3f otherPivotOffset = new Vector3f();
	private final Quaternionf otherPivotRot = new Quaternionf();
	private final Vector3f otherAnchorOffset = new Vector3f();
}
