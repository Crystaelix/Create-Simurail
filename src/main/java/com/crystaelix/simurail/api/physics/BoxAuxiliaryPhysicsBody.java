package com.crystaelix.simurail.api.physics;

import org.joml.Vector3d;
import org.joml.Vector3dc;

import com.crystaelix.simurail.api.math.SimurailMath;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.physics.PhysicsPipelineBody;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;

public class BoxAuxiliaryPhysicsBody implements AuxiliaryPhysicsBody {

	private final ServerLevel level;
	private final BlockPos parent;
	private final Vector3dc halfExtents;
	private final double mass;

	private final Pose3d pose = new Pose3d();
	private final Vector3d scale = new Vector3d(1);
	private AttachableBoxPhysicsObject box;

	public BoxAuxiliaryPhysicsBody(ServerLevel level, BlockPos parent, Vector3dc halfExtents, double mass) {
		this.level = level;
		this.parent = parent;
		this.halfExtents = halfExtents;
		this.mass = mass;
	}

	@Override
	public boolean isRemoved() {
		return box == null || box.isRemoved();
	}

	@Override
	public void create(Pose3dc initialPose) {
		if(isRemoved()) {
			pose.set(initialPose);
			scale.set(pose.scale());
			pose.scale().set(1);
			ServerSubLevel subLevel = (ServerSubLevel)Sable.HELPER.getContaining(level, parent);
			SubLevelPhysicsSystem physics = SubLevelPhysicsSystem.require(level);
			box = new AttachableBoxPhysicsObject(subLevel, pose, new Vector3d(halfExtents).mul(scale), mass * scale.x * scale.y * scale.z);
			physics.addObject(box);
			pose.scale().set(scale);
		}
	}

	@Override
	public void remove() {
		if(box != null) {
			SubLevelPhysicsSystem physics = SubLevelContainer.getContainer(level).physicsSystem();
			physics.removeObject(box);
			box = null;
		}
	}

	@Override
	public void afterParentMove() {
	}

	@Override
	public boolean scaleEquals(Vector3dc scale) {
		return !isRemoved() && this.scale.equals(scale, SimurailMath.EPSILON);
	}

	@Override
	public Pose3dc pose() {
		if(!isRemoved()) {
			box.updatePose();
			pose.set(box.getPose());
			pose.scale().set(scale);
		}
		return pose;
	}

	@Override
	public Vector3d position(Vector3dc offset) {
		return new Vector3d(offset).mul(scale);
	}

	@Override
	public PhysicsPipelineBody physicsBody() {
		return box;
	}

	@Override
	public CompoundTag write() {
		return new CompoundTag();
	}

	@Override
	public void read(CompoundTag tag) {
	}
}
