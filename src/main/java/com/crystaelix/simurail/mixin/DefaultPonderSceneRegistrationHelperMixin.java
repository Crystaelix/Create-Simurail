package com.crystaelix.simurail.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.crystaelix.simurail.extension.PonderSceneRegistrationHelperExtension;

import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.foundation.registration.DefaultPonderSceneRegistrationHelper;
import net.createmod.ponder.foundation.registration.PonderSceneRegistry;
import net.minecraft.resources.ResourceLocation;

@Mixin(DefaultPonderSceneRegistrationHelper.class)
public abstract class DefaultPonderSceneRegistrationHelperMixin implements PonderSceneRegistrationHelperExtension<ResourceLocation> {

	@Shadow
	protected PonderSceneRegistry sceneRegistry;

	@Override
	public PonderSceneRegistrationHelper<ResourceLocation> withNamespace(String namespace) {
		return new DefaultPonderSceneRegistrationHelper(namespace, sceneRegistry);
	}
}
