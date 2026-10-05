package com.crystaelix.simurail.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.simibubi.create.infrastructure.config.CTrains;

@Mixin(CTrains.class)
public abstract class CTrainsMixin {

	@ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/infrastructure/config/CTrains;i(IIILjava/lang/String;[Ljava/lang/String;)Lnet/createmod/catnip/config/ConfigBase$ConfigInt;"), index = 2)
	private int simurail$modifyMaxTrackPlacementLength(int current, int min, int max, String name, String[] comment) {
		return !name.equals("maxTrackPlacementLength") ? max : Math.max(max, 256);
	}
}
