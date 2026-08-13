package com.crystaelix.simurail.content.bogey;

import java.util.UUID;
import java.util.concurrent.locks.StampedLock;

import com.machinezoo.noexception.CloseableScope;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class PhysicsBogeyProbeData {

	private final StampedLock lock = new StampedLock();

	protected double stationDistance;
	protected String stationName;
	protected UUID stationId;
	protected Vec3 stationPos;
	protected BlockPos stationBlockPos;
	protected double signalDistance;
	protected Vec3 signalPos;
	protected boolean signalAligned;
	protected boolean signalBidirectional;
	protected double occupiedSignalDistance;
	protected Vec3 occupiedSignalPos;
	protected boolean occupiedSignalAligned;
	protected boolean occupiedSignalBidirectional;
	protected double blockedDistance;

	protected PhysicsBogeyProbeData() {
	}

	protected void reset() {
		stationDistance = -1;
		stationName = null;
		stationPos = null;
		stationBlockPos = null;
		signalDistance = -1;
		signalPos = null;
		signalAligned = false;
		occupiedSignalDistance = -1;
		occupiedSignalPos = null;
		occupiedSignalAligned = false;
		blockedDistance = -1;
	}

	protected CloseableScope writeScope() {
		long stamp = lock.writeLock();
		return () -> lock.unlockWrite(stamp);
	}

	protected CloseableScope readScope() {
		long stamp = lock.readLock();
		return () -> lock.unlockRead(stamp);
	}

	public double getStationDistance() {
		long stamp = lock.tryOptimisticRead();
		double v = stationDistance;
		if(lock.validate(stamp)) {
			return v;
		}
		try(CloseableScope scope = readScope()) {
			return stationDistance;
		}
	}

	public String getStationName() {
		long stamp = lock.tryOptimisticRead();
		String v = stationName;
		if(lock.validate(stamp)) {
			return v;
		}
		try(CloseableScope scope = readScope()) {
			return stationName;
		}
	}

	public UUID getStationId() {
		long stamp = lock.tryOptimisticRead();
		UUID v = stationId;
		if(lock.validate(stamp)) {
			return v;
		}
		try(CloseableScope scope = readScope()) {
			return stationId;
		}
	}

	public Vec3 getStationPos() {
		long stamp = lock.tryOptimisticRead();
		Vec3 v = stationPos;
		if(lock.validate(stamp)) {
			return v;
		}
		try(CloseableScope scope = readScope()) {
			return stationPos;
		}
	}

	public BlockPos getStationBlockPos() {
		long stamp = lock.tryOptimisticRead();
		BlockPos v = stationBlockPos;
		if(lock.validate(stamp)) {
			return v;
		}
		try(CloseableScope scope = readScope()) {
			return stationBlockPos;
		}
	}

	public double getSignalDistance() {
		long stamp = lock.tryOptimisticRead();
		double v = signalDistance;
		if(lock.validate(stamp)) {
			return v;
		}
		try(CloseableScope scope = readScope()) {
			return signalDistance;
		}
	}

	public Vec3 getSignalPos() {
		long stamp = lock.tryOptimisticRead();
		Vec3 v = signalPos;
		if(lock.validate(stamp)) {
			return v;
		}
		try(CloseableScope scope = readScope()) {
			return signalPos;
		}
	}

	public boolean isSignalAligned() {
		long stamp = lock.tryOptimisticRead();
		boolean v = signalAligned;
		if(lock.validate(stamp)) {
			return v;
		}
		try(CloseableScope scope = readScope()) {
			return signalAligned;
		}
	}

	public boolean isSignalBidirectional() {
		long stamp = lock.tryOptimisticRead();
		boolean v = signalBidirectional;
		if(lock.validate(stamp)) {
			return v;
		}
		try(CloseableScope scope = readScope()) {
			return signalBidirectional;
		}
	}

	public double getOccupiedSignalDistance() {
		long stamp = lock.tryOptimisticRead();
		double v = occupiedSignalDistance;
		if(lock.validate(stamp)) {
			return v;
		}
		try(CloseableScope scope = readScope()) {
			return occupiedSignalDistance;
		}
	}

	public Vec3 getOccupiedSignalPos() {
		long stamp = lock.tryOptimisticRead();
		Vec3 v = occupiedSignalPos;
		if(lock.validate(stamp)) {
			return v;
		}
		try(CloseableScope scope = readScope()) {
			return occupiedSignalPos;
		}
	}

	public boolean isOccupiedSignalAligned() {
		long stamp = lock.tryOptimisticRead();
		boolean v = occupiedSignalAligned;
		if(lock.validate(stamp)) {
			return v;
		}
		try(CloseableScope scope = readScope()) {
			return occupiedSignalAligned;
		}
	}

	public boolean isOccupiedSignalBidirectional() {
		long stamp = lock.tryOptimisticRead();
		boolean v = occupiedSignalBidirectional;
		if(lock.validate(stamp)) {
			return v;
		}
		try(CloseableScope scope = readScope()) {
			return occupiedSignalBidirectional;
		}
	}

	public double getBlockedDistance() {
		long stamp = lock.tryOptimisticRead();
		double v = blockedDistance;
		if(lock.validate(stamp)) {
			return v;
		}
		try(CloseableScope scope = readScope()) {
			return blockedDistance;
		}
	}
}
