package com.crystaelix.simurail.content.bogey;

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

public class PhysicsBogeyMenu extends AbstractContainerMenu {

	protected final ResourceKey<Level> dimension;
	protected final BlockPos pos;
	protected final boolean unpowered;
	protected final boolean inverted;
	protected final boolean isCustomName;
	protected final boolean hasComputer;
	protected final PhysicsBogeyOptions options;
	protected final boolean secondary;

	public PhysicsBogeyMenu(MenuType<PhysicsBogeyMenu> type, int windowId, Inventory inv, RegistryFriendlyByteBuf extraData) {
		super(type, windowId);
		dimension = ResourceKey.streamCodec(Registries.DIMENSION).decode(extraData);
		pos = extraData.readBlockPos();
		unpowered = extraData.readBoolean();
		inverted = extraData.readBoolean();
		isCustomName = extraData.readBoolean();
		hasComputer = extraData.readBoolean();
		options = PhysicsBogeyOptions.STREAM_CODEC.decode(extraData);
		secondary = extraData.readBoolean();
	}

	public PhysicsBogeyMenu(int windowId, PhysicsBogeyBlockEntity be) {
		super(SimurailMenus.PHYSICS_BOGEY.get(), windowId);
		dimension = be.getLevel().dimension();
		pos = be.getBlockPos();
		unpowered = be.isUnpowered();
		inverted = be.isInverted();
		isCustomName = be.hasCustomName();
		hasComputer = be.computerBehaviour.hasAttachedComputer();
		options = be.options;
		secondary = false;
	}

	public static void prepare(RegistryFriendlyByteBuf extraData, PhysicsBogeyBlockEntity be, boolean secondary) {
		ResourceKey.streamCodec(Registries.DIMENSION).encode(extraData, be.getLevel().dimension());
		extraData.writeBlockPos(be.getBlockPos());
		extraData.writeBoolean(be.isUnpowered());
		extraData.writeBoolean(be.isInverted());
		extraData.writeBoolean(be.hasCustomName());
		extraData.writeBoolean(be.computerBehaviour.hasAttachedComputer());
		PhysicsBogeyOptions.STREAM_CODEC.encode(extraData, be.getOptions());
		extraData.writeBoolean(secondary);
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
