package com.crystaelix.simurail.config;

public class SimurailPhysicsConfig extends SimurailBaseConfig {

	public final ConfigGroup bogey = group(1, "bogey", "Physics Bogies");
	public final ConfigFloat bogeyPivotMass = f(1, 0, Float.MAX_VALUE, "pivotMass", Units.mass, Comments.bogeyPivotMass);

	public final ConfigGroup bogeyVertical = group(2, "vertical", "Vertical Movement");
	public final ConfigFloat bogeyVerticalSpringFrequency = f(10, 0, Float.MAX_VALUE, "springFrequency", Units.angularVelocity, Comments.bogeyVerticalSpringFrequency);
	public final ConfigFloat bogeyVerticalSpringDampingRate = f(1.25F, 0, Float.MAX_VALUE, "springDampingRate", Comments.bogeyVerticalSpringDampingRate);
	public final ConfigFloat bogeyVerticalSpringMaxForce = f(10000, 0, Float.MAX_VALUE, "springMaxForce", Units.force, Comments.bogeyVerticalSpringMaxForce);

	public final ConfigGroup bogeyLateral = group(2, "lateral", "Lateral Movement");
	public final ConfigFloat bogeyLateralSpringFrequency = f(10, 0, Float.MAX_VALUE, "springFrequency", Units.angularVelocity, Comments.bogeyLateralSpringFrequency);
	public final ConfigFloat bogeyLateralSpringDampingRate = f(1.25F, 0, Float.MAX_VALUE, "springDampingRate", Comments.bogeyLateralSpringDampingRate);
	public final ConfigFloat bogeyLateralSpringMaxForce = f(10000, 0, Float.MAX_VALUE, "springMaxForce", Units.force, Comments.bogeyLateralSpringMaxForce);

	public final ConfigGroup bogeyRoll = group(2, "roll", "Roll Movement");
	public final ConfigFloat bogeyRollSpringFrequency = f(15, 0, Float.MAX_VALUE, "springFrequency", Units.angularVelocity, Comments.bogeyRollSpringFrequency);
	public final ConfigFloat bogeyRollSpringDampingRate = f(1.25F, 0, Float.MAX_VALUE, "springDampingRate", Comments.bogeyRollSpringDampingRate);
	public final ConfigFloat bogeyRollSpringMomentMultiplier = f(2, 0, Float.MAX_VALUE, "springMomentMultiplier", Comments.bogeyRollSpringMomentMultiplier);
	public final ConfigFloat bogeyRollSpringMaxTorque = f(10000, 0, Float.MAX_VALUE, "springMaxTorque", Units.torque, Comments.bogeyRollSpringMaxTorque);

	public final ConfigGroup axle = group(1, "axle", "Physics Bogie Axles");
	public final ConfigFloat axleSpacingUpdateTime = f(2, 0, 10, "spacingUpdateTime", Units.time, Comments.axleSpacingUpdateTime);
	public final ConfigFloat axlePassiveLinearDamping = f(100, 0, Float.MAX_VALUE, "passiveLinearDamping", Units.damping, Comments.axlePassiveLinearDamping);
	public final ConfigFloat axlePassiveAngularDamping = f(1, 0, Float.MAX_VALUE, "passiveAngularDamping", Units.angularDamping, Comments.axlePassiveAngularDamping);
	public final ConfigFloat axleStandardLateralMaxSpeedFactor = f(30, 0, Float.MAX_VALUE, "standardLateralMaxSpeedFactor", Units.acceleration, Comments.axleStandardLateralMaxSpeedFactor);
	public final ConfigFloat axleStandardVerticalMaxSpeedFactor = f(50, 0, Float.MAX_VALUE, "standardVerticalMaxSpeedFactor", Units.acceleration, Comments.axleStandardVerticalMaxSpeedFactor);
	public final ConfigFloat axleTargetSpeedFactor = f(0.25F, 0, Float.MAX_VALUE, "targetSpeedFactor", Units.velocity, Comments.axleTargetSpeedFactor);
	public final ConfigFloat axleDriveForceFactor = f(0.5F, 0, Float.MAX_VALUE, "driveForceFactor", Units.damping, Comments.axleDriveForceFactor);
	public final ConfigFloat axleBrakeStrengthFactor = f(20, 0, Float.MAX_VALUE, "brakeStrengthFactor", Units.acceleration, Comments.axleBrakeStrengthFactor);
	public final ConfigFloat axleDerailFrictionFactor = f(0.5F, 0, 1, "derailFrictionFactor", Comments.axleDerailFrictionFactor);
	public final ConfigFloat axleTrackCheckTime = f(0.1F, 0, 5, "trackCheckTime", Units.time, Comments.axleTrackCheckTime);
	public final ConfigFloat axleTrackRecheckTime = f(3, 0, 60, "trackRecheckTime", Units.time, Comments.axleTrackRecheckTime);

	public final ConfigGroup axleSlip = group(2, "slip", "Wheel Slip");
	public final ConfigBool axleWheelSlip = b(true, "enabled", Comments.axleWheelSlip);
	public final ConfigFloat axleAdhesionFactor = f(6, 0, Float.MAX_VALUE, "adhesionFactor", Units.acceleration, Comments.axleAdhesionFactor);
	public final ConfigFloat axleSlipAcceleration = f(15, 0, Float.MAX_VALUE, "acceleration", Units.acceleration, Comments.axleSlipAcceleration);
	public final ConfigFloat axleSlipDecay = f(20, 0, Float.MAX_VALUE, "decay", Units.acceleration, Comments.axleSlipDecay);
	public final ConfigFloat axleSlipBindTime = f(0.75F, 0, Float.MAX_VALUE, "bindTime", Units.time, Comments.axleSlipBindTime);
	public final ConfigFloat axleSlipBurstFactor = f(0.5F, 0, Float.MAX_VALUE, "burstFactor", Comments.axleSlipBurstFactor);
	public final ConfigFloat axleSlipMaxSpeed = f(4, 0, Float.MAX_VALUE, "maxSpeed", Units.velocity, Comments.axleSlipMaxSpeed);

