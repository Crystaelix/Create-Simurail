package com.crystaelix.simurail.compat.offroad;

import com.simibubi.create.api.stress.BlockStressValues;

import dev.ryanhcode.offroad.index.OffroadBlocks;

public class SimurailOffroadCompat {

	public static void onConstruct() {
		SimurailOffroadBlocks.register();
		SimurailOffroadBlockEntities.register();
	}

	public static void onCommonSetupLate() {
		BlockStressValues.IMPACTS.register(SimurailOffroadBlocks.BRASS_BOREHEAD_BEARING.get(),
				() -> BlockStressValues.getImpact(OffroadBlocks.BOREHEAD_BEARING_BLOCK.get()));
	}
}
