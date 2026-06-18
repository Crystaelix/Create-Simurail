package com.crystaelix.simurail.extension;

import com.crystaelix.simurail.content.bogey.PhysicsBogeyBlockEntity;

public interface SignalEdgeGroupExtension {

	void simurail$queueBogey(PhysicsBogeyBlockEntity bogey);

	void simurail$updateBogeys();

	boolean simurail$isOccupiedUnless(PhysicsBogeyBlockEntity bogey);

	boolean simurail$isThisOccupiedUnless(PhysicsBogeyBlockEntity bogey);
}
