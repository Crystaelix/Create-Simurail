package com.crystaelix.simurail.config;

public class SimurailPhysicsConfig extends SimurailBaseConfig {

	public final ConfigGroup bogey = group(1, "bogey", "Physics Bogeys");
	public final ConfigFloat bogeyPivotMass = f(1, 0, Float.MAX_VALUE, "bogeyPivotMass", Units.mass, Comments.bogeyPivotMass);
	public final ConfigFloat bogeyPassiveAngularDamping = f(10, 0, Float.MAX_VALUE, "bogeyPassiveAngularDamping", Units.angularDamping, Comments.bogeyPassiveAngularDamping);
	public final ConfigFloat bogeyVerticalSpringFrequency = f(50, 0, Float.MAX_VALUE, "bogeyVerticalSpringFrequency", Units.angularVelocity, Comments.bogeyVerticalSpringFrequency);
	public final ConfigFloat bogeyVerticalSpringDampingRate = f(1.2F, 0, Float.MAX_VALUE, "bogeyVerticalSpringDampingRate", Comments.bogeyVerticalSpringDampingRate);
	public final ConfigFloat bogeyVerticalSpringMaxForce = f(10000, 0, Float.MAX_VALUE, "bogeyVerticalSpringMaxForce", Units.force, Comments.bogeyVerticalSpringMaxForce);
	public final ConfigFloat bogeyLateralSpringFrequency = f(10, 0, Float.MAX_VALUE, "bogeyLateralSpringFrequency", Units.angularVelocity, Comments.bogeyLateralSpringFrequency);
	public final ConfigFloat bogeyLateralSpringDampingRate = f(1.2F, 0, Float.MAX_VALUE, "bogeyLateralSpringDampingRate", Comments.bogeyLateralSpringDampingRate);
	public final ConfigFloat bogeyLateralSpringMaxForce = f(10000, 0, Float.MAX_VALUE, "bogeyLateralSpringMaxForce", Units.force, Comments.bogeyLateralSpringMaxForce);
	public final ConfigFloat bogeyAngularSpringFrequency = f(15, 0, Float.MAX_VALUE, "bogeyAngularSpringFrequency", Units.angularVelocity, Comments.bogeyAngularSpringFrequency);
	public final ConfigFloat bogeyAngularSpringDampingRate = f(1.2F, 0, Float.MAX_VALUE, "bogeyAngularSpringDampingRate", Comments.bogeyAngularSpringDampingRate);
	public final ConfigFloat bogeyAngularSpringMaxTorque = f(5000, 0, Float.MAX_VALUE, "bogeyAngularSpringMaxTorque", Units.torque, Comments.bogeyAngularSpringMaxTorque);

	public final ConfigGroup axle = group(1, "axle", "Physics Bogey Axles");
	public final ConfigFloat axleSpacingUpdateTime = f(2, 0, 10, "axleSpacingUpdateTime", Units.time, Comments.axleSpacingUpdateTime);
	public final ConfigFloat axlePassiveLinearDamping = f(100, 0, Float.MAX_VALUE, "axlePassiveLinearDamping", Units.damping, Comments.axlePassiveLinearDamping);
	public final ConfigFloat axlePassiveAngularDamping = f(1, 0, Float.MAX_VALUE, "axlePassiveAngularDamping", Units.angularDamping, Comments.axlePassiveAngularDamping);
	public final ConfigFloat axleStandardLateralMaxSpeedFactor = f(30, 0, Float.MAX_VALUE, "axleStandardLateralMaxSpeedFactor", Units.acceleration, Comments.axleStandardLateralMaxSpeedFactor);
	public final ConfigFloat axleStandardVerticalMaxSpeedFactor = f(50, 0, Float.MAX_VALUE, "axleStandardVerticalMaxSpeedFactor", Units.acceleration, Comments.axleStandardVerticalMaxSpeedFactor);
	public final ConfigFloat axleTargetSpeedFactor = f(0.25F, 0, Float.MAX_VALUE, "axleTargetSpeedFactor", Units.velocity, Comments.axleTargetSpeedFactor);
	public final ConfigFloat axleDriveForceFactor = f(0.5F, 0, Float.MAX_VALUE, "axleDriveForceFactor", Units.damping, Comments.axleDriveForceFactor);
	public final ConfigFloat axleBrakeStrengthFactor = f(20, 0, Float.MAX_VALUE, "axleBrakeStrengthFactor", Units.acceleration, Comments.axleBrakeStrengthFactor);
	public final ConfigFloat axleDerailFrictionFactor = f(0.5F, 0, 1, "axleDerailFrictionFactor", Comments.axleDerailFrictionFactor);
	public final ConfigFloat axleTrackCheckTime = f(0.1F, 0, 5, "axleTrackCheckTime", Units.time, Comments.axleTrackCheckTime);
	public final ConfigFloat axleTrackRecheckTime = f(3, 0, 60, "axleTrackRecheckTime", Units.time, Comments.axleTrackRecheckTime);

