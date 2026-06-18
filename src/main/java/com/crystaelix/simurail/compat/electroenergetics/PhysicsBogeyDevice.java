package com.crystaelix.simurail.compat.electroenergetics;

import com.crystaelix.simurail.content.bogey.PhysicsBogeyBlockEntity;
import com.george_vi.electroenergetics.devices.device.DevicesSavedData;
import com.george_vi.electroenergetics.devices.device.SimulatedDeviceType;
import com.george_vi.electroenergetics.foundation.device.SimpleElectricalDevice;
import com.george_vi.electroenergetics.simulation.BridgeCollector;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class PhysicsBogeyDevice extends SimpleElectricalDevice {

	public PhysicsBogeyDevice(SimulatedDeviceType<?> type, Level level, BlockPos pos, DevicesSavedData deviceSD) {
		super(level, pos, deviceSD, type);
	}

	@Override
	public void preTick(BridgeCollector bridges) {
		if(level.getBlockEntity(pos) instanceof PhysicsBogeyBlockEntity bogey &&
				(bogey.getAxle(true).hasTrack() || bogey.getAxle(false).hasTrack())) {
			bridges.builder(pos).ground(0, 1);
			bridges.builder(pos).ground(1, 1);
			bridges.builder(pos).ground(2, 1);
		}
	}
}
