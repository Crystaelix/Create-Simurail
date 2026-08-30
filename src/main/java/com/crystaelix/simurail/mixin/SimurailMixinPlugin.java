package com.crystaelix.simurail.mixin;

import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.neoforged.fml.loading.LoadingModList;

public class SimurailMixinPlugin implements IMixinConfigPlugin {

	@Override
	public void onLoad(String mixinPackage) {}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		if(mixinClassName.contains("compat.electroenergetics")) {
			return isLoaded("electroenergetics");
		}
		if(mixinClassName.contains("compat.railways")) {
			if(mixinClassName.contains("TrackBufferBlockMixin")) {
				return isLoaded("railways");
			}
			return isLoaded("railways");
		}
		if(mixinClassName.contains("compat.railx")) {
			return isLoaded("railx");
		}
		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

	public boolean isLoaded(String modId) {
		return LoadingModList.get().getModFileById(modId) != null;
	}
}
