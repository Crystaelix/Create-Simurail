package com.crystaelix.simurail.content.probe_reader;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import com.crystaelix.simurail.compat.SimurailCompat;
import com.crystaelix.simurail.compat.computercraft.SimurailComputerCraftProxy;
import com.crystaelix.simurail.config.SimurailConfig;
import com.crystaelix.simurail.content.SimurailBlockEntities;
import com.crystaelix.simurail.content.bogey.PhysicsBogeyBlockEntity;
import com.crystaelix.simurail.content.bogey.PhysicsBogeyProbeData;
import com.google.common.base.Predicates;
import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import dan200.computercraft.api.peripheral.PeripheralCapability;
import dev.ryanhcode.sable.Sable;
import net.createmod.catnip.data.Glob;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class ProbeReaderBlockEntity extends SmartBlockEntity implements MenuProvider {

	public static final Component NAME = Component.translatable("block.simurail.probe_reader");

	protected final ProbeReaderOptions options = new ProbeReaderOptions();
	protected AbstractComputerBehaviour computerBehaviour;

	private BlockPos targetPos;
	protected boolean targetFront;

	protected String lastFilter = "";
	protected Predicate<String> matcher = Predicates.alwaysTrue();

	protected int signal;
	protected boolean signalChanged;

	public ProbeReaderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		SimurailCompat.COMPUTERCRAFT.ifLoaded(() -> () -> {
			event.registerBlockEntity(
					PeripheralCapability.get(),
					SimurailBlockEntities.PROBE_READER.get(),
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

	public ProbeReaderOptions getOptions() {
		return options;
	}

	public void setOptions(ProbeReaderOptions options) {
		this.options.set(options);
	}

	@Override
	public void tick() {
		super.tick();
		if(level.isClientSide()) {
			return;
		}
		if(!lastFilter.equals(options.getFilter())) {
			lastFilter = options.getFilter();
			matcher = lastFilter.isBlank() ? Predicates.alwaysTrue() : Pattern.compile(Glob.toRegexPattern(lastFilter, "")).asMatchPredicate();
		}
		int lastSignal = signal;
		signal = calculateSignal();
		signalChanged = signal != lastSignal;
		BlockState state = getBlockState();
		if((signal > 0) != state.getValue(BlockStateProperties.POWERED)) {
			level.setBlockAndUpdate(getBlockPos(), state.cycle(BlockStateProperties.POWERED));
		}
		if(signalChanged) {
			updateSelfAndAttached(state);
		}
	}

	protected int calculateSignal() {
		if(getTargetPos() == null) {
			return 0;
		}
		double range = SimurailConfig.server().blocks.probeReaderRange.get();
		double distSq = Sable.HELPER.distanceSquaredWithSubLevels(level, getBlockPos().getCenter(), getTargetPos().getCenter());
		if(distSq <= range * range && level.getBlockEntity(getTargetPos()) instanceof PhysicsBogeyBlockEntity bogey) {
			bogey.addProbeReader(getBlockPos());
			PhysicsBogeyProbeData probeData = bogey.getAxle(targetFront).getProbeData();
			switch(options.mode) {
			case OCCUPIED_SIGNAL -> {
				double distance = probeData.getOccupiedSignalDistance();
				if(distance >= 0) {
					if(lastFilter.isBlank() || probeData.getOccupiedSignalNames().stream().anyMatch(matcher)) {
						return distanceToSignal(distance);
					}
				}
			}
			case ALIGNED_SIGNAL, OPPOSITE_SIGNAL, SIGNAL -> {
				double distance = probeData.getSignalDistance();
				if(distance >= 0) {
					if(options.mode == ProbeReaderMode.SIGNAL ||
							options.mode == (probeData.isSignalAligned() ? ProbeReaderMode.ALIGNED_SIGNAL : ProbeReaderMode.OPPOSITE_SIGNAL)) {
						if(lastFilter.isBlank() || probeData.getSignalNames().stream().anyMatch(matcher)) {
							return distanceToSignal(distance);
						}
					}
				}
			}
			case STATION, POWERED_STATION -> {
				double distance = probeData.getStationDistance();
				if(distance >= 0) {
					if(options.mode == ProbeReaderMode.STATION || probeData.isStationPowered()) {
						if(matcher.test(probeData.getStationName())) {
							return distanceToSignal(distance);
						}
					}
				}
			}
			case DISCONTINUITY -> {
				double distance = probeData.getDiscontinuityDistance();
				if(distance >= 0) {
					return distanceToSignal(distance);
				}
			}
			case null, default -> {}
			}
		}
		return 0;
	}

	protected int distanceToSignal(double distance) {
		if(distance > options.getMaxDistance()) {
			return 0;
		}
		if(distance < options.getMinDistance()) {
			return 15;
		}
		if(options.getMinDistance() == options.getMaxDistance()) {
			return 15;
		}
		double strength = Math.clamp((options.getMaxDistance() - distance) / (options.getMaxDistance() - options.getMinDistance()), 0, 1);
		return Mth.ceil(strength * 15);
	}

	public int getSignal() {
		return signal;
	}

	public void updateSelfAndAttached(BlockState state) {
		Direction attachedFace = state.getValue(BlockStateProperties.FACING).getOpposite();
		BlockPos attachedPos = getBlockPos().relative(attachedFace);
		level.blockUpdated(getBlockPos(), level.getBlockState(getBlockPos()).getBlock());
		level.blockUpdated(attachedPos, level.getBlockState(attachedPos).getBlock());
		signalChanged = false;
	}

	public BlockPos getTargetPos() {
		return targetPos;
	}

	public void setTargetPos(BlockPos targetPos) {
		this.targetPos = targetPos;
	}

	public boolean getTargetFront() {
		return targetFront;
	}

	public void setTargetFront(boolean targetFront) {
		this.targetFront = targetFront;
	}

	@Override
	public ProbeReaderMenu createMenu(int windowId, Inventory inv, Player player) {
		return new ProbeReaderMenu(windowId, this);
	}

	@Override
	public void invalidate() {
		super.invalidate();
		computerBehaviour.removePeripheral();
		if(!level.isClientSide() && level.getBlockEntity(targetPos) instanceof PhysicsBogeyBlockEntity bogey) {
			bogey.removeProbeReader(getBlockPos());
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
		if(!clientPacket) {
			tag.put("options", options.write());
			if(getTargetPos() != null) {
				tag.put("target_pos", NbtUtils.writeBlockPos(getTargetPos()));
			}
			tag.putBoolean("target_front", targetFront);
		}
	}

	@Override
	public void writeSafe(CompoundTag tag, Provider registries) {
		super.writeSafe(tag, registries);
		tag.put("options", options.write());
	}

	@Override
	protected void read(CompoundTag tag, Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		if(!clientPacket) {
			options.read(tag.getCompound("options"));
			setTargetPos(NBTHelper.readBlockPos(tag, "target_pos"));
			targetFront = tag.getBoolean("target_front");
		}
	}
}
