package com.crystaelix.simurail.content;

import com.crystaelix.simurail.Simurail;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;

public class SimurailPartialModels {

	public static final PartialModel
	COUPLER_BAR = block("coupler/bar"),
	COUPLER_BAR_SHORT = block("coupler/bar_short"),
	COUPLER_BAR_EXTRA_LONG = block("coupler/bar_extra_long"),

	AUTOMATIC_COUPLER_KNUCKLE = block("coupler/automatic/knuckle"),
	AUTOMATIC_COUPLER_SHIBATA = block("coupler/automatic/shibata"),
	AUTOMATIC_COUPLER_SCHARFENBERG = block("coupler/automatic/scharfenberg"),
	AUTOMATIC_COUPLER_SA3 = block("coupler/automatic/sa3"),

	PHYSICS_ROLLER_FRAME = block("physics_roller/frame"),
	PHYSICS_ROLLER_WHEEL = block("physics_roller/wheel"),

	TRACK_SWITCH_SIGN = block("track_switch/sign"),
	TRACK_SWITCH_NONE = block("track_switch/overlay/none"),
	TRACK_SWITCH_LEFT_STRAIGHT = block("track_switch/overlay/left_straight"),
	TRACK_SWITCH_LEFT_TURN = block("track_switch/overlay/left_turn"),
	TRACK_SWITCH_RIGHT_STRAIGHT = block("track_switch/overlay/right_straight"),
	TRACK_SWITCH_RIGHT_TURN = block("track_switch/overlay/right_turn"),
	TRACK_SWITCH_WYE_LEFT = block("track_switch/overlay/wye_left"),
	TRACK_SWITCH_WYE_RIGHT = block("track_switch/overlay/wye_right"),
	TRACK_SWITCH_3WAY_STRAIGHT = block("track_switch/overlay/3way_straight"),
	TRACK_SWITCH_3WAY_LEFT = block("track_switch/overlay/3way_left"),
	TRACK_SWITCH_3WAY_RIGHT = block("track_switch/overlay/3way_right");

	public static void register() {
	}

	public static PartialModel block(String path) {
		return PartialModel.of(Simurail.id("block/" + path));
	}
}
