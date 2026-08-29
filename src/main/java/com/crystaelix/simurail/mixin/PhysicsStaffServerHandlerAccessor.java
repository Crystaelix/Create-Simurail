package com.crystaelix.simurail.mixin;

import java.util.Map;
import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import dev.simulated_team.simulated.content.physics_staff.PhysicsStaffServerHandler;

@Mixin(PhysicsStaffServerHandler.class)
public interface PhysicsStaffServerHandlerAccessor {

	@Accessor("draggingSessions")
	Map<UUID, ?> simurail$draggingSessions();
}
