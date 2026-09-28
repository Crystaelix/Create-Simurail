package com.crystaelix.simurail.ponder.scenes;

import com.crystaelix.simurail.api.math.SimurailMath;
import com.crystaelix.simurail.content.SimurailBlocks;
import com.crystaelix.simurail.content.track_switch.TrackSwitchState;
import com.crystaelix.simurail.ponder.instruction.TrackSwitchExitInstruction;
import com.crystaelix.simurail.ponder.instruction.TrackSwitchRotInstruction;
import com.crystaelix.simurail.ponder.instruction.TrackSwitchStateInstruction;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.OverlayInstructions;
import net.createmod.ponder.api.scene.PositionUtil;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.SelectionUtil;
import net.createmod.ponder.api.scene.VectorUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class TrackSwitchScenes {

	public static void intro(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		CreateSceneBuilder.WorldInstructions world = scene.world();
		OverlayInstructions overlay = scene.overlay();
		SelectionUtil select = util.select();
		PositionUtil grid = util.grid();
		VectorUtil vector = util.vector();

		scene.title("track_switch.intro", "header");
		scene.configureBasePlate(0, 0, 15);
		scene.scaleSceneView(0.75F);
		scene.showBasePlate();
		scene.idle(10);
		world.showSection(select.fromTo(7, 1, 0, 7, 1, 14), Direction.DOWN);
		world.showSection(select.fromTo(1, 1, 12, 2, 1, 13), Direction.DOWN);
		world.showSection(select.fromTo(12, 1, 12, 13, 1, 13), Direction.DOWN);
		scene.idle(10);

		overlay.showControls(vector.of(7.5, 1.1875, 1.5), Pointing.DOWN, 40).
		rightClick().
		withItem(SimurailBlocks.TRACK_SWITCH.asStack());
		scene.idle(10);
		overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, "outline", new AABB(7, 1, 1, 8, 1.125, 2), 60);
		scene.idle(10);

		overlay.showText(60).
		pointAt(vector.of(7.5, 1.1875, 1.5)).
		placeNearTarget().
		colored(PonderPalette.GREEN).
		text("1_placing");
		scene.idle(40);

		scene.addInstruction(new TrackSwitchRotInstruction(grid.at(4, 1, 1), SimurailMath.ROT_ZPYPXN));
		scene.addInstruction(new TrackSwitchExitInstruction(grid.at(4, 1, 1),
				new TrackNodeLocation(vector.of(7.5, 1, 12)),
				new TrackNodeLocation(vector.of(12, 1, 12)),
				new TrackNodeLocation(vector.of(3, 1, 12))));
		world.showSection(select.position(4, 1, 1), Direction.DOWN);
		scene.idle(10);
		overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, "outline", new AABB(4, 1, 1, 5, 1.25, 2), 15);
		scene.idle(20);

		Vec3 straightFrom = vector.of(7.5, 1.375, 2);
		Vec3 straightTo = vector.of(7.5, 1.375, 12);
		Vec3 leftFrom = vector.of(7.875, 1.375, 2);
		Vec3 leftTo = vector.of(12, 1.375, 12);
		Vec3 rightFrom = vector.of(7.125, 1.375, 2);
		Vec3 rightTo = vector.of(3, 1.375, 12);

		overlay.showLine(PonderPalette.GREEN, straightFrom, straightTo, 140);
		overlay.showLine(PonderPalette.RED, leftFrom, leftTo, 140);
		overlay.showLine(PonderPalette.RED, rightFrom, rightTo, 140);

		overlay.showText(60).
		pointAt(vector.centerOf(4, 1, 1)).
		attachKeyFrame().
		placeNearTarget().
		text("2_description");
		scene.idle(70);

		overlay.showText(60).
		pointAt(vector.centerOf(4, 1, 1)).
		attachKeyFrame().
		placeNearTarget().
		text("3_right");
		overlay.showControls(vector.centerOf(4, 1, 1), Pointing.RIGHT, 60).
		rightClick();
		scene.idle(70);

		scene.addInstruction(new TrackSwitchStateInstruction(grid.at(4, 1, 1), TrackSwitchState.RIGHT));
		overlay.showLine(PonderPalette.RED, straightFrom, straightTo, 90);
		overlay.showLine(PonderPalette.RED, leftFrom, leftTo, 90);
		overlay.showLine(PonderPalette.GREEN, rightFrom, rightTo, 90);
		scene.idle(20);

		overlay.showText(60).
		pointAt(vector.centerOf(4, 1, 1)).
		placeNearTarget().
		text("4_left");
		overlay.showControls(vector.centerOf(4, 1, 1), Pointing.RIGHT, 60).
		rightClick().whileSneaking();
		scene.idle(70);

		scene.addInstruction(new TrackSwitchStateInstruction(grid.at(4, 1, 1), TrackSwitchState.STRAIGHT));
		overlay.showLine(PonderPalette.GREEN, straightFrom, straightTo, 30);
		overlay.showLine(PonderPalette.RED, leftFrom, leftTo, 30);
		overlay.showLine(PonderPalette.RED, rightFrom, rightTo, 30);
		scene.idle(10);

		overlay.showControls(vector.centerOf(4, 1, 1), Pointing.RIGHT, 10).
		rightClick().whileSneaking();
		scene.idle(20);

		scene.addInstruction(new TrackSwitchStateInstruction(grid.at(4, 1, 1), TrackSwitchState.LEFT));
		overlay.showLine(PonderPalette.RED, straightFrom, straightTo, 100);
		overlay.showLine(PonderPalette.GREEN, leftFrom, leftTo, 100);
		overlay.showLine(PonderPalette.RED, rightFrom, rightTo, 100);
		scene.idle(20);

		world.showSection(select.position(4, 1, 0), Direction.DOWN);
		world.showSection(select.position(3, 1, 1), Direction.DOWN);
		world.showSection(select.position(5, 1, 1), Direction.DOWN);
		scene.idle(10);

		overlay.showText(60).
		pointAt(vector.topOf(3, 0, 1)).
		attachKeyFrame().
		placeNearTarget().
		text("5_redstone");
		scene.idle(70);

		world.cycleBlockProperty(grid.at(3, 1, 1), BlockStateProperties.POWERED);
		scene.addInstruction(new TrackSwitchStateInstruction(grid.at(4, 1, 1), TrackSwitchState.RIGHT));
		overlay.showLine(PonderPalette.RED, straightFrom, straightTo, 40);
		overlay.showLine(PonderPalette.RED, leftFrom, leftTo, 40);
		overlay.showLine(PonderPalette.GREEN, rightFrom, rightTo, 40);
		scene.idle(20);

		world.cycleBlockProperty(grid.at(3, 1, 1), BlockStateProperties.POWERED);
		scene.idle(20);

		world.cycleBlockProperty(grid.at(4, 1, 0), BlockStateProperties.POWERED);
		scene.addInstruction(new TrackSwitchStateInstruction(grid.at(4, 1, 1), TrackSwitchState.STRAIGHT));
		overlay.showLine(PonderPalette.GREEN, straightFrom, straightTo, 40);
		overlay.showLine(PonderPalette.RED, leftFrom, leftTo, 40);
		overlay.showLine(PonderPalette.RED, rightFrom, rightTo, 40);
		scene.idle(20);

		world.cycleBlockProperty(grid.at(4, 1, 0), BlockStateProperties.POWERED);
	}
}
