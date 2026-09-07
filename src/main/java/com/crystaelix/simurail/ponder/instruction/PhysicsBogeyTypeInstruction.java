package com.crystaelix.simurail.ponder.instruction;

import com.crystaelix.simurail.api.bogey.BogeySubtype;
import com.crystaelix.simurail.api.bogey.BogeyType;
import com.crystaelix.simurail.content.bogey.PhysicsBogeyBlockEntity;
import com.simibubi.create.content.trains.bogey.BogeySizes.BogeySize;
import com.simibubi.create.content.trains.bogey.BogeyStyle;

import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.core.BlockPos;

public class PhysicsBogeyTypeInstruction extends PonderInstruction {

	protected final BlockPos pos;
	protected final BogeySubtype type;

	public PhysicsBogeyTypeInstruction(BlockPos pos, BogeySubtype type) {
		this.pos = pos;
		this.type = type;
	}

	public PhysicsBogeyTypeInstruction(BlockPos pos, BogeyStyle style, BogeySize size) {
		this(pos, new BogeySubtype(new BogeyType(style, size)));
	}

	@Override
	public boolean isComplete() {
		return true;
	}

	@Override
	public void tick(PonderScene scene) {
		PonderLevel world = scene.getWorld();
		if(world.getBlockEntity(pos) instanceof PhysicsBogeyBlockEntity be) {
			be.getOptions().type = type;
		}
	}
}
