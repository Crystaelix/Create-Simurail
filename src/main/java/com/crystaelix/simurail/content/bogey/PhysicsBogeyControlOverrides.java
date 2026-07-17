package com.crystaelix.simurail.content.bogey;

import net.minecraft.nbt.CompoundTag;

public class PhysicsBogeyControlOverrides {

	public boolean overrideBrakeStrength;
	public boolean overrideSteerValue;
	public boolean overrideStressMultiplier;
	private float brakeStrength;
	private float steerValue;
	private float stressMultiplier;

	public boolean hasOverrides() {
		return overrideBrakeStrength || overrideSteerValue || overrideStressMultiplier;
	}

	public void reset() {
		resetBrakeStrength();
		resetSteerValue();
		resetStressMultiplier();
	}

	public float getBrakeStrength() {
		return brakeStrength;
	}

	public void setBrakeStrength(float brakeStrength) {
		this.brakeStrength = Math.clamp(brakeStrength, 0, 1);
		overrideBrakeStrength = true;
	}

	public void resetBrakeStrength() {
		brakeStrength = 0;
		overrideBrakeStrength = false;
	}

	public float getSteerValue() {
		return steerValue;
	}

	public void setSteerValue(float steerValue) {
		this.steerValue = Math.clamp(steerValue, -1, 1);
		overrideSteerValue = true;
	}

	public void resetSteerValue() {
		steerValue = 0;
		overrideSteerValue = false;
	}

	public float getStressMultiplier() {
		return stressMultiplier;
	}

	public void setStressMultiplier(float stressMultiplier) {
		this.stressMultiplier = Math.clamp(stressMultiplier, 0, 1);
		overrideStressMultiplier = true;
	}

	public void resetStressMultiplier() {
		stressMultiplier = 1;
		overrideStressMultiplier = false;
	}

	protected CompoundTag write() {
		CompoundTag tag = new CompoundTag();
		if(overrideBrakeStrength) {
			tag.putFloat("brake_strength", brakeStrength);
		}
		if(overrideSteerValue) {
			tag.putFloat("steer_value", steerValue);
		}
		if(overrideStressMultiplier) {
			tag.putFloat("stress_multiplier", stressMultiplier);
		}
		return tag;
	}

	protected void read(CompoundTag tag) {
		reset();
		if(tag.contains("brake_strength")) {
			overrideBrakeStrength = true;
			brakeStrength = tag.getFloat("brake_strength");
		}
		if(tag.contains("steer_value")) {
			overrideSteerValue = true;
			steerValue = tag.getFloat("steer_value");
		}
		if(tag.contains("stress_multiplier")) {
			overrideStressMultiplier = true;
			stressMultiplier = tag.getFloat("stress_multiplier");
		}
	}
}
