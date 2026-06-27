package com.crystaelix.simurail.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.crystaelix.simurail.content.bogey.PhysicsBogeyBlockEntity;
import com.crystaelix.simurail.extension.TrackObserverExtension;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.ObservedTrainNameSource;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.content.trains.observer.TrackObserver;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;

@Mixin(ObservedTrainNameSource.class)
public abstract class ObservedTrainNameSourceMixin extends SingleLineDisplaySource {

	@Inject(method = "provideLine", at = @At(value = "RETURN", ordinal = 2), cancellable = true)
	private void simurail$modifyProvideLine(DisplayLinkContext context, DisplayTargetStats stats, CallbackInfoReturnable<MutableComponent> ci, @Local TrackObserver observer) {
		BlockPos currentBogey = ((TrackObserverExtension)observer).simurail$getCurrentBogey();
		if(currentBogey != null && context.level().getBlockEntity(currentBogey) instanceof PhysicsBogeyBlockEntity bogey) {
			ci.setReturnValue(bogey.getName().copy());
		}
	}
}
