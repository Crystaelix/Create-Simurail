package com.crystaelix.simurail.content;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import com.crystaelix.simurail.Simurail;
import com.crystaelix.simurail.api.bogey.BogeyLinkData;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SimurailDataComponents {

	public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Simurail.MOD_ID);

	public static final Supplier<DataComponentType<BogeyLinkData>> BOGEY_LINK_DATA = dataComponent(
			"bogey_link_data", builder -> builder.persistent(BogeyLinkData.CODEC).networkSynchronized(BogeyLinkData.STREAM_CODEC));

	public static void register(IEventBus modEventBus) {
		REGISTRAR.register(modEventBus);
	}
	
	private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> dataComponent(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
		return REGISTRAR.registerComponentType(name, builder);
	}
}
