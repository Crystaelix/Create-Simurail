package com.crystaelix.simurail.config;

public class SimurailBlocksConfig extends SimurailBaseConfig {

	public final ConfigGroup connection = group(1, "connection", "Connections");
	public final ConfigFloat connectionBogeyRangeSame = f(32, 1, 256, "bogeyRangeSame", Units.length, Comments.connectionBogeyRangeSame);
	public final ConfigFloat connectionBogeyRangeDifferent = f(12, 1, 256, "bogeyRangeDifferent", Units.length, Comments.connectionBogeyRangeDifferent);
	public final ConfigFloat connectionCouplerRange = f(4, 1, 256, "couplerRange", Units.length, Comments.connectionCouplerRange);

	public final ConfigGroup bogey = group(1, "bogey", "Physics Bogies");
	public final ConfigInt bogeyProbeInterval = i(5, 1, 40, "bogeyProbeInterval", Units.ticks, Comments.bogeyProbeInterval);

	public final ConfigGroup probeReader = group(1, "probeReader", "Bogie Probe Readers");
	public final ConfigFloat probeReaderRange = f(64, 1, 512, "probeReaderRange", Units.length, Comments.probeReaderRange);

	public final ConfigGroup remoteController = group(1, "remoteController", "Bogie Remote Controllers");
	public final ConfigFloat remoteControllerRange = f(64, 1, 512, "remoteControllerRange", Units.length, Comments.remoteControllerRange);

	@Override
	public String getName() {
		return "blocks";
	}

	static class Comments {
		static String connectionBogeyRangeSame = "The maximum distance of connections between Physics Bogies on the same sublevel.";
		static String connectionBogeyRangeDifferent = "The maximum distance of connections between Physics Bogies on different sublevels.";
		static String connectionCouplerRange = "The maximum distance of connections between a Train Coupler and a Physics Bogie.";

		static String bogeyProbeInterval = "Interval to probe the track for track points for the Physics Bogie.";

		static String probeReaderRange = "Maximum distance between a Bogie Probe Reader and its targeted Physics Bogie.";

		static String remoteControllerRange = "Maximum distance between a Bogie Remote Controller and its targeted Physics Bogie.";
	}
}
