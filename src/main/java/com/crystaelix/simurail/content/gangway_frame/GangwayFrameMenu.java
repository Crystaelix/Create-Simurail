package com.crystaelix.simurail.content.gangway_frame;

import com.crystaelix.simurail.content.SimurailMenus;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class GangwayFrameMenu extends AbstractContainerMenu {

	protected final BlockPos pos;
	protected final GangwayFrameBlockShape shape;
	protected final float restLength;

	public GangwayFrameMenu(MenuType<GangwayFrameMenu> type, int windowId, Inventory inv, RegistryFriendlyByteBuf extraData) {
		super(type, windowId);
		pos = extraData.readBlockPos();
		shape = GangwayFrameBlockShape.STREAM_CODEC.decode(extraData);
		restLength = extraData.readFloat();
	}

	public GangwayFrameMenu(int windowId, GangwayFrameBlockEntity be) {
		super(SimurailMenus.GANGWAY_FRAME.get(), windowId);
		pos = be.getBlockPos();
		shape = be.getGangwayShape();
		restLength = be.restLength;
	}

	public static void prepare(RegistryFriendlyByteBuf extraData, GangwayFrameBlockEntity be) {
		extraData.writeBlockPos(be.getBlockPos());
		GangwayFrameBlockShape.STREAM_CODEC.encode(extraData, be.getGangwayShape());
		extraData.writeFloat(be.restLength);
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
