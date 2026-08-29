package com.crystaelix.simurail.compat.electroenergetics.ponder;

import com.crystaelix.simurail.content.SimurailBlocks;
import com.crystaelix.simurail.content.SimurailItems;
import com.george_vi.electroenergetics.CEEBlocks;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class SimurailElectroEnergeticsPonderScenes {

	public static void register(PonderSceneRegistrationHelper<ResourceLocation> registry) {
		PonderSceneRegistrationHelper<ItemProviderEntry<?, ?>> helper = registry.withKeyFunction(RegistryEntry::getId);

		helper.forComponents(SimurailBlocks.PHYSICS_BOGEY, SimurailItems.INVERTED_PHYSICS_BOGEY).
		addStoryBoard("physics_bogey/electric", PhysicsBogeyElectricScenes::catenary).
		addStoryBoard("physics_bogey/third_rail", PhysicsBogeyElectricScenes::thirdRail);

		registry.forComponents(CEEBlocks.PANTOGRAPH.getId(), CEEBlocks.CATENARY_HOLDER.getId()).
		addStoryBoard("physics_bogey/electric", PhysicsBogeyElectricScenes::catenary);

		registry.forComponents(CEEBlocks.RAIL_CONTACT_SHOE.getId()).
		addStoryBoard("physics_bogey/third_rail", PhysicsBogeyElectricScenes::thirdRail);
	}
}
