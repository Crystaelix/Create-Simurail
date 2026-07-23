package com.crystaelix.simurail.ponder.instruction;

import com.crystaelix.simurail.content.bogey.PhysicsBogeyBlockEntity;

import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.core.BlockPos;

public class PhysicsBogeyConnectionInstruction extends PonderInstruction {

	protected final BlockPos pos1;
	protected final boolean front1;
	protected final BlockPos pos2;
	protected final boolean front2;

	public PhysicsBogeyConnectionInstruction(BlockPos pos1, boolean front1, BlockPos pos2, boolean front2) {
		this.pos1 = pos1;
		this.front1 = front1;
		this.pos2 = pos2;
		this.front2 = front2;
	}

	@Override
	public boolean isComplete() {
		return true;
	}

	@Override
	public void tick(PonderScene scene) {
		PonderLevel world = scene.getWorld();
		if(pos1 != null && pos2 != null &&
				world.getBlockEntity(pos1) instanceof PhysicsBogeyBlockEntity be1 &&
				world.getBlockEntity(pos2) instanceof PhysicsBogeyBlockEntity be2) {
			be1.connect(front1, be2, front2);
		}
		if(pos1 != null && pos2 == null &&
				world.getBlockEntity(pos1) instanceof PhysicsBogeyBlockEntity be1) {
			be1.disconnect(front1);
		}
	}
}
