package com.crystaelix.simurail.ponder;

import com.crystaelix.simurail.compat.SimurailCompat;
import com.crystaelix.simurail.compat.offroad.ponder.SimurailOffroadPonderTags;
import com.crystaelix.simurail.content.SimurailBlocks;
import com.crystaelix.simurail.content.SimurailItems;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;

import dev.simulated_team.simulated.index.SimPonderTags;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.world.level.ItemLike;

public class SimurailPonderTags {

	public static void register(PonderTagRegistrationHelper<ItemLike> helper) {
		helper.addToTag(SimPonderTags.PHYSICS_BEHAVIOR).
		add(SimurailBlocks.PHYSICS_BOGEY).
		add(SimurailItems.INVERTED_PHYSICS_BOGEY).
		add(SimurailBlocks.UNPOWERED_PHYSICS_BOGEY).
		add(SimurailItems.INVERTED_UNPOWERED_PHYSICS_BOGEY).
		add(SimurailBlocks.AUTOMATIC_COUPLER).
		add(SimurailBlocks.PHYSICS_ROLLER);

		helper.addToTag(AllCreatePonderTags.KINETIC_APPLIANCES).
		add(SimurailBlocks.PHYSICS_BOGEY).
		add(SimurailItems.INVERTED_PHYSICS_BOGEY);

		helper.addToTag(AllCreatePonderTags.TRAIN_RELATED).
		add(SimurailBlocks.PHYSICS_BOGEY).
		add(SimurailItems.INVERTED_PHYSICS_BOGEY).
		add(SimurailBlocks.UNPOWERED_PHYSICS_BOGEY).
		add(SimurailItems.INVERTED_UNPOWERED_PHYSICS_BOGEY).
		add(SimurailBlocks.AUTOMATIC_COUPLER).
		add(SimurailBlocks.GANGWAY_FRAME).
		add(SimurailItems.CONNECTOR).
		add(SimurailBlocks.PROBE_READER).
		add(SimurailBlocks.REMOTE_CONTROLLER).
		add(SimurailBlocks.TRACK_SWITCH).
		add(SimurailBlocks.PHYSICS_ROLLER);
		
		helper.addToTag(AllCreatePonderTags.DISPLAY_SOURCES).
		add(SimurailBlocks.TRACK_SWITCH);

		SimurailCompat.OFFROAD.ifLoaded(() -> () -> SimurailOffroadPonderTags.register(helper));
	}
}
