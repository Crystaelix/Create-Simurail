package com.crystaelix.simurail.content;

import com.crystaelix.simurail.Simurail;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;

public class SimurailPartialModels {

	public static final PartialModel
	COUPLER_BAR = block("coupler/bar"),
	COUPLER_BAR_SHORT = block("coupler/bar_short"),

	AUTOMATIC_COUPLER_KNUCKLE = block("coupler/automatic/knuckle"),
	AUTOMATIC_COUPLER_SHIBATA = block("coupler/automatic/shibata");

	public static void register() {
	}

	public static PartialModel block(String path) {
		return PartialModel.of(Simurail.id("block/" + path));
	}
}
