package com.crystaelix.simurail.compat.computercraft;

import com.crystaelix.simurail.content.bogey.PhysicsBogeyBlockEntity;
import com.simibubi.create.compat.computercraft.implementation.peripherals.SyncedPeripheral;

import dan200.computercraft.api.lua.LuaFunction;

public class PhysicsBogeyPeripheral extends SyncedPeripheral<PhysicsBogeyBlockEntity> {

	public PhysicsBogeyPeripheral(PhysicsBogeyBlockEntity blockEntity) {
		super(blockEntity);
	}

	@Override
	public String getType() {
		return "Simurail_PhysicsBogey";
	}

	@LuaFunction
	public double getLateralCurvature() {
		return blockEntity.getLateralCurvature();
	}

	@LuaFunction
	public double getVerticalCurvature() {
		return blockEntity.getVerticalCurvature();
	}
}
