package com.crystaelix.simurail.mixin.compat.ip_aeronautics;

import java.util.Map;
import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.crystaelix.simurail.compat.ip_aeronautics.IPStraddleSessionCache;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

@Mixin(targets = "qouteall.imm_ptl.core.compat.sable_straddle.IPStraddleSessions")
public class IPStraddleSessionsMixin {

	@WrapOperation(method = "openOrRefresh", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
	private static Object simurail$wrapAddSession(Map<?, ?> sessions, Object key, Object session, Operation<Object> original) {
		IPStraddleSessionCache.addSession((IPStraddleSessionAccessor)session);
		return original.call(sessions, key, session);
	}

	@WrapOperation(method = "close", at = @At(value = "INVOKE", target = "Ljava/util/Map;remove(Ljava/lang/Object;)Ljava/lang/Object;"))
	private static Object simurail$wrapRemoveSession(Map<?, ?> sessions, Object key, Operation<Object> original) {
		Object session = original.call(sessions, key);
		IPStraddleSessionCache.removeSession((IPStraddleSessionAccessor)session);
		return session;
	}

	@Inject(method = "dropShip", at = @At("TAIL"))
	private static void simurail$onDropShip(UUID subLevelId, CallbackInfo ci) {
		IPStraddleSessionCache.removeSubLevel(subLevelId);
	}

	@Inject(method = "reset", at = @At("TAIL"))
	private static void simurail$onReset(CallbackInfo ci) {
		IPStraddleSessionCache.clear();
	}
}
