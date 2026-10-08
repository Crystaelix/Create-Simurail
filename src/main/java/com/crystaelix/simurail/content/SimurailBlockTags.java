package com.crystaelix.simurail.content;

import com.crystaelix.simurail.Simurail;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class SimurailBlockTags {
	
	public static final TagKey<Block>
	ROLLER_NON_BREAKABLE = TagKey.create(Registries.BLOCK, Simurail.id("roller_non_breakable")),
	BORE_NON_BREAKABLE = TagKey.create(Registries.BLOCK, Simurail.id("bore_non_breakable"));
}
