package com.crystaelix.simurail.mixin;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.crystaelix.simurail.extension.PonderSceneRegistrationHelperExtension;

import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.foundation.registration.GenericPonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

@Mixin(GenericPonderSceneRegistrationHelper.class)
public abstract class GenericPonderSceneRegistrationHelperMixin<T> implements PonderSceneRegistrationHelperExtension<T> {

	@Shadow
	private PonderSceneRegistrationHelper<ResourceLocation> helperDelegate;
	@Shadow
	private Function<T, ResourceLocation> keyGen;

	@Override
	public PonderSceneRegistrationHelper<T> simurail$withNamespace(String namespace) {
		return new GenericPonderSceneRegistrationHelper<>(PonderSceneRegistrationHelperExtension.cast(helperDelegate).simurail$withNamespace(namespace), keyGen);
	}
}
