package com.crystaelix.simurail.ponder.scenes;

import com.crystaelix.simurail.content.SimurailBlocks;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.OverlayInstructions;
import net.createmod.ponder.api.scene.PositionUtil;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.SelectionUtil;
import net.createmod.ponder.api.scene.VectorUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;

public class ProbeReaderScenes {

	public static void intro(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		CreateSceneBuilder.WorldInstructions world = scene.world();
		OverlayInstructions overlay = scene.overlay();
		SelectionUtil select = util.select();
		PositionUtil grid = util.grid();
		VectorUtil vector = util.vector();

		scene.title("probe_reader.intro", "header");
		scene.configureBasePlate(0, 0, 9);
		scene.scaleSceneView(0.8F);
		scene.showBasePlate();
		scene.idle(10);
		world.showSection(select.fromTo(0, 1, 4, 8, 1, 4), Direction.DOWN);
		scene.idle(10);

		world.showSection(select.position(7, 2, 4), Direction.DOWN);
		scene.idle(10);

		overlay.showControls(vector.of(7.25, 3, 4.5), Pointing.DOWN, 40).
		rightClick().
		withItem(SimurailBlocks.PROBE_READER.asStack());
		scene.idle(10);
		overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, "outline", new AABB(7, 2, 4, 7.5625, 3, 5), 60);
		scene.idle(10);

		overlay.showText(60).
		pointAt(vector.of(7.25, 3, 4.5)).
		placeNearTarget().
		colored(PonderPalette.GREEN).
		text("1_placing");
		scene.idle(40);

		world.showSection(select.position(7, 3, 4), Direction.DOWN);
		scene.overlay().chaseBoundingBoxOutline(PonderPalette.GREEN, "outline", new AABB(7.125, 3, 4.125, 7.875, 3.1875, 4.875), 20);
		scene.idle(30);

		overlay.showText(60).
		pointAt(vector.centerOf(7, 3, 4)).
		attachKeyFrame().
		placeNearTarget().
		text("2_description");
		scene.idle(70);

		overlay.showText(60).
		pointAt(vector.of(7, 2.5, 4.5)).
		placeNearTarget().
		text("3_bogie");
		scene.idle(70);

		overlay.showText(60).
		pointAt(vector.centerOf(7, 3, 4)).
		attachKeyFrame().
		placeNearTarget().
		text("4_options");
		overlay.showControls(vector.centerOf(7, 3, 4), Pointing.DOWN, 60).
		rightClick();
		scene.idle(70);

		ElementLink<WorldSectionElement> signal = world.showIndependentSection(select.position(4, 1, 1), Direction.DOWN);
		scene.idle(10);

		overlay.showBigLine(PonderPalette.OUTPUT, vector.of(6.5, 1.1875, 4.5), vector.of(4.5, 1.1875, 4.5), 140);
		world.cycleBlockProperty(grid.at(7, 3, 4), BlockStateProperties.POWERED);

		overlay.showText(60).
		pointAt(vector.of(4.5, 1.1875, 4.5)).
		attachKeyFrame().
		placeNearTarget().
		text("5_signal");
		scene.idle(70);

		ElementLink<WorldSectionElement> sign = world.showIndependentSection(select.position(4, 1, 0), Direction.DOWN);
		scene.idle(10);

		overlay.showText(60).
		pointAt(vector.of(4.5, 1.5, 0.75)).
		placeNearTarget().
		text("6_signal_naming");
		scene.idle(70);

		world.cycleBlockProperty(grid.at(7, 3, 4), BlockStateProperties.POWERED);
		world.hideIndependentSection(signal, Direction.UP);
		world.hideIndependentSection(sign, Direction.UP);
		scene.idle(10);

		ElementLink<WorldSectionElement> station = world.showIndependentSection(select.fromTo(0, 1, 1, 1, 1, 1), Direction.DOWN);
		scene.idle(10);

		overlay.showBigLine(PonderPalette.OUTPUT, vector.of(6.5, 1.1875, 4.5), vector.of(1.5, 1.1875, 4.5), 60);
		world.cycleBlockProperty(grid.at(7, 3, 4), BlockStateProperties.POWERED);

		overlay.showText(60).
		pointAt(vector.of(1.5, 1.1875, 4.5)).
		attachKeyFrame().
		placeNearTarget().
		text("7_station");
		scene.idle(70);

		world.cycleBlockProperty(grid.at(7, 3, 4), BlockStateProperties.POWERED);
		world.hideIndependentSection(station, Direction.UP);
		scene.idle(10);

		overlay.showBigLine(PonderPalette.OUTPUT, vector.of(6.5, 1.1875, 4.5), vector.of(0, 1.1875, 4.5), 60);
		world.cycleBlockProperty(grid.at(7, 3, 4), BlockStateProperties.POWERED);

		overlay.showText(60).
		pointAt(vector.of(0, 1.1875, 4.5)).
		attachKeyFrame().
		placeNearTarget().
		text("8_discontinuity");
		scene.idle(70);

		world.cycleBlockProperty(grid.at(7, 3, 4), BlockStateProperties.POWERED);
	}
}
