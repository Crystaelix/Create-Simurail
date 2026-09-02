package com.crystaelix.simurail.content;

import com.crystaelix.simurail.Simurail;
import com.crystaelix.simurail.content.automatic_coupler.AutomaticCouplerBlockEntity;
import com.crystaelix.simurail.content.automatic_coupler.AutomaticCouplerRenderer;
import com.crystaelix.simurail.content.automatic_coupler.AutomaticCouplerVisual;
import com.crystaelix.simurail.content.bogey.PhysicsBogeyBlockEntity;
import com.crystaelix.simurail.content.bogey.PhysicsBogeyRenderer;
import com.crystaelix.simurail.content.bogey.PhysicsBogeyVisual;
import com.crystaelix.simurail.content.physics_roller.PhysicsRollerBlockEntity;
import com.crystaelix.simurail.content.physics_roller.PhysicsRollerRenderer;
import com.crystaelix.simurail.content.gangway_frame.GangwayFrameBlockEntity;
import com.crystaelix.simurail.content.gangway_frame.GangwayFrameRenderer;
import com.crystaelix.simurail.content.probe_reader.ProbeReaderBlockEntity;
import com.crystaelix.simurail.content.remote_controller.RemoteControllerBlockEntity;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class SimurailBlockEntities {

	private static final CreateRegistrate REGISTRATE = Simurail.registrate();

	public static final BlockEntityEntry<PhysicsBogeyBlockEntity> PHYSICS_BOGEY = REGISTRATE.
			blockEntity("physics_bogey", PhysicsBogeyBlockEntity::new).
			visual(() -> PhysicsBogeyVisual::new, false).
			renderer(() -> PhysicsBogeyRenderer::new).
			validBlocks(SimurailBlocks.PHYSICS_BOGEY, SimurailBlocks.UNPOWERED_PHYSICS_BOGEY).
			register();
	public static final BlockEntityEntry<AutomaticCouplerBlockEntity> COUPLER = REGISTRATE.
			blockEntity("coupler", AutomaticCouplerBlockEntity::new).
			visual(() -> AutomaticCouplerVisual::new).
			renderer(() -> AutomaticCouplerRenderer::new).
			validBlocks(SimurailBlocks.AUTOMATIC_COUPLER).
			register();
	public static final BlockEntityEntry<GangwayFrameBlockEntity> GANGWAY_FRAME = REGISTRATE.
			blockEntity("gangway_frame", GangwayFrameBlockEntity::new).
			renderer(() -> GangwayFrameRenderer::new).
			validBlocks(SimurailBlocks.GANGWAY_FRAME).
			register();
	public static final BlockEntityEntry<ProbeReaderBlockEntity> PROBE_READER = REGISTRATE.
			blockEntity("probe_reader", ProbeReaderBlockEntity::new).
			validBlocks(SimurailBlocks.PROBE_READER).
			register();
	public static final BlockEntityEntry<PhysicsRollerBlockEntity> PHYSICS_ROLLER = REGISTRATE.
			blockEntity("physics_roller", PhysicsRollerBlockEntity::new).
			renderer(() -> PhysicsRollerRenderer::new).
			validBlocks(SimurailBlocks.PHYSICS_ROLLER).
			register();
	public static final BlockEntityEntry<RemoteControllerBlockEntity> REMOTE_CONTROLLER = REGISTRATE.
			blockEntity("remote_controller", RemoteControllerBlockEntity::new).
			validBlocks(SimurailBlocks.REMOTE_CONTROLLER).
			register();

	public static void register() {
	}

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		PhysicsBogeyBlockEntity.registerCapabilities(event);
		ProbeReaderBlockEntity.registerCapabilities(event);
		RemoteControllerBlockEntity.registerCapabilities(event);
	}
}
