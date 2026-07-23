package com.crystaelix.simurail.ponder.scenes;

import com.crystaelix.simurail.content.SimurailItems;
import com.crystaelix.simurail.ponder.instruction.PhysicsBogeyConnectionInstruction;
import com.crystaelix.simurail.ponder.instruction.PhysicsBogeyVisualSpeedInstruction;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.OverlayInstructions;
import net.createmod.ponder.api.scene.PositionUtil;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.createmod.ponder.api.scene.SelectionUtil;
import net.createmod.ponder.api.scene.VectorUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

public class ConnectorScenes {

	public static void intro(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		CreateSceneBuilder.WorldInstructions world = scene.world();
		OverlayInstructions overlay = scene.overlay();
		SelectionUtil select = util.select();
		PositionUtil grid = util.grid();
		VectorUtil vector = util.vector();

		scene.title("connector.intro", "header");
		scene.configureBasePlate(0, 0, 15);
		scene.scaleSceneView(0.6F);
		scene.showBasePlate();
		scene.idle(10);
		world.showSection(select.layer(1), Direction.DOWN);
		scene.idle(10);

		Selection carriage1Selection = select.fromTo(9, 2, 6, 12, 3, 8);
		Selection carriage2Selection = select.fromTo(2, 2, 6, 5, 3, 8);

		world.showSection(carriage1Selection, Direction.DOWN);
		scene.idle(10);
		world.showSection(carriage2Selection, Direction.DOWN);
		scene.idle(10);

		AABB bb;
		bb = AABB.ofSize(
				vector.centerOf(9, 2, 7).add(-0.28125, 0, 0),
				0.4375, 1, 1);
		overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, "simurail.ponder.0", bb, 90);
		overlay.showText(60).
		pointAt(vector.blockSurface(grid.at(9, 2, 7), Direction.WEST)).
		attachKeyFrame().
		placeNearTarget().
		text("1_description");
		overlay.showControls(vector.topOf(9, 2, 7), Pointing.DOWN, 60).
		rightClick().withItem(SimurailItems.CONNECTOR.asStack());
		scene.idle(70);

		bb = AABB.ofSize(
				vector.centerOf(5, 2, 7).add(0.28125, 0, 0),
				0.4375, 1, 1);
		overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, "simurail.ponder.1", bb, 20);
		overlay.showControls(vector.topOf(5, 2, 7), Pointing.DOWN, 20).
		rightClick().withItem(SimurailItems.CONNECTOR.asStack());
		scene.idle(20);

		scene.addInstruction(new PhysicsBogeyConnectionInstruction(grid.at(9, 2, 7), false, grid.at(5, 2, 7), true));
		scene.idle(20);

		scene.addInstruction(new PhysicsBogeyConnectionInstruction(grid.at(12, 2, 7), false, grid.at(9, 2, 7), true));
		scene.addInstruction(new PhysicsBogeyConnectionInstruction(grid.at(5, 2, 7), false, grid.at(2, 2, 7), true));
		scene.idle(20);

		overlay.showText(60).
		pointAt(vector.topOf(9, 2, 7)).
		attachKeyFrame().
		placeNearTarget().
		text("2_following");
		scene.idle(70);

		overlay.showText(60).
		pointAt(vector.centerOf(9, 3, 8)).
		placeNearTarget().
		text("3_control");
		scene.idle(70);

		overlay.showText(60).
		pointAt(vector.topOf(9, 2, 7)).
		attachKeyFrame().
		placeNearTarget().
		text("4_disconnect");
		scene.idle(70);

		bb = AABB.ofSize(
				vector.centerOf(9, 2, 7).add(-0.28125, 0, 0),
				0.4375, 1, 1);
		overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, "simurail.ponder.0", bb, 40);
		overlay.showControls(vector.topOf(9, 2, 7), Pointing.DOWN, 15).
		rightClick().withItem(SimurailItems.CONNECTOR.asStack());
		scene.idle(20);

		overlay.showControls(vector.topOf(9, 2, 7), Pointing.DOWN, 20).
		rightClick().withItem(SimurailItems.CONNECTOR.asStack());
		scene.idle(20);

		scene.addInstruction(new PhysicsBogeyConnectionInstruction(grid.at(9, 2, 7), false, null, true));
		scene.idle(10);
	}

	public static void coupler(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		CreateSceneBuilder.WorldInstructions world = scene.world();
		OverlayInstructions overlay = scene.overlay();
		SelectionUtil select = util.select();
		PositionUtil grid = util.grid();
		VectorUtil vector = util.vector();

		scene.title("connector.coupler", "header");
		scene.configureBasePlate(0, 0, 9);
		scene.scaleSceneView(0.8F);
		scene.showBasePlate();
		scene.idle(10);
		world.showSection(select.layer(1), Direction.DOWN);
		scene.idle(10);

		Selection carriage1Selection = select.fromTo(4, 2, 4, 6, 2, 4);
		Selection carriage2Selection = select.fromTo(1, 2, 4, 3, 2, 4);

		ElementLink<WorldSectionElement> carriage1Element = world.showIndependentSection(carriage1Selection, Direction.DOWN);
		world.moveSection(carriage1Element, vector.of(1, 0, 0), 0);
		scene.idle(10);
		world.showSection(carriage2Selection, Direction.DOWN);
		scene.idle(10);

		AABB bb;
		bb = AABB.ofSize(
				vector.centerOf(5, 2, 4).add(0.40625, 0, 0),
				0.1875, 0.375, 0.375);
		overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, "simurail.ponder.0", bb, 90);
		overlay.showText(60).
		pointAt(vector.centerOf(5, 2, 4)).
		attachKeyFrame().
		placeNearTarget().
		text("1_description");
		overlay.showControls(vector.centerOf(5, 2, 4), Pointing.DOWN, 60).
		rightClick().withItem(SimurailItems.CONNECTOR.asStack());
		scene.idle(70);

		bb = AABB.ofSize(
				vector.centerOf(7, 2, 4).add(-0.28125, 0, 0),
				0.4375, 1, 1);
		overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, "simurail.ponder.1", bb, 20);
		overlay.showControls(vector.topOf(7, 2, 4), Pointing.DOWN, 20).
		rightClick().withItem(SimurailItems.CONNECTOR.asStack());
		scene.idle(30);

		bb = AABB.ofSize(
				vector.centerOf(3, 2, 4).add(-0.40625, 0, 0),
				0.1875, 0.375, 0.375);
		overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, "simurail.ponder.0", bb, 40);
		overlay.showControls(vector.centerOf(3, 2, 4), Pointing.DOWN, 20).
		rightClick().withItem(SimurailItems.CONNECTOR.asStack());
		scene.idle(20);

		bb = AABB.ofSize(
				vector.centerOf(1, 2, 4).add(0.28125, 0, 0),
				0.4375, 1, 1);
		overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, "simurail.ponder.1", bb, 20);
		overlay.showControls(vector.topOf(1, 2, 4), Pointing.DOWN, 20).
		rightClick().withItem(SimurailItems.CONNECTOR.asStack());
		scene.idle(30);

		overlay.showText(60).
		pointAt(vector.centerOf(5, 2, 4)).
		attachKeyFrame().
		placeNearTarget().
		text("2_connect");
		scene.idle(70);

		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(6, 2, 4), -2));
		world.moveSection(carriage1Element, vector.of(-1, 0, 0), 10);
		scene.idle(10);

		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(6, 2, 4), 0));
		scene.addInstruction(new PhysicsBogeyConnectionInstruction(grid.at(6, 2, 4), false, grid.at(1, 2, 4), true));
		scene.idle(10);
	}
}
