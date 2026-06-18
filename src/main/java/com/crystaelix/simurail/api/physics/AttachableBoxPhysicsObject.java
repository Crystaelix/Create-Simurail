package com.crystaelix.simurail.api.physics;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.joml.Vector3dc;

import dev.ryanhcode.sable.api.physics.mass.MassData;
import dev.ryanhcode.sable.api.physics.object.box.BoxPhysicsObject;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;

public class AttachableBoxPhysicsObject extends BoxPhysicsObject {

	private final ServerSubLevel parent;
	private final BoxMassData massData;

	public AttachableBoxPhysicsObject(ServerSubLevel parent, Pose3dc pose, Vector3dc halfExtents, double mass) {
		super(pose, halfExtents, 1);
		this.parent = parent;
		massData = new BoxMassData(halfExtents, mass);
	}

	@Override
	public void getBoundingBox(BoundingBox3d dest) {
		dest.set(parent.boundingBox());
	}

	@Override
	public MassData getMassTracker() {
		return massData;
	}

	@Override
	public void wakeUp() {}
}
