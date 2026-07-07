package com.crystaelix.simurail.ponder.scenes;

import com.crystaelix.simurail.content.SimurailBlocks;
import com.crystaelix.simurail.content.gangway_frame.GangwayFrameBlock;
import com.crystaelix.simurail.content.gangway_frame.GangwayFrameBlockShape;
import com.crystaelix.simurail.ponder.instruction.GangwayFramePartnerInstruction;
import com.crystaelix.simurail.ponder.instruction.GangwayFrameRestLengthInstruction;
import com.crystaelix.simurail.ponder.instruction.PhysicsBogeyVisualSpeedInstruction;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;

import net.createmod.catnip.math.Pointing;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class GangwayFrameScenes {

	public static void intro(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		CreateSceneBuilder.WorldInstructions world = scene.world();
		OverlayInstructions overlay = scene.overlay();
		SelectionUtil select = util.select();
		PositionUtil grid = util.grid();
		VectorUtil vector = util.vector();

		scene.title("gangway_frame.intro", "header");
		scene.configureBasePlate(0, 0, 9);
		scene.scaleSceneView(0.8F);
		scene.showBasePlate();
		scene.idle(10);
		world.showSection(select.layer(1), Direction.DOWN);
		scene.idle(10);

		Selection carriage1Selection = select.fromTo(5, 2, 3, 7, 3, 5).add(select.position(4, 2, 4));
		Selection carriage2Selection = select.fromTo(0, 2, 3, 2, 3, 5).add(select.position(3, 2, 4));

		world.showSection(carriage1Selection, Direction.DOWN);
		scene.idle(10);
		world.showSection(carriage2Selection, Direction.DOWN);
		scene.idle(10);

		overlay.showText(60).
		pointAt(vector.topOf(4, 2, 4)).
		attachKeyFrame().
		placeNearTarget().
		text("1_description");
		scene.idle(70);

		overlay.showText(60).
		pointAt(vector.topOf(4, 2, 4)).
		placeNearTarget().
		text("2_usage");
		overlay.showControls(vector.topOf(4, 2, 4), Pointing.DOWN, 60).
		rightClick();
		scene.idle(70);

		scene.addInstruction(new GangwayFramePartnerInstruction(grid.at(4, 2, 4), grid.at(3, 2, 4)));
		scene.addInstruction(new GangwayFramePartnerInstruction(grid.at(3, 2, 4), grid.at(4, 2, 4)));
		scene.idle(20);

		overlay.showText(60).
		pointAt(vector.centerOf(5, 3, 4)).
		placeNearTarget().
		text("3_power");
		scene.idle(70);

		world.cycleBlockProperty(grid.at(5, 3, 4), BlockStateProperties.POWERED);
		world.cycleBlockProperty(grid.at(4, 2, 4), BlockStateProperties.POWERED);
		scene.addInstruction(new GangwayFramePartnerInstruction(grid.at(4, 2, 4), null));
		scene.addInstruction(new GangwayFramePartnerInstruction(grid.at(3, 2, 4), null));
		scene.idle(20);

		world.cycleBlockProperty(grid.at(5, 3, 4), BlockStateProperties.POWERED);
		world.cycleBlockProperty(grid.at(4, 2, 4), BlockStateProperties.POWERED);
		scene.idle(10);

		overlay.showText(60).
		pointAt(vector.topOf(4, 2, 5)).
		attachKeyFrame().
		placeNearTarget().
		text("4_multiple");
		world.showSection(select.position(4, 2, 3).add(select.position(4, 2, 5)), Direction.DOWN);
		world.showSection(select.position(3, 2, 3).add(select.position(3, 2, 5)), Direction.DOWN);
		scene.idle(70);

		overlay.showControls(vector.topOf(4, 2, 4), Pointing.DOWN, 20).
		rightClick();
		scene.idle(30);

		scene.addInstruction(new GangwayFramePartnerInstruction(grid.at(4, 2, 4), grid.at(3, 2, 4)));
		scene.addInstruction(new GangwayFramePartnerInstruction(grid.at(3, 2, 4), grid.at(4, 2, 4)));
		scene.addInstruction(new GangwayFramePartnerInstruction(grid.at(4, 2, 3), grid.at(3, 2, 3)));
		scene.addInstruction(new GangwayFramePartnerInstruction(grid.at(3, 2, 3), grid.at(4, 2, 3)));
		scene.addInstruction(new GangwayFramePartnerInstruction(grid.at(4, 2, 5), grid.at(3, 2, 5)));
		scene.addInstruction(new GangwayFramePartnerInstruction(grid.at(3, 2, 5), grid.at(4, 2, 5)));
		scene.idle(20);

		overlay.showControls(vector.topOf(4, 2, 4), Pointing.DOWN, 20).
		rightClick();
		scene.idle(30);

		scene.addInstruction(new GangwayFramePartnerInstruction(grid.at(4, 2, 4), null));
		scene.addInstruction(new GangwayFramePartnerInstruction(grid.at(3, 2, 4), null));
		scene.addInstruction(new GangwayFramePartnerInstruction(grid.at(4, 2, 3), null));
		scene.addInstruction(new GangwayFramePartnerInstruction(grid.at(3, 2, 3), null));
		scene.addInstruction(new GangwayFramePartnerInstruction(grid.at(4, 2, 5), null));
		scene.addInstruction(new GangwayFramePartnerInstruction(grid.at(3, 2, 5), null));
		scene.idle(20);

		overlay.showText(60).
		pointAt(vector.topOf(4, 2, 4)).
		attachKeyFrame().
		placeNearTarget().
		text("5_wrench");
		overlay.showControls(vector.topOf(4, 2, 5), Pointing.DOWN, 60).
		rightClick().withItem(AllItems.WRENCH.asStack());
		scene.idle(70);

		BlockState westState = SimurailBlocks.GANGWAY_FRAME.getDefaultState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST);
		BlockState eastState = SimurailBlocks.GANGWAY_FRAME.getDefaultState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST);

		world.setBlock(grid.at(4, 2, 5), westState.setValue(GangwayFrameBlock.SHAPE, GangwayFrameBlockShape.LU), false);
		world.setBlock(grid.at(4, 2, 3), westState.setValue(GangwayFrameBlock.SHAPE, GangwayFrameBlockShape.UR), false);
		world.setBlock(grid.at(3, 2, 5), eastState.setValue(GangwayFrameBlock.SHAPE, GangwayFrameBlockShape.UR), false);
		scene.addInstruction(new GangwayFrameRestLengthInstruction(grid.at(4, 2, 4), 0.375F));
		scene.addInstruction(new GangwayFrameRestLengthInstruction(grid.at(4, 2, 5), 0.375F));
		scene.addInstruction(new GangwayFrameRestLengthInstruction(grid.at(4, 2, 3), 0.375F));
		scene.addInstruction(new GangwayFrameRestLengthInstruction(grid.at(3, 2, 4), 0.375F));
		scene.addInstruction(new GangwayFrameRestLengthInstruction(grid.at(3, 2, 5), 0.375F));
		scene.addInstruction(new GangwayFrameRestLengthInstruction(grid.at(3, 2, 3), 0.375F));
		scene.idle(10);

		overlay.showText(60).
		pointAt(vector.topOf(4, 2, 5)).
		placeNearTarget().
		text("6_shape");
		scene.idle(70);

		overlay.showControls(vector.topOf(4, 2, 4), Pointing.DOWN, 20).
		rightClick();
		scene.idle(30);

		scene.addInstruction(new GangwayFramePartnerInstruction(grid.at(4, 2, 4), grid.at(3, 2, 4)));
		scene.addInstruction(new GangwayFramePartnerInstruction(grid.at(3, 2, 4), grid.at(4, 2, 4)));
		scene.addInstruction(new GangwayFramePartnerInstruction(grid.at(4, 2, 5), grid.at(3, 2, 5)));
		scene.addInstruction(new GangwayFramePartnerInstruction(grid.at(3, 2, 5), grid.at(4, 2, 5)));
		scene.idle(20);
	}

	public static void coupler(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		CreateSceneBuilder.WorldInstructions world = scene.world();
		OverlayInstructions overlay = scene.overlay();
		SelectionUtil select = util.select();
		PositionUtil grid = util.grid();
		VectorUtil vector = util.vector();

		scene.title("gangway_frame.coupler", "header");
		scene.configureBasePlate(0, 0, 9);
		scene.scaleSceneView(0.8F);
		scene.showBasePlate();
		scene.idle(10);
		world.showSection(select.layer(1), Direction.DOWN);
		scene.idle(10);

		Selection carriage1Selection = select.fromTo(5, 2, 3, 7, 3, 5).add(select.position(4, 2, 4));
		Selection carriage2Selection = select.fromTo(0, 2, 3, 2, 3, 5).add(select.position(3, 2, 4));

		ElementLink<WorldSectionElement> carriage1Element = world.showIndependentSection(carriage1Selection, Direction.DOWN);
		world.moveSection(carriage1Element, vector.of(1, 0, 0), 0);
		scene.idle(10);
		world.showSection(carriage2Selection, Direction.DOWN);
		scene.idle(10);

		overlay.showText(60).
		pointAt(vector.centerOf(5, 2, 4)).
		attachKeyFrame().
		placeNearTarget().
		text("1_description");
		scene.idle(70);

		overlay.showText(60).
		pointAt(vector.topOf(5, 2, 4)).
		placeNearTarget().
		text("2_connect");
		scene.idle(70);

		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(6, 2, 4), -2));
		world.moveSection(carriage1Element, vector.of(-1, 0, 0), 10);
		scene.idle(10);

		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(6, 2, 4), 0));
		scene.addInstruction(new GangwayFramePartnerInstruction(grid.at(4, 2, 4), grid.at(3, 2, 4)));
		scene.addInstruction(new GangwayFramePartnerInstruction(grid.at(3, 2, 4), grid.at(4, 2, 4)));
		scene.idle(20);
	}
}
