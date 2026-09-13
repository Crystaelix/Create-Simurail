package com.crystaelix.simurail.content.automatic_coupler.copycat;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;

import com.crystaelix.simurail.content.copycat.SimurailCopycatModel;

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
	protected List<CropTransform> getComponents(BlockState state) {
		Direction dir = state.getOptionalValue(BlockStateProperties.HORIZONTAL_FACING).orElse(Direction.NORTH);
		Direction op = dir.getOpposite();
		Direction ccw = dir.getCounterClockWise();
		Direction cw = dir.getClockWise();
		CopycatAutomaticCouplerShape shape = state.getOptionalValue(CopycatAutomaticCouplerBlock.SHAPE).orElse(CopycatAutomaticCouplerShape.FULL);
		List<CropTransform> list = new ArrayList<>(12);
		switch(shape) {
		case FULL -> {
			list.add(cropForHorizontal(
					dir,
					0, 0, 0,
					4, 16, 1,
					0, 0, 0,
					EnumSet.of(dir, ccw)));
			list.add(cropForHorizontal(
					dir,
					0, 0, 14,
					4, 16, 16,
					0, 0, -13,
					EnumSet.of(op, ccw)));
			list.add(cropForHorizontal(
					dir,
					12, 0, 0,
					16, 16, 1,
					0, 0, 0,
					EnumSet.of(dir, cw)));
			list.add(cropForHorizontal(
					dir,
					12, 0, 14,
					16, 16, 16,
					0, 0, -13,
					EnumSet.of(op, cw)));
			list.add(cropForHorizontal(
					dir,
					4, 0, 0,
					12, 5, 1,
					0, 0, 0,
					EnumSet.of(dir, ccw, cw, Direction.UP)));
			list.add(cropForHorizontal(
					dir,
					4, 0, 14,
					12, 5, 16,
					0, 0, -13,
					EnumSet.of(op, ccw, cw, Direction.UP)));
			list.add(cropForHorizontal(
					dir,
					4, 11, 0,
					12, 16, 1,
					0, 0, 0,
					EnumSet.of(dir, ccw, cw, Direction.DOWN)));
			list.add(cropForHorizontal(
					dir,
					4, 11, 14,
					12, 16, 16,
					0, 0, -13,
					EnumSet.of(op, ccw, cw, Direction.DOWN)));
		}
		case TOP -> {
			list.add(cropForHorizontal(
					dir,
					0, 0, 0,
					4, 6, 1,
					0, 4, 0,
					EnumSet.of(dir, ccw, Direction.UP)));
			list.add(cropForHorizontal(
					dir,
					0, 0, 14,
					4, 6, 16,
					0, 4, -13,
					EnumSet.of(op, ccw, Direction.UP)));
			list.add(cropForHorizontal(
					dir,
					12, 0, 0,
					16, 6, 1,
					0, 4, 0,
					EnumSet.of(dir, cw, Direction.UP)));
			list.add(cropForHorizontal(
					dir,
					12, 0, 14,
					16, 6, 16,
					0, 4, -13,
					EnumSet.of(op, cw, Direction.UP)));
			list.add(cropForHorizontal(
					dir,
					0, 10, 0,
					4, 16, 1,
					0, 0, 0,
					EnumSet.of(dir, ccw, Direction.DOWN)));
			list.add(cropForHorizontal(
					dir,
					0, 10, 14,
					4, 16, 16,
					0, 0, -13,
					EnumSet.of(op, ccw, Direction.DOWN)));
			list.add(cropForHorizontal(
					dir,
					12, 10, 0,
					16, 16, 1,
					0, 0, 0,
					EnumSet.of(dir, cw, Direction.DOWN)));
			list.add(cropForHorizontal(
					dir,
					12, 10, 14,
					16, 16, 16,
					0, 0, -13,
					EnumSet.of(op, cw, Direction.DOWN)));
			list.add(cropForHorizontal(
					dir,
					4, 0, 0,
					12, 1, 1,
					0, 4, 0,
					EnumSet.of(dir, ccw, cw, Direction.UP)));
			list.add(cropForHorizontal(
					dir,
					4, 0, 14,
					12, 1, 16,
					0, 4, -13,
					EnumSet.of(op, ccw, cw, Direction.UP)));
			list.add(cropForHorizontal(
					dir,
					4, 11, 0,
					12, 16, 1,
					0, 0, 0,
					EnumSet.of(dir, ccw, cw, Direction.DOWN)));
			list.add(cropForHorizontal(
					dir,
					4, 11, 14,
					12, 16, 16,
					0, 0, -13,
					EnumSet.of(op, ccw, cw, Direction.DOWN)));
		}
		case BOTTOM -> {
			list.add(cropForHorizontal(
					dir,
					0, 0, 0,
					4, 6, 1,
					0, 0, 0,
					EnumSet.of(dir, ccw, Direction.UP)));
			list.add(cropForHorizontal(
					dir,
					0, 0, 14,
					4, 6, 16,
					0, 0, -13,
					EnumSet.of(op, ccw, Direction.UP)));
			list.add(cropForHorizontal(
					dir,
					12, 0, 0,
					16, 6, 1,
					0, 0, 0,
					EnumSet.of(dir, cw, Direction.UP)));
			list.add(cropForHorizontal(
					dir,
					12, 0, 14,
					16, 6, 16,
					0, 0, -13,
					EnumSet.of(op, cw, Direction.UP)));
			list.add(cropForHorizontal(
					dir,
					0, 10, 0,
					4, 16, 1,
					0, -4, 0,
					EnumSet.of(dir, ccw, Direction.DOWN)));
			list.add(cropForHorizontal(
					dir,
					0, 10, 14,
					4, 16, 16,
					0, -4, -13,
					EnumSet.of(op, ccw, Direction.DOWN)));
			list.add(cropForHorizontal(
					dir,
					12, 10, 0,
					16, 16, 1,
					0, -4, 0,
					EnumSet.of(dir, cw, Direction.DOWN)));
			list.add(cropForHorizontal(
					dir,
					12, 10, 14,
					16, 16, 16,
					0, -4, -13,
					EnumSet.of(op, cw, Direction.DOWN)));
			list.add(cropForHorizontal(
					dir,
					4, 0, 0,
					12, 5, 1,
					0, 0, 0,
					EnumSet.of(dir, ccw, cw, Direction.UP)));
			list.add(cropForHorizontal(
					dir,
					4, 0, 14,
					12, 5, 16,
					0, 0, -13,
					EnumSet.of(op, ccw, cw, Direction.UP)));
			list.add(cropForHorizontal(
					dir,
					4, 15, 0,
					12, 16, 1,
					0, -4, 0,
					EnumSet.of(dir, ccw, cw, Direction.DOWN)));
			list.add(cropForHorizontal(
					dir,
					4, 15, 14,
					12, 16, 16,
					0, -4, -13,
					EnumSet.of(op, ccw, cw, Direction.DOWN)));
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
		list.add(cropForHorizontal(
				dir,
				0, 5, 0,
				1, 11, 1,
				12, 0, 0,
				EnumSet.complementOf(EnumSet.of(cw))));
		list.add(cropForHorizontal(
				dir,
				0, 5, 14,
				1, 11, 16,
				12, 0, -13,
				EnumSet.complementOf(EnumSet.of(cw))));
		list.add(cropForHorizontal(
				dir,
				15, 5, 0,
				16, 11, 1,
				-12, 0, 0,
				EnumSet.complementOf(EnumSet.of(ccw))));
		list.add(cropForHorizontal(
				dir,
				15, 5, 14,
				16, 11, 16,
				-12, 0, -13,
				EnumSet.complementOf(EnumSet.of(ccw))));
		list.add(cropForHorizontal(
				dir,
				4, 0, 0,
				12, 1, 1,
				0, 11, 0,
				EnumSet.complementOf(EnumSet.of(Direction.DOWN))));
		list.add(cropForHorizontal(
				dir,
				4, 0, 14,
				12, 1, 16,
				0, 11, -13,
				EnumSet.complementOf(EnumSet.of(Direction.DOWN))));
		list.add(cropForHorizontal(
				dir,
				4, 15, 0,
				12, 16, 1,
				0, -11, 0,
				EnumSet.complementOf(EnumSet.of(Direction.UP))));
		list.add(cropForHorizontal(
				dir,
				4, 15, 14,
				12, 16, 16,
				0, -11, -13,
				EnumSet.complementOf(EnumSet.of(Direction.UP))));
		return list;
	}
}
