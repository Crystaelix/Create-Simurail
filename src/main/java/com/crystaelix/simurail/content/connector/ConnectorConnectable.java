package com.crystaelix.simurail.content.connector;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

public interface ConnectorConnectable {

	BlockPos getBlockPos();

	Direction getFacing();

	AABB getOutline(Direction direction);

	boolean canConnectTo(Direction selfDir, ConnectorConnectable other, Direction otherDir);

	double connectionRange(ConnectorConnectable other);

	void connect(boolean front, ConnectorConnectable other, boolean otherFront);

	void disconnect(boolean front);
}
