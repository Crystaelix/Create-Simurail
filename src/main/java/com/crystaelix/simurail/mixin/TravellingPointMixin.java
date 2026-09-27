package com.crystaelix.simurail.mixin;

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.crystaelix.simurail.content.track_switch.TrackSwitch;
import com.crystaelix.simurail.content.track_switch.TrackSwitchVariables;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.entity.TravellingPoint.ITrackSelector;
import com.simibubi.create.content.trains.graph.EdgeData;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;

import net.createmod.catnip.data.Pair;

@Mixin(TravellingPoint.class)
public abstract class TravellingPointMixin {

	@Shadow
	public TrackEdge edge;

	@WrapOperation(method = "travel(Lcom/simibubi/create/content/trains/graph/TrackGraph;DLcom/simibubi/create/content/trains/entity/TravellingPoint$ITrackSelector;Lcom/simibubi/create/content/trains/entity/TravellingPoint$IEdgePointListener;Lcom/simibubi/create/content/trains/entity/TravellingPoint$ITurnListener;Lcom/simibubi/create/content/trains/entity/TravellingPoint$IPortalListener;)D", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/entity/TravellingPoint$ITrackSelector;apply(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
	private Object simurail$replaceSelectEdge(ITrackSelector trackSelector, Object graphObj, Object pairObj, Operation<Object> original) {
		if(!TrackSwitchVariables.skipSwitches && TrackSwitchVariables.signalPropagatorCallDepth == 0) {
			Pair<Boolean, List<Map.Entry<TrackNode, TrackEdge>>> pair = (Pair<Boolean, List<Map.Entry<TrackNode, TrackEdge>>>)pairObj;
			boolean forward = pair.getFirst();
			EdgeData edgeData = edge.getEdgeData();
			List<TrackEdgePoint> edgePoints = forward ? edgeData.getPoints().reversed() : edgeData.getPoints();
			TrackSwitch sw = edgePoints.stream().
					filter(p -> p.isPrimary(forward ? edge.node2 : edge.node1)).
					filter(TrackSwitch.class::isInstance).
					map(TrackSwitch.class::cast).
					filter(TrackSwitch::isActive).
					findFirst().
					orElse(null);
			if(sw != null) {
				Map.Entry<TrackNode, TrackEdge> target = sw.selectTarget(pair.getSecond());
				if(target != null) {
					return target;
				}
			}
		}
		return original.call(trackSelector, graphObj, pairObj);
	}
}
