package com.crystaelix.simurail.compat.computercraft.peripheral;

import com.crystaelix.simurail.content.probe_reader.ProbeReaderBlockEntity;
import com.crystaelix.simurail.content.probe_reader.ProbeReaderMode;
import com.simibubi.create.compat.computercraft.implementation.peripherals.SyncedPeripheral;

import dan200.computercraft.api.lua.LuaFunction;
import net.minecraft.core.BlockPos;

public class ProbeReaderPeripheral extends SyncedPeripheral<ProbeReaderBlockEntity> {

	public ProbeReaderPeripheral(ProbeReaderBlockEntity blockEntity) {
		super(blockEntity);
	}

	@Override
	public final String getType() {
		return "Simurail_ProbeReader";
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
	public final boolean getTargetFront() {
		return blockEntity.getTargetFront();
	}

	@LuaFunction(mainThread = true)
	public final void setTargetFront(boolean targetFront) {
		blockEntity.setTargetFront(targetFront);
	}

	@LuaFunction
	public final int getMode() {
		return blockEntity.getOptions().mode.ordinal();
	}

	@LuaFunction(mainThread = true)
	public final void setMode(int mode) {
		blockEntity.getOptions().mode = ProbeReaderMode.BY_ID.apply(mode);
		blockEntity.setChanged();
	}

	@LuaFunction
	public final String getFilter() {
		return blockEntity.getOptions().getFilter();
	}

	@LuaFunction(mainThread = true)
	public final void setMode(String filter) {
		blockEntity.getOptions().setFilter(filter);
		blockEntity.setChanged();
	}

	@LuaFunction
	public final double getMinDistance() {
		return blockEntity.getOptions().getMinDistance();
	}

	@LuaFunction(mainThread = true)
	public final void setMinDistance(double minDistance) {
		blockEntity.getOptions().setMinDistance((float)minDistance);
		blockEntity.setChanged();
	}

	@LuaFunction
	public final double getMaxDistance() {
		return blockEntity.getOptions().getMaxDistance();
	}

	@LuaFunction(mainThread = true)
	public final void setMaxDistance(double maxDistance) {
		blockEntity.getOptions().setMaxDistance((float)maxDistance);
		blockEntity.setChanged();
	}
}
