package com.crystaelix.simurail.content.gangway_frame.copycat;

import java.util.UUID;

import org.joml.Vector3d;

import com.crystaelix.simurail.api.math.SimurailMath;
import com.crystaelix.simurail.api.util.SchematicContextUtil;
import com.crystaelix.simurail.content.SimurailBlocks;
import com.crystaelix.simurail.content.SimurailSoundEvents;
import com.crystaelix.simurail.content.gangway_frame.GangwayFrame;
import com.crystaelix.simurail.content.gangway_frame.GangwayFrameBlockCollisions;
import com.crystaelix.simurail.content.gangway_frame.GangwayFrameBlockShape;
import com.simibubi.create.content.decoration.copycat.CopycatBlockEntity;
import com.simibubi.create.content.equipment.clipboard.ClipboardCloneable;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.createmod.catnip.data.Pair;
import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;

// Copycate version of GangwayFrameBlockEntity
public class CopycatGangwayFrameBlockEntity extends CopycatBlockEntity implements MenuProvider, GangwayFrame, ClipboardCloneable {

	protected BlockPos gangwayPartnerPos;
	protected UUID gangwayPartnerSubLevelID;

	public float restLength = 0;
	public int color = DyeColor.GRAY.getFireworkColor();
	protected boolean manualDisconnectDisabled = false;

	protected Pose3d endPose = new Pose3d();
	protected boolean hasPartner = false;
	public int timer = 0;
	protected final Iterable<BoundingBox3dc> collisionBoxes;

