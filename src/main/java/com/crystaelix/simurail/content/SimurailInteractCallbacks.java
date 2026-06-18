package com.crystaelix.simurail.content;

import com.crystaelix.simurail.content.steering_connector.SteeringConnectorInteractCallback;

import dev.simulated_team.simulated.index.SimClickInteractions;

public class SimurailInteractCallbacks {

	public static final SteeringConnectorInteractCallback CONNECTOR = SimClickInteractions.register(new SteeringConnectorInteractCallback());

	public static void register() {
	}
}
