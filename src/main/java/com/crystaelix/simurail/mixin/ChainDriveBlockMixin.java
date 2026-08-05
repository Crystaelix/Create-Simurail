package com.crystaelix.simurail.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.crystaelix.simurail.api.kinetics.CustomChainDriveModifier;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.chainDrive.ChainDriveBlock;

@Mixin(ChainDriveBlock.class)
public abstract class ChainDriveBlockMixin extends RotatedPillarKineticBlock {

	public ChainDriveBlockMixin(Properties properties) {
		super(properties);
	}

	@Inject(method = "getRotationSpeedModifier", at = @At("MIXINEXTRAS:EXPRESSION"))
	@Definition(id = "fromMod", local = @Local(name = "fromMod", type = float.class))
	@Definition(id = "toMod", local = @Local(name = "toMod", type = float.class))
	@Expression("fromMod / toMod")
	private static void simurail$modifyRotationSpeedModifier(KineticBlockEntity from, KineticBlockEntity to, CallbackInfoReturnable<Float> ci, @Local(name = "fromMod") LocalFloatRef fromMod, @Local(name = "toMod") LocalFloatRef toMod) {
		if(from instanceof CustomChainDriveModifier custom) {
			fromMod.set(custom.getModifier());
		}
		if(to instanceof CustomChainDriveModifier custom) {
			toMod.set(custom.getModifier());
		}
	}
}
