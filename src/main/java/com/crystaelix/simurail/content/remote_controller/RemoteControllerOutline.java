package com.crystaelix.simurail.content.remote_controller;

import com.crystaelix.simurail.content.SimurailBlocks;
import com.crystaelix.simurail.content.bogey.PhysicsBogeyBlockEntity;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.redstone.displayLink.ClickToLinkBlockItem.ClickToLinkData;

import net.createmod.catnip.outliner.Outliner;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class RemoteControllerOutline {

	private static BlockPos lastShownSelectionPos;
	private static Direction lastShownSelectionDir;
	private static AABB lastShownSelectionAABB;

	private static Vec3 sourceVec;
	private static Vec3 targetVec;
	private static AABB targetAABB;
	private static int targetTicks = 0;

	public static void clientTick() {
		Player player = Minecraft.getInstance().player;
		if(player == null) {
			return;
		}
		showSelectionOutline(player);
		showTargetOutline();
	}

	public static void setTargetOutline(Level level, BlockPos sourcePos, BlockPos targetPos) {
		sourceVec = sourcePos.getCenter();
		targetVec = targetPos.getCenter();
		targetAABB = new AABB(targetPos);
		targetTicks = 200;
	}

	private static void showSelectionOutline(Player player) {
		ItemStack stack = player.getMainHandItem();
		if(stack.has(AllDataComponents.CLICK_TO_LINK_DATA)) {
			ClickToLinkData data = stack.get(AllDataComponents.CLICK_TO_LINK_DATA);
			BlockPos selectedPos = data.selectedPos();
			if(!selectedPos.equals(lastShownSelectionPos)) {
				lastShownSelectionAABB = new AABB(selectedPos);
				lastShownSelectionPos = selectedPos;
			}
			Outliner.getInstance().showAABB("simurail.remote_controller.selection", lastShownSelectionAABB).colored(0x9EDE73).lineWidth(0.0625F);
		}
		else if(stack.is(SimurailBlocks.REMOTE_CONTROLLER.asItem())) {
			Level level = Minecraft.getInstance().level;
			HitResult clientHit = Minecraft.getInstance().hitResult;
			if(clientHit != null && clientHit.getType() != HitResult.Type.MISS && clientHit instanceof BlockHitResult hit) {
				BlockPos pos = hit.getBlockPos();
				BlockEntity be = level.getBlockEntity(pos);
				if(be instanceof PhysicsBogeyBlockEntity) {
					if(!pos.equals(lastShownSelectionPos)) {
						lastShownSelectionAABB = new AABB(pos);
						lastShownSelectionPos = pos;
					}
					Outliner.getInstance().showAABB("simurail.remote_controller.selection", lastShownSelectionAABB).colored(0xFFEB85).lineWidth(0.0625F);
				}
			}
		}
	}

	private static void showTargetOutline() {
		if(targetTicks > 0) {
			Outliner.getInstance().showLine("simurail.remote_controller.pointer", sourceVec, targetVec).colored(0xCD0000).lineWidth(0.0625F);
			Outliner.getInstance().showAABB("simurail.remote_controller.target", targetAABB).colored(0xCD0000).lineWidth(0.0625F);
			--targetTicks;
		}
	}
}
