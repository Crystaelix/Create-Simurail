package com.crystaelix.simurail.content;

import com.crystaelix.simurail.Simurail;
import com.crystaelix.simurail.content.bogey.InvertedPhysicsBogeyBlockItem;
import com.crystaelix.simurail.content.connector.ConnectorItem;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.ItemEntry;

public class SimurailItems {

	private static final CreateRegistrate REGISTRATE = Simurail.registrate();

	public static final ItemEntry<InvertedPhysicsBogeyBlockItem> INVERTED_PHYSICS_BOGEY = REGISTRATE.
			item("inverted_physics_bogey", InvertedPhysicsBogeyBlockItem::new).
			register();
	public static final ItemEntry<ConnectorItem> CONNECTOR = REGISTRATE.
			item("connector", ConnectorItem::new).
			properties(p -> p.stacksTo(1)).
			register();

	public static void register() {
	}
}
