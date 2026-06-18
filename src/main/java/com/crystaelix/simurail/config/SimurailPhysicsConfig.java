package com.crystaelix.simurail.config;

import net.createmod.catnip.config.ConfigBase;

public class SimurailPhysicsConfig extends ConfigBase {

	public final ConfigGroup bogey = group(1, "bogey", "Physics Bogeys");
	public final ConfigFloat bogeyPivotMass = f(1, 0, Float.MAX_VALUE, "bogeyPivotMass", Units.mass, Comments.bogeyPivotMass);
	public final ConfigFloat bogeyPassiveLinearDamping = f(100, 0, Float.MAX_VALUE, "bogeyPassiveLinearDamping", Units.damping, Comments.bogeyPassiveLinearDamping);
	public final ConfigFloat bogeyPassiveAngularDamping = f(1, 0, Float.MAX_VALUE, "bogeyPassiveAngularDamping", Units.angularDamping, Comments.bogeyPassiveAngularDamping);
	public final ConfigFloat bogeyVerticalSpringFrequency = f(10, 0, Float.MAX_VALUE, "bogeyVerticalSpringFrequency", Units.angularVelocity, Comments.bogeyVerticalSpringFrequency);
	public final ConfigFloat bogeyVerticalSpringDampingRate = f(1, 0, Float.MAX_VALUE, "bogeyVerticalSpringDampingRate", Comments.bogeyVerticalSpringDampingRate);
	public final ConfigFloat bogeyLateralSpringFrequency = f(10, 0, Float.MAX_VALUE, "bogeyLateralSpringFrequency", Units.angularVelocity, Comments.bogeyLateralSpringFrequency);
	public final ConfigFloat bogeyLateralSpringDampingRate = f(1, 0, Float.MAX_VALUE, "bogeyLateralSpringDampingRate", Comments.bogeyLateralSpringDampingRate);
	public final ConfigFloat bogeyAngularSpringFrequency = f(20, 0, Float.MAX_VALUE, "bogeyAngularSpringFrequency", Units.angularVelocity, Comments.bogeyAngularSpringFrequency);
	public final ConfigFloat bogeyAngularSpringDampingRate = f(1, 0, Float.MAX_VALUE, "bogeyAngularSpringDampingRate", Comments.bogeyAngularSpringDampingRate);
	public final ConfigFloat bogeyAngularSpringMaxMoment = f(20, 0, Float.MAX_VALUE, "bogeyAngularSpringMaxMoment", Units.moment, Comments.bogeyAngularSpringMaxMoment);

	public final ConfigGroup axle = group(1, "axle", "Physics Bogey Axles");
	public final ConfigFloat axleSpacingUpdateTime = f(2, 0, Float.MAX_VALUE, "axleSpacingUpdateTime", Units.time, Comments.axleSpacingUpdateTime);
	public final ConfigFloat axlePassiveLinearDamping = f(100, 0, Float.MAX_VALUE, "axlePassiveLinearDamping", Units.damping, Comments.axlePassiveLinearDamping);
	public final ConfigFloat axlePassiveAngularDamping = f(1, 0, Float.MAX_VALUE, "axlePassiveAngularDamping", Units.angularDamping, Comments.axlePassiveAngularDamping);
	public final ConfigFloat axleLateralMaxSpeedFactor = f(30, 0, Float.MAX_VALUE, "axleLateralMaxSpeedFactor", Units.acceleration, Comments.axleLateralMaxSpeedFactor);
	public final ConfigFloat axleVerticalMaxSpeedFactor = f(50, 0, Float.MAX_VALUE, "axleVerticalMaxSpeedFactor", Units.acceleration, Comments.axleVerticalMaxSpeedFactor);
	public final ConfigFloat axleBrakeStrengthFactor = f(20, 0, Float.MAX_VALUE, "axleBrakeStrengthFactor", Units.force, Comments.axleBrakeStrengthFactor);
	public final ConfigFloat axleTargetSpeedFactor = f(0.25F, 0, Float.MAX_VALUE, "axleTargetSpeedFactor", Units.velocity, Comments.axleTargetSpeedFactor);
	public final ConfigFloat axleDriveForceFactor = f(0.5F, 0, Float.MAX_VALUE, "axleDriveForceFactor", Units.damping, Comments.axleDriveForceFactor);
	public final ConfigFloat axleTrackCheckTime = f(0.1F, 0, Float.MAX_VALUE, "axleTrackCheckTime", Units.time, Comments.axleTrackCheckTime);
	public final ConfigFloat axleTrackRecheckTime = f(3, 0, Float.MAX_VALUE, "axleTrackRecheckTime", Units.time, Comments.axleTrackRecheckTime);

