package com.crystaelix.simurail.api.util;

import java.util.Collections;

import org.joml.Vector3dc;

import com.google.common.collect.Iterables;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.world.level.Level;

public class SubLevelUtil {

	public static Iterable<SubLevel> getIntersectingSubLevels(Level level, Vector3dc globalPos, double range) {
		BoundingBox3d bb = new BoundingBox3d(globalPos.x() - range, globalPos.y() - range, globalPos.z() - range, globalPos.x() + range, globalPos.y() + range, globalPos.z() + range);
		return Iterables.concat(Collections.singleton(null), Sable.HELPER.getAllIntersecting(level, bb));
	}
}
