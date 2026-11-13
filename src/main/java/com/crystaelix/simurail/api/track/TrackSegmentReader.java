package com.crystaelix.simurail.api.track;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public interface TrackSegmentReader<T extends TrackSegment> {

	T read(ResourceKey<Level> dimension, CompoundTag tag);
}
