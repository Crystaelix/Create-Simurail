package com.crystaelix.simurail.content;

import com.crystaelix.simurail.Simurail;
import com.crystaelix.simurail.content.track_switch.TrackSwitch;
import com.simibubi.create.content.trains.graph.EdgePointType;

public class SimurailEdgePoints {

	public static final EdgePointType<TrackSwitch> TRACK_SWITCH = EdgePointType.register(
			Simurail.id("physics_track_switch"),
			TrackSwitch::new);
}
