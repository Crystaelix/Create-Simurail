package com.crystaelix.simurail.content.track_switch;

import com.crystaelix.simurail.content.SimurailEdgePoints;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.content.trains.track.TrackBlockOutline.BezierPointSelection;
import com.simibubi.create.content.trains.track.TrackTargetingBlockItem;
import com.simibubi.create.foundation.utility.CreateLang;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class TrackSwitchBlockItem extends TrackTargetingBlockItem {

	public TrackSwitchBlockItem(Block block, Properties properties) {
		super(block, properties, SimurailEdgePoints.TRACK_SWITCH);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);
		if(player.isSecondaryUseActive() && stack.has(AllDataComponents.TRACK_TARGETING_ITEM_SELECTED_POS)) {
			if(!level.isClientSide()) {
				stack.remove(AllDataComponents.TRACK_TARGETING_ITEM_SELECTED_POS);
				stack.remove(AllDataComponents.TRACK_TARGETING_ITEM_SELECTED_DIRECTION);
				stack.remove(AllDataComponents.TRACK_TARGETING_ITEM_BEZIER);
				AllSoundEvents.CONTROLLER_CLICK.play(level, null, player.position(), 1, 0.5F);
				player.displayClientMessage(CreateLang.translateDirect("track_target.clear"), true);
			}
			return InteractionResultHolder.success(stack);
		}
		return super.use(level, player, usedHand);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean useOnCurve(BezierPointSelection selection, ItemStack stack) {
		BezierTrackPointLocation loc = selection.loc();
		BezierConnection curve = selection.blockEntity().getConnections().get(loc.curveTarget());
		Player player = Minecraft.getInstance().player;
		boolean front = player.getLookAngle().dot(selection.direction()) < 0;
		return (front ? loc.segment() < 3 : loc.segment() > curve.getSegmentCount() - 4) && super.useOnCurve(selection, stack);
	}
}
