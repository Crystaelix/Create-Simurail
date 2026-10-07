package com.crystaelix.simurail.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.crystaelix.simurail.extension.PonderSceneRegistrationHelperExtension;

import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.foundation.registration.DefaultPonderSceneRegistrationHelper;
import net.createmod.ponder.foundation.registration.PonderSceneRegistry;
import net.minecraft.resources.ResourceLocation;

@Mixin(DefaultPonderSceneRegistrationHelper.class)
public abstract class DefaultPonderSceneRegistrationHelperMixin implements PonderSceneRegistrationHelperExtension<ResourceLocation> {

	@Shadow
	protected String namespace;
	@Shadow
	protected PonderSceneRegistry sceneRegistry;

	@Unique
	protected String storyBoardNamespace = null;

	@Override
	public PonderSceneRegistrationHelper<ResourceLocation> simurail$withNamespace(String namespace) {
		DefaultPonderSceneRegistrationHelper helper = new DefaultPonderSceneRegistrationHelper(namespace, sceneRegistry);
		((DefaultPonderSceneRegistrationHelperMixin)(Object)helper).storyBoardNamespace = namespace;
		return helper;
	}

	@ModifyArg(method = "createStoryBoardEntry", at = @At(value = "INVOKE", target = "Lnet/createmod/ponder/foundation/PonderStoryBoardEntry;<init>(Lnet/createmod/ponder/api/scene/PonderStoryBoard;Ljava/lang/String;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;)V"))
	private String simurail$modifyStoryBoardNamespace(String namespace) {
		return storyBoardNamespace != null ? storyBoardNamespace : namespace;
	}
}
