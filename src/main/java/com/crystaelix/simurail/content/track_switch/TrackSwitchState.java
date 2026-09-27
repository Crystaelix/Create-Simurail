package com.crystaelix.simurail.content.track_switch;

import java.util.function.IntFunction;

import net.minecraft.util.ByIdMap;
import net.minecraft.util.ByIdMap.OutOfBoundsStrategy;

public enum TrackSwitchState {
	STRAIGHT,
	LEFT,
	RIGHT;
	
	public static final IntFunction<TrackSwitchState> BY_ID = ByIdMap.continuous(TrackSwitchState::ordinal, values(), OutOfBoundsStrategy.WRAP);
}
