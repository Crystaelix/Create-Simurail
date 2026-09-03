package com.crystaelix.simurail.ponder.scenes;

import com.crystaelix.simurail.ponder.instruction.AutomaticCouplerLengthInstruction;
import com.crystaelix.simurail.ponder.instruction.AutomaticCouplerTypeInstruction;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class AutomaticCouplerScenes {

	public static void intro(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		CreateSceneBuilder.WorldInstructions world = scene.world();
		OverlayInstructions overlay = scene.overlay();
		SelectionUtil select = util.select();
		PositionUtil grid = util.grid();
		VectorUtil vector = util.vector();

		scene.title("automatic_coupler.intro", "header");
		scene.configureBasePlate(0, 0, 15);
		scene.scaleSceneView(0.75F);
		scene.showBasePlate();
		scene.idle(10);
		world.showSection(select.layer(1), Direction.DOWN);
		scene.idle(10);

		Selection carriage1Selection = select.fromTo(8, 2, 6, 14, 3, 8);
		Selection carriage2Selection = select.fromTo(3, 2, 6, 6, 3, 8);

		ElementLink<WorldSectionElement> carriage1Element = world.showIndependentSection(carriage1Selection, Direction.DOWN);
		scene.idle(10);
		ElementLink<WorldSectionElement> carriage2Element = world.showIndependentSection(carriage2Selection, Direction.DOWN);
		scene.idle(10);

		overlay.showText(60).
		pointAt(vector.centerOf(8, 2, 7)).
		attachKeyFrame().
		placeNearTarget().
		text("1_description");
		scene.idle(70);

		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(10, 2, 7), -2.5));
		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(13, 2, 7), -2.5));
		world.moveSection(carriage1Element, vector.of(-1, 0, 0), 8);
		scene.idle(8);

		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(10, 2, 7), -5/3D));
		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(13, 2, 7), -5/3D));
		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(4, 2, 7), -5/3D));
		world.moveSection(carriage1Element, vector.of(-3, 0, 0), 36);
		world.moveSection(carriage2Element, vector.of(-3, 0, 0), 36);
		scene.idle(36);

		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(10, 2, 7), 0));
		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(13, 2, 7), 0));
		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(4, 2, 7), 0));

		overlay.showText(60).
		pointAt(vector.centerOf(4, 3, 7)).
		attachKeyFrame().
		placeNearTarget().
		text("2_disconnecting");
		scene.idle(70);

		world.cycleBlockProperty(grid.at(9, 3, 7), BlockStateProperties.POWERED);
		world.cycleBlockProperty(grid.at(8, 2, 7), BlockStateProperties.POWERED);
		scene.idle(10);

		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(10, 2, 7), 8/3D));
		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(13, 2, 7), 8/3D));
		world.moveSection(carriage1Element, vector.of(4, 0, 0), 30);
		scene.idle(30);

		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(10, 2, 7), 0));
		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(13, 2, 7), 0));
		scene.idle(10);

		world.cycleBlockProperty(grid.at(9, 3, 7), BlockStateProperties.POWERED);
		world.cycleBlockProperty(grid.at(8, 2, 7), BlockStateProperties.POWERED);

		overlay.showText(60).
		pointAt(vector.centerOf(8, 2, 7)).
		attachKeyFrame().
		placeNearTarget().
		text("3_length");
		overlay.showControls(vector.centerOf(8, 2, 7), Pointing.DOWN, 60).
		rightClick();
		scene.idle(70);

		scene.addInstruction(new AutomaticCouplerLengthInstruction(grid.at(8, 2, 7)));
		scene.idle(30);
		scene.addInstruction(new AutomaticCouplerLengthInstruction(grid.at(8, 2, 7)));
		scene.idle(10);

		overlay.showText(60).
		pointAt(vector.centerOf(8, 2, 7)).
		attachKeyFrame().
		placeNearTarget().
		text("4_type");
		overlay.showControls(vector.centerOf(8, 2, 7), Pointing.DOWN, 60).
		rightClick().withItem(AllItems.WRENCH.asStack());
		scene.idle(70);

		scene.addInstruction(new AutomaticCouplerTypeInstruction(grid.at(8, 2, 7)));
		scene.idle(10);
		overlay.showText(60).
		pointAt(vector.centerOf(8, 2, 7)).
		placeNearTarget().
		text("5_compatibility");
		scene.idle(70);

		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(10, 2, 7), -10/3D));
		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(13, 2, 7), -10/3D));
		world.moveSection(carriage1Element, vector.of(-5, 0, 0), 30);
		scene.idle(30);

		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(10, 2, 7), 0));
		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(13, 2, 7), 0));
		scene.idle(10);

		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(10, 2, 7), 8/5D));
		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(13, 2, 7), 8/5D));
		world.moveSection(carriage1Element, vector.of(2, 0, 0), 25);
		scene.idle(25);

		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(10, 2, 7), 0));
		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(13, 2, 7), 0));
		scene.idle(10);
	}
}
