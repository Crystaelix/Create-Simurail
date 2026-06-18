package com.crystaelix.simurail.mixin;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.crystaelix.simurail.content.bogey.PhysicsBogeyBlockEntity;
import com.crystaelix.simurail.extension.SignalEdgeGroupExtension;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import com.simibubi.create.content.trains.signal.SignalEdgeGroup;

@Mixin(SignalEdgeGroup.class)
public abstract class SignalEdgeGroupMixin implements SignalEdgeGroupExtension {

	@Shadow
	public Set<Train> trains;
	@Shadow
	public SignalBoundary reserved;
	@Shadow
	public Set<SignalEdgeGroup> intersectingResolved;

	@Unique
	private Set<PhysicsBogeyBlockEntity> queuedBogeys = new HashSet<>();
	@Unique
	private Set<PhysicsBogeyBlockEntity> bogeys = new HashSet<>();

	@Shadow
	private void walkIntersecting(Consumer<SignalEdgeGroup> callback) {}

	@Override
	public void simurail$queueBogey(PhysicsBogeyBlockEntity bogey) {
		queuedBogeys.add(bogey);
	}

	@Override
	public void simurail$updateBogeys() {
		bogeys.clear();
		Set<PhysicsBogeyBlockEntity> temp = bogeys;
		bogeys = queuedBogeys;
		queuedBogeys = temp;
	}

	@Override
	public boolean simurail$isOccupiedUnless(PhysicsBogeyBlockEntity bogey) {
		if(intersectingResolved.isEmpty()) {
			walkIntersecting(intersectingResolved::add);
		}
		for(SignalEdgeGroup group : intersectingResolved) {
			if(((SignalEdgeGroupExtension)group).simurail$isThisOccupiedUnless(bogey)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean simurail$isThisOccupiedUnless(PhysicsBogeyBlockEntity bogey) {
		return bogeys.size() > 1 || !bogeys.contains(bogey) && !bogeys.isEmpty() || reserved != null || !trains.isEmpty();
	}

	@WrapMethod(method = "isThisOccupiedUnless(Lcom/simibubi/create/content/trains/entity/Train;)Z")
	private boolean simurail$modifyIsOccupied(Train train, Operation<Boolean> original) {
		return !bogeys.isEmpty() || original.call(train);
	}

	@WrapMethod(method = "isThisOccupiedUnless(Lcom/simibubi/create/content/trains/signal/SignalBoundary;)Z")
	private boolean simurail$modifyIsOccupied(SignalBoundary boundary, Operation<Boolean> original) {
		return !bogeys.isEmpty() || original.call(boundary);
	}
}
