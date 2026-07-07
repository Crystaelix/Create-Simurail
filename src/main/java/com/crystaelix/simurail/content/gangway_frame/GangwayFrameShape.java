package com.crystaelix.simurail.content.gangway_frame;

import net.minecraft.core.Direction;

public interface GangwayFrameShape {

	GangwayFrameShape connectsTo();

	Direction adjacentOffset(Direction facing, boolean clockwise);

	int adjacentTo(GangwayFrameShape other, boolean clockwise);
}
