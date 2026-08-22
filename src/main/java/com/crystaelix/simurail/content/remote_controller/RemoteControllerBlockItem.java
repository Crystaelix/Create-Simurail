package com.crystaelix.simurail.content.remote_controller;

import com.crystaelix.simurail.config.SimurailConfig;
import com.crystaelix.simurail.content.bogey.PhysicsBogeyBlockEntity;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.redstone.displayLink.ClickToLinkBlockItem.ClickToLinkData;

import dev.ryanhcode.sable.Sable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class RemoteControllerBlockItem extends BlockItem {

	public RemoteControllerBlockItem(RemoteControllerBlock block, Properties properties) {
		super(block, properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		BlockPos pos = context.getClickedPos();
		Player player = context.getPlayer();
		if(player == null) {
			return InteractionResult.FAIL;
		}
		ItemStack stack = context.getItemInHand();
		Level level = context.getLevel();
		if(player.isSecondaryUseActive() && stack.has(AllDataComponents.CLICK_TO_LINK_DATA)) {
			if(level.isClientSide()) {
				return InteractionResult.SUCCESS;
			}
			player.displayClientMessage(Component.translatable("block.simurail.remote_controller.clear"), true);
			stack.remove(AllDataComponents.CLICK_TO_LINK_DATA);
			return InteractionResult.SUCCESS;
		}
		ResourceLocation placedDim = level.dimension().location();
		if(!stack.has(AllDataComponents.CLICK_TO_LINK_DATA)) {
			if(level.getBlockEntity(pos) instanceof PhysicsBogeyBlockEntity) {
				if(level.isClientSide()) {
					return InteractionResult.SUCCESS;
				}
				player.displayClientMessage(Component.translatable("block.simurail.remote_controller.set"), true);
				stack.set(AllDataComponents.CLICK_TO_LINK_DATA, new ClickToLinkData(pos, placedDim));
				return InteractionResult.SUCCESS;
			}
			if(level.isClientSide()) {
				AllSoundEvents.DENY.playFrom(player);
			}
			player.displayClientMessage(Component.translatable("block.simurail.remote_controller.invalid").withColor(0xFF7171), true);
			return InteractionResult.FAIL;
		}
		BlockState state = level.getBlockState(pos);
		double range = SimurailConfig.server().blocks.remoteControllerRange.get();
		ClickToLinkData data = stack.get(AllDataComponents.CLICK_TO_LINK_DATA);
		BlockPos selectedPos = data.selectedPos();
		ResourceLocation selectedDim = data.selectedDim();
		BlockPos placedPos = pos.relative(context.getClickedFace(), state.canBeReplaced() ? 0 : 1);
		if(!selectedDim.equals(placedDim) || Sable.HELPER.distanceSquaredWithSubLevels(level, selectedPos.getCenter(), placedPos.getCenter()) > range * range) {
			player.displayClientMessage(Component.translatable("block.simurail.remote_controller.too_far").withColor(0xFF7171), true);
			return InteractionResult.FAIL;
		}
		InteractionResult useOn = super.useOn(context);
		if(level.isClientSide() || useOn == InteractionResult.FAIL) {
			return useOn;
		}
		ItemStack itemInHand = player.getItemInHand(context.getHand());
		if(!itemInHand.isEmpty()) {
			stack.remove(AllDataComponents.CLICK_TO_LINK_DATA);
		}
		player.displayClientMessage(Component.translatable("block.simurail.remote_controller.success").withColor(0x9EDE73), true);
		return useOn;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);
		if(player.isSecondaryUseActive() && stack.has(AllDataComponents.CLICK_TO_LINK_DATA)) {
			if(level.isClientSide()) {
				return InteractionResultHolder.success(stack);
			}
			player.displayClientMessage(Component.translatable("block.simurail.remote_controller.clear"), true);
			stack.remove(AllDataComponents.CLICK_TO_LINK_DATA);
			return InteractionResultHolder.success(stack);
		}
		return super.use(level, player, usedHand);
	}

	@Override
	protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
		ClickToLinkData data = context.getItemInHand().get(AllDataComponents.CLICK_TO_LINK_DATA);
		if(data != null) {
			boolean result = super.placeBlock(context, state);
			Level level = context.getLevel();
			BlockPos pos = context.getClickedPos();
			if(level.getBlockEntity(pos) instanceof RemoteControllerBlockEntity controller) {
				BlockPos selectedPos = data.selectedPos();
				controller.setTargetPos(selectedPos);
			}
			return result;
		}
		return super.placeBlock(context, state);
	}
}
