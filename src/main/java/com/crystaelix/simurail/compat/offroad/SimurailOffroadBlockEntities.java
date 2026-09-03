package com.crystaelix.simurail.compat.offroad;

import com.crystaelix.simurail.Simurail;
import com.crystaelix.simurail.compat.offroad.brass_borehead_bearing.BrassBoreheadBearingBlockEntity;
import com.crystaelix.simurail.compat.offroad.brass_borehead_bearing.BrassBoreheadBearingRenderer;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import dev.ryanhcode.offroad.content.blocks.borehead_bearing.BoreheadBearingVisual;
import dev.simulated_team.simulated.service.SimInventoryService;

public class SimurailOffroadBlockEntities {

	private static final CreateRegistrate REGISTRATE = Simurail.registrate();

	public static final BlockEntityEntry<BrassBoreheadBearingBlockEntity> BRASS_BOREHEAD_BEARING = REGISTRATE.
			blockEntity("brass_borehead_bearing", BrassBoreheadBearingBlockEntity::new).
			visual(() -> BoreheadBearingVisual::new).
			renderer(() -> BrassBoreheadBearingRenderer::new).
			validBlock(SimurailOffroadBlocks.BRASS_BOREHEAD_BEARING).
			onRegister(SimInventoryService.INSTANCE.registerInventory((be, dir) -> be.getContraptionWrappedInventory())).
			register();

	public static void register() {
	}
}
