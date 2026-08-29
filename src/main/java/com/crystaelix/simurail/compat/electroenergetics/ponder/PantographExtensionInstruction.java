package com.crystaelix.simurail.compat.electroenergetics.ponder;

import com.george_vi.electroenergetics.content.railway_electrification.pantograph.PantographBlockEntity;

import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.core.BlockPos;

/**
 * Helper to extend the Pantograph
 */
public class PantographExtensionInstruction extends PonderInstruction {

	protected final BlockPos pos;
	protected final float extension;
	protected final int duration;

	protected int remainingTicks;

	public PantographExtensionInstruction(BlockPos pos, float extension, int duration) {
		this.pos = pos;
		this.extension = extension;
		this.duration = duration;
		this.remainingTicks = duration;
	}

	@Override
	public void reset(PonderScene scene) {
		remainingTicks = duration;
	}

	@Override
	public boolean isComplete() {
		return remainingTicks <= 0;
	}

	@Override
	public void tick(PonderScene scene) {
		--remainingTicks;
		PonderLevel world = scene.getWorld();
		if(world.getBlockEntity(pos) instanceof PantographBlockEntity be) {
			be.extended = true;
			be.targetExtensionState = extension;
			be.currentExtensionState = extension;
			be.prevExtensionState = extension;
		}
	}
}
