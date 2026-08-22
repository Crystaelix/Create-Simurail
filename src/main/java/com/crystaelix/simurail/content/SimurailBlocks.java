package com.crystaelix.simurail.content;

import com.crystaelix.simurail.Simurail;
import com.crystaelix.simurail.content.automatic_coupler.AutomaticCouplerBlock;
import com.crystaelix.simurail.content.automatic_coupler.AutomaticCouplerBlockItem;
import com.crystaelix.simurail.content.bogey.PhysicsBogeyBlock;
import com.crystaelix.simurail.content.bogey.PhysicsBogeyBlockItem;
import com.crystaelix.simurail.content.gangway_frame.GangwayFrameBlock;
import com.crystaelix.simurail.content.probe_reader.ProbeReaderBlock;
import com.crystaelix.simurail.content.probe_reader.ProbeReaderBlockItem;
import com.crystaelix.simurail.content.remote_controller.RemoteControllerBlock;
import com.crystaelix.simurail.content.remote_controller.RemoteControllerBlockItem;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.item.ItemDescription;
import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

public class SimurailBlocks {

	private static final CreateRegistrate REGISTRATE = Simurail.registrate();

	public static final BlockEntry<PhysicsBogeyBlock> PHYSICS_BOGEY = REGISTRATE.
			block("physics_bogey", PhysicsBogeyBlock::new).
			initialProperties(SharedProperties::softMetal).
			properties(p -> p.noOcclusion().mapColor(MapColor.TERRACOTTA_CYAN).sound(SoundType.NETHERITE_BLOCK)).
			item(PhysicsBogeyBlockItem::new).
			build().
			register();
	public static final BlockEntry<AutomaticCouplerBlock> AUTOMATIC_COUPLER = REGISTRATE.
			block("automatic_coupler", AutomaticCouplerBlock::new).
			initialProperties(SharedProperties::softMetal).
			properties(p -> p.noOcclusion().dynamicShape().mapColor(MapColor.NONE).sound(SoundType.METAL)).
			item(AutomaticCouplerBlockItem::new).
			build().
			register();
	public static final BlockEntry<GangwayFrameBlock> GANGWAY_FRAME = REGISTRATE.
			block("gangway_frame", GangwayFrameBlock::new).
			initialProperties(SharedProperties::softMetal).
			properties(p -> p.noOcclusion().dynamicShape().mapColor(MapColor.NONE).sound(SoundType.METAL)).
			simpleItem().
			register();
	public static final BlockEntry<ProbeReaderBlock> PROBE_READER = REGISTRATE.
			block("probe_reader", ProbeReaderBlock::new).
			initialProperties(SharedProperties::softMetal).
			properties(p -> p.noOcclusion().mapColor(MapColor.NONE).sound(SoundType.METAL)).
			onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey(v, "block.simurail.probe_reader")).
			item(ProbeReaderBlockItem::new).
			build().
			register();
	public static final BlockEntry<RemoteControllerBlock> REMOTE_CONTROLLER = REGISTRATE.
			block("remote_controller", RemoteControllerBlock::new).
			initialProperties(SharedProperties::softMetal).
			properties(p -> p.noOcclusion().mapColor(MapColor.NONE).sound(SoundType.METAL)).
			item(RemoteControllerBlockItem::new).
			build().
			register();

	public static void register() {
	}
}
