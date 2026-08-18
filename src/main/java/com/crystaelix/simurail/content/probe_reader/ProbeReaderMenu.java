package com.crystaelix.simurail.content.probe_reader;

import com.crystaelix.simurail.content.SimurailMenus;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class ProbeReaderMenu extends AbstractContainerMenu {

	protected final BlockPos pos;
	protected final boolean hasComputer;
	protected final ProbeReaderOptions options;

	public ProbeReaderMenu(MenuType<ProbeReaderMenu> type, int windowId, Inventory inv, RegistryFriendlyByteBuf extraData) {
		super(type, windowId);
		pos = extraData.readBlockPos();
		hasComputer = extraData.readBoolean();
		options = ProbeReaderOptions.STREAM_CODEC.decode(extraData);
	}

	public ProbeReaderMenu(int windowId, ProbeReaderBlockEntity be) {
		super(SimurailMenus.PROBE_READER.get(), windowId);
		pos = be.getBlockPos();
		hasComputer = be.computerBehaviour.hasAttachedComputer();
		options = be.options;
	}

	public static void prepare(RegistryFriendlyByteBuf extraData, ProbeReaderBlockEntity be) {
		extraData.writeBlockPos(be.getBlockPos());
		extraData.writeBoolean(be.computerBehaviour.hasAttachedComputer());
		ProbeReaderOptions.STREAM_CODEC.encode(extraData, be.getOptions());
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

}
