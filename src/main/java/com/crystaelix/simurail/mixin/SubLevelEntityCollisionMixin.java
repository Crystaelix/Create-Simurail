package com.crystaelix.simurail.mixin;

import java.util.Iterator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.crystaelix.simurail.content.gangway_frame.GangwayFrame;
import com.google.common.collect.Iterators;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import dev.ryanhcode.sable.api.math.LevelReusedVectors;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.mixinterface.voxel_shape_iteration.FastVoxelShapeIterable;
import dev.ryanhcode.sable.sublevel.entity_collision.SubLevelEntityCollision;
import dev.ryanhcode.sable.util.LevelAccelerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

@Mixin(SubLevelEntityCollision.class)
public abstract class SubLevelEntityCollisionMixin {

	@WrapOperation(method = "collide", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;betweenClosed(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)Ljava/lang/Iterable;"))
	private static Iterable<BlockPos> simurail$replaceBlockPosRange(BlockPos firstPos, BlockPos secondPos, Operation<Iterable<BlockPos>> original, @Local(argsOnly = true) LevelReusedVectors sink) {
		MutableBlockPos minPos = sink.minPos;
		MutableBlockPos maxPos = sink.maxPos;
		BoundingBox3d localBounds = sink.localBounds;
		if(minPos.getX() == Mth.floor(localBounds.minX)) {
			minPos.setX(minPos.getX() - 1);
		}
		if(minPos.getZ() == Mth.floor(localBounds.minZ)) {
			minPos.setZ(minPos.getZ() - 1);
		}
		if(maxPos.getX() == Mth.floor(localBounds.maxX)) {
			maxPos.setX(maxPos.getX() + 1);
		}
		if(maxPos.getZ() == Mth.floor(localBounds.minZ)) {
			maxPos.setZ(maxPos.getZ() + 1);
		}
		return original.call(minPos, maxPos);
	}

	@WrapOperation(method = "collide", at = @At(value = "INVOKE", target = "Ldev/ryanhcode/sable/mixinterface/voxel_shape_iteration/FastVoxelShapeIterable;sable$allBoxes()Ljava/util/Iterator;", ordinal = 0))
	private static Iterator<BoundingBox3dc> simurail$extendBoxesCollide0(FastVoxelShapeIterable shape, Operation<Iterator<BoundingBox3dc>> original, @Local(name = "level") Level level, @Local(name = "block") BlockPos pos) {
		if(level.getBlockEntity(pos) instanceof GangwayFrame gangway) {
			return Iterators.concat(original.call(shape), gangway.getGangwayCollisions().iterator());
		}
		return original.call(shape);
	}

	@WrapOperation(method = "collide", at = @At(value = "INVOKE", target = "Ldev/ryanhcode/sable/mixinterface/voxel_shape_iteration/FastVoxelShapeIterable;sable$allBoxes()Ljava/util/Iterator;", ordinal = 1))
	private static Iterator<BoundingBox3dc> simurail$extendBoxesCollide1(FastVoxelShapeIterable shape, Operation<Iterator<BoundingBox3dc>> original, @Local(name = "level") Level level, @Local(name = "newPos") BlockPos pos) {
		if(level.getBlockEntity(pos) instanceof GangwayFrame gangway) {
			return Iterators.concat(original.call(shape), gangway.getGangwayCollisions().iterator());
		}
		return original.call(shape);
	}

	@WrapOperation(method = "hasCollision", at = @At(value = "INVOKE", target = "Ldev/ryanhcode/sable/mixinterface/voxel_shape_iteration/FastVoxelShapeIterable;sable$allBoxes()Ljava/util/Iterator;"))
	private static Iterator<BoundingBox3dc> simurail$extendBoxesHasCollision(FastVoxelShapeIterable shape, Operation<Iterator<BoundingBox3dc>> original, @Local(argsOnly = true) LevelAccelerator level, @Local(name = "block") BlockPos pos) {
		if(level.getBlockEntity(pos) instanceof GangwayFrame gangway) {
			return Iterators.concat(original.call(shape), gangway.getGangwayCollisions().iterator());
		}
		return original.call(shape);
	}
}
