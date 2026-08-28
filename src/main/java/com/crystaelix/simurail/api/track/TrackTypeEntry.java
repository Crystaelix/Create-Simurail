package com.crystaelix.simurail.api.track;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public record TrackTypeEntry(
		TrackType trackType,
		Component displayName,
		Component shortName,
		boolean allowInverted,
		double width,
		double height,
		DoubleSupplier lateralMaxSpeedFactor,
		DoubleSupplier verticalMaxSpeedFactor,
		DoubleSupplier adhesionFactor,
		Supplier<BlockState> trackState) {
}
