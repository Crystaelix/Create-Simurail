package com.crystaelix.simurail.content.automatic_coupler.copycat;

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

public class CopycatAutomaticCouplerMenu extends AbstractContainerMenu {

	protected final ResourceKey<Level> dimension;
	protected final BlockPos pos;
	//protected final boolean isShort;
	//protected final CouplerType couplerType;
	protected final float gangwayRestLength;
	protected final boolean gangway;

	public CopycatAutomaticCouplerMenu(MenuType<CopycatAutomaticCouplerMenu> type, int windowId, Inventory inv, RegistryFriendlyByteBuf extraData) {
		super(type, windowId);
		dimension = ResourceKey.streamCodec(Registries.DIMENSION).decode(extraData);
		pos = extraData.readBlockPos();
		//isShort = extraData.readBoolean();
		//couplerType = CouplerType.STREAM_CODEC.decode(extraData);
		gangwayRestLength = extraData.readFloat();
		gangway = extraData.readBoolean();
	}

	public CopycatAutomaticCouplerMenu(int windowId, CopycatAutomaticCouplerBlockEntity be) {
		super(SimurailMenus.COPYCAT_AUTOMATIC_COUPLER.get(), windowId);
		dimension = be.getLevel().dimension();
		pos = be.getBlockPos();
		//isShort = be.isShort;
		//couplerType = be.type;
		gangwayRestLength = be.gangwayRestLength;
		gangway = false;
	}

	public static void prepare(RegistryFriendlyByteBuf extraData, CopycatAutomaticCouplerBlockEntity be, boolean gangway) {
		ResourceKey.streamCodec(Registries.DIMENSION).encode(extraData, be.getLevel().dimension());
		extraData.writeBlockPos(be.getBlockPos());
		//extraData.writeBoolean(be.isShort);
		//CouplerType.STREAM_CODEC.encode(extraData, be.type);
		extraData.writeFloat(be.gangwayRestLength);
		extraData.writeBoolean(gangway);
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
