package com.crystaelix.simurail.content.bogey;

import java.util.Map;

import com.crystaelix.simurail.content.SimurailBlocks;
import com.simibubi.create.AllBogeyStyles;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class InvertedPhysicsBogeyBlockItem extends PhysicsBogeyBlockItem {

	private Boolean canPlace;

	public InvertedPhysicsBogeyBlockItem(Properties properties) {
		super(SimurailBlocks.PHYSICS_BOGEY.get(), properties);
	}

	@Override
	public String getDescriptionId() {
		return "item.simurail.inverted_physics_bogey";
	}

	@Override
	public void registerBlocks(Map<Block, Item> blockToItemMap, Item item) {}

	@Override
	protected BlockState getPlacementState(BlockPlaceContext context) {
		if(canPlace == null) {
			canPlace = AllBogeyStyles.BOGEY_STYLES.values().stream().
					flatMap(s -> s.validSizes().stream().map(s::getBlockForSize)).
					anyMatch(AbstractBogeyBlock::canBeUpsideDown);
		}
		if(!canPlace) {
			return null;
		}
		BlockState state = super.getPlacementState(context);
		return state == null ? null : state.setValue(PhysicsBogeyBlock.INVERTED, true);
	}
}
