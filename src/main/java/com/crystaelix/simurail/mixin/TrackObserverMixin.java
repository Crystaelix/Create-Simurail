package com.crystaelix.simurail.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.crystaelix.simurail.content.bogey.PhysicsBogeyBlockEntity;
import com.crystaelix.simurail.extension.TrackObserverExtension;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.observer.TrackObserver;
import com.simibubi.create.content.trains.signal.SingleBlockEntityEdgePoint;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;

@Mixin(TrackObserver.class)
public abstract class TrackObserverMixin extends SingleBlockEntityEdgePoint implements TrackObserverExtension {

	@Unique
	private int bogeyActivated;
	@Unique
	private BlockPos currentBogey;

	@WrapMethod(method = "tick")
	private void simurail$wrapTick(TrackGraph graph, boolean preTrains, Operation<Void> original, @Share("simurail$ticking") LocalBooleanRef ticking) {
		ticking.set(true);
		original.call(graph, preTrains);
		if(preTrains) {
			if(bogeyActivated > 0) {
				bogeyActivated--;
				if(bogeyActivated <= 0) {
					currentBogey = null;
				}
			}
		}
		ticking.set(false);
	}

	@WrapMethod(method = "isActivated")
	private boolean simurail$wrapIsActivated(Operation<Boolean> original, @Share("simurail$ticking") LocalBooleanRef ticking) {
		return !ticking.get() && bogeyActivated > 0 || original.call();
	}

	@Inject(method = "read", at = @At("TAIL"))
	private void simurail$onRead(CompoundTag nbt, HolderLookup.Provider registries, boolean migration, DimensionPalette dimensions, CallbackInfo ci) {
		bogeyActivated = nbt.getInt("SimurailBogeyActivated");
		if(nbt.contains("SimurailBogeyPos")) {
			currentBogey = NbtUtils.readBlockPos(nbt, "SimurailBogeyPos").orElse(null);
		}
	}


	@Inject(method = "write", at = @At("TAIL"))
	private void simurail$onWrite(CompoundTag nbt, HolderLookup.Provider registries, DimensionPalette dimensions, CallbackInfo ci) {
		nbt.putInt("SimurailBogeyActivated", bogeyActivated);
		if(currentBogey != null) {
			nbt.put("SimurailBogeyPos", NbtUtils.writeBlockPos(currentBogey));
		}
	}

	@Override
	public void simurail$keepAlive(PhysicsBogeyBlockEntity bogey) {
		bogeyActivated = 8;
		currentBogey = bogey.getBlockPos();
	}

	@Override
	public BlockPos simurail$getCurrentBogey() {
		return currentBogey;
	}
}
