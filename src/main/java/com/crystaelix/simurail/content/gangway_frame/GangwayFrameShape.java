package com.crystaelix.simurail.content.gangway_frame;

import org.joml.Vector3d;

import com.crystaelix.simurail.api.math.HorizontalRelativeDirection;

import net.minecraft.core.Direction;

public interface GangwayFrameShape {

	Vector3d anchor(Direction facing, Vector3d dest);

	boolean canConnectTo(GangwayFrameShape other);

	HorizontalRelativeDirection adjacentOffset(boolean first);

	boolean isAdjacentTo(HorizontalRelativeDirection offset, GangwayFrameShape other);
}
