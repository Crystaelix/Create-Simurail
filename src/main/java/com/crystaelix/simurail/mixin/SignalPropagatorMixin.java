package com.crystaelix.simurail.mixin;

import java.util.List;
import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.crystaelix.simurail.content.track_switch.TrackSwitchVariables;
import com.simibubi.create.content.trains.graph.EdgeData;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import com.simibubi.create.content.trains.signal.SignalPropagator;

import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Pair;

/**
 * Copy of {@link com.railwayteam.railways.mixin.MixinSignalPropagator MixinSignalPropagator}
 */
@Mixin(SignalPropagator.class)
public abstract class SignalPropagatorMixin {
	
    @Inject(method = "walkSignals(Lcom/simibubi/create/content/trains/graph/TrackGraph;Ljava/util/List;Ljava/util/function/Predicate;Ljava/util/function/Predicate;Z)V", at = @At("HEAD"))
    private static void simurail$onWalkSignals(TrackGraph graph, List<Couple<TrackNode>> frontier, Predicate<Pair<TrackNode, SignalBoundary>> boundaryCallback, Predicate<EdgeData> nonBoundaryCallback, boolean forCollection, CallbackInfo ci) {
        ++TrackSwitchVariables.signalPropagatorCallDepth;
    }

    @Inject(method = "walkSignals(Lcom/simibubi/create/content/trains/graph/TrackGraph;Ljava/util/List;Ljava/util/function/Predicate;Ljava/util/function/Predicate;Z)V", at = @At("RETURN"))
    private static void simurail$onWalkSignalsReturn(TrackGraph graph, List<Couple<TrackNode>> frontier, Predicate<Pair<TrackNode, SignalBoundary>> boundaryCallback, Predicate<EdgeData> nonBoundaryCallback, boolean forCollection, CallbackInfo ci) {
        if(TrackSwitchVariables.signalPropagatorCallDepth > 0) {
        	--TrackSwitchVariables.signalPropagatorCallDepth;
        }
    }
}