	public final ConfigGroup coupler = group(1, "coupler", "Train Couplers");
	public final ConfigFloat couplerPassiveLinearDamping = f(10, 0, Float.MAX_VALUE, "passiveLinearDamping", Units.damping, Comments.couplerPassiveLinearDamping);
	public final ConfigFloat couplerPassiveAngularDamping = f(1, 0, Float.MAX_VALUE, "passiveAngularDamping", Units.angularDamping, Comments.couplerPassiveAngularDamping);
	public final ConfigFloat couplerSpringFrequency = f(100, 0, Float.MAX_VALUE, "springFrequency", Units.angularVelocity, Comments.couplerSpringFrequency);
	public final ConfigFloat couplerSpringDampingRate = f(2, 0, Float.MAX_VALUE, "springDampingRate", Comments.couplerSpringDampingRate);

	@Override
	public String getName() {
		return "physics";
	}

	static class Comments {
		static String bogeyPivotMass = "The mass of the pivot of the Physics Bogie.";
		static String bogeyVerticalSpringFrequency = "Vertical spring frequency between the Physics Bogie and its pivot when vertical offset is allowed.";
		static String bogeyVerticalSpringDampingRate = "Vertical spring damping rate between the Physics Bogie and its pivot when vertical offset is allowed.";
		static String bogeyVerticalSpringMaxForce = "Vertical spring maximum force between the Physics Bogie and its pivot when vertical offset is allowed.";
		static String bogeyLateralSpringFrequency = "Lateral spring frequency between the Physics Bogie and its pivot when lateral offset is allowed.";
		static String bogeyLateralSpringDampingRate = "Lateral spring damping rate between the Physics Bogie and its pivot when lateral offset is allowed.";
		static String bogeyLateralSpringMaxForce = "Lateral spring maximum force between the Physics Bogie and its pivot when lateral offset is allowed.";
		static String bogeyRollSpringFrequency = "Roll spring frequency between the Physics Bogie and its pivot.";
		static String bogeyRollSpringDampingRate = "Roll spring damping rate between the Physics Bogie and its pivot.";
		static String bogeyRollSpringMomentMultiplier = "Roll spring moment multiplier between the Physics Bogie and its pivot.";
		static String bogeyRollSpringMaxTorque = "Roll spring maximum torque between the Physics Bogie and its pivot.";

		static String axleSpacingUpdateTime = "Time to update the axle spacing when changed for the axles of the Physics Bogie.";
		static String axlePassiveLinearDamping = "Passive linear damping between an axle of the Physics Bogie and its track.";
		static String axlePassiveAngularDamping = "Passive angular damping between an axle of the Physics Bogie and its track.";
		static String axleStandardLateralMaxSpeedFactor = "Lateral max speed factor between an axle of the Physics Bogie and a standard track. Max speed is sqrt(factor / curvature).";
		static String axleStandardVerticalMaxSpeedFactor = "Vertical max speed factor between an axle of the Physics Bogie and a standard track. Max speed is sqrt(factor / curvature).";
		static String axleTargetSpeedFactor = "Conversion of RPM to target speed between an axle of the Physics Bogie and its track.";
		static String axleDriveForceFactor = "Conversion of current and target speed difference to drive force between an axle of the Physics Bogie and its track.";
		static String axleBrakeStrengthFactor = "Conversion of brake strength [0-1] to brake force between an axle of the Physics Bogie and its track.";
		static String axleDerailFrictionFactor = "Factor of effective friction between an axle of the Physics Bogie and the ground when derailed.";
		static String axleTrackCheckTime = "Inverval to find nearest track when derailed for an axle of the Physics Bogie.";
		static String axleTrackRecheckTime = "Inverval to re-find nearest track for an axle of the Physics Bogie.";
		static String axleWheelSlip = "Allow the wheels to lose their grip on the track. Very slightly affects physics while accelerating at low speeds.";
		static String axleAdhesionFactor = "Maximum traction coefficient a Physics Bogie axle can transmit to its track, per unit of mass resting on that axle. Any drive force beyond this spins the wheels instead of moving the bogie.";
		static String axleSlipAcceleration = "How quickly the wheels of the Physics Bogie spin up once they lose traction.";
		static String axleSlipDecay = "How quickly the track drags slipping wheels of the Physics Bogie back to its own speed.";
		static String axleSlipBindTime = "How long a drive of the Physics Bogie stays bound against wheels it cannot break loose, at the largest overload it can build up under, before all of it lets go at once. Set to 0 for wheels that break loose the moment they lose traction.";
		static String axleSlipBurstFactor = "Temporary wheel rotation speed excess just after the wheel loses grip. Set to 0 for wheels that never overrun their drive speed.";
		static String axleSlipMaxSpeed = "Speed above which the wheels of the Physics Bogie always keep their grip. Prevents the realistic behavior of a permanent slip for non progressive wheel acceleration.";

		static String couplerPassiveLinearDamping = "Passive linear damping between a Train Coupler and its partner.";
		static String couplerPassiveAngularDamping = "Passive angular damping between a Train Coupler and its partner.";
		static String couplerSpringFrequency = "Spring frequency between a Train Coupler and its partner.";
		static String couplerSpringDampingRate = "Spring damping rate between a Train Coupler and its partner.";
	}
}
