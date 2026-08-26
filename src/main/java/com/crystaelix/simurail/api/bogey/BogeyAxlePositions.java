package com.crystaelix.simurail.api.bogey;

import net.minecraft.nbt.CompoundTag;

@FunctionalInterface
public interface BogeyAxlePositions {

	double[] get(CompoundTag extra);
}
