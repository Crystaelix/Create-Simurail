package com.crystaelix.simurail.content.gangway_frame;

import java.util.LinkedHashSet;
import java.util.Set;

import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import com.crystaelix.simurail.api.math.SimurailMath;
import com.crystaelix.simurail.api.util.SubLevelUtil;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public interface GangwayFrame {

	BlockPos getBlockPos();

	Direction getFacing();

	GangwayFrameShape getGangwayShape();

	Vector3d getGangwayCenter(Vector3d dest);

	default Vector3dc getDirection() {
		return switch(getFacing()) {
		case EAST -> SimurailMath.DIR_XP;
		case WEST -> SimurailMath.DIR_XN;
		case SOUTH -> SimurailMath.DIR_ZP;
		case NORTH -> SimurailMath.DIR_ZN;
		case null, default -> throw new IllegalArgumentException("Unexpected value: " + getFacing());
		};
	}

	default Quaterniondc getOrientation() {
		return switch(getFacing()) {
		case EAST -> SimurailMath.ROT_XPYPZP;
		case WEST -> SimurailMath.ROT_XNYPZN;
		case SOUTH -> SimurailMath.ROT_ZPYPXN;
		case NORTH -> SimurailMath.ROT_ZNYPXP;
		case null, default -> throw new IllegalArgumentException("Unexpected value: " + getFacing());
		};
	}

	boolean isPowered();

	boolean isGangwayPowered();

	void setGangwayPartner(BlockPos gangwayPartnerPos);

	void setGangwayPartnerReverse(BlockPos gangwayPartnerPos);

	void removeGangwayPartner();

	GangwayFrame getGangwayPartner();

	static Set<GangwayFrame> getNeighbors(GangwayFrame self, Level level, int range) {
		Set<GangwayFrame> neighbors = new LinkedHashSet<>();
		Couple<GangwayFrame> couple = Couple.create(self, self);
		for(int i = 0; i < range; ++i) {
			for(boolean cw : Iterate.trueAndFalse) {
				if(couple.get(cw) != null) {
					GangwayFrame curr = couple.get(cw);
					GangwayFrameShape shape = curr.getGangwayShape();
					Direction offset = shape.adjacentOffset(curr.getFacing(), cw);
					BlockPos pos = curr.getBlockPos().relative(offset);
					if(level.getBlockEntity(pos) instanceof GangwayFrame neighbor &&
							!neighbors.contains(neighbor) &&
							curr.getFacing() == neighbor.getFacing()) {
						GangwayFrameShape neighborShape = neighbor.getGangwayShape();
						int currAdj = shape.adjacentTo(neighborShape, cw);
						int neighborAdj = neighborShape.adjacentTo(shape, !cw);
						if(currAdj != 0 || neighborAdj != 0) {
							neighbors.add(neighbor);
							couple.set(cw, neighbor);
							if(currAdj < 0 || neighborAdj < 0) {
								cw = !cw;
							}
							continue;
						}
					}
					couple.set(cw, null);
				}
			}
		}
		return neighbors;
	}

	static GangwayFrame findGangwayPartner(GangwayFrame self, Level level) {
		SubLevel selfSubLevel = Sable.HELPER.getContaining(level, self.getBlockPos());
		Pose3dc selfPose = selfSubLevel == null ? SimurailMath.POSE_I : selfSubLevel.logicalPose();
		GangwayFrameShape expectedShape = self.getGangwayShape().connectsTo();

		Vector3dc globalSelfVert = selfPose.transformNormal(SimurailMath.DIR_YP, new Vector3d());
		Vector3dc globalSelfDir = selfPose.transformNormal(self.getDirection(), new Vector3d());
		Vector3dc globalSelfCenter = selfPose.transformPosition(self.getGangwayCenter(new Vector3d()));
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
								checkPartner != self &&
								checkPartner.getGangwayPartner() == null &&
								checkPartner.getGangwayShape() == expectedShape &&
								!checkPartner.isGangwayPowered()) {
							double dot = checkSelfDir.dot(checkPartner.getDirection());
							if(dot > -0.7) {
								continue;
							}
							checkPartner.getGangwayCenter(checkCenter);
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
}
