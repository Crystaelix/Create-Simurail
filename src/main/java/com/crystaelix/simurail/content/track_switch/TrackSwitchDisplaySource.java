package com.crystaelix.simurail.content.track_switch;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class TrackSwitchDisplaySource extends SingleLineDisplaySource {

	public static final Component STRAIGHT = Component.translatable("block.simurail.track_switch.straight");
	public static final Component LEFT = Component.translatable("block.simurail.track_switch.left");
	public static final Component RIGHT = Component.translatable("block.simurail.track_switch.right");

	@Override
	protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
		if(context.getSourceBlockEntity() instanceof TrackSwitchBlockEntity be) {
			return switch(be.state) {
			case STRAIGHT -> STRAIGHT.copy();
			case LEFT -> LEFT.copy();
			case RIGHT -> RIGHT.copy();
			};
		}
		return EMPTY_LINE;
	}

	@Override
	public int getPassiveRefreshTicks() {
		return 40;
	}

	@Override
	protected boolean allowsLabeling(DisplayLinkContext context) {
		return true;
	}
}
