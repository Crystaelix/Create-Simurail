package com.crystaelix.simurail.config;

public class SimurailCompatConfig extends SimurailBaseConfig {

	public final ConfigGroup railways = group(1, "railways", "Steam 'n' Rails");
	public final ConfigFloat axleMonorailLateralMaxSpeedFactor = f(500, 0, Float.MAX_VALUE, "axleMonorailLateralMaxSpeedFactor", Units.acceleration, Comments.axleMonorailLateralMaxSpeedFactor);
	public final ConfigFloat axleMonorailVerticalMaxSpeedFactor = f(100, 0, Float.MAX_VALUE, "axleMonorailVerticalMaxSpeedFactor", Units.acceleration, Comments.axleMonorailVerticalMaxSpeedFactor);
	public final ConfigFloat axleNarrowLateralMaxSpeedFactor = f(20, 0, Float.MAX_VALUE, "axleNarrowLateralMaxSpeedFactor", Units.acceleration, Comments.axleNarrowLateralMaxSpeedFactor);
	public final ConfigFloat axleNarrowVerticalMaxSpeedFactor = f(50, 0, Float.MAX_VALUE, "axleNarrowVerticalMaxSpeedFactor", Units.acceleration, Comments.axleNarrowVerticalMaxSpeedFactor);
	public final ConfigFloat axleWideLateralMaxSpeedFactor = f(50, 0, Float.MAX_VALUE, "axleWideLateralMaxSpeedFactor", Units.acceleration, Comments.axleWideLateralMaxSpeedFactor);
	public final ConfigFloat axleWideVerticalMaxSpeedFactor = f(50, 0, Float.MAX_VALUE, "axleWideVerticalMaxSpeedFactor", Units.acceleration, Comments.axleWideVerticalMaxSpeedFactor);

	@Override
	public String getName() {
		return "compat";
	}

	static class Comments {
		static String axleMonorailLateralMaxSpeedFactor = "Lateral max speed factor between an axle of the Physics Bogey and a monorail track. Max speed is sqrt(factor / curvature).";
		static String axleMonorailVerticalMaxSpeedFactor = "Vertical max speed factor between an axle of the Physics Bogey and a monorail track. Max speed is sqrt(factor / curvature).";
		static String axleNarrowLateralMaxSpeedFactor = "Lateral max speed factor between an axle of the Physics Bogey and a narrow track. Max speed is sqrt(factor / curvature).";
		static String axleNarrowVerticalMaxSpeedFactor = "Vertical max speed factor between an axle of the Physics Bogey and a narrow track. Max speed is sqrt(factor / curvature).";
		static String axleWideLateralMaxSpeedFactor = "Lateral max speed factor between an axle of the Physics Bogey and a wide track. Max speed is sqrt(factor / curvature).";
		static String axleWideVerticalMaxSpeedFactor = "Vertical max speed factor between an axle of the Physics Bogey and a wide track. Max speed is sqrt(factor / curvature).";
	}
}
