package com.crystaelix.simurail.ponder;

import com.crystaelix.simurail.Simurail;
import com.crystaelix.simurail.compat.SimurailCompat;
import com.crystaelix.simurail.compat.electroenergetics.ponder.SimurailElectroEnergeticsPonderScenes;
import com.crystaelix.simurail.content.SimurailBlocks;
import com.crystaelix.simurail.content.SimurailItems;
import com.crystaelix.simurail.ponder.scenes.AutomaticCouplerScenes;
import com.crystaelix.simurail.ponder.scenes.ConnectorScenes;
import com.crystaelix.simurail.ponder.scenes.GangwayFrameScenes;
import com.crystaelix.simurail.ponder.scenes.PhysicsBogeyScenes;
import com.crystaelix.simurail.ponder.scenes.PhysicsRollerScenes;
import com.crystaelix.simurail.ponder.scenes.ProbeReaderScenes;
import com.crystaelix.simurail.ponder.scenes.RemoteControllerScenes;
import com.simibubi.create.infrastructure.ponder.scenes.RollerScenes;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class SimurailPonderScenes {

	// create's
	private static final ResourceLocation MECHANICAL_ROLLER = ResourceLocation.fromNamespaceAndPath("create", "mechanical_roller");
	private static final ResourceLocation ROLLER_CLEAR_AND_PAVE = ResourceLocation.fromNamespaceAndPath("create", "mechanical_roller/clear_and_pave");
	private static final ResourceLocation ROLLER_FILL = ResourceLocation.fromNamespaceAndPath("create", "mechanical_roller/fill");

	private static final String PHYSICS_ROLLER_INTRO = "physics_roller/intro";
	private static final String PHYSICS_ROLLER_MATERIALS = "physics_roller/materials";

	public static void register(PonderSceneRegistrationHelper<ResourceLocation> registry) {
		PonderSceneRegistrationHelper<ItemProviderEntry<?, ?>> helper = registry.withKeyFunction(RegistryEntry::getId);

		helper.forComponents(SimurailBlocks.PHYSICS_BOGEY, SimurailItems.INVERTED_PHYSICS_BOGEY, SimurailBlocks.UNPOWERED_PHYSICS_BOGEY, SimurailItems.INVERTED_UNPOWERED_PHYSICS_BOGEY).
		addStoryBoard("physics_bogey/intro", PhysicsBogeyScenes::intro);

		helper.forComponents(SimurailBlocks.AUTOMATIC_COUPLER).
		addStoryBoard("automatic_coupler/intro", AutomaticCouplerScenes::intro);

		helper.forComponents(SimurailBlocks.GANGWAY_FRAME).
		addStoryBoard("gangway_frame/intro", GangwayFrameScenes::intro).
		addStoryBoard("gangway_frame/coupler", GangwayFrameScenes::coupler);

		helper.forComponents(SimurailItems.CONNECTOR).
		addStoryBoard("connector/intro", ConnectorScenes::intro).
		addStoryBoard("connector/coupler", ConnectorScenes::coupler);

		helper.forComponents(SimurailBlocks.PROBE_READER).
		addStoryBoard("probe_reader/intro", ProbeReaderScenes::intro);

		helper.forComponents(SimurailBlocks.REMOTE_CONTROLLER).
		addStoryBoard("remote_controller/intro", RemoteControllerScenes::intro);

		helper.forComponents(SimurailBlocks.PHYSICS_ROLLER).
		addStoryBoard("physics_roller/intro", PhysicsRollerScenes::intro).
		addStoryBoard("physics_roller/materials", PhysicsRollerScenes::materials).
		addStoryBoard(ROLLER_CLEAR_AND_PAVE, RollerScenes::clearAndPave).
		addStoryBoard(ROLLER_FILL, RollerScenes::fill);

		SimurailCompat.ELECTROENERGETICS.ifLoaded(() -> () ->
				SimurailElectroEnergeticsPonderScenes.register(registry)
		);
	}
}
