package com.crystaelix.simurail.mixin.compat.railx;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.crystaelix.simurail.api.extension.BezierConnectionExtension;
import com.lhwdev.minecraft.railx.compat.BezierConnectionCompatKt;
import com.simibubi.create.content.trains.track.BezierConnection;

@Mixin(BezierConnectionCompatKt.class)
public abstract class BezierConnectionCompatMixin {

	@Inject(method = "onCurveUpdated", at = @At("TAIL"))
	private static void simurail$onCurveUpdated(BezierConnection curve, CallbackInfo ci) {
		((BezierConnectionExtension)curve).simurail$invalidateCurve();
	}
}
