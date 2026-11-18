package com.crystaelix.simurail.compat.loconautics;

import com.crystaelix.simurail.content.automatic_coupler.AutomaticCouplerBlockEntity;
import com.crystaelix.simurail.content.automatic_coupler.copycat.CopycatAutomaticCouplerBlockEntity;
import com.lycoris.loconautics.common.ConnectorDetachers;
import com.lycoris.loconautics.trainphysics.TrainConsist;
import com.lycoris.loconautics.trainphysics.coupling.BlockEntityCouplingProvider;

public class SimurailLoconauticsCompat {

	public static void onCommonSetupLate() {
		TrainConsist.registerProvider(new BlockEntityCouplingProvider<>(
				AutomaticCouplerBlockEntity.class,
				AutomaticCouplerBlockEntity::getCouplerPartnerSubLevelID,
				AutomaticCouplerBlockEntity::hasCouplerPartner));
		TrainConsist.registerProvider(new BlockEntityCouplingProvider<>(
				CopycatAutomaticCouplerBlockEntity.class,
				CopycatAutomaticCouplerBlockEntity::getCouplerPartnerSubLevelID,
				CopycatAutomaticCouplerBlockEntity::hasCouplerPartner));
		ConnectorDetachers.register(new AutomaticCouplerDetacher());
	}
}
