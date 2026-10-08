package com.crystaelix.simurail.ponder;

import com.crystaelix.simurail.compat.SimurailCompat;
import com.crystaelix.simurail.compat.electroenergetics.ponder.SimurailElectroEnergeticsPonderScenes;
import com.crystaelix.simurail.compat.offroad.ponder.SimurailOffroadPonderScenes;
import com.crystaelix.simurail.content.SimurailBlocks;
import com.crystaelix.simurail.content.SimurailItems;
import com.crystaelix.simurail.extension.PonderSceneRegistrationHelperExtension;
import com.crystaelix.simurail.ponder.scenes.AutomaticCouplerScenes;
import com.crystaelix.simurail.ponder.scenes.ConnectorScenes;
import com.crystaelix.simurail.ponder.scenes.GangwayFrameScenes;
import com.crystaelix.simurail.ponder.scenes.PhysicsBogeyScenes;
import com.crystaelix.simurail.ponder.scenes.PhysicsRollerScenes;
import com.crystaelix.simurail.ponder.scenes.ProbeReaderScenes;
import com.crystaelix.simurail.ponder.scenes.RemoteControllerScenes;
import com.crystaelix.simurail.ponder.scenes.TrackSwitchScenes;
import com.simibubi.create.Create;
import com.simibubi.create.infrastructure.ponder.scenes.RollerScenes;

import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

public class SimurailPonderScenes {

	// Create
	private static final ResourceLocation ROLLER_CLEAR_AND_PAVE = Create.asResource("mechanical_roller/clear_and_pave");
	private static final ResourceLocation ROLLER_FILL = Create.asResource("mechanical_roller/fill");

	public static void register(PonderSceneRegistrationHelperExtension<ItemLike> helper) {
		PonderSceneRegistrationHelper<ItemLike> createHelper = helper.simurail$withNamespace("create");

		helper.forComponents(SimurailBlocks.PHYSICS_BOGEY, SimurailItems.INVERTED_PHYSICS_BOGEY, SimurailBlocks.UNPOWERED_PHYSICS_BOGEY, SimurailItems.INVERTED_UNPOWERED_PHYSICS_BOGEY).
		addStoryBoard("physics_bogey/intro", PhysicsBogeyScenes::intro).
		addStoryBoard("connector/intro", ConnectorScenes::intro);

		helper.forComponents(SimurailBlocks.AUTOMATIC_COUPLER, SimurailBlocks.COPYCAT_PANEL_AUTOMATIC_COUPLER).
		addStoryBoard("automatic_coupler/intro", AutomaticCouplerScenes::intro).
		addStoryBoard("connector/coupler", ConnectorScenes::coupler).
		addStoryBoard("gangway_frame/coupler", GangwayFrameScenes::coupler);

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

		helper.forComponents(SimurailBlocks.TRACK_SWITCH).
		addStoryBoard("track_switch/intro", TrackSwitchScenes::intro);

		helper.forComponents(SimurailBlocks.PHYSICS_ROLLER).
		addStoryBoard("physics_roller/intro", PhysicsRollerScenes::intro).
		addStoryBoard("physics_roller/materials", PhysicsRollerScenes::materials);

		createHelper.forComponents(SimurailBlocks.PHYSICS_ROLLER).
		addStoryBoard(ROLLER_CLEAR_AND_PAVE, RollerScenes::clearAndPave).
		addStoryBoard(ROLLER_FILL, RollerScenes::fill);

		SimurailCompat.OFFROAD.ifLoaded(() -> () -> SimurailOffroadPonderScenes.register(helper));
		SimurailCompat.ELECTROENERGETICS.ifLoaded(() -> () -> SimurailElectroEnergeticsPonderScenes.register(helper));
	}
}
