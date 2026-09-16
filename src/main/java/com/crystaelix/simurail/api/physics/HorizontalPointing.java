package com.crystaelix.simurail.api.physics;

import org.joml.Quaterniondc;
import org.joml.Vector3dc;

import com.crystaelix.simurail.api.math.SimurailMath;

import net.minecraft.core.Direction;

public interface HorizontalPointing {

	Direction getFacing();

	default Vector3dc getDirection() {
		return switch(getFacing()) {
		case EAST -> SimurailMath.DIR_XP; case WEST -> SimurailMath.DIR_XN;
		case SOUTH -> SimurailMath.DIR_ZP; case NORTH -> SimurailMath.DIR_ZN;
		case null, default -> throw new IllegalArgumentException("Unexpected value: " + getFacing());
		};
	}

	default Quaterniondc getOrientation() {
		return switch(getFacing()) {
		case EAST -> SimurailMath.ROT_XPYPZP; case WEST -> SimurailMath.ROT_XNYPZN;
		case SOUTH -> SimurailMath.ROT_ZPYPXN; case NORTH -> SimurailMath.ROT_ZNYPXP;
		case null, default -> throw new IllegalArgumentException("Unexpected value: " + getFacing());
		};
	}
}
