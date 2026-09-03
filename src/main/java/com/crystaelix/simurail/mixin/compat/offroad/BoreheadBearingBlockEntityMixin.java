package com.crystaelix.simurail.mixin.compat.offroad;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.crystaelix.simurail.compat.offroad.brass_borehead_bearing.BrassBoreheadBearingBlockEntity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.ryanhcode.offroad.content.blocks.borehead_bearing.BoreheadBearingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(value = BoreheadBearingBlockEntity.class, remap = false)
public class BoreheadBearingBlockEntityMixin {

	@WrapOperation(
			method = "updateMiningBlocks",
			at = @At(
					value = "INVOKE",
					target = "Lcom/simibubi/create/content/kinetics/base/BlockBreakingKineticBlockEntity;isBreakable(Lnet/minecraft/world/level/block/state/BlockState;F)Z"))
	private boolean simurail$filterGatheredBlocks(BlockState state, float hardness, Operation<Boolean> original) {
		if(!original.call(state, hardness)) {
			return false;
		}
		return !((Object) this instanceof BrassBoreheadBearingBlockEntity be) || be.canMine(state);
	}
}
