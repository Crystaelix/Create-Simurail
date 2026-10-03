package com.crystaelix.simurail.compat.offroad.ponder;

import com.crystaelix.simurail.compat.offroad.SimurailOffroadBlocks;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;

import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.world.level.ItemLike;

public class SimurailOffroadPonderTags {

	public static void register(PonderTagRegistrationHelper<ItemLike> helper) {
		helper.addToTag(AllCreatePonderTags.KINETIC_APPLIANCES).
		add(SimurailOffroadBlocks.BRASS_BOREHEAD_BEARING);
	}
}