	public final ConfigGroup coupler = group(1, "coupler", "Train Couplers");
	public final ConfigFloat couplerPassiveLinearDamping = f(100, 0, Float.MAX_VALUE, "couplerPassiveLinearDamping", Units.damping, Comments.couplerPassiveLinearDamping);
	public final ConfigFloat couplerPassiveAngularDamping = f(1, 0, Float.MAX_VALUE, "couplerPassiveAngularDamping", Units.angularDamping, Comments.couplerPassiveAngularDamping);

	@Override
	public String getName() {
		return "physics";
	}

	static class Comments {
		static String bogeyPivotMass = "The mass of the pivot of the Physics Bogey.";
		static String bogeyPassiveLinearDamping = "Passive linear damping between the Physics Bogey and its pivot.";
		static String bogeyPassiveAngularDamping = "Passive angular damping between the Physics Bogey and its pivot.";
		static String bogeyVerticalSpringFrequency = "Vertical spring frequency between the Physics Bogey and its pivot.";
		static String bogeyVerticalSpringDampingRate = "Vertical spring damping rate between the Physics Bogey and its pivot.";
		static String bogeyLateralSpringFrequency = "Lateral spring frequency between the Physics Bogey and its pivot.";
		static String bogeyLateralSpringDampingRate = "Lateral spring damping rate between the Physics Bogey and its pivot.";
		static String bogeyAngularSpringFrequency = "Angular spring frequency between the Physics Bogey and its pivot.";
		static String bogeyAngularSpringDampingRate = "Angular spring damping rate between the Physics Bogey and its pivot.";
		static String bogeyAngularSpringMaxMoment = "Angular spring maximum moment between the Physics Bogey and its pivot.";

		static String axleSpacingUpdateTime = "Time to update the axle spacing when changed for the axles of the Physics Bogey.";
		static String axlePassiveLinearDamping = "Passive linear damping between an axle of the Physics Bogey and its track.";
		static String axlePassiveAngularDamping = "Passive angular damping between an axle of the Physics Bogey and its track.";
		static String axleLateralMaxSpeedFactor = "Lateral max speed factor between an axle of the Physics Bogey and its track. Max speed is sqrt(factor / curvature).";
		static String axleVerticalMaxSpeedFactor = "Vertical max speed factor between an axle of the Physics Bogey and its track. Max speed is sqrt(factor / curvature).";
		static String axleBrakeStrengthFactor = "Conversion of brake strength [0-1] to brake force between an axle of the Physics Bogey and its track.";
		static String axleTargetSpeedFactor = "Conversion of RPM to target speed between an axle of the Physics Bogey and its track.";
		static String axleDriveForceFactor = "Conversion of current and target speed difference to drive force between an axle of the Physics Bogey and its track.";
		static String axleTrackCheckTime = "Inverval to find nearest track when derailed for an axle of the Physics Bogey.";
		static String axleTrackRecheckTime = "Inverval to re-find nearest track for an axle of the Physics Bogey.";

		static String couplerPassiveLinearDamping = "Passive linear damping between a Train Coupler and its partner.";
		static String couplerPassiveAngularDamping = "Passive angular damping between a Train Coupler and its partner.";
	}
}
