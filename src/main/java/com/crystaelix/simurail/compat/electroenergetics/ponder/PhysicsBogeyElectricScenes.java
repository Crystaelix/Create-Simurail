package com.crystaelix.simurail.compat.electroenergetics.ponder;

import com.crystaelix.simurail.ponder.instruction.PhysicsBogeyVisualSpeedInstruction;
import com.crystaelix.simurail.ponder.instruction.SceneRotationInstruction;
import com.george_vi.electroenergetics.CEEItems;
import com.george_vi.electroenergetics.CEEWireTypes;
import com.george_vi.electroenergetics.foundation.nodes.InWorldNode;
import com.george_vi.electroenergetics.ponder.CurrentVisualizationPonderElement;
import com.george_vi.electroenergetics.ponder.WireConnectionInstructions;
import com.george_vi.electroenergetics.ponder.WirePonderElement;
import com.george_vi.electroenergetics.simulation.WireType;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class PhysicsBogeyElectricScenes {

	protected static final float WIRE_SAG = 0.7F;
	protected static final float PANTOGRAPH_EXTENSION = 0.4F;
	protected static final float CONTACT_SHOE_REACH = 0.3125F;

	protected static WireType wire() {
		return CEEWireTypes.HEAVILY_INSULATED.get();
	}

	public static void catenary(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		CreateSceneBuilder.WorldInstructions world = scene.world();
		OverlayInstructions overlay = scene.overlay();
		SelectionUtil select = util.select();
		PositionUtil grid = util.grid();
		VectorUtil vector = util.vector();
		WireConnectionInstructions connections = new WireConnectionInstructions(builder);

		scene.title("physics_bogey.electric", "header");
		scene.configureBasePlate(0, 0, 15);
		scene.scaleSceneView(0.5F);
		scene.showBasePlate();
		scene.idle(10);

		BlockPos holder1 = grid.at(1, 6, 3);
		BlockPos holder2 = grid.at(13, 6, 3);
		BlockPos poleConnector = grid.at(12, 6, 6);
		BlockPos supplyConnector = grid.at(12, 1, 8);
		BlockPos battery = grid.at(10, 1, 9);
		BlockPos groundRod = grid.at(8, 1, 9);
		BlockPos pantograph = grid.at(4, 4, 3);
		BlockPos motor = grid.at(3, 2, 3);
		BlockPos drivenBogey = grid.at(1, 2, 3);
		BlockPos trailingBogey = grid.at(4, 2, 3);
		Selection carriageSelection = select.fromTo(1, 2, 3, 4, 4, 3);

		world.showSection(select.fromTo(0, 1, 3, 14, 1, 3), Direction.EAST);
		scene.idle(20);

		world.showSection(select.fromTo(1, 1, 6, 13, 7, 6), Direction.DOWN);
		world.showSection(select.fromTo(holder1, holder2), Direction.DOWN);
		scene.idle(20);

		overlay.showText(70).
		pointAt(vector.centerOf(holder1)).
		attachKeyFrame().
		placeNearTarget().
		colored(PonderPalette.BLUE).
		text("1_catenary");
		scene.idle(80);

		overlay.showControls(vector.centerOf(holder1), Pointing.DOWN, 20).
		rightClick().
		withItem(CEEItems.COPPER_WIRE_SPOOL.asStack());
		scene.idle(30);

		overlay.showControls(vector.centerOf(holder2), Pointing.DOWN, 20).
		rightClick().
		withItem(CEEItems.COPPER_WIRE_SPOOL.asStack());
		scene.idle(30);

		connections.createCatenaryConnection(holder1, holder2, CEEWireTypes.COPPER.get(), 10);
		scene.idle(20);

		overlay.showText(70).
		pointAt(vector.centerOf(7, 6, 3)).
		attachKeyFrame().
		placeNearTarget().
		text("2_overhead");
		scene.idle(80);

		scene.rotateCameraY(-90.0F);
		scene.idle(20);

		connections.createConnection(new InWorldNode(0, holder2), new InWorldNode(0, poleConnector), wire());
		scene.idle(20);

		world.showSection(select.fromTo(8, 1, 8, 12, 1, 9), Direction.DOWN);
		scene.idle(20);

		connections.createConnection(new InWorldNode(0, supplyConnector), new InWorldNode(0, poleConnector), wire());
		scene.idle(2);
		connections.createConnection(new InWorldNode(1, battery), new InWorldNode(0, supplyConnector), wire());
		scene.idle(20);

		overlay.showText(70).
		pointAt(vector.blockSurface(battery, Direction.EAST)).
		attachKeyFrame().
		placeNearTarget().
		text("3_supply");
		scene.idle(80);

		connections.createConnection(new InWorldNode(0, battery), new InWorldNode(0, groundRod), wire());
		scene.idle(20);

		overlay.showText(70).
		pointAt(vector.blockSurface(battery, Direction.WEST)).
		attachKeyFrame().
		placeNearTarget().
		text("4_ground");
		scene.idle(80);

		overlay.showText(40).
		pointAt(vector.topOf(battery)).
		placeNearTarget().
		text("5_voltage");
		scene.idle(50);

		scene.rotateCameraY(90.0F);
		scene.idle(20);

		// 1015 is what is left of the scene from here on until the end
		scene.addInstruction(new PantographExtensionInstruction(pantograph, PANTOGRAPH_EXTENSION, 1015));
		ElementLink<WorldSectionElement> carriageElement = world.showIndependentSection(carriageSelection, Direction.DOWN);
		scene.idle(20);

		overlay.showText(80).
		pointAt(vector.topOf(drivenBogey)).
		attachKeyFrame().
		placeNearTarget().
		text("6_contraption");
		scene.idle(90);

		overlay.showText(70).
		pointAt(vector.topOf(pantograph)).
		attachKeyFrame().
		placeNearTarget().
		text("7_pantograph");
		scene.idle(80);

		overlay.showControls(vector.topOf(pantograph), Pointing.DOWN, 20).
		rightClick().
		withItem(CEEItems.HEAVILY_INSULATED_WIRE_SPOOL.asStack());
		scene.idle(30);

		overlay.showControls(vector.topOf(motor), Pointing.DOWN, 20).
		rightClick().
		withItem(CEEItems.HEAVILY_INSULATED_WIRE_SPOOL.asStack());
		scene.idle(20);

		ElementLink<WirePonderElement> pantographWire = connections.createConnection(new InWorldNode(0, pantograph), new InWorldNode(0, motor), wire());
		scene.idle(20);

		overlay.showText(70).
		pointAt(vector.blockSurface(motor, Direction.SOUTH)).
		attachKeyFrame().
		placeNearTarget().
		text("8_motor");
		scene.idle(80);

		ElementLink<WirePonderElement> bogeyWire = connections.createConnection(new InWorldNode(1, motor), new InWorldNode(2, drivenBogey), wire());
		scene.idle(20);

		overlay.showText(80).
		pointAt(vector.topOf(drivenBogey)).
		attachKeyFrame().
		placeNearTarget().
		colored(PonderPalette.GREEN).
		text("9_return");
		scene.idle(90);

		ElementLink<CurrentVisualizationPonderElement> pantographCurrent =
				connections.createCurrentVisualization(new InWorldNode(0, pantograph), new InWorldNode(0, motor), WIRE_SAG, 1.0F, true);
		ElementLink<CurrentVisualizationPonderElement> bogeyCurrent =
				connections.createCurrentVisualization(new InWorldNode(1, motor), new InWorldNode(2, drivenBogey), WIRE_SAG, 1.0F, true);
		connections.createCurrentVisualization(new InWorldNode(0, holder2), new InWorldNode(0, poleConnector), WIRE_SAG, -1.0F, true);
		connections.createCurrentVisualization(new InWorldNode(0, supplyConnector), new InWorldNode(0, poleConnector), WIRE_SAG, 1.0F, true);
		connections.createCurrentVisualization(new InWorldNode(1, battery), new InWorldNode(0, supplyConnector), WIRE_SAG, 1.0F, true);
		connections.createCurrentVisualization(new InWorldNode(0, battery), new InWorldNode(0, groundRod), WIRE_SAG, -1.0F, true);
		scene.idle(30);

		overlay.showText(80).
		pointAt(vector.blockSurface(motor, Direction.SOUTH)).
		attachKeyFrame().
		placeNearTarget().
		text("10_driving");
		scene.idle(90);

		overlay.showText(80).
		pointAt(vector.topOf(drivenBogey)).
		attachKeyFrame().
		placeNearTarget().
		text("11_strength");
		scene.idle(90);

		overlay.showText(80).
		pointAt(vector.blockSurface(motor, Direction.SOUTH)).
		placeNearTarget().
		text("12_capacity");
		scene.idle(90);

		overlay.showText(80).
		pointAt(vector.topOf(battery)).
		placeNearTarget().
		text("13_example");
		scene.idle(90);

		overlay.showText(80).
		pointAt(vector.centerOf(7, 6, 3)).
		attachKeyFrame().
		placeNearTarget().
		text("14_journey");
		scene.idle(40);

		world.showSection(select.fromTo(15, 1, 3, 20, 1, 3), Direction.EAST);
		connections.createCatenaryConnection(holder2, grid.at(21, 6, 3), CEEWireTypes.COPPER.get(), 5);
		scene.idle(25);

		connections.removeCurrentVisualization(pantographCurrent);
		connections.removeCurrentVisualization(bogeyCurrent);
		connections.removeConnection(pantographWire, 10);
		connections.removeConnection(bogeyWire, 10);
		scene.idle(10);

		world.setKineticSpeed(carriageSelection, 12);
		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(drivenBogey, 3));
		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(trailingBogey, 3));
		world.moveSection(carriageElement, vector.of(9, 0, 0), 60);
		scene.idle(60);

		world.moveSection(carriageElement, vector.of(6, 0, 0), 40);
		scene.idle(10);

		world.hideIndependentSection(carriageElement, Direction.EAST);
		scene.idle(30);
	}

	public static void thirdRail(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		CreateSceneBuilder.WorldInstructions world = scene.world();
		OverlayInstructions overlay = scene.overlay();
		SelectionUtil select = util.select();
		PositionUtil grid = util.grid();
		VectorUtil vector = util.vector();
		WireConnectionInstructions connections = new WireConnectionInstructions(builder);

		scene.title("physics_bogey.third_rail", "header");
		scene.configureBasePlate(0, 0, 9);
		scene.scaleSceneView(0.75F);
		scene.addInstruction(new SceneRotationInstruction(-90));
		scene.showBasePlate();
		scene.idle(10);

		BlockPos railStart = grid.at(0, 1, 6);
		BlockPos railEnd = grid.at(8, 1, 6);
		BlockPos railExtensionEnd = grid.at(14, 1, 6);
		BlockPos contactShoe = grid.at(1, 2, 6);
		BlockPos motor = grid.at(3, 2, 4);
		BlockPos drivenBogey = grid.at(1, 2, 4);
		BlockPos trailingBogey = grid.at(4, 2, 4);
		Selection carriageSelection = select.fromTo(1, 2, 4, 4, 2, 6);

		world.showSection(select.fromTo(0, 1, 4, 8, 1, 4), Direction.EAST);
		scene.idle(20);

		world.showSection(select.fromTo(railStart, railEnd), Direction.DOWN);
		scene.idle(10);

		overlay.showControls(vector.topOf(railStart), Pointing.DOWN, 20).
		rightClick().
		withItem(CEEItems.IRON_RAIL_SPOOL.asStack());
		scene.idle(30);

		connections.createConnection(new InWorldNode(0, railStart), new InWorldNode(0, railEnd), CEEWireTypes.IRON_RAIL.get(), 10);
		scene.idle(20);

		overlay.showText(70).
		pointAt(vector.centerOf(4, 1, 6)).
		attachKeyFrame().
		placeNearTarget().
		colored(PonderPalette.BLUE).
		text("1_third_rail");
		scene.idle(80);

		/// @see #catenary
		scene.addInstruction(new RailContactShoeDistanceInstruction(contactShoe, CONTACT_SHOE_REACH, 595));
		ElementLink<WorldSectionElement> carriageElement = world.showIndependentSection(carriageSelection, Direction.DOWN);
		scene.idle(20);

		overlay.showText(70).
		pointAt(vector.blockSurface(contactShoe, Direction.SOUTH)).
		attachKeyFrame().
		placeNearTarget().
		text("2_contact_shoe");
		scene.idle(80);

		overlay.showControls(vector.topOf(contactShoe), Pointing.DOWN, 20).
		rightClick().
		withItem(CEEItems.HEAVILY_INSULATED_WIRE_SPOOL.asStack());
		scene.idle(20);

		ElementLink<WirePonderElement> shoeWire = connections.createConnection(new InWorldNode(0, contactShoe), new InWorldNode(1, motor), wire());
		scene.idle(20);

		overlay.showText(70).
		pointAt(vector.blockSurface(motor, Direction.SOUTH)).
		attachKeyFrame().
		placeNearTarget().
		text("3_motor");
		scene.idle(80);

		ElementLink<WirePonderElement> bogeyWire = connections.createConnection(new InWorldNode(0, motor), new InWorldNode(2, drivenBogey), wire());
		scene.idle(20);

		overlay.showText(80).
		pointAt(vector.topOf(drivenBogey)).
		attachKeyFrame().
		placeNearTarget().
		colored(PonderPalette.GREEN).
		text("4_return");
		scene.idle(90);

		ElementLink<CurrentVisualizationPonderElement> shoeCurrent =
				connections.createCurrentVisualization(new InWorldNode(0, contactShoe), new InWorldNode(1, motor), WIRE_SAG, 1.0F, true);
		ElementLink<CurrentVisualizationPonderElement> bogeyCurrent =
				connections.createCurrentVisualization(new InWorldNode(0, motor), new InWorldNode(2, drivenBogey), WIRE_SAG, 1.0F, true);
		connections.createCurrentVisualization(new InWorldNode(0, railStart), new InWorldNode(0, railEnd), 0.0F, 1.0F, true);
		scene.idle(30);

		overlay.showText(80).
		pointAt(vector.topOf(trailingBogey)).
		attachKeyFrame().
		placeNearTarget().
		text("5_driving");
		scene.idle(90);

		scene.rotateCameraY(90.0F);
		scene.idle(30);

		world.showSection(select.fromTo(9, 1, 4, 14, 1, 4), Direction.EAST);
		world.showSection(select.position(railExtensionEnd), Direction.DOWN);
		connections.createConnection(new InWorldNode(0, railEnd), new InWorldNode(0, railExtensionEnd), CEEWireTypes.IRON_RAIL.get(), 5);
		connections.createCurrentVisualization(new InWorldNode(0, railEnd), new InWorldNode(0, railExtensionEnd), 0.0F, 1.0F, true);
		scene.idle(25);

		connections.removeCurrentVisualization(shoeCurrent);
		connections.removeCurrentVisualization(bogeyCurrent);
		connections.removeConnection(shoeWire, 10);
		connections.removeConnection(bogeyWire, 10);
		scene.idle(10);

		world.setKineticSpeed(carriageSelection, 8);
		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(drivenBogey, 2));
		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(trailingBogey, 2));
		world.moveSection(carriageElement, vector.of(4, 0, 0), 40);
		scene.idle(40);

		world.moveSection(carriageElement, vector.of(4, 0, 0), 40);
		scene.idle(10);

		world.hideIndependentSection(carriageElement, Direction.EAST);
		scene.idle(30);
	}
}
