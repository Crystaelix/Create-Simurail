package com.crystaelix.simurail.mixin;

import java.util.Iterator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.crystaelix.simurail.content.gangway_frame.GangwayFrame;
import com.google.common.collect.Iterators;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.mixinhelpers.CanFallAtleastHelper;
import dev.ryanhcode.sable.mixinterface.voxel_shape_iteration.FastVoxelShapeIterable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

@Mixin(CanFallAtleastHelper.class)
public class CanFallAtleastHelperMixin {

	@WrapOperation(method = "canFallAtleastWithSubLevels", at = @At(value = "INVOKE", target = "Ldev/ryanhcode/sable/mixinterface/voxel_shape_iteration/FastVoxelShapeIterable;sable$allBoxes()Ljava/util/Iterator;"))
	private static Iterator<BoundingBox3dc> simurail$extendBoxes(FastVoxelShapeIterable shape, Operation<Iterator<BoundingBox3dc>> original, @Local(argsOnly = true) Level level, @Local(name = "block") BlockPos pos) {
		if(level.getBlockEntity(pos) instanceof GangwayFrame gangway) {
			return Iterators.concat(original.call(shape), gangway.getGangwayCollisions().iterator());
		}
		return original.call(shape);
	}
}
