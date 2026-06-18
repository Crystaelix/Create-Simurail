package com.crystaelix.simurail.content.track;

import java.util.Set;

import org.joml.Vector3dc;

import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;

import dev.ryanhcode.sable.sublevel.SubLevel;
import it.unimi.dsi.fastutil.objects.ObjectDoublePair;
import net.minecraft.world.level.Level;

public interface TrackSegmentFinder {

	ObjectDoublePair<? extends TrackSegment> findTrackSegment(Level level, SubLevel subLevel, Vector3dc globalPos, Vector3dc globalDir, Vector3dc globalNormal, boolean inverted, Set<TrackType> validTypes);
}
