package com.crystaelix.simurail.content;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class SimurailItemTags {

	public static final TagKey<Item>
	CEE_WIRE_SPOOLS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("electroenergetics", "wire_spools"));
}
