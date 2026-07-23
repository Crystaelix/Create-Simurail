package com.crystaelix.simurail.content;

import com.crystaelix.simurail.content.connector.ConnectorInteractCallback;

import dev.simulated_team.simulated.index.SimClickInteractions;

public class SimurailInteractCallbacks {

	public static final ConnectorInteractCallback CONNECTOR = SimClickInteractions.register(new ConnectorInteractCallback());

	public static void register() {
	}
}
