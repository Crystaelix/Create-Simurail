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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ProbeReaderSelectionOutline {

	private static BlockPos lastShownPos = null;
	private static Direction lastShownDir = null;
	private static AABB lastShownAABB = null;

	@OnlyIn(Dist.CLIENT)
	public static void clientTick() {
		Player player = Minecraft.getInstance().player;
		if(player == null) {
			return;
		}
		ItemStack stack = player.getMainHandItem();
		if(stack.has(SimurailDataComponents.BOGEY_LINK_DATA)) {
			BogeyLinkData data = stack.get(SimurailDataComponents.BOGEY_LINK_DATA);
			BlockPos selectedPos = data.position();
			Direction selectedDir = data.direction();
			if(!selectedPos.equals(lastShownPos) || selectedDir != lastShownDir) {
				lastShownAABB = data.getOutline();
				lastShownPos = selectedPos;
				lastShownDir = selectedDir;
			}
			Outliner.getInstance().showAABB("target", lastShownAABB).colored(SimColors.SUCCESS_LIME).lineWidth(0.0625F);
		}
		else if(stack.is(SimurailBlocks.PROBE_READER.asItem())) {
			Level level = Minecraft.getInstance().level;
			HitResult clientHit = Minecraft.getInstance().hitResult;
			if(clientHit != null && clientHit.getType() != HitResult.Type.MISS && clientHit instanceof BlockHitResult hit) {
				BlockPos pos = hit.getBlockPos();
				BlockEntity be = level.getBlockEntity(pos);
				if(be instanceof PhysicsBogeyBlockEntity bogey) {
					Direction dir = ProbeReaderBlockItem.getDirection(bogey, hit.getLocation());
					if(!pos.equals(lastShownPos) || dir != lastShownDir) {
						BogeyLinkData data = new BogeyLinkData(pos, dir, level.dimension().location());
						lastShownAABB = data.getOutline();
						lastShownPos = pos;
						lastShownDir = dir;
					}
					Outliner.getInstance().showAABB("target", lastShownAABB).colored(SimColors.ACTIVE_YELLOW).lineWidth(0.0625F);
				}
			}
		}
	}
}
