package com.crystaelix.simurail.api.bogey;

import org.joml.Vector3f;

import net.minecraft.nbt.CompoundTag;

public interface ConnectorAnchorOffsetOverride {

	Vector3f apply(boolean inverted, CompoundTag extra, Vector3f dest);
}
