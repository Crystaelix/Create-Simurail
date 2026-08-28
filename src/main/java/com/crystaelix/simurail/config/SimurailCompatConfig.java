package com.crystaelix.simurail.config;

public class SimurailCompatConfig extends SimurailBaseConfig {

	public final ConfigGroup railways = group(1, "railways", "Steam 'n' Rails");

	public final ConfigGroup monorail = group(2, "monorail", "Monorail");
	public final ConfigFloat monorailLateralMaxSpeedFactor = f(500, 0, Float.MAX_VALUE, "lateralMaxSpeedFactor", Units.acceleration, Comments.monorailLateralMaxSpeedFactor);
	public final ConfigFloat monorailVerticalMaxSpeedFactor = f(100, 0, Float.MAX_VALUE, "verticalMaxSpeedFactor", Units.acceleration, Comments.monorailVerticalMaxSpeedFactor);
	public final ConfigFloat monorailAdhesionFactor = f(15, 0, Float.MAX_VALUE, "adhesionFactor", Units.acceleration, Comments.monorailAdhesionFactor);

	public final ConfigGroup narrow = group(2, "narrow", "Narrow");
	public final ConfigFloat narrowLateralMaxSpeedFactor = f(25, 0, Float.MAX_VALUE, "lateralMaxSpeedFactor", Units.acceleration, Comments.narrowLateralMaxSpeedFactor);
	public final ConfigFloat narrowVerticalMaxSpeedFactor = f(50, 0, Float.MAX_VALUE, "verticalMaxSpeedFactor", Units.acceleration, Comments.narrowVerticalMaxSpeedFactor);
	public final ConfigFloat narrowAdhesionFactor = f(6, 0, Float.MAX_VALUE, "adhesionFactor", Units.acceleration, Comments.narrowAdhesionFactor);

	public final ConfigGroup wide = group(2, "wide", "Wide");
	public final ConfigFloat wideLateralMaxSpeedFactor = f(50, 0, Float.MAX_VALUE, "lateralMaxSpeedFactor", Units.acceleration, Comments.wideLateralMaxSpeedFactor);
	public final ConfigFloat wideVerticalMaxSpeedFactor = f(50, 0, Float.MAX_VALUE, "lateralMaxSpeedFactor", Units.acceleration, Comments.wideVerticalMaxSpeedFactor);
	public final ConfigFloat wideAdhesionFactor = f(6, 0, Float.MAX_VALUE, "adhesionFactor", Units.acceleration, Comments.wideAdhesionFactor);

	@Override
	public String getName() {
		return "compat";
	}

	static class Comments {
		static String monorailLateralMaxSpeedFactor = "Lateral max speed factor between an axle of the Physics Bogie and a monorail track. Max speed is sqrt(factor / curvature).";
		static String monorailVerticalMaxSpeedFactor = "Vertical max speed factor between an axle of the Physics Bogie and a monorail track. Max speed is sqrt(factor / curvature).";
		static String monorailAdhesionFactor = "Maximum traction coefficient a Physics Bogie axle can transmit to a monorail track, per unit of mass resting on that axle. Any drive force beyond this spins the wheels instead of moving the bogie.";

		static String narrowLateralMaxSpeedFactor = "Lateral max speed factor between an axle of the Physics Bogie and a narrow track. Max speed is sqrt(factor / curvature).";
		static String narrowVerticalMaxSpeedFactor = "Vertical max speed factor between an axle of the Physics Bogie and a narrow track. Max speed is sqrt(factor / curvature).";
		static String narrowAdhesionFactor = "Maximum traction coefficient a Physics Bogie axle can transmit to a narrow track, per unit of mass resting on that axle. Any drive force beyond this spins the wheels instead of moving the bogie.";

		static String wideLateralMaxSpeedFactor = "Lateral max speed factor between an axle of the Physics Bogie and a wide track. Max speed is sqrt(factor / curvature).";
		static String wideVerticalMaxSpeedFactor = "Vertical max speed factor between an axle of the Physics Bogie and a wide track. Max speed is sqrt(factor / curvature).";
		static String wideAdhesionFactor = "Maximum traction coefficient a Physics Bogie axle can transmit to a wide track, per unit of mass resting on that axle. Any drive force beyond this spins the wheels instead of moving the bogie.";
	}
}
