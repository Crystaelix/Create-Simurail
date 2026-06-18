package com.crystaelix.simurail.content.automatic_coupler;

import java.util.function.Consumer;

import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

import com.crystaelix.simurail.api.coupler.CouplerType;
import com.crystaelix.simurail.api.math.SimurailMath;
import com.crystaelix.simurail.api.math.SimurailMathf;
import com.crystaelix.simurail.content.SimurailPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.minecraft.core.BlockPos;

public class AutomaticCouplerVisual extends AbstractBlockEntityVisual<AutomaticCouplerBlockEntity> implements SimpleDynamicVisual {

	private boolean isShort;
	private CouplerType type;

	private TransformedInstance bar;
	private TransformedInstance head;

	public AutomaticCouplerVisual(VisualizationContext context, AutomaticCouplerBlockEntity blockEntity, float partialTick) {
		super(context, blockEntity, partialTick);
	}

	@Override
	public void beginFrame(DynamicVisual.Context context) {
		if(bar == null || blockEntity.isShort != isShort) {
			if(bar != null) {
				bar.delete();
				bar = null;
			}
			isShort = blockEntity.isShort;
			bar = instancerProvider().
					instancer(InstanceTypes.TRANSFORMED, Models.partial(isShort ? SimurailPartialModels.COUPLER_BAR_SHORT : SimurailPartialModels.COUPLER_BAR)).
					createInstance();
			relight(bar);
		}
		if(head == null || blockEntity.type != type) {
			if(head != null) {
				head.delete();
				head = null;
			}
			type = blockEntity.type;
			head = instancerProvider().
					instancer(InstanceTypes.TRANSFORMED, Models.partial(PartialModel.of(type.modelId()))).
					createInstance();
			relight(head);
		}

		float partialTick = context.partialTick();
		BlockPos visualPos = getVisualPosition();
		
		if(bar != null || head != null) {
			couplerOffset.set(blockEntity.getDirection()).mul(-0.4375F);

			boolean hasPartner;
			blockEntity.getJointPosition(jointPos);
			if(blockEntity.partnerPos != null && level.getBlockEntity(blockEntity.partnerPos) instanceof AutomaticCouplerBlockEntity partner) {
				hasPartner = true;
				ClientSubLevel selfSubLevel = Sable.HELPER.getContainingClient(blockEntity);
				ClientSubLevel partnerSubLevel = Sable.HELPER.getContainingClient(partner);
				Pose3dc selfPose = selfSubLevel == null ? SimurailMath.POSE_I : selfSubLevel.renderPose(partialTick);
				Pose3dc partnerPose = partnerSubLevel == null ? SimurailMath.POSE_I : partnerSubLevel.renderPose(partialTick);
				selfPose.transformPositionInverse(partnerPose.transformPosition(partner.getJointPosition(targetPos)));
				selfPose.orientation().transformInverse(partnerPose.orientation().transform(SimurailMathf.DIR_YP, targetVert));
			}
			else {
				hasPartner = false;
				blockEntity.getEndPosition(targetPos);
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

			if(bar != null) {
				bar.setIdentityTransform().
				translate(visualPos).
				translate(0.5F, 0.5F, 0.5F).
				translate(couplerOffset).
				rotate(couplerRot).
				setChanged();
			}

			if(head != null) {
				head.setIdentityTransform().
				translate(visualPos).
				translate(0.5F, 0.5F, 0.5F).
				translate(couplerOffset).
				rotate(couplerRot).
				translate(blockEntity.getLength(), 0, 0).
				setChanged();
			}
		}
	}

	@Override
	public void updateLight(float partialTick) {
		relight(bar, head);
	}

	@Override
	protected void _delete() {
		if(bar != null) {
			bar.delete();
		}
		if(head != null) {
			head.delete();
		}
	}

	@Override
	public void collectCrumblingInstances(Consumer<Instance> consumer) {
		consumer.accept(bar);
		consumer.accept(head);
	}

	private final PoseStack poseStack = new PoseStack();
	private final Vector3f couplerOffset = new Vector3f();
	private final Vector3d jointPos = new Vector3d();
	private final Vector3d targetPos = new Vector3d();
	private final Vector3f targetVert = new Vector3f();
	private final Vector3f couplerDir = new Vector3f();
	private final Quaternionf targetRot = new Quaternionf();
	private final Quaternionf couplerRot = new Quaternionf();
}
