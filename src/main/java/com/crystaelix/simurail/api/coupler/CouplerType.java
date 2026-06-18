package com.crystaelix.simurail.api.coupler;

import java.util.List;

import com.crystaelix.simurail.api.bogey.BogeyType;
import com.crystaelix.simurail.api.bogey.menu.BogeyDataOption;

import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CouplerType {

	private final ResourceLocation id;
	private final Component displayName;
	private final ResourceLocation modelId;

	public CouplerType(ResourceLocation id, Component displayName, ResourceLocation modelId) {
		this.id = id;
		this.displayName = displayName;
		this.modelId = modelId;
	}

	public CouplerType(ResourceLocation id, ResourceLocation modelId) {
		this(id, Component.translatable(Util.makeDescriptionId("simurail_coupler_type", id)), modelId);
	}

	public ResourceLocation id() {
		return id;
	}

	public Component displayName() {
		return displayName;
	}

	public ResourceLocation modelId() {
		return modelId;
	}

	public boolean canConnectTo(CouplerType other) {
		return this == other;
	}
}
