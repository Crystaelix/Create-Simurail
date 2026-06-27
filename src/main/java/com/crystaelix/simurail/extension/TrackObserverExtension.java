package com.crystaelix.simurail.extension;

import com.crystaelix.simurail.content.bogey.PhysicsBogeyBlockEntity;

import net.minecraft.core.BlockPos;

public interface TrackObserverExtension {

	void simurail$keepAlive(PhysicsBogeyBlockEntity bogey);
	
	BlockPos simurail$getCurrentBogey();
}
