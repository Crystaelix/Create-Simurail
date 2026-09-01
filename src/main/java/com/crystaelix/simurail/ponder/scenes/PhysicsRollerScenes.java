package com.crystaelix.simurail.ponder.scenes;

import com.crystaelix.simurail.content.SimurailBlocks;
import com.crystaelix.simurail.content.physics_roller.PhysicsRollerBlockEntity;
import com.crystaelix.simurail.ponder.instruction.PhysicsBogeyVisualSpeedInstruction;
import com.crystaelix.simurail.ponder.instruction.SceneRotationInstruction;
import com.simibubi.create.AllItems;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.phys.Vec3;

public class PhysicsRollerScenes {

	// the value boxes sit on the back half of a roller's top face, which faces west here
	private static final double SLOT_X = 5 / 16D;
	private static final double MODE_Z = 5 / 16D;
	private static final double FILTER_Z = 11 / 16D;

	// one block of travel
	private static final int STEP_TICKS = 12;
	private static final int BREAKING_STAGES = 10;
	private static final int STAGES_PER_TICK = 2;
	private static final int BREAKING_TICKS = BREAKING_STAGES / STAGES_PER_TICK;
	private static final float ROLLING_SPEED = -100;
	private static final float BELT_SPEED = 16;
	private static final double BOGEY_SPEED = 20D / STEP_TICKS;

	public static void intro(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		CreateSceneBuilder.WorldInstructions world = scene.world();
		OverlayInstructions overlay = scene.overlay();
		SelectionUtil select = util.select();
		PositionUtil grid = util.grid();
		VectorUtil vector = util.vector();

		scene.title("physics_roller.intro", "header");
		scene.configureBasePlate(0, 0, 15);
		scene.addInstruction(new SceneRotationInstruction(180));
		scene.showBasePlate();
		scene.idle(10);

		world.showSection(trackSelection(select), Direction.DOWN);
		scene.idle(10);

		ElementLink<WorldSectionElement> contraption = world.showIndependentSection(contraptionSelection(select), Direction.DOWN);
		scene.idle(15);

		overlay.showText(80).
		pointAt(vector.centerOf(5, 2, 7)).
		attachKeyFrame().
		placeNearTarget().
		text("1_simulated_contraptions");
		scene.idle(90);

		overlay.showControls(vector.of(5.5, 3, 9.5), Pointing.DOWN, 40).
		rightClick().
		withItem(SimurailBlocks.PHYSICS_ROLLER.asStack());
		overlay.showText(70).
		pointAt(vector.centerOf(5, 2, 9)).
		attachKeyFrame().
		placeNearTarget().
		text("2_own_block");
		scene.idle(80);

		overlay.showOutline(PonderPalette.RED, "power", wireSelection(select), 120);
		overlay.showControls(vector.of(4.5, 4, 9.5), Pointing.DOWN, 40).
		rightClick();
		scene.idle(30);

		switchOn(world, select);
		scene.idle(20);

		overlay.showText(70).
		pointAt(vector.centerOf(4, 3, 9)).
		attachKeyFrame().
		placeNearTarget().
		colored(PonderPalette.RED).
		text("3_redstone");
		scene.idle(80);

		overlay.showText(70).
		pointAt(vector.centerOf(6, 2, 7)).
		attachKeyFrame().
		placeNearTarget().
		text("4_alignment");
		scene.idle(80);

		world.showSection(riseSelection(select), Direction.DOWN);
		scene.idle(15);

		overlay.showText(70).
		pointAt(vector.centerOf(5, 3, 7)).
		attachKeyFrame().
		placeNearTarget().
		colored(PonderPalette.INPUT).
		text("5_material_source");
		scene.idle(80);

		int x = travel(scene, world, select, grid, vector, contraption);
		scene.idle(20);

		overlay.showScrollInput(slot(vector, x, 9, MODE_Z), Direction.UP, 60);
		overlay.showText(70).
		pointAt(slot(vector, x, 9, MODE_Z)).
		attachKeyFrame().
		placeNearTarget().
		text("6_modes");
		scene.idle(80);
	}

