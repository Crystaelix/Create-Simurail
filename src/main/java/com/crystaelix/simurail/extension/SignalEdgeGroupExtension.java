package com.crystaelix.simurail.extension;

import com.crystaelix.simurail.content.bogey.PhysicsBogeyBlockEntity;
import com.crystaelix.simurail.content.bogey.PhysicsBogeyGroup;

public interface SignalEdgeGroupExtension {

	void simurail$queueBogey(PhysicsBogeyBlockEntity bogey);

	void simurail$updateBogeys();

	boolean simurail$isOccupiedUnless(PhysicsBogeyBlockEntity bogey);

	boolean simurail$isOccupiedUnless(PhysicsBogeyGroup group);

	boolean simurail$isThisOccupiedUnless(PhysicsBogeyBlockEntity bogey);

	boolean simurail$isThisOccupiedUnless(PhysicsBogeyGroup group);
}
