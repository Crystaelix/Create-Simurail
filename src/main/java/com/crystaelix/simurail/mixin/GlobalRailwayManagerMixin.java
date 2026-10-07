package com.crystaelix.simurail.mixin;

import java.util.Map;
import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.crystaelix.simurail.content.track.CurvedTrackSegmentCache;
import com.crystaelix.simurail.extension.SignalEdgeGroupExtension;
import com.simibubi.create.content.trains.GlobalRailwayManager;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.signal.SignalEdgeGroup;

@Mixin(GlobalRailwayManager.class)
public abstract class GlobalRailwayManagerMixin {

	@Shadow
	public Map<UUID, TrackGraph> trackNetworks;
	@Shadow
	public Map<UUID, SignalEdgeGroup> signalEdgeGroups;

	@Inject(method = "levelLoaded", at = @At("TAIL"))
	private void simurail$onLoad(CallbackInfo ci) {
		for(TrackGraph graph : trackNetworks.values()) {
			CurvedTrackSegmentCache.addGraph(graph);
		}
	}

	@Inject(method = "tickTrains", at = @At("HEAD"))
	private void simurail$onTickTrains(CallbackInfo ci) {
		for(SignalEdgeGroup group : signalEdgeGroups.values()) {
			((SignalEdgeGroupExtension)group).simurail$updateBogeys();
		}
	}
}
