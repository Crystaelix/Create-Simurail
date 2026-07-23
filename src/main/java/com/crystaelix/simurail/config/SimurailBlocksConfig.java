package com.crystaelix.simurail.config;

public class SimurailBlocksConfig extends SimurailBaseConfig {

	public final ConfigGroup connection = group(1, "connection", "Connections");
	public final ConfigFloat connectionBogeyRangeSame = f(32, 1, 256, "bogeyRangeSame", Units.length, Comments.connectionBogeyRangeSame);
	public final ConfigFloat connectionBogeyRangeDifferent = f(12, 1, 256, "bogeyRangeDifferent", Units.length, Comments.connectionBogeyRangeDifferent);
	public final ConfigFloat connectionCouplerRange = f(4, 1, 256, "couplerRange", Units.length, Comments.connectionCouplerRange);

	@Override
	public String getName() {
		return "blocks";
	}

	static class Comments {
		static String connectionBogeyRangeSame = "The maximum distance of connections between Physics Bogeys on the same sublevel.";
		static String connectionBogeyRangeDifferent = "The maximum distance of connections between Physics Bogeys on different sublevels.";
		static String connectionCouplerRange = "The maximum distance of connections between a Train Coupler and a Physics Bogey.";
	}
}
