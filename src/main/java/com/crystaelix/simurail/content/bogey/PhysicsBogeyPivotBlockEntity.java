package com.crystaelix.simurail.content.bogey;

import java.util.List;
import java.util.UUID;

import com.crystaelix.simurail.api.physics.AuxiliaryPhysicsObjectAnchor;
import com.crystaelix.simurail.api.util.SchematicContextUtil;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PhysicsBogeyPivotBlockEntity extends SmartBlockEntity implements AuxiliaryPhysicsObjectAnchor, BlockEntitySubLevelActor {

	protected BlockPos parent;
	protected UUID parentID;

	public PhysicsBogeyPivotBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	@Override
	public void setParent(BlockPos parent) {
		this.parent = parent;
		SubLevel parentSubLevel = Sable.HELPER.getContaining(level, parent);
		parentID = parentSubLevel == null ? null : parentSubLevel.getUniqueId();
	}

	@Override
	public void lazyTick() {
		if(parent == null || !(level.getBlockEntity(parent) instanceof PhysicsBogeyBlockEntity)) {
			level.removeBlock(getBlockPos(), false);
		}
	}

	@Override
	public Iterable<SubLevel> sable$getConnectionDependencies() {
		SubLevelContainer container = SubLevelContainer.getContainer(level);
		SubLevel parentSubLevel = container.getSubLevel(parentID);
		return parentSubLevel == null ? List.of() : List.of(parentSubLevel);
	}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		Pair<BlockPos, UUID> p = SchematicContextUtil.writeTransform(parent, parentID);
		if(p.getFirst() != null) {
			tag.put("parent", NbtUtils.writeBlockPos(p.getFirst()));
			if(p.getSecond() != null) {
				tag.putUUID("parent_id", p.getSecond());
			}
		}
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		Pair<BlockPos, UUID> p = SchematicContextUtil.readTransform(
				NbtUtils.readBlockPos(tag, "parent").orElse(null),
				tag.hasUUID("parent_id") ? tag.getUUID("parent_id") : null);
		parent = p.getFirst();
		parentID = p.getSecond();
	}
}
