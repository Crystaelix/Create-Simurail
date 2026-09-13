package com.crystaelix.simurail.content.gangway_frame;

import org.joml.Vector3d;

import net.minecraft.core.Direction;

public interface GangwayFrameShape {
	
	Vector3d center(Direction facing, Vector3d dest);

	GangwayFrameShape connectsTo();

	Direction adjacentOffset(Direction facing, boolean clockwise);

	int adjacentTo(GangwayFrameShape other, boolean clockwise);
}
