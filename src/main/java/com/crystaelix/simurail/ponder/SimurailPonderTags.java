package com.crystaelix.simurail.ponder;

import com.crystaelix.simurail.content.SimurailBlocks;
import com.crystaelix.simurail.content.SimurailItems;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;

import dev.simulated_team.simulated.index.SimPonderTags;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

public class SimurailPonderTags {

	public static void register(PonderTagRegistrationHelper<ResourceLocation> registry) {
		PonderTagRegistrationHelper<ItemLike> helper = registry.withKeyFunction(RegisteredObjectsHelper::getKeyOrThrow);

		helper.addToTag(SimPonderTags.PHYSICS_BEHAVIOR).
		add(SimurailBlocks.PHYSICS_BOGEY.get()).
		add(SimurailBlocks.AUTOMATIC_COUPLER.get());

		helper.addToTag(AllCreatePonderTags.KINETIC_APPLIANCES).
		add(SimurailBlocks.PHYSICS_BOGEY.get());

		helper.addToTag(AllCreatePonderTags.TRAIN_RELATED).
		add(SimurailBlocks.PHYSICS_BOGEY.get()).
		add(SimurailBlocks.AUTOMATIC_COUPLER.get()).
		add(SimurailBlocks.GANGWAY_FRAME.get()).
		add(SimurailItems.CONNECTOR.get());
	}
}
