package com.crystaelix.simurail.compat.electroenergetics;

import java.util.function.Supplier;

import com.crystaelix.simurail.Simurail;
import com.george_vi.electroenergetics.CEERegistries;
import com.george_vi.electroenergetics.devices.device.SimulatedDevice;
import com.george_vi.electroenergetics.devices.device.SimulatedDeviceFactory;
import com.george_vi.electroenergetics.devices.device.SimulatedDeviceType;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SimurailDeviceTypes {

	public static final DeferredRegister<SimulatedDeviceType<?>> REGISTRAR = DeferredRegister.create(CEERegistries.SIMULATED_DEVICE_TYPE, Simurail.MOD_ID);

	public static final Supplier<SimulatedDeviceType<PhysicsBogeyDevice>> PHYSICS_BOGEY = register("physics_bogey", PhysicsBogeyDevice::new);

	public static void register(IEventBus modEventBus) {
		REGISTRAR.register(modEventBus);
	}

	public static <T extends SimulatedDevice> DeferredHolder<SimulatedDeviceType<?>, SimulatedDeviceType<T>> register(String id, SimulatedDeviceFactory<T> factory) {
		return REGISTRAR.register(id, () -> new SimulatedDeviceType<>(Simurail.id(id), factory));
	}
}
