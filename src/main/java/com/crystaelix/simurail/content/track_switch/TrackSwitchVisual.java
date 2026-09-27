package com.crystaelix.simurail.content.track_switch;

import java.util.function.Consumer;

import org.joml.Vector3d;

import com.crystaelix.simurail.api.math.SimurailMath;
import com.crystaelix.simurail.content.SimurailPartialModels;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour.RenderedTrackOverlayType;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visual.TickableVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import dev.engine_room.flywheel.lib.visual.SimpleTickableVisual;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class TrackSwitchVisual extends AbstractBlockEntityVisual<TrackSwitchBlockEntity> implements SimpleTickableVisual, SimpleDynamicVisual {

	private PartialModel partialModel;
	private BlockPos targetPos;
	private TransformedInstance overlay;
	private TransformedInstance sign;

	public TrackSwitchVisual(VisualizationContext ctx, TrackSwitchBlockEntity blockEntity, float partialTick) {
		super(ctx, blockEntity, partialTick);
		partialModel = TrackSwitchRenderer.getOverlayModel(blockEntity);
		overlay = instancerProvider().
				instancer(InstanceTypes.TRANSFORMED, Models.partial(partialModel)).
				createInstance();
		sign = instancerProvider().
				instancer(InstanceTypes.TRANSFORMED, Models.partial(SimurailPartialModels.TRACK_SWITCH_SIGN)).
				createInstance();
		tickOverlay();
	}

	@Override
	public void tick(TickableVisual.Context context) {
		PartialModel partialModel = TrackSwitchRenderer.getOverlayModel(blockEntity);
		if(this.partialModel != partialModel) {
			this.partialModel = partialModel;
			instancerProvider().
			instancer(InstanceTypes.TRANSFORMED, Models.partial(partialModel)).
			stealInstance(overlay);
		}
		tickOverlay();
	}

	private void tickOverlay() {
		TrackTargetingBehaviour<TrackSwitch> target = blockEntity.edgePoint;
		BlockPos targetPosition = target.getGlobalPosition();
		Level level = blockEntity.getLevel();
		BlockState trackState = level.getBlockState(targetPosition);
		Block block = trackState.getBlock();

		if(block instanceof ITrackBlock trackBlock) {
			if(!targetPosition.equals(this.targetPos)) {
				this.targetPos = targetPosition;
				overlay.setIdentityTransform().translate(targetPosition.subtract(renderOrigin()));
				trackBlock.prepareTrackOverlay(overlay, level, targetPosition, trackState, target.getTargetBezier(), target.getTargetDirection(), RenderedTrackOverlayType.SIGNAL);
				overlay.setChanged();
			}
		}
		else {
			overlay.setZeroTransform().setChanged();
		}
	}

	@Override
	public void beginFrame(DynamicVisual.Context ctx) {
		blockEntity.trackRot.transform(SimurailMath.DIR_XP, trackDir);
		float trackAngle = (float)Math.atan2(-trackDir.z, trackDir.x);
		float signAngle = blockEntity.lerpedAngle.getValue(ctx.partialTick());
		sign.setIdentityTransform().
		translate(visualPos).
		translate(0.5, 0.75, 0.5).
		rotateY(trackAngle).
		rotateX(signAngle).
		setChanged();
	}

	@Override
	public void updateLight(float partialTick) {
		relight(overlay, sign);
	}

	@Override
	protected void _delete() {
		overlay.delete();
		sign.delete();
	}

	@Override
	public void collectCrumblingInstances(Consumer<Instance> consumer) {
		if(overlay != null) {
			consumer.accept(overlay);
		}
	}

	private final Vector3d trackDir = new Vector3d();
}
