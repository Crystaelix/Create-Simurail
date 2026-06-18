package com.crystaelix.simurail.content.steering_connector;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

public interface SteeringConnectable {

	BlockPos getBlockPos();

	Direction getFacing();

	AABB getOutline(Direction direction);

	boolean canConnectSteeringTo(SteeringConnectable other);

	double connectionRange(SteeringConnectable other);

	void connectSteering(boolean front, SteeringConnectable other, boolean otherFront);

	void disconnectSteering(boolean front);
}
