package com.crystaelix.simurail.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import dev.ryanhcode.sable.sublevel.ServerSubLevel;

@Mixin(targets = "dev.simulated_team.simulated.content.physics_staff.PhysicsStaffServerHandler$DragSession")
public interface PhysicsStaffDragSessionAccessor {

	@Accessor("subLevel")
	ServerSubLevel simurail$subLevel();
}
