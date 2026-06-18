package com.crystaelix.simurail.content.gangway_joint;

import com.crystaelix.simurail.content.steering_connector.SteeringConnectable;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class GangwayJointBlockEntity extends SmartBlockEntity implements BlockEntitySubLevelActor, SteeringConnectable {

	public GangwayJointBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
}
