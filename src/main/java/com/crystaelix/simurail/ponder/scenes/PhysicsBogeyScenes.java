package com.crystaelix.simurail.ponder.scenes;

import org.joml.Vector3d;

import com.crystaelix.simurail.content.SimurailBlocks;
import com.crystaelix.simurail.ponder.instruction.PhysicsBogeyPivotOffsetInstruction;
import com.crystaelix.simurail.ponder.instruction.PhysicsBogeyPivotRotateInstruction;
import com.crystaelix.simurail.ponder.instruction.PhysicsBogeyTypeInstruction;
import com.crystaelix.simurail.ponder.instruction.PhysicsBogeyVisualSpeedInstruction;
import com.crystaelix.simurail.ponder.instruction.PortableEngineEnableInstruction;
import com.mojang.math.Axis;
import com.simibubi.create.AllBogeyStyles;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;

import dev.simulated_team.simulated.ponder.SmoothMovementUtils;
import dev.simulated_team.simulated.ponder.instructions.CustomAnimateWorldSectionInstruction;
import dev.simulated_team.simulated.ponder.instructions.PullTheAssemblerKronkInstruction;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

public class PhysicsBogeyScenes {

	public static void intro(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		CreateSceneBuilder.WorldInstructions world = scene.world();
		OverlayInstructions overlay = scene.overlay();
		SelectionUtil select = util.select();
		PositionUtil grid = util.grid();
		VectorUtil vector = util.vector();

		scene.title("physics_bogey.intro", "header");
		scene.configureBasePlate(0, 0, 15);
		scene.scaleSceneView(0.75F);
		scene.showBasePlate();
		scene.idle(10);
		world.showSection(select.layer(1), Direction.DOWN);
		scene.idle(10);

		ElementLink<WorldSectionElement> carriageElement = world.showIndependentSection(select.position(10, 2, 7), Direction.DOWN);
		scene.idle(10);

		overlay.showText(60).
		pointAt(vector.topOf(10, 2, 7)).
		attachKeyFrame().
		placeNearTarget().
		text("1_description");
		scene.idle(70);

		overlay.showText(100).
		pointAt(vector.centerOf(10, 1, 7)).
		attachKeyFrame().
		placeNearTarget().
		text("2_placing");
		overlay.showControls(vector.centerOf(10, 1, 7), Pointing.DOWN, 100).
		rightClick().whileSneaking().
		withItem(SimurailBlocks.PHYSICS_BOGEY.asStack());
		scene.idle(110);

		Selection carriageSelection = select.fromTo(10, 2, 6, 13, 3, 8);
		world.showSectionAndMerge(carriageSelection.substract(select.position(10, 2, 7)), Direction.DOWN, carriageElement);
		scene.idle(10);

		scene.addInstruction(new PullTheAssemblerKronkInstruction(grid.at(13, 3, 7), true, false));
		overlay.showText(60).
		pointAt(vector.topOf(13, 3, 7)).
		placeNearTarget().
		text("3_assembling");
		scene.idle(70);

		overlay.showText(60).
		pointAt(vector.blockSurface(grid.at(10, 2, 7), Direction.WEST)).
		attachKeyFrame().
		placeNearTarget().
		text("4_driving");
		scene.idle(70);

		scene.addInstruction(new PortableEngineEnableInstruction(grid.at(12, 3, 7)));
		world.cycleBlockProperty(grid.at(12, 3, 7), BlockStateProperties.LIT);
		world.setKineticSpeed(carriageSelection, -12);
		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(10, 2, 7), -3));
		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(13, 2, 7), -3));
		world.moveSection(carriageElement, vector.of(-9, 0, 0), 60);
		scene.idle(60);

		world.setKineticSpeed(carriageSelection, 0);
		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(10, 2, 7), 0));
		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(13, 2, 7), 0));
		world.cycleBlockProperty(grid.at(10, 3, 7), BlockStateProperties.POWERED);
		scene.idle(10);

		world.cycleBlockProperty(grid.at(10, 3, 7), BlockStateProperties.POWERED);
		overlay.showText(60).
		pointAt(vector.blockSurface(grid.at(1, 2, 7), Direction.WEST)).
		placeNearTarget().
		text("5_direction");
		scene.idle(70);

		world.setKineticSpeed(carriageSelection, 12);
		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(10, 2, 7), 3));
		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(13, 2, 7), 3));
		world.moveSection(carriageElement, vector.of(9, 0, 0), 60);
		scene.idle(60);

		world.setKineticSpeed(carriageSelection, 0);
		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(10, 2, 7), 0));
		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(13, 2, 7), 0));
		world.cycleBlockProperty(grid.at(10, 3, 7), BlockStateProperties.POWERED);
		scene.idle(10);

		world.cycleBlockProperty(grid.at(10, 3, 7), BlockStateProperties.POWERED);
		overlay.showText(60).
		pointAt(vector.centerOf(10, 3, 7)).
		attachKeyFrame().
		placeNearTarget().
		text("6_braking");
		scene.idle(70);

		overlay.showText(60).
		pointAt(vector.centerOf(10, 3, 8)).
		placeNearTarget().
		text("7_steering");
		scene.idle(70);

		overlay.showText(60).
		pointAt(vector.topOf(10, 2, 7)).
		attachKeyFrame().
		placeNearTarget().
		text("8_options");
		overlay.showControls(vector.topOf(10, 2, 7), Pointing.DOWN, 60).
		rightClick();
		scene.idle(70);

		overlay.showText(60).
		pointAt(vector.topOf(10, 2, 7)).
		attachKeyFrame().
		placeNearTarget().
		text("9_type");
		overlay.showControls(vector.topOf(10, 2, 7), Pointing.DOWN, 60).
		rightClick().whileSneaking();
		scene.idle(70);

		scene.addInstruction(new PhysicsBogeyTypeInstruction(grid.at(10, 2, 7), AllBogeyStyles.STANDARD, BogeySizes.LARGE));
		scene.idle(10);

		world.setKineticSpeed(carriageSelection, -12);
		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(10, 2, 7), -3));
		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(13, 2, 7), -3));
		world.moveSection(carriageElement, vector.of(-9, 0, 0), 60);
		scene.idle(60);

		world.setKineticSpeed(carriageSelection, 0);
		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(10, 2, 7), 0));
		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(13, 2, 7), 0));
		world.cycleBlockProperty(grid.at(10, 3, 7), BlockStateProperties.POWERED);
	}

	public static void options(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		CreateSceneBuilder.WorldInstructions world = scene.world();
		OverlayInstructions overlay = scene.overlay();
		SelectionUtil select = util.select();
		PositionUtil grid = util.grid();
		VectorUtil vector = util.vector();

		scene.title("physics_bogey.options", "header");
		scene.configureBasePlate(0, 0, 9);
		scene.scaleSceneView(0.75F);
		scene.showBasePlate();
		scene.idle(10);
		world.showSection(select.layer(1), Direction.DOWN);
		scene.idle(10);

		BlockPos bogeyPos = grid.at(4, 2, 4);
		Vec3 bogeyTop = vector.topOf(4, 2, 4);
		ElementLink<WorldSectionElement> bogeyElement = world.showIndependentSection(select.position(bogeyPos), Direction.DOWN);
		scene.idle(10);

		overlay.showText(80).
		pointAt(bogeyTop).
		attachKeyFrame().
		placeNearTarget().
		text("1_enabled");
		scene.idle(90);

		scene.addInstruction(CustomAnimateWorldSectionInstruction.move(bogeyElement, new Vec3(0, -1, 0), 10, SmoothMovementUtils.quadraticRise()));
		scene.idle(20);
		scene.addInstruction(CustomAnimateWorldSectionInstruction.move(bogeyElement, new Vec3(0, 1, 0), 10, SmoothMovementUtils.quadraticRise()));
		scene.idle(20);

		overlay.showText(80).
		pointAt(bogeyTop).
		attachKeyFrame().
		placeNearTarget().
		text("2_angular");
		scene.idle(90);

		scene.addInstruction(new PhysicsBogeyPivotRotateInstruction(bogeyPos, Axis.YP.rotationDegrees(-30), 10, SmoothMovementUtils.quadraticJump()));
		scene.idle(1);
		scene.addInstruction(CustomAnimateWorldSectionInstruction.rotate(bogeyElement, new Vec3(0, 30, 0), 10, SmoothMovementUtils.quadraticJump()));
		scene.idle(9);
		scene.addInstruction(new PhysicsBogeyPivotRotateInstruction(bogeyPos, Axis.YP.rotationDegrees(30), 10, SmoothMovementUtils.quadraticJump()));
		scene.idle(1);
		scene.addInstruction(CustomAnimateWorldSectionInstruction.rotate(bogeyElement, new Vec3(0, -30, 0), 10, SmoothMovementUtils.quadraticJump()));
		scene.idle(14);
		scene.addInstruction(new PhysicsBogeyPivotRotateInstruction(bogeyPos, Axis.ZP.rotationDegrees(-30), 10, SmoothMovementUtils.quadraticJump()));
		scene.idle(1);
		scene.addInstruction(CustomAnimateWorldSectionInstruction.rotate(bogeyElement, new Vec3(0, 0, 30), 10, SmoothMovementUtils.quadraticJump()));
		scene.idle(9);
		scene.addInstruction(new PhysicsBogeyPivotRotateInstruction(bogeyPos, Axis.ZP.rotationDegrees(30), 10, SmoothMovementUtils.quadraticJump()));
		scene.idle(1);
		scene.addInstruction(CustomAnimateWorldSectionInstruction.rotate(bogeyElement, new Vec3(0, 0, -30), 10, SmoothMovementUtils.quadraticJump()));
		scene.idle(19);

		overlay.showText(80).
		pointAt(bogeyTop).
		placeNearTarget().
		text("3_angular_default");
		scene.idle(90);

		overlay.showText(80).
		pointAt(bogeyTop).
		attachKeyFrame().
		placeNearTarget().
		text("4_linear");
		scene.idle(90);

		scene.addInstruction(new PhysicsBogeyPivotOffsetInstruction(bogeyPos, new Vector3d(0, 0, -0.5), 10, SmoothMovementUtils.quadraticJump()));
		scene.idle(1);
		scene.addInstruction(CustomAnimateWorldSectionInstruction.move(bogeyElement, new Vec3(0, 0, 0.5), 10, SmoothMovementUtils.quadraticJump()));
		scene.idle(9);
		scene.addInstruction(new PhysicsBogeyPivotOffsetInstruction(bogeyPos, new Vector3d(0, 0, 0.5), 10, SmoothMovementUtils.quadraticJump()));
		scene.idle(1);
		scene.addInstruction(CustomAnimateWorldSectionInstruction.move(bogeyElement, new Vec3(0, 0, -0.5), 10, SmoothMovementUtils.quadraticJump()));
		scene.idle(14);
		scene.addInstruction(new PhysicsBogeyPivotOffsetInstruction(bogeyPos, new Vector3d(0, 0.25, 0), 10, SmoothMovementUtils.quadraticJump()));
		scene.idle(1);
		scene.addInstruction(CustomAnimateWorldSectionInstruction.move(bogeyElement, new Vec3(0, -0.25, 0), 10, SmoothMovementUtils.quadraticJump()));
		scene.idle(9);
		scene.addInstruction(new PhysicsBogeyPivotOffsetInstruction(bogeyPos, new Vector3d(0, -0.25, 0), 10, SmoothMovementUtils.quadraticJump()));
		scene.idle(1);
		scene.addInstruction(CustomAnimateWorldSectionInstruction.move(bogeyElement, new Vec3(0, 0.25, 0), 10, SmoothMovementUtils.quadraticJump()));
		scene.idle(19);

		overlay.showText(80).
		pointAt(bogeyTop).
		attachKeyFrame().
		placeNearTarget().
		text("5_presets");
		scene.idle(90);

		overlay.showText(80).
		pointAt(bogeyTop).
		attachKeyFrame().
		placeNearTarget().
		text("6_vertical");
		scene.idle(90);

		scene.addInstruction(CustomAnimateWorldSectionInstruction.move(bogeyElement, new Vec3(0, 1, 0), 20, SmoothMovementUtils.quadraticJump()));
		scene.idle(30);

		overlay.showText(80).
		pointAt(bogeyTop).
		attachKeyFrame().
		placeNearTarget().
		text("7_offset");
		scene.idle(90);

		scene.addInstruction(new PhysicsBogeyPivotOffsetInstruction(bogeyPos, new Vector3d(0, 0.5, 0), 10, SmoothMovementUtils.quadraticRiseInOut()));
		scene.idle(1);
		scene.addInstruction(CustomAnimateWorldSectionInstruction.move(bogeyElement, new Vec3(0, -0.5, 0), 10, SmoothMovementUtils.quadraticRiseInOut()));
		scene.idle(19);
		scene.addInstruction(new PhysicsBogeyPivotOffsetInstruction(bogeyPos, new Vector3d(0, -0.5, 0), 10, SmoothMovementUtils.quadraticRiseInOut()));
		scene.idle(1);
		scene.addInstruction(CustomAnimateWorldSectionInstruction.move(bogeyElement, new Vec3(0, 0.5, 0), 10, SmoothMovementUtils.quadraticRiseInOut()));
		scene.idle(19);

		overlay.showText(80).
		pointAt(bogeyTop).
		attachKeyFrame().
		placeNearTarget().
		text("8_control");
		scene.idle(90);

		overlay.showText(80).
		pointAt(vector.of(4, 2.5, 4.5)).
		attachKeyFrame().
		placeNearTarget().
		text("9_strength");
		scene.idle(90);

		overlay.showText(80).
		pointAt(bogeyTop).
		attachKeyFrame().
		placeNearTarget().
		text("10_tilt");
		scene.idle(90);

		scene.addInstruction(new PhysicsBogeyPivotRotateInstruction(bogeyPos, Axis.XP.rotationDegrees(-10), 10, SmoothMovementUtils.quadraticJump()));
		scene.idle(1);
		scene.addInstruction(CustomAnimateWorldSectionInstruction.rotate(bogeyElement, new Vec3(10, 0, 0), 10, SmoothMovementUtils.quadraticJump()));
		scene.idle(9);
		scene.addInstruction(new PhysicsBogeyPivotRotateInstruction(bogeyPos, Axis.XP.rotationDegrees(10), 10, SmoothMovementUtils.quadraticJump()));
		scene.idle(1);
		scene.addInstruction(CustomAnimateWorldSectionInstruction.rotate(bogeyElement, new Vec3(-10, 0, 0), 10, SmoothMovementUtils.quadraticJump()));
		scene.idle(19);

		overlay.showText(80).
		pointAt(bogeyTop).
		attachKeyFrame().
		placeNearTarget().
		text("11_probe");
		overlay.showBigLine(PonderPalette.OUTPUT, vector.of(3.5, 1.1875, 4.5), vector.of(0.5, 1.1875, 4.5), 80);
		scene.idle(90);

		overlay.showText(80).
		pointAt(vector.of(3.5, 2.25, 4.5)).
		attachKeyFrame().
		placeNearTarget().
		text("12_connector");
		scene.idle(90);
	}
}
