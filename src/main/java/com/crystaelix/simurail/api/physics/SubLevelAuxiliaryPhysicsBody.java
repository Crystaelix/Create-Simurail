package com.crystaelix.simurail.api.physics;

import java.util.UUID;

import org.joml.Vector3d;
import org.joml.Vector3dc;

import com.crystaelix.simurail.api.math.SimurailMath;
import com.crystaelix.simurail.api.util.SchematicContextUtil;
import com.crystaelix.simurail.content.bogey.PhysicsBogeyPivotBlockEntity;

import dev.ryanhcode.sable.api.physics.PhysicsPipelineBody;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SubLevelAuxiliaryPhysicsBody implements AuxiliaryPhysicsBody {

	private final ServerLevel level;
	private final BlockPos parent;
	private final BlockState state;

	private final Pose3d pose = new Pose3d();
	private ServerSubLevel subLevel;
	private BlockPos anchorPos;

	public SubLevelAuxiliaryPhysicsBody(ServerLevel level, BlockPos parent, BlockState state) {
		this.level = level;
		this.parent = parent;
		this.state = state;
	}

	@Override
	public boolean isRemoved() {
		return subLevel == null || subLevel.isRemoved() ||
				anchorPos == null || !(level.getBlockEntity(anchorPos) instanceof AuxiliaryPhysicsObjectAnchor);
	}

	@Override
	public void create(Pose3dc initialPose) {
		if(isRemoved()) {
			pose.set(initialPose);
			SubLevelContainer container = SubLevelContainer.getContainer(level);
			subLevel = (ServerSubLevel)container.allocateNewSubLevel(pose);
			LevelPlot plot = subLevel.getPlot();
			plot.newEmptyChunk(plot.getCenterChunk());
			anchorPos = plot.getCenterBlock();
			level.setBlock(anchorPos, state, Block.UPDATE_ALL_IMMEDIATE);
			subLevel.updateLastPose();
			if(level.getBlockEntity(anchorPos) instanceof AuxiliaryPhysicsObjectAnchor anchor) {
				anchor.setParent(parent);
			}
		}
	}

	@Override
	public void remove() {
	}

	@Override
	public void afterParentMove() {
		if(!isRemoved() && level.getBlockEntity(anchorPos) instanceof PhysicsBogeyPivotBlockEntity pivot) {
			pivot.setParent(parent);
		}
		else {
			subLevel = null;
			anchorPos = null;
		}
	}

	@Override
	public boolean scaleEquals(Vector3dc scale) {
		return !isRemoved() && subLevel.logicalPose().scale().equals(scale, SimurailMath.EPSILON);
	}

	@Override
	public Pose3dc pose() {
		if(!isRemoved()) {
			pose.set(subLevel.logicalPose());
			pose.rotationPoint().sub(JOMLConversion.atCenterOf(anchorPos));
		}
		return pose;
	}

	@Override
	public Vector3d position(Vector3dc offset) {
		return JOMLConversion.atCenterOf(anchorPos).fma(1, offset);
	}

	@Override
	public PhysicsPipelineBody physicsBody() {
		return subLevel;
	}

	@Override
	public CompoundTag write() {
		CompoundTag tag = new CompoundTag();
		if(!isRemoved()) {
			Pair<BlockPos, UUID> anchor = SchematicContextUtil.writeTransform(anchorPos, subLevel.getUniqueId());
			if(anchor.getFirst() != null && anchor.getSecond() != null) {
				tag.putUUID("sublevel_id", anchor.getSecond());
				tag.put("anchor_pos", NbtUtils.writeBlockPos(anchor.getFirst()));
			}
		}
		return tag;
	}

	@Override
	public void read(CompoundTag tag) {
		Pair<BlockPos, UUID> anchor = SchematicContextUtil.readTransform(
				NbtUtils.readBlockPos(tag, "anchor_pos").orElse(null),
				tag.hasUUID("sublevel_id") ? tag.getUUID("sublevel_id") : null);
		subLevel = (ServerSubLevel)SubLevelContainer.getContainer(level).getSubLevel(anchor.getSecond());
		anchorPos = anchor.getFirst();
	}
}
