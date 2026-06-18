package com.crystaelix.simurail.compat.create_bb;

public class SimurailBlocksBogiesCompat {

	public static void onCommonSetupLate() {
		BlocksBogiesOverrides.register();
		BlocksBogiesBogeys.register();
	}
}
