package com.crystaelix.simurail.ponder.instruction;

import com.crystaelix.simurail.content.track_switch.TrackSwitchBlockEntity;
import com.crystaelix.simurail.content.track_switch.TrackSwitchState;

import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.core.BlockPos;

public class TrackSwitchStateInstruction extends PonderInstruction {

	protected final BlockPos pos;
	protected final TrackSwitchState state;

	public TrackSwitchStateInstruction(BlockPos pos, TrackSwitchState state) {
		this.pos = pos;
		this.state = state;
	}

	@Override
	public boolean isComplete() {
		return true;
	}

	@Override
	public void tick(PonderScene scene) {
		PonderLevel world = scene.getWorld();
		if(world.getBlockEntity(pos) instanceof TrackSwitchBlockEntity be) {
			be.setPonderState(state);
		}
	}
}
