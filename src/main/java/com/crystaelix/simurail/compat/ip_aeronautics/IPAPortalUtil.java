package com.crystaelix.simurail.compat.ip_aeronautics;

import java.util.Set;

import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import com.crystaelix.simurail.api.math.Frame3d;
import com.crystaelix.simurail.mixin.compat.ip_aeronautics.IPStraddleSessionAccessor;

import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.util.SableNBTUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import qouteall.imm_ptl.core.compat.sable_straddle.IPPortalIsometry;
import qouteall.imm_ptl.core.portal.Portal;

public class IPAPortalUtil {
	
	public static IPStraddleSessionAccessor getStraddleSession(Level srcLevel, SubLevel subLevel, Vector3dc globalPos) {
		Set<IPStraddleSessionAccessor> sessions = IPStraddleSessionCache.getSessions(subLevel);
		if(sessions.isEmpty()) {
			return null;
		}
		Frame3d portalFrame = new Frame3d();
		Vector3d portalDelta = new Vector3d();
		for(IPStraddleSessionAccessor session : sessions) {
			if(session.simurail$srcLevel() != srcLevel) {
				continue;
			}
			Portal portal = session.simurail$portal();
			JOMLConversion.toJOML(portal.getNormal(), portalFrame.direction);
			JOMLConversion.toJOML(portal.getAxisW(), portalFrame.lateral);
			JOMLConversion.toJOML(portal.getAxisH(), portalFrame.vertical);
			JOMLConversion.toJOML(portal.getOriginPos(), portalFrame.position);
			globalPos.sub(portalFrame.position, portalDelta);
			if(portalDelta.dot(portalFrame.direction) > 0) {
				continue;
			}
			// Multiple straddle unimplemented
			return session;
		}
		return null;
	}

	public static Vector3d transformPosition(Vector3dc pos, IPPortalIsometry isometry, Vector3d dest) {
		Vec3 src = isometry.origin();
		Vec3 dst = isometry.dest();
		Quaterniondc rot = isometry.rotation();
		return rot.transform(pos.sub(src.x, src.y, src.z, dest)).add(dst.x, dst.y, dst.z);
	}

	public static Vector3d transformPosition(Vector3dc pos, IPPortalIsometry isometry) {
		return transformPosition(pos, isometry, new Vector3d());
	}

	public static Vector3d transformPositionInverse(Vector3dc pos, IPPortalIsometry isometry, Vector3d dest) {
		Vec3 src = isometry.origin();
		Vec3 dst = isometry.dest();
		Quaterniondc rot = isometry.rotation();
		return rot.transformInverse(pos.sub(dst.x, dst.y, dst.z, dest)).add(src.x, src.y, src.z);
	}

	public static Vector3d transformPositionInverse(Vector3dc pos, IPPortalIsometry isometry) {
		return transformPositionInverse(pos, isometry, new Vector3d());
	}

	public static Vector3d transformNormal(Vector3dc normal, IPPortalIsometry isometry, Vector3d dest) {
		return isometry.rotation().transform(normal, dest);
	}

	public static Vector3d transformNormal(Vector3dc pos, IPPortalIsometry isometry) {
		return transformNormal(pos, isometry, new Vector3d());
	}

	public static Vector3d transformNormalInverse(Vector3dc normal, IPPortalIsometry isometry, Vector3d dest) {
		return isometry.rotation().transformInverse(normal, dest);
	}

	public static Vector3d transformNormalInverse(Vector3dc pos, IPPortalIsometry isometry) {
		return transformNormalInverse(pos, isometry, new Vector3d());
	}

	public static CompoundTag writeVec3(Vec3 v) {
		CompoundTag tag = new CompoundTag();
		tag.putDouble("x", v.x);
		tag.putDouble("y", v.y);
		tag.putDouble("z", v.z);
		return tag;
	}
	
	public static Vec3 readVec3(CompoundTag tag) {
        return new Vec3(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"));
	}

	public static CompoundTag writeIsometry(IPPortalIsometry isometry) {
		CompoundTag tag = new CompoundTag();
		tag.put("origin", writeVec3(isometry.origin()));
		tag.put("dest", writeVec3(isometry.dest()));
		tag.put("rotation", SableNBTUtils.writeQuaternion(isometry.rotation()));
		return tag;
	}
	
	public static IPPortalIsometry readIsometry(CompoundTag tag) {
		return new IPPortalIsometry(
				readVec3(tag.getCompound("origin")),
				readVec3(tag.getCompound("dest")),
				SableNBTUtils.readQuaternion(tag.getCompound("rotation")));
	}
}
