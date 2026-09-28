package com.crystaelix.simurail.ponder.instruction;

import com.crystaelix.simurail.content.track_switch.TrackSwitchBlockEntity;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;

import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.core.BlockPos;

public class TrackSwitchExitInstruction extends PonderInstruction {

	protected final BlockPos pos;
	protected final TrackNodeLocation straight;
	protected final TrackNodeLocation left;
	protected final TrackNodeLocation right;

	public TrackSwitchExitInstruction(BlockPos pos, TrackNodeLocation straight, TrackNodeLocation left, TrackNodeLocation right) {
		this.pos = pos;
		this.straight = straight;
		this.left = left;
		this.right = right;
	}

	@Override
	public boolean isComplete() {
		return true;
	}

	@Override
	public void tick(PonderScene scene) {
		PonderLevel world = scene.getWorld();
		if(world.getBlockEntity(pos) instanceof TrackSwitchBlockEntity be) {
			be.setPonderExits(straight, left, right);
		}
	}
}
