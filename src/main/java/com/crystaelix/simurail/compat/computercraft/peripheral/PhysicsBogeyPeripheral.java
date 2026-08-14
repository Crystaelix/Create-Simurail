package com.crystaelix.simurail.compat.computercraft.peripheral;

import java.util.Optional;
import java.util.UUID;

import org.joml.Vector3dc;

import com.crystaelix.simurail.content.bogey.PhysicsBogeyBlockEntity;
import com.crystaelix.simurail.content.bogey.PhysicsBogeyControlMode;
import com.crystaelix.simurail.content.bogey.PhysicsBogeyProbeData;
import com.simibubi.create.compat.computercraft.implementation.peripherals.SyncedPeripheral;

import dan200.computercraft.api.lua.LuaFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

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
	public final double getProbeDistance() {
		return blockEntity.getOptions().getProbeDistance();
	}

	@LuaFunction(mainThread = true)
	public final void setProbeDistance(double probeDistance) {
		blockEntity.getOptions().setProbeDistance((float)probeDistance);
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
	public final double getBrakeStrength() {
		return blockEntity.getBrakeStrength();
	}

	@LuaFunction
	public final double getGroupBrakeStrength() {
		return blockEntity.getGroupBrakeStrength();
	}

	@LuaFunction
	public final boolean hasBrakeStrengthOverride() {
		return blockEntity.getComputerOverrides().overrideBrakeStrength;
	}

	@LuaFunction(mainThread = true)
	public final void setBrakeStrengthOverride(double brakeStrength) {
		blockEntity.getComputerOverrides().setBrakeStrength((float)brakeStrength);
		blockEntity.setChanged();
	}

	@LuaFunction(mainThread = true)
	public final void disableBrakeStrengthOverride() {
		blockEntity.getComputerOverrides().resetBrakeStrength();
		blockEntity.setChanged();
	}

	@LuaFunction
	public final double getSteerValue() {
		return blockEntity.getSteerValue();
	}

	@LuaFunction
	public final double getGroupSteerValue() {
		return blockEntity.getGroupSteerValue();
	}

	@LuaFunction
	public final boolean hasSteerValueOverride() {
		return blockEntity.getComputerOverrides().overrideSteerValue;
	}

	@LuaFunction(mainThread = true)
	public final void setSteerValueOverride(double steerValue) {
		blockEntity.getComputerOverrides().setSteerValue((float)steerValue);
		blockEntity.setChanged();
	}

	@LuaFunction(mainThread = true)
	public final void disableSteerValueOverride() {
		blockEntity.getComputerOverrides().resetSteerValue();
		blockEntity.setChanged();
	}

	@LuaFunction
	public final double getStressMultiplier() {
		return blockEntity.getStressMultiplier();
	}

	@LuaFunction
	public final boolean hasStressMultiplierOverride() {
		return blockEntity.getComputerOverrides().overrideStressMultiplier;
	}

	@LuaFunction(mainThread = true)
	public final void setStressMultiplierOverride(double stressMultiplier) {
		blockEntity.getComputerOverrides().setStressMultiplier((float)stressMultiplier);
		blockEntity.setChanged();
	}

	@LuaFunction(mainThread = true)
	public final void disableStressMultiplierOverride() {
		blockEntity.getComputerOverrides().resetStressMultiplier();
		blockEntity.setChanged();
	}

	@LuaFunction
	public final boolean hasTrack(Optional<Boolean> front) {
		if(front.isPresent()) {
			return blockEntity.getAxle(front.get()).hasTrack();
		}
		else {
			return blockEntity.hasTrack();
		}
	}

	@LuaFunction
	public final boolean isDerailed(Optional<Boolean> front) {
		if(front.isPresent()) {
			return !blockEntity.getAxle(front.get()).hasTrack();
		}
		else {
			return blockEntity.isDerailed();
		}
	}

	@LuaFunction
	public final double getLateralCurvature(Optional<Boolean> front) {
		if(front.isPresent()) {
			return blockEntity.getAxle(front.get()).getLateralCurvature();
		}
		else {
			return blockEntity.getLateralCurvature();
		}
	}

	@LuaFunction
	public final double getVerticalCurvature(Optional<Boolean> front) {
		if(front.isPresent()) {
			return blockEntity.getAxle(front.get()).getVerticalCurvature();
		}
		else {
			return blockEntity.getVerticalCurvature();
		}
	}

	@LuaFunction
	public final Object[] getTrackPos(boolean front) {
		if(!blockEntity.getAxle(front).hasTrack()) {
			return null;
		}
		Vector3dc pos = blockEntity.getAxle(front).getTrackFrame().position();
		return new Object[] {pos.x(), pos.y(), pos.z()};
	}

	@LuaFunction
	public final Object[] getTrackDir(boolean front) {
		if(!blockEntity.getAxle(front).hasTrack()) {
			return null;
		}
		Vector3dc dir = blockEntity.getAxle(front).getTrackFrame().direction();
		return new Object[] {dir.x(), dir.y(), dir.z()};
	}

	@LuaFunction
	public final Object[] getTrackVert(boolean front) {
		if(!blockEntity.getAxle(front).hasTrack()) {
			return null;
		}
		Vector3dc vert = blockEntity.getAxle(front).getTrackFrame().vertical();
		return new Object[] {vert.x(), vert.y(), vert.z()};
	}

	@LuaFunction
	public final double getProbeStationDistance(boolean front) {
		PhysicsBogeyProbeData data = blockEntity.getAxle(front).getProbeData();
		return data.getStationDistance();
	}

	@LuaFunction
	public final String getProbeStationName(boolean front) {
		PhysicsBogeyProbeData data = blockEntity.getAxle(front).getProbeData();
		return data.getStationName();
	}

	@LuaFunction
	public final String getProbeStationId(boolean front) {
		PhysicsBogeyProbeData data = blockEntity.getAxle(front).getProbeData();
		UUID id = data.getStationId();
		return id == null ? null : id.toString();
	}

	@LuaFunction
	public final Object[] getProbeStationPos(boolean front) {
		PhysicsBogeyProbeData data = blockEntity.getAxle(front).getProbeData();
		Vec3 pos = data.getStationPos();
		return pos == null ? null : new Object[] {pos.x, pos.y, pos.z};
	}

	@LuaFunction
	public final Object[] getProbeStationBlockPos(boolean front) {
		PhysicsBogeyProbeData data = blockEntity.getAxle(front).getProbeData();
		BlockPos pos = data.getStationBlockPos();
		return pos == null ? null : new Object[] {pos.getX(), pos.getY(), pos.getZ()};
	}

	@LuaFunction
	public final double getProbeSignalDistance(boolean front) {
		PhysicsBogeyProbeData data = blockEntity.getAxle(front).getProbeData();
		return data.getSignalDistance();
	}

	@LuaFunction
	public final Object[] getProbeSignalPos(boolean front) {
		PhysicsBogeyProbeData data = blockEntity.getAxle(front).getProbeData();
		Vec3 pos = data.getSignalPos();
		return pos == null ? null : new Object[] {pos.x, pos.y, pos.z};
	}

	@LuaFunction
	public final boolean getProbeSignalAligned(boolean front) {
		PhysicsBogeyProbeData data = blockEntity.getAxle(front).getProbeData();
		return data.isSignalAligned();
	}

	@LuaFunction
	public final boolean getProbeSignalBidirectional(boolean front) {
		PhysicsBogeyProbeData data = blockEntity.getAxle(front).getProbeData();
		return data.isSignalBidirectional();
	}

	@LuaFunction
	public final double getProbeOccupiedSignalDistance(boolean front) {
		PhysicsBogeyProbeData data = blockEntity.getAxle(front).getProbeData();
		return data.getOccupiedSignalDistance();
	}

	@LuaFunction
	public final Object[] getProbeOccupiedSignalPos(boolean front) {
		PhysicsBogeyProbeData data = blockEntity.getAxle(front).getProbeData();
		Vec3 pos = data.getOccupiedSignalPos();
		return pos == null ? null : new Object[] {pos.x, pos.y, pos.z};
	}

	@LuaFunction
	public final boolean getProbeOccupiedSignalAligned(boolean front) {
		PhysicsBogeyProbeData data = blockEntity.getAxle(front).getProbeData();
		return data.isOccupiedSignalAligned();
	}

	@LuaFunction
	public final boolean getProbeOccupiedSignalBidirectional(boolean front) {
		PhysicsBogeyProbeData data = blockEntity.getAxle(front).getProbeData();
		return data.isOccupiedSignalBidirectional();
	}

	@LuaFunction
	public final double getProbeBlockedDistance(boolean front) {
		PhysicsBogeyProbeData data = blockEntity.getAxle(front).getProbeData();
		return data.getBlockedDistance();
	}
}
