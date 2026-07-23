package com.crystaelix.simurail.ponder;

import com.crystaelix.simurail.content.SimurailBlocks;
import com.crystaelix.simurail.content.SimurailItems;
import com.crystaelix.simurail.ponder.scenes.AutomaticCouplerScenes;
import com.crystaelix.simurail.ponder.scenes.GangwayFrameScenes;
import com.crystaelix.simurail.ponder.scenes.PhysicsBogeyScenes;
import com.crystaelix.simurail.ponder.scenes.ConnectorScenes;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class SimurailPonderScenes {

	public static void register(PonderSceneRegistrationHelper<ResourceLocation> registry) {
		PonderSceneRegistrationHelper<ItemProviderEntry<?, ?>> helper = registry.withKeyFunction(RegistryEntry::getId);

		helper.forComponents(SimurailBlocks.PHYSICS_BOGEY, SimurailItems.INVERTED_PHYSICS_BOGEY).
		addStoryBoard("physics_bogey/intro", PhysicsBogeyScenes::intro);

		helper.forComponents(SimurailBlocks.AUTOMATIC_COUPLER).
		addStoryBoard("automatic_coupler/intro", AutomaticCouplerScenes::intro);

		helper.forComponents(SimurailBlocks.GANGWAY_FRAME).
		addStoryBoard("gangway_frame/intro", GangwayFrameScenes::intro).
		addStoryBoard("gangway_frame/coupler", GangwayFrameScenes::coupler);

		helper.forComponents(SimurailItems.CONNECTOR).
		addStoryBoard("connector/intro", ConnectorScenes::intro).
		addStoryBoard("connector/coupler", ConnectorScenes::coupler);
	}
}
