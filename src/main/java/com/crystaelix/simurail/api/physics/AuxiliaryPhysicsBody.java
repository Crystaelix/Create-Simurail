package com.crystaelix.simurail.api.physics;

import org.joml.Vector3d;
import org.joml.Vector3dc;

import dev.ryanhcode.sable.api.physics.PhysicsPipelineBody;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import net.minecraft.nbt.CompoundTag;

public interface AuxiliaryPhysicsBody {

	boolean isRemoved();

	void create(Pose3dc initialPose);

	void remove();

	void afterParentMove();

	boolean scaleEquals(Vector3dc scale);

	Pose3dc pose();

	Vector3d position(Vector3dc offset);

	PhysicsPipelineBody physicsBody();

	CompoundTag write();

	void read(CompoundTag tag);
}
