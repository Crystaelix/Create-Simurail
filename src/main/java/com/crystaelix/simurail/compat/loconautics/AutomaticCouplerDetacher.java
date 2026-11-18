package com.crystaelix.simurail.compat.loconautics;

import java.util.UUID;

import com.crystaelix.simurail.content.automatic_coupler.AutomaticCoupler;
import com.lycoris.loconautics.common.ConnectorDetacher;
import com.lycoris.loconautics.common.sable.SubLevelData;

import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.plot.ServerLevelPlot;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public class AutomaticCouplerDetacher implements ConnectorDetacher {

	@Override
	public int detachAll(ServerLevel level, UUID subLevelID) {
		ServerSubLevel subLevel = SubLevelData.resolve(level, subLevelID);
		if(subLevel == null) {
			return 0;
		}
		int count = 0;
		for(BlockEntitySubLevelActor actor : subLevel.getPlot().getBlockEntityActors()) {
			if(actor instanceof AutomaticCoupler coupler && coupler.hasCouplerPartner()) {
				coupler.removeCouplerPartner();
				++count;
			}
		}
		return count;
	}

	@Override
	public boolean decoupleBetween(ServerLevel level, UUID subLevelID1, UUID subLevelID2) {
		ServerSubLevel subLevel1 = SubLevelData.resolve(level, subLevelID1);
		ServerSubLevel subLevel2 = SubLevelData.resolve(level, subLevelID2);
		if(subLevel1 == null || subLevel2 == null) {
			return false;
		}
		ServerLevelPlot plot1 = subLevel1.getPlot();
		ServerLevelPlot plot2 = subLevel2.getPlot();
		boolean split = false;
		for(BlockEntitySubLevelActor actor : plot1.getBlockEntityActors()) {
			if(actor instanceof AutomaticCoupler coupler && coupler.hasCouplerPartner()) {
				BlockPos partnerPos = coupler.getCouplerPartner();
				if(plot2.contains(partnerPos.getX(), partnerPos.getZ())) {
					coupler.removeCouplerPartner();
					split = true;
				}
			}
		}
		return split;
	}
}
