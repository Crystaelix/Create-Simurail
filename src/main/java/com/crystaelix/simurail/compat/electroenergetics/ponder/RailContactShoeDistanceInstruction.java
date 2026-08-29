package com.crystaelix.simurail.compat.electroenergetics.ponder;

import com.george_vi.electroenergetics.content.railway_electrification.third_rail.RailContactShoeBlockEntity;

import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.core.BlockPos;

public class RailContactShoeDistanceInstruction extends PonderInstruction {

	protected final BlockPos pos;
	protected final float distance;
	protected final int duration;

	protected int remainingTicks;

	public RailContactShoeDistanceInstruction(BlockPos pos, float distance, int duration) {
		this.pos = pos;
		this.distance = distance;
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
		if(world.getBlockEntity(pos) instanceof RailContactShoeBlockEntity be) {
			be.distanceY = distance;
			be.prevDistanceY = distance;
		}
	}
}
