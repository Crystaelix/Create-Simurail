package com.crystaelix.simurail.content.probe_reader;

import com.crystaelix.simurail.api.bogey.BogeyLinkData;
import com.crystaelix.simurail.content.SimurailBlocks;
import com.crystaelix.simurail.content.SimurailDataComponents;
import com.crystaelix.simurail.content.bogey.PhysicsBogeyBlockEntity;

import dev.simulated_team.simulated.util.SimColors;
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

public class ProbeReaderOutline {

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

	public static void setTargetOutline(Level level, BlockPos sourcePos, BlockPos targetPos, boolean targetFront) {
		sourceVec = sourcePos.getCenter();
		if(level.getBlockEntity(targetPos) instanceof PhysicsBogeyBlockEntity bogey) {
			Direction dir = bogey.getFacing();
			if(!targetFront) {
				dir = dir.getOpposite();
			}
			targetAABB = AABB.ofSize(
					targetPos.getCenter().add(dir.getStepX() * 0.28125, 0, dir.getStepZ() * 0.28125),
					dir.getStepX() == 0 ? 1 : 0.4375, 1, dir.getStepZ() == 0 ? 1 : 0.4375);
			targetVec = targetPos.getCenter().add(dir.getStepX() * 0.5, 0, dir.getStepZ() * 0.5);
		}
		else {
			targetAABB = new AABB(targetPos);
			targetVec = targetPos.getCenter();
		}
		targetTicks = 200;
	}

	private static void showSelectionOutline(Player player) {
		ItemStack stack = player.getMainHandItem();
		if(stack.has(SimurailDataComponents.BOGEY_LINK_DATA)) {
			BogeyLinkData data = stack.get(SimurailDataComponents.BOGEY_LINK_DATA);
			BlockPos selectedPos = data.position();
			Direction selectedDir = data.direction();
			if(!selectedPos.equals(lastShownSelectionPos) || selectedDir != lastShownSelectionDir) {
				lastShownSelectionAABB = data.getOutline();
				lastShownSelectionPos = selectedPos;
				lastShownSelectionDir = selectedDir;
			}
			Outliner.getInstance().showAABB("simurail.probe_reader.selection", lastShownSelectionAABB).colored(0x9EDE73).lineWidth(0.0625F);
		}
		else if(stack.is(SimurailBlocks.PROBE_READER.asItem())) {
			Level level = Minecraft.getInstance().level;
			HitResult clientHit = Minecraft.getInstance().hitResult;
			if(clientHit != null && clientHit.getType() != HitResult.Type.MISS && clientHit instanceof BlockHitResult hit) {
				BlockPos pos = hit.getBlockPos();
				BlockEntity be = level.getBlockEntity(pos);
				if(be instanceof PhysicsBogeyBlockEntity bogey) {
					Direction dir = ProbeReaderBlockItem.getDirection(bogey, hit.getLocation());
					if(!pos.equals(lastShownSelectionPos) || dir != lastShownSelectionDir) {
						BogeyLinkData data = new BogeyLinkData(pos, dir, level.dimension().location());
						lastShownSelectionAABB = data.getOutline();
						lastShownSelectionPos = pos;
						lastShownSelectionDir = dir;
					}
					Outliner.getInstance().showAABB("simurail.probe_reader.selection", lastShownSelectionAABB).colored(0xFFEB85).lineWidth(0.0625F);
				}
			}
		}
	}

	private static void showTargetOutline() {
		if(targetTicks > 0) {
			Outliner.getInstance().showLine("simurail.probe_reader.pointer", sourceVec, targetVec).colored(0xCD0000).lineWidth(0.0625F);
			Outliner.getInstance().showAABB("simurail.probe_reader.target", targetAABB).colored(0xCD0000).lineWidth(0.0625F);
			--targetTicks;
		}
	}
}
