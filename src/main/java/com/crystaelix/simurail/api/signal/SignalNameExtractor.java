package com.crystaelix.simurail.api.signal;

import java.util.stream.Stream;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface SignalNameExtractor {

	Stream<String> getSignalNames(BlockState state, BlockGetter level, BlockPos pos, Direction direction);
}