	public static void materials(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		CreateSceneBuilder.WorldInstructions world = scene.world();
		OverlayInstructions overlay = scene.overlay();
		SelectionUtil select = util.select();
		PositionUtil grid = util.grid();
		VectorUtil vector = util.vector();

		scene.title("physics_roller.materials", "header");
		scene.configureBasePlate(0, 0, 15);
		scene.addInstruction(new SceneRotationInstruction(180));
		scene.showBasePlate();
		scene.idle(10);

		world.showSection(trackSelection(select), Direction.DOWN);
		scene.idle(10);

		// chest -> vault
		world.showSection(contraptionSelection(select).substract(vaultSelection(select)).substract(chestSelection(select)), Direction.DOWN);
		scene.idle(10);

		ElementLink<WorldSectionElement> chest = world.showIndependentSection(chestSelection(select), Direction.DOWN);
		scene.idle(15);

		overlay.showControls(vector.of(4.5, 4, 9.5), Pointing.DOWN, 30).
		rightClick();
		scene.idle(15);
		switchOn(world, select);
		scene.idle(20);

		overlay.showFilterSlotInput(slot(vector, 5, 9, FILTER_Z), Direction.UP, 120);
		overlay.showControls(slot(vector, 5, 9, FILTER_Z), Pointing.DOWN, 120).
		rightClick().
		withItem(new ItemStack(Items.ANDESITE));
		overlay.showText(110).
		pointAt(slot(vector, 5, 9, FILTER_Z)).
		attachKeyFrame().
		placeNearTarget().
		colored(PonderPalette.INPUT).
		text("1_filter");
		scene.idle(80);

		world.setFilterData(lineSelection(select), PhysicsRollerBlockEntity.class, new ItemStack(Items.ANDESITE));
		scene.idle(50);

		overlay.showOutline(PonderPalette.OUTPUT, "line", lineSelection(select), 80);
		overlay.showText(70).
		pointAt(vector.centerOf(5, 2, 7)).
		attachKeyFrame().
		placeNearTarget().
		colored(PonderPalette.OUTPUT).
		text("2_shared_settings");
		scene.idle(80);

		overlay.showOutline(PonderPalette.INPUT, "chest", chestSelection(select), 90);
		overlay.showBigLine(PonderPalette.INPUT, vector.centerOf(5, 3, 9), vector.centerOf(5, 3, 5), 90);
		overlay.showText(70).
		pointAt(vector.centerOf(5, 3, 9)).
		attachKeyFrame().
		placeNearTarget().
		colored(PonderPalette.INPUT).
		text("3_nearest_container");
		scene.idle(80);

		setRolling(scene, world, grid, ROLLING_SPEED, 0);
		for(int stage = 0; stage < BREAKING_STAGES; ++stage) {
			crackColumn(world, grid, 5, false);
			scene.idle(2);
		}
		setRolling(scene, world, grid, 0, 0);
		scene.idle(10);

		overlay.showBigLine(PonderPalette.OUTPUT, vector.centerOf(5, 1, 9), vector.centerOf(5, 3, 5), 80);
		overlay.showControls(vector.centerOf(5, 4, 5), Pointing.DOWN, 80).
		withItem(new ItemStack(Items.COBBLESTONE));
		overlay.showText(70).
		pointAt(vector.centerOf(5, 3, 5)).
		attachKeyFrame().
		placeNearTarget().
		colored(PonderPalette.OUTPUT).
		text("4_collecting");
		scene.idle(80);

		scene.rotateCameraY(90);
		world.hideIndependentSection(chest, Direction.UP);
		scene.idle(20);

		world.showSection(vaultSelection(select), Direction.DOWN);
		overlay.showOutline(PonderPalette.INPUT, "vault", vaultSelection(select), 160);
		scene.idle(35);

		world.showSection(rigSelection(select), Direction.DOWN);
		scene.idle(20);
		runBelts(world, select, BELT_SPEED);

		overlay.showControls(aboveFunnel(vector, 6), Pointing.DOWN, 110).
		withItem(new ItemStack(Items.ANDESITE));
		overlay.showControls(aboveFunnel(vector, 8), Pointing.DOWN, 110).
		withItem(AllItems.FILTER.asStack());
		overlay.showBigLine(PonderPalette.INPUT, vector.centerOf(2, 4, 6), vector.centerOf(4, 4, 6), 110);
		overlay.showBigLine(PonderPalette.OUTPUT, vector.centerOf(4, 4, 8), vector.centerOf(2, 4, 8), 110);

		overlay.showText(110).
		pointAt(vector.centerOf(5, 4, 7)).
		attachKeyFrame().
		colored(PonderPalette.INPUT).
		text("5_storages");
		scene.idle(120);

		runBelts(world, select, 0);
	}

