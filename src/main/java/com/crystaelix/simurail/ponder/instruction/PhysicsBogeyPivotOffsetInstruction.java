package com.crystaelix.simurail.ponder.instruction;

import org.joml.Vector3d;
import org.joml.Vector3dc;

import com.crystaelix.simurail.content.bogey.PhysicsBogeyBlockEntity;

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.TickingInstruction;
import net.minecraft.core.BlockPos;

public class PhysicsBogeyPivotOffsetInstruction extends TickingInstruction {

	protected final BlockPos pos;
	protected final Vector3dc offset;
	protected final FloatUnaryOperator smoothing;

	protected PhysicsBogeyBlockEntity bogey;
	protected Vector3dc start;

	public PhysicsBogeyPivotOffsetInstruction(BlockPos pos, Vector3dc offset, int ticks, FloatUnaryOperator smoothing) {
		super(false, ticks);
		this.pos = pos;
		this.offset = offset;
		this.smoothing = smoothing;
	}

	@Override
	protected void firstTick(PonderScene scene) {
		super.firstTick(scene);
		bogey = (PhysicsBogeyBlockEntity)scene.getWorld().getBlockEntity(pos);
		if(bogey == null) {
			return;
		}
		start = new Vector3d(bogey.localPivotOffset);
	}

	@Override
	public void tick(PonderScene scene) {
		super.tick(scene);
		if(bogey == null) {
			return;
		}
		if(totalTicks == 0) {
			start.add(offset, bogey.localPivotOffset);
			return;
		}
		float t = smoothing.apply(1 - (float)remainingTicks / totalTicks);
		start.fma(t, offset, bogey.localPivotOffset);
	}
}
