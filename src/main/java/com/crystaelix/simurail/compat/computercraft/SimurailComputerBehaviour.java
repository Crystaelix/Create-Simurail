package com.crystaelix.simurail.compat.computercraft;

import java.util.function.Supplier;

import com.crystaelix.simurail.content.bogey.PhysicsBogeyBlockEntity;
import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.compat.computercraft.events.ComputerEvent;
import com.simibubi.create.compat.computercraft.implementation.peripherals.SyncedPeripheral;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.registries.BuiltInRegistries;

public class SimurailComputerBehaviour extends AbstractComputerBehaviour {

	SyncedPeripheral<?> peripheral;
	Supplier<SyncedPeripheral<?>> peripheralSupplier;
	SmartBlockEntity be;
	
	public SimurailComputerBehaviour(SmartBlockEntity be) {
		super(be);
		this.be = be;
	}

	public static Supplier<SyncedPeripheral<?>> getPeripheralFor(SmartBlockEntity be) {
		if(be instanceof PhysicsBogeyBlockEntity pbbe) {
			return () -> new PhysicsBogeyPeripheral(pbbe);
		}
		throw new IllegalArgumentException("No peripheral available for " + BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(be.getType()));
	}

	@Override
	public IPeripheral getPeripheralCapability() {
		if(peripheral == null) {
			peripheral = peripheralSupplier.get();
		}
		return peripheral;
	}

	@Override
	public void removePeripheral() {
		if(peripheral != null) {
			getWorld().invalidateCapabilities(be.getBlockPos());
		}
	}

	@Override
	public void prepareComputerEvent(ComputerEvent event) {
		if(peripheral != null) {
			peripheral.prepareComputerEvent(event);
		}
	}
}
