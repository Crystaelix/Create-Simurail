package com.crystaelix.simurail.compat.electroenergetics.device;

import com.crystaelix.simurail.content.automatic_coupler.AutomaticCouplerBlockEntity;
import com.george_vi.electroenergetics.devices.device.DevicesSavedData;
import com.george_vi.electroenergetics.devices.device.SimulatedDeviceType;
import com.george_vi.electroenergetics.foundation.device.SimpleElectricalDevice;
import com.george_vi.electroenergetics.foundation.nodes.InWorldNode;
import com.george_vi.electroenergetics.simulation.BridgeCollector;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class AutomaticCouplerDevice extends SimpleElectricalDevice {
	
	protected AutomaticCouplerBlockEntity be;

	public AutomaticCouplerDevice(SimulatedDeviceType<?> type, Level level, BlockPos pos, DevicesSavedData deviceSD) {
		super(level, pos, deviceSD, type);
	}

	@Override
	public void preTick(BridgeCollector bridges) {
		if(be == null && level.isLoaded(pos) && level.getBlockEntity(pos) instanceof AutomaticCouplerBlockEntity be) {
			this.be = be;
		}
		if(be != null) {
			if(be.isRemoved()) {
				be = null;
			}
			else if(be.isPrimary()) {
				bridges.builder(pos).resistor(new InWorldNode(0, pos), new InWorldNode(0, be.getCouplerPartner()), 0.01);
			}
		}
	}

}
