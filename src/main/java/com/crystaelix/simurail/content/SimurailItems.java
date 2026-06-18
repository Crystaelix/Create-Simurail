package com.crystaelix.simurail.content;

import com.crystaelix.simurail.Simurail;
import com.crystaelix.simurail.content.bogey.InvertedPhysicsBogeyBlockItem;
import com.crystaelix.simurail.content.steering_connector.SteeringConnectorItem;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.ItemEntry;

public class SimurailItems {

	private static final CreateRegistrate REGISTRATE = Simurail.registrate();

	public static final ItemEntry<InvertedPhysicsBogeyBlockItem> INVERTED_PHYSICS_BOGEY = REGISTRATE.
			item("inverted_physics_bogey", InvertedPhysicsBogeyBlockItem::new).
			register();
	public static final ItemEntry<SteeringConnectorItem> STEERING_CONNECTOR = REGISTRATE.
			item("steering_connector", SteeringConnectorItem::new).
			register();

	public static void register() {
	}
}
