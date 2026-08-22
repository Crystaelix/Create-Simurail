package com.crystaelix.simurail.compat.computercraft.peripheral;

import com.crystaelix.simurail.content.remote_controller.RemoteControllerBlockEntity;
import com.crystaelix.simurail.content.remote_controller.RemoteControllerMode;
import com.simibubi.create.compat.computercraft.implementation.peripherals.SyncedPeripheral;

import dan200.computercraft.api.lua.LuaFunction;
import net.minecraft.core.BlockPos;

public class RemoteControllerPeripheral extends SyncedPeripheral<RemoteControllerBlockEntity> {

	public RemoteControllerPeripheral(RemoteControllerBlockEntity blockEntity) {
		super(blockEntity);
	}

	@Override
	public final String getType() {
		return "Simurail_RemoteController";
	}

	@LuaFunction
	public final Object[] getTargetPos() {
		BlockPos pos = blockEntity.getTargetPos();
		return new Object[] {pos.getX(), pos.getY(), pos.getZ()};
	}

	@LuaFunction(mainThread = true)
	public final void setTargetPos(int x, int y, int z) {
		blockEntity.setTargetPos(new BlockPos(x, y, z));
	}

	@LuaFunction
	public final int getMode() {
		return blockEntity.getMode().ordinal();
	}

	@LuaFunction(mainThread = true)
	public final void setMode(int mode) {
		blockEntity.setMode(RemoteControllerMode.BY_ID.apply(mode));
	}
}
