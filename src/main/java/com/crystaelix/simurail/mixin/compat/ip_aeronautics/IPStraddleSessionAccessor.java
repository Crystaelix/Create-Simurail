package com.crystaelix.simurail.mixin.compat.ip_aeronautics;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import qouteall.imm_ptl.core.compat.sable_straddle.IPPortalIsometry;
import qouteall.imm_ptl.core.compat.ship_portals.IPShipPortalAnchors;
import qouteall.imm_ptl.core.portal.Portal;

@Mixin(targets = "qouteall.imm_ptl.core.compat.sable_straddle.IPStraddleSessions$Session")
public interface IPStraddleSessionAccessor {

	@Accessor("ship")
	ServerSubLevel simurail$subLevel();
	@Accessor("portal")
	Portal simurail$portal();
	@Accessor("source")
	ServerLevel simurail$srcLevel();
	@Accessor("dest")
	ServerLevel simurail$dstLevel();
	@Accessor("isometry")
	IPPortalIsometry simurail$isometry();
	@Accessor("normal")
	Vec3 simurail$normal();
	@Accessor("origin")
	Vec3 simurail$origin();
	@Accessor("mount")
	IPShipPortalAnchors.Mount simurail$mount();
}
