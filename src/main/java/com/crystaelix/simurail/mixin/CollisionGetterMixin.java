package com.crystaelix.simurail.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.crystaelix.simurail.content.gangway_frame.GangwayFrameCollisions;
import com.google.common.collect.Iterables;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

@Mixin(CollisionGetter.class)
public interface CollisionGetterMixin {

	@WrapMethod(method = "getBlockCollisions")
	private Iterable<VoxelShape> simurail$extendBlockCollisions(Entity entity, AABB collisionBox, Operation<Iterable<VoxelShape>> original) {
		return Iterables.concat(original.call(entity, collisionBox), () -> new GangwayFrameCollisions((CollisionGetter)this, collisionBox));
	}
}
