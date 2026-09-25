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
			Simurail.id("block/coupler/automatic/shibata")),
	SCHARFENBERG = new CouplerType(
			Simurail.id("scharfenberg"),
			Simurail.id("block/coupler/automatic/scharfenberg")),
	SA3 = new CouplerType(
			Simurail.id("sa3"),
			Simurail.id("block/coupler/automatic/sa3"));

	public static void register() {
		CouplerTypeRegistry.register(KNUCKLE);
		CouplerTypeRegistry.register(SHIBATA);
		CouplerTypeRegistry.register(SCHARFENBERG);
		CouplerTypeRegistry.register(SA3);
	}
}
