package com.crystaelix.simurail.api.math;

import net.minecraft.core.Direction;

public enum HorizontalRelativeDirection {
	CW,
	ACW,
	D,
	U,
	I,
	O;

	public static final HorizontalRelativeDirection[] ROTATED = {CW, ACW, U, D};

	public HorizontalRelativeDirection opposite() {
		return switch(this) {
		case CW -> ACW;
		case ACW -> CW;
		case D -> U;
		case U -> D;
		case I -> O;
		case O -> I;
		};
	};

	public HorizontalRelativeDirection mirror() {
		return switch(this) {
		case CW -> ACW;
		case ACW -> CW;
		default -> this;
		};
	};

	public Direction transform(Direction direction) {
		return switch(this) {
		case CW -> direction.getClockWise();
		case ACW -> direction.getCounterClockWise();
		case D -> Direction.DOWN;
		case U -> Direction.UP;
		case I -> direction;
		case O -> direction.getOpposite();
		};
	}
}
