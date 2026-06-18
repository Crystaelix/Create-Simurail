package com.crystaelix.simurail.mixin;

import java.util.Map;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeyStyle;

@Mixin(BogeyStyle.class)
public interface BogeyStyleAccessor {

	@Accessor("sizes")
	Map<BogeySizes.BogeySize, Supplier<? extends AbstractBogeyBlock<?>>> simurail$sizes();
}
