package com.crystaelix.simurail.content.gangway_frame.copycat;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import com.crystaelix.simurail.content.SimurailBlocks;
import com.crystaelix.simurail.content.copycat.CopycatBarsModel;
import com.crystaelix.simurail.content.copycat.SimurailCopycatModel;
import com.crystaelix.simurail.content.gangway_frame.GangwayFrameBlockShape;
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

public class CopycatPanelGangwayFrameModel extends SimurailCopycatModel {

	public CopycatPanelGangwayFrameModel(BakedModel originalModel) {
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
					SimurailBlocks.COPYCAT_BARS_GANGWAY_FRAME.getDefaultState().
					setValue(BlockStateProperties.HORIZONTAL_FACING, state.getValue(BlockStateProperties.HORIZONTAL_FACING)).
					setValue(CopycatGangwayFrameBlock.SHAPE, state.getValue(CopycatGangwayFrameBlock.SHAPE)));
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
		GangwayFrameBlockShape shape = state.getValue(CopycatGangwayFrameBlock.SHAPE);
		List<CropTransform> list = new ArrayList<>(28);
		switch(shape) {
		case D, DRA, DLA -> {
			addPanelCropPair(
					list, dir,
					0, 0, 16, 6, 0, 4,
					EnumSet.of(Direction.UP));
			addPanelCropPair(
					list, dir,
					0, 10, 16, 16, 0, 0,
					EnumSet.of(Direction.DOWN));
		}
		case U, URA, ULA -> {
			addPanelCropPair(
					list, dir,
					0, 0, 16, 6, 0, 0,
					EnumSet.of(Direction.UP));
			addPanelCropPair(
					list, dir,
					0, 10, 16, 16, 0, -4,
					EnumSet.of(Direction.DOWN));
		}
		case L -> {
			addPanelCropPair(
					list, dir,
					0, 0, 6, 16, 4, 0,
					EnumSet.of(ccw));
			addPanelCropPair(
					list, dir,
					10, 0, 16, 16, 0, 0,
					EnumSet.of(cw));
		}
		case R -> {
			addPanelCropPair(
					list, dir,
					0, 0, 6, 16, 0, 0,
					EnumSet.of(ccw));
			addPanelCropPair(
					list, dir,
					10, 0, 16, 16, -4, 0,
					EnumSet.of(cw));
		}
		case DL -> {
			addPanelCropPair(
					list, dir,
					0, 10, 16, 16, 0, 0,
					EnumSet.of(Direction.DOWN));
			addPanelCropPair(
					list, dir,
					10, 0, 16, 10, 0, 0,
					EnumSet.of(cw, Direction.UP));
			for(int i = 0; i < 6; ++i) {
				addPanelCropPair(
						list, dir,
						0, i, 5 + i, 1 + i, 0, 4,
						EnumSet.of(ccw, Direction.DOWN, Direction.UP));
				addPanelCropPair(
						list, dir,
						i, 0, 1 + i, 4 + i, 4, 0,
						EnumSet.of(ccw, cw, Direction.UP));
			}
		}
		case RD -> {
			addPanelCropPair(
					list, dir,
					0, 10, 16, 16, 0, 0,
					EnumSet.of(Direction.DOWN));
			addPanelCropPair(
					list, dir,
					0, 0, 6, 10, 0, 0,
					EnumSet.of(ccw, Direction.UP));
			for(int i = 0; i < 6; ++i) {
				addPanelCropPair(
						list, dir,
						11 - i, i, 16, i + 1, 0, 4,
						EnumSet.of(cw, Direction.DOWN, Direction.UP));
				addPanelCropPair(
						list, dir,
						15 - i, 0, 16 - i, 4 + i, -4, 0,
						EnumSet.of(ccw, cw, Direction.UP));
			}
		}
		case LU -> {
			addPanelCropPair(
					list, dir,
					0, 0, 16, 6, 0, 0,
					EnumSet.of(Direction.UP));
			addPanelCropPair(
					list, dir,
					10, 6, 16, 16, 0, 0,
					EnumSet.of(cw, Direction.DOWN));
			for(int i = 0; i < 6; ++i) {
				addPanelCropPair(
						list, dir,
						0, 15 - i, 5 + i, 16 - i, 0, -4,
						EnumSet.of(ccw, Direction.DOWN, Direction.UP));
				addPanelCropPair(
						list, dir,
						i, 12 - i, 1 + i, 16, 4, 0,
						EnumSet.of(ccw, cw, Direction.DOWN));
			}
		}
		case UR -> {
			addPanelCropPair(
					list, dir,
					0, 0, 16, 6, 0, 0,
					EnumSet.of(Direction.UP));
			addPanelCropPair(
					list, dir,
					0, 6, 6, 16, 0, 0,
					EnumSet.of(ccw, Direction.DOWN));
			for(int i = 0; i < 6; ++i) {
				addPanelCropPair(
						list, dir,
						11 - i, 15 - i, 16, 16 - i, 0, -4,
						EnumSet.of(cw, Direction.DOWN, Direction.UP));
				addPanelCropPair(
						list, dir,
						15 - i, 12 - i, 16 - i, 16, -4, 0,
						EnumSet.of(ccw, cw, Direction.DOWN));
			}
		}
		default -> {}
		}
		return list;
	}

	@Override
	protected List<CropTransform> getUnculledComponents(BlockState state) {
		Direction dir = state.getOptionalValue(BlockStateProperties.HORIZONTAL_FACING).orElse(Direction.NORTH);
		Direction ccw = dir.getCounterClockWise();
		Direction cw = dir.getClockWise();
		GangwayFrameBlockShape shape = state.getValue(CopycatGangwayFrameBlock.SHAPE);
		List<CropTransform> list = new ArrayList<>(4);
		switch(shape) {
		case DL -> {
			addPanelCropPair(
					list, dir,
					0, 0, 4, 1, 0, 4,
					EnumSet.complementOf(EnumSet.of(Direction.DOWN)));
			addPanelCropPair(
					list, dir,
					0, 0, 1, 4, 4, 0,
					EnumSet.complementOf(EnumSet.of(cw)));
		}
		case RD -> {
			addPanelCropPair(
					list, dir,
					12, 0, 16, 1, 0, 4,
					EnumSet.complementOf(EnumSet.of(Direction.DOWN)));
			addPanelCropPair(
					list, dir,
					15, 0, 16, 4, -4, 0,
					EnumSet.complementOf(EnumSet.of(ccw)));
		}
		case LU -> {
			addPanelCropPair(
					list, dir,
					0, 15, 4, 16, 0, -4,
					EnumSet.complementOf(EnumSet.of(Direction.UP)));
			addPanelCropPair(
					list, dir,
					0, 12, 1, 16, 4, 0,
					EnumSet.complementOf(EnumSet.of(cw)));
		}
		case UR -> {
			addPanelCropPair(
					list, dir,
					12, 15, 16, 16, 0, -4,
					EnumSet.complementOf(EnumSet.of(Direction.UP)));
			addPanelCropPair(
					list, dir,
					15, 12, 16, 16, -4, 0,
					EnumSet.complementOf(EnumSet.of(ccw)));
		}
		default -> {}
		}
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
