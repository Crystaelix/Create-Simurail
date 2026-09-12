package com.crystaelix.simurail.content.automatic_coupler;

import org.joml.Quaterniond;
import org.joml.Vector3d;

import com.crystaelix.simurail.api.coupler.CouplerType;
import com.crystaelix.simurail.api.physics.HorizontalPointing;
import com.crystaelix.simurail.content.connector.ConnectorConnectable;

import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

public interface AutomaticCoupler extends HorizontalPointing, ConnectorConnectable {

	boolean isPowered();

	void setCouplerPartner(BlockPos couplerPartnerPos);

	void setCouplerPartnerReverse(BlockPos couplerPartnerPos);

	void removeCouplerPartner();

	boolean hasCouplerJoint();

	void removeCouplerJoint();

	CouplerType getCouplerType();

	double getCouplerLength();

	default Vector3d getCouplerEndPos(Vector3d dest) {
		return JOMLConversion.atCenterOf(getBlockPos(), dest).fma(getCouplerLength() + 0.0625 - 0.5, getDirection());
	}

	default Vector3d getCouplerJointPos(Vector3d dest) {
		return JOMLConversion.atCenterOf(getBlockPos(), dest).fma(0.0625 - 0.5, getDirection());
	}

	Quaterniond getCouplerJointRot(Quaterniond dest);

	BlockPos getConnectedBogeyPos();

	boolean getConnectedBogeyFront();

	void updateCouplerJointPos(AutomaticCoupler partner, Pose3dc partnerPose);

	@Override
	default AABB getOutline(Direction direction) {
		return AABB.ofSize(
				getBlockPos().getCenter().add(direction.getStepX() * 0.40625, 0, direction.getStepZ() * 0.40625),
				direction.getStepX() == 0 ? 0.5 : 0.1875, 0.375, direction.getStepZ() == 0 ? 0.5 : 0.1875);
	}
}
