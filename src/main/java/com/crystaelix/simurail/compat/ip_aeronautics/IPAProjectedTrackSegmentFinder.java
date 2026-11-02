package com.crystaelix.simurail.compat.ip_aeronautics;

import java.util.Objects;
import java.util.Set;

import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import com.crystaelix.simurail.api.math.SimurailMath;
import com.crystaelix.simurail.content.track.TrackSegment;
import com.crystaelix.simurail.content.track.TrackSegmentFinder;
import com.crystaelix.simurail.content.track.TrackSegmentHelper;
import com.crystaelix.simurail.mixin.compat.ip_aeronautics.IPStraddleSessionAccessor;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import it.unimi.dsi.fastutil.objects.ObjectDoublePair;
import net.minecraft.world.level.Level;
import qouteall.imm_ptl.core.compat.sable_straddle.IPPortalIsometry;
import qouteall.imm_ptl.core.compat.ship_portals.IPShipPortalAnchors;

public class IPAProjectedTrackSegmentFinder {

	private static final TrackSegmentFinder FINDER = IPAProjectedTrackSegmentFinder::findProjectedTrackSegment;

	static {
		TrackSegmentHelper.addSegmentFinder(FINDER, -1000);
	}

	public static void register() {
	}

	public static ObjectDoublePair<IPAProjectedTrackSegment> findProjectedTrackSegment(Level level, SubLevel subLevel, Vector3dc globalPos, Vector3dc globalDir, Vector3dc globalVert, boolean inverted, Set<TrackType> validTypes) {
		IPStraddleSessionAccessor session = IPAPortalUtil.getStraddleSession(level, subLevel, globalPos);
		if(session == null) {
			return null;
		}

		Level dstLevel = session.simurail$dstLevel();
		Vector3d dstGlobalPos = new Vector3d(globalPos);
		Vector3d dstGlobalDir = new Vector3d(globalDir);
		Vector3d dstGlobalVert = new Vector3d(globalVert);

		IPPortalIsometry isometry = session.simurail$isometry();
		IPAPortalUtil.transformPosition(dstGlobalPos, isometry, dstGlobalPos);
		IPAPortalUtil.transformNormal(dstGlobalDir, isometry, dstGlobalDir);
		IPAPortalUtil.transformNormal(dstGlobalVert, isometry, dstGlobalVert);

		ObjectDoublePair<? extends TrackSegment> dstSegment = TrackSegmentHelper.getSegmentFinders().
				stream().
				filter(f -> f != FINDER).
				map(f -> f.findTrackSegment(dstLevel, subLevel, dstGlobalPos, dstGlobalDir, dstGlobalVert, inverted, validTypes)).
				filter(Objects::nonNull).
				reduce((r, e) -> r.rightDouble() - e.rightDouble() > SimurailMath.EPSILON ? e : r).
				orElse(null);

		if(dstSegment == null) {
			return null;
		}

		SubLevel dstSubLevel = Sable.HELPER.getContaining(dstLevel, dstSegment.first().start());
		IPShipPortalAnchors.Mount mount = session.simurail$mount();

		if(dstSubLevel != (mount == null ? null : mount.destination())) {
			return null;
		}

		if(mount != null) {
			if(mount.destination() != null) {
				Pose3dc pose = mount.destination().logicalPose();
				isometry = new IPPortalIsometry(
						isometry.origin(),
						pose.transformPositionInverse(isometry.dest()),
						pose.orientation().conjugate(new Quaterniond()).mul(isometry.rotation()));
			}
			if(mount.source() != null) {
				Pose3dc pose = mount.source().logicalPose();
				isometry = new IPPortalIsometry(
						pose.transformPositionInverse(isometry.origin()),
						isometry.dest(),
						isometry.rotation().mul(pose.orientation(), new Quaterniond()));
			}
		}

		return ObjectDoublePair.of(new IPAProjectedTrackSegment(dstLevel.dimension(), isometry, dstSegment.first()), dstSegment.secondDouble());
	}
}
