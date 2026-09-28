package com.crystaelix.simurail.compat.computercraft.peripheral;

import com.crystaelix.simurail.content.track_switch.TrackSwitchBlockEntity;
import com.crystaelix.simurail.content.track_switch.TrackSwitchState;
import com.simibubi.create.compat.computercraft.implementation.peripherals.SyncedPeripheral;

import dan200.computercraft.api.lua.LuaFunction;

public class TrackSwitchPeripheral extends SyncedPeripheral<TrackSwitchBlockEntity> {

	public TrackSwitchPeripheral(TrackSwitchBlockEntity blockEntity) {
		super(blockEntity);
	}

	@Override
	public String getType() {
		return "Simurail_TrackSwitch";
	}

	@LuaFunction
	public final int getState() {
		return blockEntity.getState().ordinal();
	}

	@LuaFunction(mainThread = true)
	public final void setState(int state) {
		blockEntity.trySetState(TrackSwitchState.BY_ID.apply(state));
	}
}
