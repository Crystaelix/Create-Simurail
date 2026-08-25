package com.crystaelix.simurail.content.remote_controller;

import java.util.List;
import java.util.UUID;

import com.crystaelix.simurail.api.util.SchematicContextUtil;
import com.crystaelix.simurail.compat.SimurailCompat;
import com.crystaelix.simurail.compat.computercraft.SimurailComputerCraftProxy;
import com.crystaelix.simurail.config.SimurailConfig;
import com.crystaelix.simurail.content.SimurailBlockEntities;
import com.crystaelix.simurail.content.bogey.PhysicsBogeyBlockEntity;
import com.crystaelix.simurail.content.bogey.PhysicsBogeyOptions;
import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import dan200.computercraft.api.peripheral.PeripheralCapability;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class RemoteControllerBlockEntity extends SmartBlockEntity implements MenuProvider {

	public static final Component NAME = Component.translatable("block.simurail.remote_controller");

	protected AbstractComputerBehaviour computerBehaviour;

	private BlockPos targetPos;
	private UUID targetSubLevelID;

	private RemoteControllerMode mode = RemoteControllerMode.BRAKING;
	private int power;

	public RemoteControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		SimurailCompat.COMPUTERCRAFT.ifLoaded(() -> () -> {
			event.registerBlockEntity(
					PeripheralCapability.get(),
					SimurailBlockEntities.REMOTE_CONTROLLER.get(),
					(be, context) -> be.computerBehaviour.getPeripheralCapability());
		});
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		behaviours.add(computerBehaviour = SimurailComputerCraftProxy.behaviour(this));
	}

	@Override
	public Component getDisplayName() {
		return NAME;
	}

	@Override
	public void tick() {
		super.tick();
		if(level.isClientSide()) {
			return;
		}
		updateBogey();
	}

	protected void updateBogey() {
		if(targetPos == null) {
			return;
		}
		BlockPos pos = getBlockPos();
		double range = SimurailConfig.server().blocks.remoteControllerRange.get();
		double distSq = Sable.HELPER.distanceSquaredWithSubLevels(level, pos.getCenter(), getTargetPos().getCenter());
		if(level.getBlockEntity(targetPos) instanceof PhysicsBogeyBlockEntity bogey) {
			if(distSq > range * range) {
				bogey.removeRemoteController(pos);
				return;
			}
			bogey.addRemoteController(pos);
			switch(mode) {
			case BRAKING -> bogey.setRemoteBrakeOverride(pos, power);
			case BRAKING_INVERTED -> bogey.setRemoteBrakeOverride(pos, 15 - power);
			case STEERING_LEFT -> bogey.setRemoteLeftSteerOverride(pos, power);
			case STEERING_RIGHT -> bogey.setRemoteRightSteerOverride(pos, power);
			case null, default -> {}
			}
		}
	}

	protected void updateRisingEdge() {
		if(targetPos == null) {
			return;
		}
		BlockPos pos = getBlockPos();
		double range = SimurailConfig.server().blocks.remoteControllerRange.get();
		double distSq = Sable.HELPER.distanceSquaredWithSubLevels(level, pos.getCenter(), getTargetPos().getCenter());
		if(level.isLoaded(targetPos) && level.getBlockEntity(targetPos) instanceof PhysicsBogeyBlockEntity bogey) {
			if(distSq > range * range) {
				return;
			}
			switch(mode) {
			case VERTICAL_MOVEMENT -> {
				PhysicsBogeyOptions options = bogey.getOptions();
				options.allowVerticalMovement = !options.allowVerticalMovement;
				bogey.setChanged();
			}
			case ENABLED -> {
				PhysicsBogeyOptions options = bogey.getOptions();
				options.enabled = !options.enabled;
				bogey.setChanged();
			}
			case null, default -> {}
			}
		}
	}

	public void updateSelfAndAttached(BlockState state) {
		Direction attachedFace = state.getValue(BlockStateProperties.FACING).getOpposite();
		BlockPos attachedPos = getBlockPos().relative(attachedFace);
		level.blockUpdated(getBlockPos(), level.getBlockState(getBlockPos()).getBlock());
		level.blockUpdated(attachedPos, level.getBlockState(attachedPos).getBlock());
	}

	public BlockPos getTargetPos() {
		return targetPos;
	}

	public void setTargetPos(BlockPos targetPos) {
		this.targetPos = targetPos;
		if(targetPos != null) {
			SubLevel targetSubLevel = Sable.HELPER.getContaining(level, targetPos);
			targetSubLevelID = targetSubLevel == null ? null : targetSubLevel.getUniqueId();
		}
		else {
			targetSubLevelID = null;
		}
		setChanged();
		sendData();
	}

	public RemoteControllerMode getMode() {
		return mode;
	}

	public void setMode(RemoteControllerMode mode) {
		this.mode = mode;
		setChanged();
	}

	public void setPower(int power) {
		boolean wasPowered = this.power > 0;
		this.power = power;
		if(power > 0 && power > 0 != wasPowered) {

		}
		setChanged();
	}

	@Override
	public RemoteControllerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new RemoteControllerMenu(windowId, this);
	}

	@Override
	public void invalidate() {
		super.invalidate();
		computerBehaviour.removePeripheral();
		if(!level.isClientSide() && targetPos != null && level.isLoaded(targetPos) && level.getBlockEntity(targetPos) instanceof PhysicsBogeyBlockEntity bogey) {
			bogey.removeRemoteController(getBlockPos());
		}
	}

	@Override
	public void remove() {
		super.remove();
		updateSelfAndAttached(getBlockState());
	}

	@Override
	protected void write(CompoundTag tag, Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		tag.putByte("mode", (byte)mode.ordinal());
		if(targetPos != null) {
			Pair<BlockPos, UUID> target = SchematicContextUtil.writeTransform(targetPos, targetSubLevelID);
			if(target.getFirst() != null) {
				tag.put("target_pos", NbtUtils.writeBlockPos(target.getFirst()));
				if(target.getSecond() != null) {
					tag.putUUID("target_id", target.getSecond());
				}
			}
		}
		tag.putInt("power", power);
	}

	@Override
	public void writeSafe(CompoundTag tag, Provider registries) {
		super.writeSafe(tag, registries);
		tag.putByte("mode", (byte)mode.ordinal());
	}

	@Override
	protected void read(CompoundTag tag, Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		mode = RemoteControllerMode.BY_ID.apply(tag.getByte("mode"));
		Pair<BlockPos, UUID> target = SchematicContextUtil.readTransform(
				NbtUtils.readBlockPos(tag, "target_pos").orElse(null),
				tag.hasUUID("target_id") ? tag.getUUID("target_id") : null);
		targetPos = target.getFirst();
		targetSubLevelID = target.getSecond();
		power = tag.getInt("power");
	}
}
