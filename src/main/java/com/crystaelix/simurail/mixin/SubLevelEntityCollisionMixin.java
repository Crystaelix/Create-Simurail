package com.crystaelix.simurail.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import dev.ryanhcode.sable.api.math.LevelReusedVectors;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.sublevel.entity_collision.SubLevelEntityCollision;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.Mth;

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
}
