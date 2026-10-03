package com.crystaelix.simurail.compat.electroenergetics.ponder;

import com.crystaelix.simurail.content.SimurailBlocks;
import com.crystaelix.simurail.content.SimurailItems;
import com.crystaelix.simurail.extension.PonderSceneRegistrationHelperExtension;
import com.george_vi.electroenergetics.CEEBlocks;

import net.minecraft.world.level.ItemLike;

public class SimurailElectroEnergeticsPonderScenes {

	public static void register(PonderSceneRegistrationHelperExtension<ItemLike> helper) {
		helper.forComponents(SimurailBlocks.PHYSICS_BOGEY, SimurailItems.INVERTED_PHYSICS_BOGEY, SimurailBlocks.UNPOWERED_PHYSICS_BOGEY, SimurailItems.INVERTED_UNPOWERED_PHYSICS_BOGEY).
		addStoryBoard("physics_bogey/electric", PhysicsBogeyElectricScenes::catenary).
		addStoryBoard("physics_bogey/third_rail", PhysicsBogeyElectricScenes::thirdRail);

		helper.forComponents(CEEBlocks.PANTOGRAPH, CEEBlocks.CATENARY_HOLDER).
		addStoryBoard("physics_bogey/electric", PhysicsBogeyElectricScenes::catenary);

		helper.forComponents(CEEBlocks.RAIL_CONTACT_SHOE).
		addStoryBoard("physics_bogey/third_rail", PhysicsBogeyElectricScenes::thirdRail);
	}
}
