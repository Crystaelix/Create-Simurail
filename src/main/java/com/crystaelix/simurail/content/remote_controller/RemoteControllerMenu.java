package com.crystaelix.simurail.content.remote_controller;

import com.crystaelix.simurail.content.SimurailMenus;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RemoteControllerMenu extends AbstractContainerMenu {

	protected final ResourceKey<Level> dimension;
	protected final BlockPos pos;
	protected final boolean hasComputer;
	protected final RemoteControllerMode mode;

	public RemoteControllerMenu(MenuType<RemoteControllerMenu> type, int windowId, Inventory inv, RegistryFriendlyByteBuf extraData) {
		super(type, windowId);
		dimension = ResourceKey.streamCodec(Registries.DIMENSION).decode(extraData);
		pos = extraData.readBlockPos();
		hasComputer = extraData.readBoolean();
		mode = RemoteControllerMode.STREAM_CODEC.decode(extraData);
	}

	public RemoteControllerMenu(int windowId, RemoteControllerBlockEntity be) {
		super(SimurailMenus.REMOTE_CONTROLLER.get(), windowId);
		dimension = be.getLevel().dimension();
		pos = be.getBlockPos();
		hasComputer = be.computerBehaviour.hasAttachedComputer();
		mode = be.getMode();
	}

	public static void prepare(RegistryFriendlyByteBuf extraData, RemoteControllerBlockEntity be) {
		ResourceKey.streamCodec(Registries.DIMENSION).encode(extraData, be.getLevel().dimension());
		extraData.writeBlockPos(be.getBlockPos());
		extraData.writeBoolean(be.computerBehaviour.hasAttachedComputer());
		RemoteControllerMode.STREAM_CODEC.encode(extraData, be.getMode());
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
