package com.crystaelix.simurail.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.crystaelix.simurail.content.track.CurvedTrackSegmentCache;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.content.trains.graph.TrackNodeLocation.DiscoveredLocation;
import com.simibubi.create.content.trains.track.BezierConnection;

import net.minecraft.world.level.LevelAccessor;

@Mixin(TrackGraph.class)
public abstract class TrackGraphMixin {

	@Inject(method = "removeNode", at = @At(value = "INVOKE", target = "Ljava/util/Map;entrySet()Ljava/util/Set;"))
	private void simurail$onRemoveNode(LevelAccessor level, TrackNodeLocation loc, CallbackInfoReturnable<Boolean> ci, @Local(name = "connections") Map<TrackNode, TrackEdge> connections) {
		if(level != null && !level.isClientSide()) {
			CurvedTrackSegmentCache cache = CurvedTrackSegmentCache.getOrCreateCache(loc.dimension);
			for(TrackEdge edge : connections.values()) {
				if(edge.isTurn()) {
					cache.removeCurve(edge.getTurn());
				}
			}
		}
	}

	@Inject(method = "connectNodes", at = @At("HEAD"))
	private void simurail$onConnectNodes(LevelAccessor level, DiscoveredLocation loc1, DiscoveredLocation loc2, BezierConnection turn, CallbackInfo ci) {
		if(level != null && !level.isClientSide() && turn != null) {
			CurvedTrackSegmentCache.getOrCreateCache(loc1.dimension).addCurve(turn);
		}
	}
}
