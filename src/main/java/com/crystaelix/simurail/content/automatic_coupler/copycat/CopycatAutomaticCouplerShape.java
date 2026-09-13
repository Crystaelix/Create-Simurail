package com.crystaelix.simurail.content.automatic_coupler.copycat;

import java.util.Locale;

import net.minecraft.util.StringRepresentable;

public enum CopycatAutomaticCouplerShape implements StringRepresentable {
	FULL,
	TOP,
	BOTTOM;

	@Override
	public String getSerializedName() {
		return name().toLowerCase(Locale.ROOT);
	}
}
