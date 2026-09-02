package com.crystaelix.simurail.content.physics_roller;

import java.util.function.Consumer;

import com.crystaelix.simurail.content.SimurailPartialModels;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class PhysicsRollerVisual extends AbstractBlockEntityVisual<PhysicsRollerBlockEntity> implements SimpleDynamicVisual {

	private TransformedInstance wheel;
	private TransformedInstance frame;

	public PhysicsRollerVisual(VisualizationContext context, PhysicsRollerBlockEntity blockEntity, float partialTick) {
		super(context, blockEntity, partialTick);
		wheel = instancerProvider().
				instancer(InstanceTypes.TRANSFORMED, Models.partial(SimurailPartialModels.PHYSICS_ROLLER_WHEEL)).
				createInstance();
		frame = instancerProvider().
				instancer(InstanceTypes.TRANSFORMED, Models.partial(SimurailPartialModels.PHYSICS_ROLLER_FRAME)).
				createInstance();
		relight(wheel, frame);
	}

	@Override
	public void beginFrame(DynamicVisual.Context context) {
		Level level = blockEntity.getLevel();
		BlockState state = blockEntity.getBlockState();
		Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);

		float time = (AnimationTickHolder.getTicks(level) + context.partialTick()) / 20;
		float yAngle = AngleHelper.horizontalAngle(facing);
		float xAngle = (time * blockEntity.getAnimatedSpeed()) % 360;

		wheel.setIdentityTransform().
		translate(visualPos).
		rotateYCenteredDegrees(yAngle).
		translate(0, -0.25, 1.0625).
		rotateXDegrees(-xAngle).
		translate(0, -0.5, 0.5).
		rotateYDegrees(90).
		setChanged();

		frame.setIdentityTransform().
		translate(visualPos).
		rotateYCenteredDegrees(yAngle + 180).
		setChanged();
	}

	@Override
	public void updateLight(float partialTick) {
		relight(wheel, frame);
	}

	@Override
	protected void _delete() {
		wheel.delete();
		frame.delete();
	}

	@Override
	public void collectCrumblingInstances(Consumer<Instance> consumer) {
		consumer.accept(wheel);
		consumer.accept(frame);
	}
}