	public CopycatGangwayFrameBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		collisionBoxes = new GangwayFrameBlockCollisions(this::getGangwayShape, this::getFacing, () -> this.restLength, () -> this.hasPartner, endPose);
	}

	@Override
	public Component getDisplayName() {
		return SimurailBlocks.GANGWAY_FRAME.get().getName();
	}

	@Override
	public Direction getFacing() {
		return getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
	}

	@Override
	public GangwayFrameBlockShape getGangwayShape() {
		return getBlockState().getValue(CopycatGangwayFrameBlock.SHAPE);
	}

	@Override
	public Vector3d getGangwayAnchor(Vector3d dest) {
		BlockPos pos = getBlockPos();
		return getGangwayShape().anchor(getFacing(), dest).add(pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public boolean isPowered() {
		return getBlockState().getValue(BlockStateProperties.POWERED);
	}

	@Override
	public boolean isGangwayPowered() {
		return isPowered() || getNeighbors(level, 15).stream().anyMatch(GangwayFrame::isPowered);
	}

	@Override
	public void setGangwayPartner(BlockPos gangwayPartnerPos) {
		if(gangwayPartnerPos.equals(this.gangwayPartnerPos)) {
			return;
		}
		if(level.getBlockEntity(gangwayPartnerPos) instanceof GangwayFrame partner &&
				getGangwayShape().canConnectTo(partner.getGangwayShape())) {
			SubLevel partnerSubLevel = Sable.HELPER.getContaining(level, gangwayPartnerPos);
			if(this.gangwayPartnerPos != null) {
				removeGangwayPartner();
			}
			this.gangwayPartnerPos = gangwayPartnerPos;
			gangwayPartnerSubLevelID = partnerSubLevel == null ? null : partnerSubLevel.getUniqueId();
			partner.setGangwayPartnerReverse(getBlockPos());
			if(!level.isClientSide()) {
				setChanged();
				sendData();
				level.playSound(null, getBlockPos(), SimurailSoundEvents.GANGWAY_FRAME_CONNECT.get(), SoundSource.BLOCKS, 0.25F, 1F);
				level.playSound(null, gangwayPartnerPos, SimurailSoundEvents.GANGWAY_FRAME_CONNECT.get(), SoundSource.BLOCKS, 0.25F, 1F);
			}
		}
	}

	@Override
	public void setGangwayPartnerReverse(BlockPos gangwayPartnerPos) {
		this.gangwayPartnerPos = gangwayPartnerPos;
		if(gangwayPartnerPos != null) {
			SubLevel partnerSubLevel = Sable.HELPER.getContaining(level, gangwayPartnerPos);
			gangwayPartnerSubLevelID = partnerSubLevel == null ? null : partnerSubLevel.getUniqueId();
		}
		else {
			gangwayPartnerSubLevelID = null;
		}
		if(!level.isClientSide()) {
			setChanged();
			sendData();
		}
	}

	@Override
	public void removeGangwayPartner() {
		if(gangwayPartnerPos == null) {
			return;
		}
		if(gangwayPartnerPos != null && level.getBlockEntity(gangwayPartnerPos) instanceof GangwayFrame partner) {
			partner.setGangwayPartnerReverse(null);
			if(!level.isClientSide()) {
				level.playSound(null, gangwayPartnerPos, SimurailSoundEvents.GANGWAY_FRAME_DISCONNECT.get(), SoundSource.BLOCKS, 0.25F, 1F);
			}
		}
		gangwayPartnerPos = null;
		gangwayPartnerSubLevelID = null;
		if(!level.isClientSide()) {
			setChanged();
			sendData();
			level.playSound(null, getBlockPos(), SimurailSoundEvents.GANGWAY_FRAME_DISCONNECT.get(), SoundSource.BLOCKS, 0.25F, 1F);
		}
	}

	@Override
	public GangwayFrame getGangwayPartner() {
		if(gangwayPartnerPos != null && level.getBlockEntity(gangwayPartnerPos) instanceof GangwayFrame partner) {
			return partner;
		}
		return null;
	}

	@Override
	public Iterable<BoundingBox3dc> getGangwayCollisions() {
		return collisionBoxes;
	}

	public void setColor(int color) {
		this.color = color;
		if(!level.isClientSide()) {
			setChanged();
			sendData();
		}
	}

	public void afterMove() {
		if(gangwayPartnerPos != null) {
			setGangwayPartner(gangwayPartnerPos);
		}
	}

	@Override
	public void initialize() {
		super.initialize();
		hasPartner = getGangwayPartner() != null;
	}

	@Override
	public void tick() {
		super.tick();
		GangwayFrame partner = getGangwayPartner();
		GangwayFrameBlockShape shape = getGangwayShape();
		Direction facing = getFacing();
		boolean newHasPartner = partner != null || level instanceof PonderLevel && gangwayPartnerPos != null;

		if(newHasPartner != hasPartner) {
			hasPartner = newHasPartner;
			timer = 10 - timer;
		}

		BlockPos selfPos = getBlockPos();
		SubLevel selfSubLevel = Sable.HELPER.getContaining(this);
		Pose3dc selfPose = selfSubLevel == null ? SimurailMath.POSE_I : selfSubLevel.logicalPose();
		if(partner != null) {
			BlockPos partnerPos = partner.getBlockPos();
			SubLevel partnerSubLevel = Sable.HELPER.getContaining(level, partnerPos);
			Pose3dc partnerPose = partnerSubLevel == null ? SimurailMath.POSE_I : partnerSubLevel.logicalPose();

			partner.getGangwayAnchor(endPose.position());
			partnerPose.transformPosition(endPose.position());
			selfPose.transformPositionInverse(endPose.position());
			endPose.position().sub(selfPos.getX(), selfPos.getY(), selfPos.getZ());

			selfPose.orientation().conjugate(endPose.orientation());
			endPose.orientation().mul(partnerPose.orientation());
			endPose.orientation().mul(partner.getOrientation()).rotateY(Math.PI);

			shape.anchor(facing, centerOffset);
		}
		if(timer > 0) {
			timer--;
		}
	}

	@Override
	public void lazyTick() {
		if(!level.isClientSide()) {
			double maxDist = 6;
			if(gangwayPartnerPos != null) {
				if(level.getBlockEntity(gangwayPartnerPos) instanceof GangwayFrame partner) {
					SubLevel selfSubLevel = Sable.HELPER.getContaining(this);
					SubLevel otherSubLevel = Sable.HELPER.getContaining(level, gangwayPartnerPos);
					if(selfSubLevel != otherSubLevel && Sable.HELPER.distanceSquaredWithSubLevels(level, getBlockPos().getCenter(), gangwayPartnerPos.getCenter()) > Mth.square(maxDist)) {
						removeGangwayPartner();
					}
					else if(partner.getGangwayPartner() != this) {
						removeGangwayPartner();
					}
				}
				else {
					removeGangwayPartner();
				}
			}
		}
	}

	@Override
	protected AABB createRenderBoundingBox() {
		return super.createRenderBoundingBox().inflate(4);
	}

	@Override
	public CopycatGangwayFrameMenu createMenu(int windowId, Inventory inv, Player player) {
		return new CopycatGangwayFrameMenu(windowId, this);
	}

	@Override
	public void setBlockState(BlockState blockState) {
		GangwayFrameBlockShape oldShape = getGangwayShape();
		super.setBlockState(blockState);
		GangwayFrameBlockShape newShape = getGangwayShape();
		if(newShape != oldShape) {
			removeGangwayPartner();
		}
	}

	@Override
	public String getClipboardKey() {
		return "gangway_frame";
	}

	@Override
	public boolean writeToClipboard(HolderLookup.Provider registries, CompoundTag tag, Direction side) {
		tag.putBoolean("gangway", true);
		tag.putDouble("gangway_rest_length", restLength);
		tag.putInt("gangway_color", color);
		return true;
	}

	@Override
	public boolean readFromClipboard(HolderLookup.Provider registries, CompoundTag tag, Player player, Direction side, boolean simulate) {
		if(!tag.getBoolean("gangway")) {
			return false;
		}
		if(simulate) {
			return true;
		}
		if(tag.contains("gangway_rest_length")) {
			restLength = tag.getFloat("gangway_rest_length");
		}
		if(tag.contains("gangway_color")) {
			color = tag.getInt("gangway_color");
		}
		if(!level.isClientSide()) {
			setChanged();
			sendData();
		}
		return true;
	}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		tag.putFloat("rest_length", restLength);
		tag.putInt("color", color);

		Pair<BlockPos, UUID> gangwayPartner = SchematicContextUtil.writeTransform(gangwayPartnerPos, gangwayPartnerSubLevelID);

		if(gangwayPartner.getFirst() != null) {
			tag.put("gangway_partner", NbtUtils.writeBlockPos(gangwayPartner.getFirst()));
			if(gangwayPartner.getSecond() != null) {
				tag.putUUID("gangway_partner_id", gangwayPartner.getSecond());
			}
		}
	}

	@Override
	public void writeSafe(CompoundTag tag, HolderLookup.Provider registries) {
		super.writeSafe(tag, registries);
		tag.putDouble("rest_length", restLength);
		tag.putInt("color", color);
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);

		restLength = tag.getFloat("rest_length");
		if(tag.contains("color")) {
			color = tag.getInt("color");
		}

		Pair<BlockPos, UUID> gangwayPartner = SchematicContextUtil.readTransform(
				NbtUtils.readBlockPos(tag, "gangway_partner").orElse(null),
				tag.hasUUID("gangway_partner_id") ? tag.getUUID("gangway_partner_id") : null);

		gangwayPartnerPos = gangwayPartner.getFirst();
		gangwayPartnerSubLevelID = gangwayPartner.getSecond();
	}

	protected final Vector3d centerOffset = new Vector3d();
}
