package com.crystaelix.simurail.mixin.compat.railx;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.crystaelix.simurail.api.extension.BezierConnectionExtension;
import com.simibubi.create.content.trains.track.BezierConnection;

/**
 * Railx edits the starts, axes and normals of live curves in place and announces every such edit through this method
 */
@Mixin(targets = "com.lhwdev.minecraft.railx.compat.BezierConnectionCompatKt", remap = false)
public class BezierConnectionCompatMixin {

	@Inject(method = "onCurveUpdated", at = @At("TAIL"))
	private static void simurail$onCurveUpdated(BezierConnection curve, CallbackInfo ci) {
		((BezierConnectionExtension)curve).simurail$invalidateCurve();
	}
}
