package com.crystaelix.simurail.content.gangway_frame.copycat;

import com.crystaelix.simurail.content.SimurailMenus;
import com.crystaelix.simurail.content.gangway_frame.GangwayFrameBlockShape;

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

public class CopycatGangwayFrameMenu extends AbstractContainerMenu {

	protected final ResourceKey<Level> dimension;
	protected final BlockPos pos;
	protected final GangwayFrameBlockShape shape;
	protected final float restLength;

	public CopycatGangwayFrameMenu(MenuType<CopycatGangwayFrameMenu> type, int windowId, Inventory inv, RegistryFriendlyByteBuf extraData) {
		super(type, windowId);
		dimension = ResourceKey.streamCodec(Registries.DIMENSION).decode(extraData);
		pos = extraData.readBlockPos();
		shape = GangwayFrameBlockShape.STREAM_CODEC.decode(extraData);
		restLength = extraData.readFloat();
	}

	public CopycatGangwayFrameMenu(int windowId, CopycatGangwayFrameBlockEntity be) {
		super(SimurailMenus.COPYCAT_GANGWAY_FRAME.get(), windowId);
		dimension = be.getLevel().dimension();
		pos = be.getBlockPos();
		shape = be.getGangwayShape();
		restLength = be.restLength;
	}

	public static void prepare(RegistryFriendlyByteBuf extraData, CopycatGangwayFrameBlockEntity be) {
		ResourceKey.streamCodec(Registries.DIMENSION).encode(extraData, be.getLevel().dimension());
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