	public final ConfigGroup coupler = group(1, "coupler", "Train Couplers");
	public final ConfigFloat couplerPassiveLinearDamping = f(10, 0, Float.MAX_VALUE, "couplerPassiveLinearDamping", Units.damping, Comments.couplerPassiveLinearDamping);
	public final ConfigFloat couplerPassiveAngularDamping = f(1, 0, Float.MAX_VALUE, "couplerPassiveAngularDamping", Units.angularDamping, Comments.couplerPassiveAngularDamping);
	public final ConfigFloat couplerSpringFrequency = f(100, 0, Float.MAX_VALUE, "couplerSpringFrequency", Units.angularVelocity, Comments.couplerSpringFrequency);
	public final ConfigFloat couplerSpringDampingRate = f(2, 0, Float.MAX_VALUE, "couplerSpringDampingRate", Comments.couplerSpringDampingRate);

	@Override
	public String getName() {
		return "physics";
	}

	static class Comments {
		static String bogeyPivotMass = "The mass of the pivot of the Physics Bogey.";
		static String bogeyPassiveAngularDamping = "Passive angular damping between the Physics Bogey and its pivot.";
		static String bogeyVerticalSpringFrequency = "Vertical spring frequency between the Physics Bogey and its pivot.";
		static String bogeyVerticalSpringDampingRate = "Vertical spring damping rate between the Physics Bogey and its pivot.";
		static String bogeyVerticalSpringMaxForce = "Vertical spring maximum force between the Physics Bogey and its pivot.";
		static String bogeyLateralSpringFrequency = "Lateral spring frequency between the Physics Bogey and its pivot.";
		static String bogeyLateralSpringDampingRate = "Lateral spring damping rate between the Physics Bogey and its pivot.";
		static String bogeyLateralSpringMaxForce = "Lateral spring maximum force between the Physics Bogey and its pivot.";
		static String bogeyAngularSpringFrequency = "Angular spring frequency between the Physics Bogey and its pivot.";
		static String bogeyAngularSpringDampingRate = "Angular spring damping rate between the Physics Bogey and its pivot.";
		static String bogeyAngularSpringMaxTorque = "Angular spring maximum torque between the Physics Bogey and its pivot.";

		static String axleSpacingUpdateTime = "Time to update the axle spacing when changed for the axles of the Physics Bogey.";
		static String axlePassiveLinearDamping = "Passive linear damping between an axle of the Physics Bogey and its track.";
		static String axlePassiveAngularDamping = "Passive angular damping between an axle of the Physics Bogey and its track.";
		static String axleStandardLateralMaxSpeedFactor = "Lateral max speed factor between an axle of the Physics Bogey and a standard track. Max speed is sqrt(factor / curvature).";
		static String axleStandardVerticalMaxSpeedFactor = "Vertical max speed factor between an axle of the Physics Bogey and a standard track. Max speed is sqrt(factor / curvature).";
		static String axleTargetSpeedFactor = "Conversion of RPM to target speed between an axle of the Physics Bogey and its track.";
		static String axleDriveForceFactor = "Conversion of current and target speed difference to drive force between an axle of the Physics Bogey and its track.";
		static String axleBrakeStrengthFactor = "Conversion of brake strength [0-1] to brake force between an axle of the Physics Bogey and its track.";
		static String axleDerailFrictionFactor = "Factor of effective friction between an axle of the Physics Bogey and the ground when derailed.";
		static String axleTrackCheckTime = "Inverval to find nearest track when derailed for an axle of the Physics Bogey.";
		static String axleTrackRecheckTime = "Inverval to re-find nearest track for an axle of the Physics Bogey.";

		static String couplerPassiveLinearDamping = "Passive linear damping between a Train Coupler and its partner.";
		static String couplerPassiveAngularDamping = "Passive angular damping between a Train Coupler and its partner.";
		static String couplerSpringFrequency = "Spring frequency between a Train Coupler and its partner.";
		static String couplerSpringDampingRate = "Spring damping rate between a Train Coupler and its partner.";
	}
}
