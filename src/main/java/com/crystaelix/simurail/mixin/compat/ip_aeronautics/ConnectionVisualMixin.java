package com.crystaelix.simurail.mixin.compat.ip_aeronautics;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.crystaelix.simurail.content.automatic_coupler.AutomaticCouplerVisual;
import com.crystaelix.simurail.content.automatic_coupler.copycat.CopycatAutomaticCouplerVisual;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import qouteall.imm_ptl.core.compat.sable_straddle.client.IPStraddleRender;

@Mixin({
	AutomaticCouplerVisual.class,
	CopycatAutomaticCouplerVisual.class,
})
public class ConnectionVisualMixin {

	@WrapOperation(method = "beginFrame", at = @At(value = "INVOKE", target = "Ldev/ryanhcode/sable/sublevel/ClientSubLevel;renderPose(F)Ldev/ryanhcode/sable/companion/math/Pose3dc;"))
	private Pose3dc simurail$unmapPose(ClientSubLevel subLevel, float partialTick, Operation<Pose3dc> original) {
		if(IPStraddleRender.isProjecting()) {
			return IPStraddleRender.realRenderPose(subLevel);
		}
		return original.call(subLevel, partialTick);
	}
}
