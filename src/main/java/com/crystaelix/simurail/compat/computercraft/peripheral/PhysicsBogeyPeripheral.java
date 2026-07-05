package com.crystaelix.simurail.compat.computercraft.peripheral;

import com.crystaelix.simurail.content.bogey.PhysicsBogeyBlockEntity;
import com.crystaelix.simurail.content.bogey.PhysicsBogeyControlMode;
import com.simibubi.create.compat.computercraft.implementation.peripherals.SyncedPeripheral;

import dan200.computercraft.api.lua.LuaFunction;

public class PhysicsBogeyPeripheral extends SyncedPeripheral<PhysicsBogeyBlockEntity> {

	public PhysicsBogeyPeripheral(PhysicsBogeyBlockEntity blockEntity) {
		super(blockEntity);
	}

	@Override
	public final String getType() {
		return "Simurail_PhysicsBogey";
	}

	@LuaFunction
	public final boolean isPhysicsEnabled() {
		return blockEntity.getOptions().enabled;
	}

	@LuaFunction(mainThread = true)
	public final void setPhysicsEnabled(boolean enabled) {
		blockEntity.getOptions().enabled = enabled;
		blockEntity.setChanged();
	}

	@LuaFunction
	public final boolean allowsYawOffset() {
		return blockEntity.getOptions().allowYawOffset;
	}

	@LuaFunction(mainThread = true)
	public final void setAllowYawOffset(boolean allow) {
		blockEntity.getOptions().allowYawOffset = allow;
		blockEntity.setChanged();
	}

	@LuaFunction
	public final boolean allowsPitchOffset() {
		return blockEntity.getOptions().allowPitchOffset;
	}

	@LuaFunction(mainThread = true)
	public final void setAllowPitchOffset(boolean allow) {
		blockEntity.getOptions().allowPitchOffset = allow;
		blockEntity.setChanged();
	}

	@LuaFunction
	public final boolean allowsVerticalOffset() {
		return blockEntity.getOptions().allowVerticalOffset;
	}

	@LuaFunction(mainThread = true)
	public final void setAllowVerticalOffset(boolean allow) {
		blockEntity.getOptions().allowVerticalOffset = allow;
		blockEntity.setChanged();
	}

	@LuaFunction
	public final boolean allowsLateralOffset() {
		return blockEntity.getOptions().allowLateralOffset;
	}

	@LuaFunction(mainThread = true)
	public final void setAllowLateralOffset(boolean allow) {
		blockEntity.getOptions().allowLateralOffset = allow;
		blockEntity.setChanged();
	}

	@LuaFunction
	public final boolean allowsVerticalMovement() {
		return blockEntity.getOptions().allowVerticalMovement;
	}

	@LuaFunction(mainThread = true)
	public final void setAllowVerticalMovement(boolean allow) {
		blockEntity.getOptions().allowVerticalMovement = allow;
		blockEntity.setChanged();
	}

	@LuaFunction
	public final double getAxleOffset() {
		return blockEntity.getOptions().getAxleOffset();
	}

	@LuaFunction(mainThread = true)
	public final void setAxleOffset(double axleOffset) {
		blockEntity.getOptions().setAxleOffset((float)axleOffset);
		blockEntity.setChanged();
		blockEntity.sendData();
	}

	@LuaFunction
	public final double getMaxStress() {
		return blockEntity.getOptions().getStress();
	}

	@LuaFunction(mainThread = true)
	public final void setMaxStress(double stress) {
		blockEntity.getOptions().setStress((float)stress);
		blockEntity.setChanged();
	}

	@LuaFunction
	public final double getTiltStrength() {
		return blockEntity.getOptions().getTiltStrength();
	}

	@LuaFunction(mainThread = true)
	public final void setTiltStrength(double tiltStrength) {
		blockEntity.getOptions().setTiltStrength((float)tiltStrength);
		blockEntity.setChanged();
	}

	@LuaFunction
	public final int getControlMode() {
		return blockEntity.getOptions().controlMode.ordinal();
	}

	@LuaFunction(mainThread = true)
	public final void setControlMode(int mode) {
		blockEntity.getOptions().controlMode = PhysicsBogeyControlMode.BY_ID.apply(mode);
		blockEntity.setChanged();
	}

	@LuaFunction
	public final boolean hasBrakeStrengthOverride() {
		return blockEntity.getComputerOverrides().overrideBrakeStrength;
	}

	@LuaFunction
	public final double getBrakeStrengthOverride() {
		return blockEntity.getComputerOverrides().getBrakeStrength();
	}

	@LuaFunction(mainThread = true)
	public final void setBrakeStrengthOverride(double brakeStrength) {
		blockEntity.getComputerOverrides().setBrakeStrength(brakeStrength);
		blockEntity.setChanged();
	}

	@LuaFunction(mainThread = true)
	public final void disableBrakeStrengthOverride() {
		blockEntity.getComputerOverrides().resetBrakeStrength();
		blockEntity.setChanged();
	}

	@LuaFunction
	public final boolean hasSteerValueOverride() {
		return blockEntity.getComputerOverrides().overrideSteerValue;
	}

	@LuaFunction
	public final double getSteerValueOverride() {
		return blockEntity.getComputerOverrides().getSteerValue();
	}

	@LuaFunction(mainThread = true)
	public final void setSteerValueOverride(double steerValue) {
		blockEntity.getComputerOverrides().setSteerValue(steerValue);
		blockEntity.setChanged();
	}

	@LuaFunction(mainThread = true)
	public final void disableSteerValueOverride() {
		blockEntity.getComputerOverrides().resetSteerValue();
		blockEntity.setChanged();
	}

	@LuaFunction
	public final boolean hasStressMultiplierOverride() {
		return blockEntity.getComputerOverrides().overrideStressMultiplier;
	}

	@LuaFunction
	public final double getStressMultiplierOverride() {
		return blockEntity.getComputerOverrides().getStressMultiplier();
	}

	@LuaFunction(mainThread = true)
	public final void setStressMultiplierOverride(double stressMultiplier) {
		blockEntity.getComputerOverrides().setStressMultiplier(stressMultiplier);
		blockEntity.setChanged();
	}

	@LuaFunction(mainThread = true)
	public final void disableStressMultiplierOverride() {
		blockEntity.getComputerOverrides().resetStressMultiplier();
		blockEntity.setChanged();
	}

	@LuaFunction
	public final boolean hasTrack() {
		return blockEntity.hasTrack();
	}

	@LuaFunction
	public final boolean isDerailed() {
		return blockEntity.isDerailed();
	}

	@LuaFunction
	public final double getLateralCurvature() {
		return blockEntity.getLateralCurvature();
	}

	@LuaFunction
	public final double getVerticalCurvature() {
		return blockEntity.getVerticalCurvature();
	}
}
