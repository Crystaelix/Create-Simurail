package com.crystaelix.simurail.ponder;

import com.crystaelix.simurail.Simurail;
import com.crystaelix.simurail.extension.PonderSceneRegistrationHelperExtension;

import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

public class SimurailPonderPlugin implements PonderPlugin {

	@Override
	public String getModId() {
		return Simurail.MOD_ID;
	}

	@Override
	public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> registry) {
		SimurailPonderScenes.register(PonderSceneRegistrationHelperExtension.cast(registry.withKeyFunction(SimurailPonderPlugin::getKey)));
	}

	@Override
	public void registerTags(PonderTagRegistrationHelper<ResourceLocation> registry) {
		SimurailPonderTags.register(registry.withKeyFunction(SimurailPonderPlugin::getKey));
	}

	public static ResourceLocation getKey(ItemLike item) {
		if(item instanceof DeferredHolder<?, ?> holder) {
			return holder.getId();
		}
		if(item instanceof Block block) {
			return RegisteredObjectsHelper.getKeyOrThrow(block);
		}
		return RegisteredObjectsHelper.getKeyOrThrow(item.asItem());
	}
}
