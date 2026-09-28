package com.crystaelix.simurail.ponder.instruction;

import org.joml.Quaterniondc;

import com.crystaelix.simurail.content.track_switch.TrackSwitchBlockEntity;

import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.core.BlockPos;

public class TrackSwitchRotInstruction extends PonderInstruction {

	protected final BlockPos pos;
	protected final Quaterniondc trackRot;

	public TrackSwitchRotInstruction(BlockPos pos, Quaterniondc trackRot) {
		this.pos = pos;
		this.trackRot = trackRot;
	}

	@Override
	public boolean isComplete() {
		return true;
	}

	@Override
	public void tick(PonderScene scene) {
		PonderLevel world = scene.getWorld();
		if(world.getBlockEntity(pos) instanceof TrackSwitchBlockEntity be) {
			be.setPonderTrackRot(trackRot);
		}
	}
}
