package com.crystaelix.simurail.content.gangway_frame;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.joml.Vector3d;
import org.joml.Vector3dc;

import com.crystaelix.simurail.api.math.HorizontalRelativeDirection;
import com.crystaelix.simurail.api.math.SimurailMath;
import com.crystaelix.simurail.api.physics.HorizontalPointing;
import com.crystaelix.simurail.api.util.SubLevelUtil;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public interface GangwayFrame extends HorizontalPointing {

	BlockPos getBlockPos();

	GangwayFrameShape getGangwayShape();

	Vector3d getGangwayAnchor(Vector3d dest);

	boolean isPowered();

	boolean isGangwayPowered();

	void setGangwayPartner(BlockPos gangwayPartnerPos);

	void setGangwayPartnerReverse(BlockPos gangwayPartnerPos);

	void removeGangwayPartner();

	GangwayFrame getGangwayPartner();

	default Set<GangwayFrame> getNeighbors(Level level, int range) {
		Set<GangwayFrame> neighbors = new LinkedHashSet<>();
		Couple<GangwayFrame> couple = Couple.create(this, this);
		for(boolean f : Iterate.trueAndFalse) {
			GangwayFrame curr = this;
			for(int i = 0; i < range; ++i) {
				if(couple.get(f) != null) {
					GangwayFrameShape shape = curr.getGangwayShape();
					HorizontalRelativeDirection offset = shape.adjacentOffset(f);
					BlockPos pos = curr.getBlockPos().relative(offset.transform(curr.getFacing()));
					if(level.getBlockEntity(pos) instanceof GangwayFrame neighbor &&
							!neighbors.contains(neighbor) &&
							curr.getFacing() == neighbor.getFacing()) {
						GangwayFrameShape neighborShape = neighbor.getGangwayShape();
						if(shape.isAdjacentTo(offset, neighborShape) ||
								neighborShape.isAdjacentTo(offset.opposite(), shape)) {
							neighbors.add(neighbor);
							curr = neighbor;
							if(neighborShape.adjacentOffset(!f) != offset.opposite()) {
								f = !f;
							}
							continue;
						}
					}
					break;
				}
			}
		}
		return neighbors;
	}

	default GangwayFrame findGangwayPartner(Level level) {
		SubLevel selfSubLevel = Sable.HELPER.getContaining(level, getBlockPos());
		Pose3dc selfPose = selfSubLevel == null ? SimurailMath.POSE_I : selfSubLevel.logicalPose();

		Vector3dc globalSelfVert = selfPose.transformNormal(SimurailMath.DIR_YP, new Vector3d());
		Vector3dc globalSelfDir = selfPose.transformNormal(getDirection(), new Vector3d());
		Vector3dc globalSelfCenter = selfPose.transformPosition(getGangwayAnchor(new Vector3d()));
		Vector3dc globalStartPos = new Vector3d(globalSelfCenter).add(globalSelfDir);

		double score = Double.POSITIVE_INFINITY;
		GangwayFrame partner = null;

		MutableBlockPos checkPos = new MutableBlockPos();
		Vector3d checkSelfVert = new Vector3d();
		Vector3d checkStartPos = new Vector3d();
		Vector3d checkSelfDir = new Vector3d();
		Vector3d checkSelfCenter = new Vector3d();
		Vector3d checkCenter = new Vector3d();
		Vector3d checkOffset = new Vector3d();

		for(SubLevel checkSubLevel : SubLevelUtil.getIntersectingSubLevels(level, globalStartPos, 2)) {
			Pose3dc checkPose = checkSubLevel == null ? SimurailMath.POSE_I : checkSubLevel.logicalPose();
			checkPose.transformNormalInverse(globalSelfVert, checkSelfVert);
			if(checkSelfVert.y < 0.7) {
				continue;
			}
			checkPose.transformPositionInverse(globalStartPos, checkStartPos);
			checkPose.transformNormalInverse(globalSelfDir, checkSelfDir);
			checkPose.transformPositionInverse(globalSelfCenter, checkSelfCenter);
			for(int x = Mth.floor(checkStartPos.x) - 2; x < Mth.ceil(checkStartPos.x) + 2; ++x) {
				for(int y = Mth.floor(checkStartPos.y) - 2; y < Mth.ceil(checkStartPos.y) + 2; ++y) {
					for(int z = Mth.floor(checkStartPos.z) - 2; z < Mth.ceil(checkStartPos.z) + 2; ++z) {
						checkPos.set(x, y, z);
						if(level.getBlockEntity(checkPos) instanceof GangwayFrame checkPartner &&
								checkPartner != this &&
								checkPartner.getGangwayPartner() == null &&
								getGangwayShape().canConnectTo(checkPartner.getGangwayShape()) &&
								!checkPartner.isGangwayPowered()) {
							double dot = checkSelfDir.dot(checkPartner.getDirection());
							if(dot > -0.7) {
								continue;
							}
							checkPartner.getGangwayAnchor(checkCenter);
							checkCenter.sub(checkSelfCenter, checkOffset);
							if(checkSelfDir.dot(checkOffset) < 0.01) {
								continue;
							}
							double distScore = checkCenter.distanceSquared(checkSelfCenter);
							if(distScore > 9) {
								continue;
							}
							double orientScore = 1 + dot;
							double checkScore = distScore + 0.04 * orientScore;
							if(checkScore < score) {
								score = checkScore;
								partner = checkPartner;
							}
						}
					}
				}
			}
		}

		return partner;
	}

	default void tryConnectGangway(Level level, int range) {
		if(getGangwayShape() == GangwayFrameBlockShape.NONE || isGangwayPowered()) {
			return;
		}
		GangwayFrame partner = findGangwayPartner(level);
		if(partner == null) {
			return;
		}
		setGangwayPartner(partner.getBlockPos());
		Set<GangwayFrame> visited = new HashSet<>();
		visited.add(this);
		for(boolean f : Iterate.trueAndFalse) {
			GangwayFrame cSelf = this;
			GangwayFrame cPartner = partner;
			for(int i = 0; i < range; ++i) {
				GangwayFrameShape selfShape = cSelf.getGangwayShape();
				HorizontalRelativeDirection selfOffset = selfShape.adjacentOffset(f);
				HorizontalRelativeDirection partnerOffset = selfOffset.mirror();
				BlockPos selfPos = cSelf.getBlockPos().relative(selfOffset.transform(cSelf.getFacing()));
				BlockPos partnerPos = cPartner.getBlockPos().relative(partnerOffset.transform(cPartner.getFacing()));
				if(level.getBlockEntity(selfPos) instanceof GangwayFrame selfNeighbor &&
						level.getBlockEntity(partnerPos) instanceof GangwayFrame partnerNeighbor &&
						!visited.contains(selfNeighbor) &&
						cSelf.getFacing() == selfNeighbor.getFacing()) {
					GangwayFrameShape neighborShape = selfNeighbor.getGangwayShape();
					if((selfShape.isAdjacentTo(selfOffset, neighborShape) ||
							neighborShape.isAdjacentTo(selfOffset.opposite(), selfShape)) &&
							cPartner.getFacing() == partnerNeighbor.getFacing() &&
							neighborShape.canConnectTo(partnerNeighbor.getGangwayShape())) {
						visited.add(selfNeighbor);
						cSelf = selfNeighbor;
						cPartner = partnerNeighbor;
						selfNeighbor.setGangwayPartner(partnerPos);
						if(neighborShape.adjacentOffset(!f) != selfOffset.opposite()) {
							f = !f;
						}
						continue;
					}
				}
				break;
			}
		}
	}

	default void tryDisconnectGangway(Level level, int range) {
		removeGangwayPartner();
		if(getGangwayShape() == GangwayFrameBlockShape.NONE) {
			return;
		}
		getNeighbors(level, range).forEach(GangwayFrame::removeGangwayPartner);
	}
}
