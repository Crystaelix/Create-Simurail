package com.crystaelix.simurail.content.automatic_coupler.copycat;

import com.crystaelix.simurail.content.gangway_frame.GangwayFrame;
import com.crystaelix.simurail.content.gangway_frame.GangwayFrameBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CopycatAutomaticCouplerBlockItem extends BlockItem {

	public CopycatAutomaticCouplerBlockItem(CopycatAutomaticCouplerBlock block, Properties properties) {
		super(block, properties);
	}

	@Override
	protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
		float restLength = 0;
		int color = DyeColor.GRAY.getFireworkColor();
		BlockPos gangwayPartnerPos = null;
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		if(level.getBlockEntity(pos) instanceof GangwayFrameBlockEntity frame) {
			restLength = frame.restLength;
			color = frame.color;
			GangwayFrame partner = frame.getGangwayPartner();
			if(partner != null) {
				gangwayPartnerPos = partner.getBlockPos();
			}
		}
		boolean result = super.placeBlock(context, state);
		if(level.getBlockEntity(pos) instanceof CopycatAutomaticCouplerBlockEntity coupler) {
			coupler.gangwayRestLength = restLength;
			coupler.gangwayColor = color;
			if(gangwayPartnerPos != null) {
				coupler.setGangwayPartnerReverse(gangwayPartnerPos);
			}
		}
		return result;
	}
}
