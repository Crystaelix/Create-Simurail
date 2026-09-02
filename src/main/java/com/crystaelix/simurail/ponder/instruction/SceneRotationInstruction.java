package com.crystaelix.simurail.ponder.instruction;

import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.PonderInstruction;

public class SceneRotationInstruction extends PonderInstruction {

	protected final float rotation;

	public SceneRotationInstruction(float rotation) {
		this.rotation = rotation;
	}

	@Override
	public boolean isComplete() {
		return true;
	}

	@Override
	public void tick(PonderScene scene) {
		LerpedFloat yRotation = scene.getTransform().yRotation;
		yRotation.startWithValue(yRotation.getChaseTarget() + rotation);
	}
}
