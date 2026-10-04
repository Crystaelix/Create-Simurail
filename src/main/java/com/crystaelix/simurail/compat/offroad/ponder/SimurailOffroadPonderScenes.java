package com.crystaelix.simurail.compat.offroad.ponder;

import com.crystaelix.simurail.compat.offroad.SimurailOffroadBlocks;
import com.crystaelix.simurail.extension.PonderSceneRegistrationHelperExtension;

import dev.ryanhcode.offroad.Offroad;
import dev.ryanhcode.offroad.content.ponder.scenes.BoreheadBearingScenes;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

public class SimurailOffroadPonderScenes {

	// Offroad
	private static final ResourceLocation BOREHEAD_BEARING_INTRO = Offroad.path("borehead_bearing/intro");
	private static final ResourceLocation BOREHEAD_BEARING_EXCAVATING = Offroad.path("mechanical_roller/excavating");
	private static final ResourceLocation BOREHEAD_BEARING_EFFICIENCY = Offroad.path("mechanical_roller/efficiency");

	public static void register(PonderSceneRegistrationHelperExtension<ItemLike> helper) {
		PonderSceneRegistrationHelper<ItemLike> offroadHelper = helper.simurail$withNamespace("offroad");

		offroadHelper.forComponents(SimurailOffroadBlocks.BRASS_BOREHEAD_BEARING).
		addStoryBoard(BOREHEAD_BEARING_INTRO, BoreheadBearingScenes::boreheadIntro).
		addStoryBoard(BOREHEAD_BEARING_EXCAVATING, BoreheadBearingScenes::boreheadExcavating).
		addStoryBoard(BOREHEAD_BEARING_EFFICIENCY, BoreheadBearingScenes::boreheadEfficiency);
	}
}