	// carries the line east & paving behind
	private static int travel(CreateSceneBuilder scene, CreateSceneBuilder.WorldInstructions world, SelectionUtil select, PositionUtil grid, VectorUtil vector, ElementLink<WorldSectionElement> contraption) {
		setRolling(scene, world, grid, ROLLING_SPEED, BOGEY_SPEED);

		int x = 5;
		while(x < 12) {
			++x;
			world.moveSection(contraption, vector.of(1, 0, 0), STEP_TICKS);

			// ticks of the approach rather than on arrival
			for(int stage = 0; stage < BREAKING_STAGES; ++stage) {
				crackColumn(world, grid, x, true);
				if(stage % STAGES_PER_TICK == STAGES_PER_TICK - 1) {
					scene.idle(1);
				}
			}

			world.replaceBlocks(select.fromTo(x, 0, 5, x, 0, 9), Blocks.ANDESITE.defaultBlockState(), true);
			scene.idle(STEP_TICKS - BREAKING_TICKS);
		}

		setRolling(scene, world, grid, 0, 0);
		return x;
	}

	private static void crackColumn(CreateSceneBuilder.WorldInstructions world, PositionUtil grid, int x, boolean tunnel) {
		// crack all but tracks
		for(int z = 5; z <= 9; ++z) {
			if(z != 7) {
				world.incrementBlockBreakingProgress(grid.at(x, 1, z));
			}
			if(tunnel) {
				world.incrementBlockBreakingProgress(grid.at(x, 2, z));
			}
		}
	}

	private static void setRolling(CreateSceneBuilder scene, CreateSceneBuilder.WorldInstructions world, PositionUtil grid, float wheelSpeed, double bogeySpeed) {
		for(int z = 5; z <= 9; ++z) {
			world.modifyBlockEntity(grid.at(5, 2, z), PhysicsRollerBlockEntity.class, be -> be.setAnimatedSpeed(wheelSpeed));
		}
		scene.addInstruction(new PhysicsBogeyVisualSpeedInstruction(grid.at(2, 2, 7), bogeySpeed));
	}

	private static void runBelts(CreateSceneBuilder.WorldInstructions world, SelectionUtil select, float speed) {
		world.setKineticSpeed(beltSelection(select, 6), -speed);
		world.setKineticSpeed(beltSelection(select, 8), speed);
	}

	private static void switchOn(CreateSceneBuilder.WorldInstructions world, SelectionUtil select) {
		world.setBlocks(select.position(4, 3, 9), Blocks.LEVER.defaultBlockState().
				setValue(BlockStateProperties.ATTACH_FACE, AttachFace.FLOOR).
				setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST).
				setValue(BlockStateProperties.POWERED, true), false);

		int power = 15;
		for(int z = 8; z >= 5; --z, --power) {
			world.setBlocks(select.position(4, 3, z), Blocks.REDSTONE_WIRE.defaultBlockState().
					setValue(BlockStateProperties.POWER, power).
					setValue(BlockStateProperties.NORTH_REDSTONE, RedstoneSide.SIDE).
					setValue(BlockStateProperties.SOUTH_REDSTONE, RedstoneSide.SIDE), false);
		}
	}

	private static Vec3 slot(VectorUtil vector, int x, int z, double alongLine) {
		return vector.of(x + SLOT_X, 3, z + alongLine);
	}

	private static Vec3 aboveFunnel(VectorUtil vector, int z) {
		return vector.centerOf(4, 5, z).add(0, 0.5, 0);
	}

	private static Selection contraptionSelection(SelectionUtil select) {
		return select.fromTo(2, 2, 5, 5, 3, 9);
	}

	private static Selection lineSelection(SelectionUtil select) {
		return select.fromTo(5, 2, 5, 5, 2, 9);
	}

	private static Selection wireSelection(SelectionUtil select) {
		return select.fromTo(4, 3, 5, 4, 3, 9);
	}

	private static Selection chestSelection(SelectionUtil select) {
		return select.position(5, 3, 5);
	}

	private static Selection vaultSelection(SelectionUtil select) {
		return select.fromTo(5, 3, 6, 5, 5, 8);
	}

	private static Selection rigSelection(SelectionUtil select) {
		return select.fromTo(2, 4, 5, 4, 5, 9);
	}

	private static Selection beltSelection(SelectionUtil select, int z) {
		return select.fromTo(2, 4, z, 4, 4, z);
	}

	private static Selection trackSelection(SelectionUtil select) {
		return select.fromTo(0, 1, 7, 14, 1, 7);
	}

	private static Selection riseSelection(SelectionUtil select) {
		return select.fromTo(7, 1, 5, 10, 2, 9).substract(trackSelection(select));
	}
}
