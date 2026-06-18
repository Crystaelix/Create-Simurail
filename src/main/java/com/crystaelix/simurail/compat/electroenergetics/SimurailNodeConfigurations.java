package com.crystaelix.simurail.compat.electroenergetics;

import com.george_vi.electroenergetics.foundation.nodes.NodeConfigurator;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class SimurailNodeConfigurations {

	public static final NodeConfigurator PHYSICS_BOGEY = new NodeConfigurator.Builder().
			add(new Vec3(0.125, 0.5, 0.5)).
			add(new Vec3(0.875, 0.5, 0.5)).
			add(new Vec3(0.5, 0.875, 0.5)).
			simple(Direction.SOUTH);
	public static final NodeConfigurator INVERTED_PHYSICS_BOGEY = new NodeConfigurator.Builder().
			add(new Vec3(0.125, 0.5, 0.5)).
			add(new Vec3(0.875, 0.5, 0.5)).
			add(new Vec3(0.5, 0.125, 0.5)).
			simple(Direction.SOUTH);
}
