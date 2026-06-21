package com.crystaelix.simurail.config;

public class SimurailBlocksConfig extends SimurailBaseConfig {

	public final ConfigGroup connection = group(1, "steeringConnection", "Steering Connections");
	public final ConfigFloat bogeyConnectionRangeSame = f(32, 1, 256, "bogeyConnectionRangeSame", Units.length, Comments.bogeyConnectionRangeSame);
	public final ConfigFloat bogeyConnectionRangeDifferent = f(12, 1, 256, "bogeyConnectionRangeDifferent", Units.length, Comments.bogeyConnectionRangeDifferent);
	public final ConfigFloat couplerConnectionRange = f(4, 1, 256, "couplerConnectionRange", Units.length, Comments.couplerConnectionRange);

	@Override
	public String getName() {
		return "blocks";
	}

	static class Comments {
		static String bogeyConnectionRangeSame = "The maximum distance of steering connections between Physics Bogeys on the same sublevel.";
		static String bogeyConnectionRangeDifferent = "The maximum distance of steering connections between Physics Bogeys on different sublevels.";
		static String couplerConnectionRange = "The maximum distance of steering connections between a Train Coupler and a Physics Bogey.";
	}
}
