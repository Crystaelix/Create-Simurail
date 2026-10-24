package com.crystaelix.simurail.content.automatic_coupler.copycat;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import com.crystaelix.simurail.content.SimurailBlocks;
import com.crystaelix.simurail.content.copycat.CopycatBarsModel;
import com.crystaelix.simurail.content.copycat.SimurailCopycatModel;
import com.simibubi.create.content.decoration.copycat.CopycatSpecialCases;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.data.ModelData;

public class CopycatPanelAutomaticCouplerModel extends SimurailCopycatModel {

	public CopycatPanelAutomaticCouplerModel(BakedModel originalModel) {
		super(originalModel);
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand) {
		return Stream.concat(
				originalModel.getQuads(state, side, rand).stream(),
				super.getQuads(state, side, rand).stream()).
				toList();
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData data, RenderType renderType) {
		return Stream.concat(
				originalModel.getQuads(state, side, rand, data, renderType).stream(),
				super.getQuads(state, side, rand, data, renderType).stream()).
				toList();
	}

	@Override
	protected List<BakedQuad> getCroppedQuads(BlockState state, Direction side, RandomSource rand, BlockState material, ModelData wrappedData, RenderType renderType) {
		if(CopycatSpecialCases.isBarsMaterial(material)) {
			BakedModel blockModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(
					SimurailBlocks.COPYCAT_BARS_AUTOMATIC_COUPLER.getDefaultState().
					setValue(BlockStateProperties.HORIZONTAL_FACING, state.getValue(BlockStateProperties.HORIZONTAL_FACING)).
					setValue(CopycatAutomaticCouplerBlock.SHAPE, state.getValue(CopycatAutomaticCouplerBlock.SHAPE)));
			if(blockModel instanceof CopycatBarsModel cm) {
				return cm.getCroppedQuads(state, side, rand, material, wrappedData, renderType);
			}
		}
		return super.getCroppedQuads(state, side, rand, material, wrappedData, renderType);
	}

	@Override
	protected List<CropTransform> getComponents(BlockState state) {
		Direction dir = state.getOptionalValue(BlockStateProperties.HORIZONTAL_FACING).orElse(Direction.NORTH);
		Direction ccw = dir.getCounterClockWise();
		Direction cw = dir.getClockWise();
		CopycatAutomaticCouplerShape shape = state.getOptionalValue(CopycatAutomaticCouplerBlock.SHAPE).orElse(CopycatAutomaticCouplerShape.FULL);
		List<CropTransform> list = new ArrayList<>(12);
		switch(shape) {
		case FULL -> {
			addPanelCropPair(
					list, dir,
					0, 0, 4, 16, 0, 0,
					EnumSet.of(ccw));
			addPanelCropPair(
					list, dir,
					12, 0, 16, 16, 0, 0,
					EnumSet.of(cw));
			addPanelCropPair(
					list, dir,
					4, 0, 12, 5, 0, 0,
					EnumSet.of(ccw, cw, Direction.UP));
			addPanelCropPair(
					list, dir,
					4, 11, 12, 16, 0, 0,
					EnumSet.of(ccw, cw, Direction.DOWN));
		}
		case TOP -> {
			addPanelCropPair(
					list, dir,
					0, 0, 4, 6, 0, 4,
					EnumSet.of(ccw, Direction.UP));
			addPanelCropPair(
					list, dir,
					12, 0, 16, 6, 0, 4,
					EnumSet.of(cw, Direction.UP));
			addPanelCropPair(
					list, dir,
					0, 10, 4, 16, 0, 0,
					EnumSet.of(ccw, Direction.DOWN));
			addPanelCropPair(
					list, dir,
					12, 10, 16, 16, 0, 0,
					EnumSet.of(cw, Direction.DOWN));
			addPanelCropPair(
					list, dir,
					4, 0, 12, 1, 0, 4,
					EnumSet.of(ccw, cw, Direction.UP));
			addPanelCropPair(
					list, dir,
					4, 11, 12, 16, 0, 0,
					EnumSet.of(ccw, cw, Direction.DOWN));
		}
		case BOTTOM -> {
			addPanelCropPair(
					list, dir,
					0, 0, 4, 6, 0, 0,
					EnumSet.of(ccw, Direction.UP));
			addPanelCropPair(
					list, dir,
					12, 0, 16, 6, 0, 0,
					EnumSet.of(cw, Direction.UP));
			addPanelCropPair(
					list, dir,
					0, 10, 4, 16, 0, -4,
					EnumSet.of(ccw, Direction.DOWN));
			addPanelCropPair(
					list, dir,
					12, 10, 16, 16, 0, -4,
					EnumSet.of(cw, Direction.DOWN));
			addPanelCropPair(
					list, dir,
					4, 0, 12, 5, 0, 0,
					EnumSet.of(ccw, cw, Direction.UP));
			addPanelCropPair(
					list, dir,
					4, 15, 12, 16, 0, -4,
					EnumSet.of(ccw, cw, Direction.DOWN));
		}
		}
		return list;
	}

	@Override
	protected List<CropTransform> getUnculledComponents(BlockState state) {
		Direction dir = state.getOptionalValue(BlockStateProperties.HORIZONTAL_FACING).orElse(Direction.NORTH);
		Direction ccw = dir.getCounterClockWise();
		Direction cw = dir.getClockWise();
		List<CropTransform> list = new ArrayList<>(12);
		addPanelCropSidePair(
				list, dir,
				0, 5, 1, 11, 12, 0,
				EnumSet.complementOf(EnumSet.of(cw)));
		addPanelCropSidePair(
				list, dir,
				15, 5, 16, 11, -12, 0,
				EnumSet.complementOf(EnumSet.of(ccw)));
		addPanelCropSidePair(
				list, dir,
				4, 0, 12, 1, 0, 11,
				EnumSet.complementOf(EnumSet.of(Direction.DOWN)));
		addPanelCropSidePair(
				list, dir,
				4, 15, 12, 16, 0, -11,
				EnumSet.complementOf(EnumSet.of(Direction.UP)));
		return list;
	}

	protected void addPanelCropPair(
			List<CropTransform> list, Direction facing,
			double x1, double y1, double x2, double y2, double mx, double my,
			Set<Direction> removedDirections) {
		EnumSet<Direction> set1 = EnumSet.copyOf(removedDirections);
		set1.add(facing);
		set1.remove(facing.getOpposite());
		list.add(cropForHorizontal(facing, x1, y1, 0, x2, y2, 1, mx, my, 0, set1));
		EnumSet<Direction> set2 = EnumSet.copyOf(removedDirections);
		set2.add(facing.getOpposite());
		set2.remove(facing);
		list.add(cropForHorizontal(facing, x1, y1, 14, x2, y2, 16, mx, my, -13, set2));
	}

	protected void addPanelCropSidePair(
			List<CropTransform> list, Direction facing,
			double x1, double y1, double x2, double y2, double mx, double my,
			Set<Direction> removedDirections) {
		EnumSet<Direction> set = EnumSet.copyOf(removedDirections);
		set.remove(facing);
		set.remove(facing.getOpposite());
		list.add(cropForHorizontal(facing, x1, y1, 0, x2, y2, 1, mx, my, 0, removedDirections));
		list.add(cropForHorizontal(facing, x1, y1, 14, x2, y2, 16, mx, my, -13, removedDirections));
	}
}
