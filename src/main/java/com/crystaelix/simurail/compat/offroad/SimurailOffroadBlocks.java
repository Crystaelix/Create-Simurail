package com.crystaelix.simurail.compat.offroad;

import com.crystaelix.simurail.Simurail;
import com.crystaelix.simurail.compat.offroad.brass_borehead_bearing.BrassBoreheadBearingBlock;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

public class SimurailOffroadBlocks {

	private static final CreateRegistrate REGISTRATE = Simurail.registrate();

	public static final BlockEntry<BrassBoreheadBearingBlock> BRASS_BOREHEAD_BEARING = REGISTRATE.
			block("brass_borehead_bearing", BrassBoreheadBearingBlock::new).
			initialProperties(SharedProperties::softMetal).
			properties(p -> p.noOcclusion().requiresCorrectToolForDrops().mapColor(MapColor.COLOR_GRAY).sound(SoundType.NETHERITE_BLOCK).
					isRedstoneConductor((state, level, pos) -> false)).
			simpleItem().
			register();

	public static void register() {
	}
}
