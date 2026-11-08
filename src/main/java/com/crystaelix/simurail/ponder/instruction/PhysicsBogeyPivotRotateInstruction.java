package com.crystaelix.simurail.ponder.instruction;

import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Quaternionfc;

import com.crystaelix.simurail.content.bogey.PhysicsBogeyBlockEntity;

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.TickingInstruction;
import net.minecraft.core.BlockPos;

public class PhysicsBogeyPivotRotateInstruction extends TickingInstruction {

	protected final BlockPos pos;
	protected final Quaterniondc target;
	protected final FloatUnaryOperator smoothing;

	protected PhysicsBogeyBlockEntity bogey;
	protected Quaterniondc start;

	public PhysicsBogeyPivotRotateInstruction(BlockPos pos, Quaternionfc target, int ticks, FloatUnaryOperator smoothing) {
		this(pos, new Quaterniond(target), ticks, smoothing);
	}

	public PhysicsBogeyPivotRotateInstruction(BlockPos pos, Quaterniondc target, int ticks, FloatUnaryOperator smoothing) {
		super(false, ticks);
		this.pos = pos;
		this.target = target;
		this.smoothing = smoothing;
	}

	@Override
	protected void firstTick(PonderScene scene) {
		super.firstTick(scene);
		bogey = (PhysicsBogeyBlockEntity)scene.getWorld().getBlockEntity(pos);
		if(bogey == null) {
			return;
		}
		start = new Quaterniond(bogey.localPivotRot);
	}

	@Override
	public void tick(PonderScene scene) {
		super.tick(scene);
		if(bogey == null) {
			return;
		}
		if(totalTicks == 0) {
			bogey.localPivotRot.set(target);
			return;
		}
		float t = smoothing.apply(1 - (float)remainingTicks / totalTicks);
		start.nlerp(target, t, bogey.localPivotRot);
	}
}
