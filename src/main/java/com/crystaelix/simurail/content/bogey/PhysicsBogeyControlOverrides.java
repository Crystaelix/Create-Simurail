package com.crystaelix.simurail.content.bogey;


public class PhysicsBogeyControlOverrides {

	public boolean overrideBrakeStrength;
	public double brakeStrength;
	public boolean overrideSteerValue;
	public double steerValue;
	public boolean overrideStressMultiplier;
	public double stressMultiplier;

	public boolean hasOverrides() {
		return overrideBrakeStrength || overrideSteerValue || overrideStressMultiplier;
	}

	public void reset() {
		overrideBrakeStrength = false;
		brakeStrength = 0;
		overrideSteerValue = false;
		steerValue = 0;
		overrideStressMultiplier = false;
		stressMultiplier = 1;
	}
}
