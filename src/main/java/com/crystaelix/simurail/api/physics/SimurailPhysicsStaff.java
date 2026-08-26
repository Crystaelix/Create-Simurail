package com.crystaelix.simurail.api.physics;

import java.util.Map;
import java.util.UUID;

import com.crystaelix.simurail.mixin.PhysicsStaffDragSessionAccessor;
import com.crystaelix.simurail.mixin.PhysicsStaffServerHandlerAccessor;

import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.simulated_team.simulated.content.physics_staff.PhysicsStaffServerHandler;

public class SimurailPhysicsStaff {

	public static boolean isRestrained(ServerSubLevel subLevel) {
		PhysicsStaffServerHandler handler = PhysicsStaffServerHandler.get(subLevel.getLevel());
		if(handler == null) {
			return false;
		}
		if(handler.isLocked(subLevel)) {
			return true;
		}

		// controlling
		Map<UUID, ?> sessions = ((PhysicsStaffServerHandlerAccessor)handler).simurail$draggingSessions();
		for(Object session : sessions.values()) {
			if(((PhysicsStaffDragSessionAccessor)session).simurail$subLevel() == subLevel) {
				return true;
			}
		}
		return false;
	}

	private SimurailPhysicsStaff() {}
}
