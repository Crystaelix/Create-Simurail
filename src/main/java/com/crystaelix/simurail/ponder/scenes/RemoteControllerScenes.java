package com.crystaelix.simurail.ponder.scenes;

import com.crystaelix.simurail.content.SimurailBlocks;
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

public class RemoteControllerScenes {

	public static void intro(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		CreateSceneBuilder.WorldInstructions world = scene.world();
		OverlayInstructions overlay = scene.overlay();
		SelectionUtil select = util.select();
		PositionUtil grid = util.grid();
		VectorUtil vector = util.vector();

		scene.title("remote_controller.intro", "header");
		scene.configureBasePlate(0, 0, 9);
		scene.scaleSceneView(0.8F);
		scene.showBasePlate();
		scene.idle(10);
		world.showSection(select.layer(1), Direction.DOWN);
		scene.idle(10);

		world.showSection(select.fromTo(3, 2, 3, 5, 2, 5), Direction.DOWN);
		scene.idle(10);

		overlay.showControls(vector.of(4.5, 3, 4.5), Pointing.DOWN, 40).
		rightClick().
		withItem(SimurailBlocks.REMOTE_CONTROLLER.asStack());
		scene.idle(10);
		overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, "outline", new AABB(4, 2, 4, 5, 3, 5), 60);
		scene.idle(10);

		overlay.showText(60).
		pointAt(vector.of(4.5, 3, 4.5)).
		placeNearTarget().
		colored(PonderPalette.GREEN).
		text("1_placing");
		scene.idle(40);

		world.showSection(select.position(3, 3, 4), Direction.DOWN);
		scene.overlay().chaseBoundingBoxOutline(PonderPalette.GREEN, "outline", new AABB(3.125, 3, 4.125, 3.875, 3.1875, 4.875), 20);
		scene.idle(30);

		overlay.showText(60).
		pointAt(vector.centerOf(3, 3, 4)).
		attachKeyFrame().
		placeNearTarget().
		text("2_description");
		scene.idle(70);

		overlay.showText(60).
		pointAt(vector.centerOf(3, 3, 4)).
		attachKeyFrame().
		placeNearTarget().
		text("3_options");
		overlay.showControls(vector.centerOf(3, 3, 4), Pointing.DOWN, 60).
		rightClick();
		scene.idle(70);

		world.showSection(select.position(3, 3, 3), Direction.DOWN);
		scene.idle(10);

		overlay.showText(60).
		pointAt(vector.centerOf(3, 3, 4)).
		attachKeyFrame().
		placeNearTarget().
		text("4_usage");
		scene.idle(70);

		world.cycleBlockProperty(grid.at(3, 3, 4), BlockStateProperties.POWERED);
		world.cycleBlockProperty(grid.at(3, 3, 3), BlockStateProperties.POWERED);
		scene.idle(40);
	}
}
