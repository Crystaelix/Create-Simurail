package com.crystaelix.simurail.compat.loconautics;

import com.crystaelix.simurail.content.automatic_coupler.AutomaticCouplerBlockEntity;
import com.crystaelix.simurail.content.automatic_coupler.copycat.CopycatAutomaticCouplerBlockEntity;
import com.lycoris.loconautics.trainphysics.TrainConsist;
import com.lycoris.loconautics.trainphysics.coupling.BlockEntityCouplingProvider;

public class SimurailLoconauticsCompat {

	public static void onCommonSetupLate() {
		TrainConsist.registerProvider(new BlockEntityCouplingProvider<>(
				AutomaticCouplerBlockEntity.class,
				AutomaticCouplerBlockEntity::getPartnerSubLevelID,
				AutomaticCouplerBlockEntity::hasCouplerPartner));
		TrainConsist.registerProvider(new BlockEntityCouplingProvider<>(
				CopycatAutomaticCouplerBlockEntity.class,
				CopycatAutomaticCouplerBlockEntity::getPartnerSubLevelID,
				CopycatAutomaticCouplerBlockEntity::hasCouplerPartner));
	}
}
