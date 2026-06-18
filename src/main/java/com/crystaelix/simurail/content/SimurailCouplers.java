package com.crystaelix.simurail.content;

import com.crystaelix.simurail.Simurail;
import com.crystaelix.simurail.api.coupler.CouplerType;
import com.crystaelix.simurail.api.coupler.CouplerTypeRegistry;

public class SimurailCouplers {

	public static final CouplerType
	KNUCKLE = new CouplerType(
			Simurail.id("knuckle"),
			Simurail.id("block/coupler/automatic/knuckle")),
	SHIBATA = new CouplerType(
			Simurail.id("shibata"),
			Simurail.id("block/coupler/automatic/shibata"));

	public static void register() {
		CouplerTypeRegistry.register(KNUCKLE);
		CouplerTypeRegistry.register(SHIBATA);
	}
}
