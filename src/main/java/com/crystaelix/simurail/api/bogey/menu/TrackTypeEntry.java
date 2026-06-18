package com.crystaelix.simurail.api.bogey.menu;

import java.util.function.Supplier;

import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public record TrackTypeEntry(TrackType trackType, Component displayName, Component shortName, boolean allowInverted, Supplier<BlockState> trackState) {

}
