package com.crystaelix.simurail.content.physics_roller;

import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.gui.AllIcons;

import net.createmod.catnip.lang.Lang;

public enum PhysicsRollerMode implements INamedIconOptions {

	TUNNEL_PAVE(AllIcons.I_ROLLER_PAVE),
	STRAIGHT_FILL(AllIcons.I_ROLLER_FILL),
	WIDE_FILL(AllIcons.I_ROLLER_WIDE_FILL);

	private final AllIcons icon;
	private final String translationKey;

	PhysicsRollerMode(AllIcons icon) {
		this.icon = icon;
		this.translationKey = "gui.simurail.physics_roller.mode." + Lang.asId(name());
	}

	@Override
	public AllIcons getIcon() {
		return icon;
	}

	@Override
	public String getTranslationKey() {
		return translationKey;
	}
}
