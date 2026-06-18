package com.crystaelix.simurail.content;

import com.crystaelix.simurail.Simurail;
import com.crystaelix.simurail.content.automatic_coupler.AutomaticCouplerBlock;
import com.crystaelix.simurail.content.bogey.PhysicsBogeyBlock;
import com.crystaelix.simurail.content.bogey.PhysicsBogeyBlockItem;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;

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
			properties(p -> p.noOcclusion().mapColor(MapColor.NONE).sound(SoundType.METAL)).
			simpleItem().
			register();

	public static void register() {
	}
}
