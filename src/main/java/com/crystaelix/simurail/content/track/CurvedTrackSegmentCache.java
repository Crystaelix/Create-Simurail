package com.crystaelix.simurail.content.track;

import java.util.Map;
import java.util.Set;

import org.joml.Vector3d;
import org.joml.Vector3dc;

import com.crystaelix.simurail.api.extension.BezierConnectionExtension;
import com.crystaelix.simurail.api.math.CubicBezier3dc;
import com.crystaelix.simurail.api.math.SimurailMath;
import com.crystaelix.simurail.api.track.TrackTypeOverrides;
import com.crystaelix.simurail.api.util.SubLevelUtil;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;

import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectDoublePair;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class CurvedTrackSegmentCache {

	public static final int CHUNK_FACTOR = 16;

	private static final Map<ResourceKey<Level>, CurvedTrackSegmentCache> CACHES = new Object2ObjectOpenHashMap<>();

	public static CurvedTrackSegmentCache getOrCreateCache(ResourceKey<Level> dimension) {
		return CACHES.computeIfAbsent(dimension, CurvedTrackSegmentCache::new);
	}

	public static void removeCache(ResourceKey<Level> dimension) {
		if(CACHES.containsKey(dimension)) {
			CACHES.remove(dimension).clear();
			System.gc();
		}
	}

	public static void removeCaches() {
		for(CurvedTrackSegmentCache cache : CACHES.values()) {
			cache.clear();
		}
		CACHES.clear();
		System.gc();
	}

	public static void addGraph(TrackGraph graph) {
		Set<TrackNodeLocation> visited = new ObjectOpenHashSet<>();
		for(TrackNodeLocation nodeLoc : graph.getNodes()) {
			visited.add(nodeLoc);
			TrackNode node = graph.locateNode(nodeLoc);
			for(Map.Entry<TrackNode, TrackEdge> entry : graph.getConnectionsFrom(node).entrySet()) {
				if(visited.contains(entry.getKey().getLocation())) {
					continue;
				}
				TrackEdge edge = entry.getValue();
				if(edge.isTurn()) {
					getOrCreateCache(nodeLoc.dimension).addCurve(edge.getTurn());
				}
			}
		}
		visited.clear();
	}

	public static ObjectDoublePair<CurvedTrackSegment> findSegmentGlobal(Level level, SubLevel subLevel, Vector3dc globalPos, Vector3dc globalDir, Vector3dc globalNormal, boolean upsideDown, Set<TrackType> validTypes) {
		CurvedTrackSegmentCache cache = getOrCreateCache(level.dimension());
		if(cache == null) {
			return null;
		}
		return cache.findSegment(level, subLevel, globalPos, globalDir, globalNormal, upsideDown, validTypes);
	}

	private ResourceKey<Level> dimension;
	private SetMultimap<Vec3i, BezierConnection> chunkToCurve = Multimaps.newSetMultimap(new Object2ObjectOpenHashMap<>(), () -> new ObjectOpenCustomHashSet<>(BezierHashStrategy.INSTANCE));
	private SetMultimap<BezierConnection, Vec3i> curveToChunk = Multimaps.newSetMultimap(new Object2ObjectOpenCustomHashMap<>(BezierHashStrategy.INSTANCE), ObjectOpenHashSet::new);

	private CurvedTrackSegmentCache(ResourceKey<Level> dimension) {
		this.dimension = dimension;
	}

	public boolean addCurve(BezierConnection curve) {
		if(!curve.isPrimary()) {
			curve = curve.secondary();
		}
		removeCurve(curve);
		BezierConnectionExtension curveExt = (BezierConnectionExtension)curve;
		CubicBezier3dc controlPoints = curveExt.simurail$controlPoints();
		double length = curveExt.simurail$quadratureLength();
		int segments = Mth.ceil(length / CHUNK_FACTOR);
		Vector3d pos0 = new Vector3d();
		Vector3d pos1 = new Vector3d();
		Vector3d minPos = new Vector3d();
		Vector3d maxPos = new Vector3d();
		for(int i = 0; i < segments; ++i) {
			controlPoints.position((double)i / segments, pos0);
			controlPoints.position((double)(i + 1) / segments, pos1);
			pos0.min(pos1, minPos).sub(0.5, 0.5, 0.5);
			pos0.max(pos1, maxPos).add(0.5, 0.5, 0.5);
			int minChunkX = Mth.floor(minPos.x / CHUNK_FACTOR);
			int minChunkY = Mth.floor(minPos.y / CHUNK_FACTOR);
			int minChunkZ = Mth.floor(minPos.z / CHUNK_FACTOR);
			int maxChunkX = Mth.ceil(maxPos.x / CHUNK_FACTOR);
			int maxChunkY = Mth.ceil(maxPos.y / CHUNK_FACTOR);
			int maxChunkZ = Mth.ceil(maxPos.z / CHUNK_FACTOR);
			for(int x = minChunkX; x < maxChunkX; ++x) {
				for(int y = minChunkY; y < maxChunkY; ++y) {
					for(int z = minChunkZ; z < maxChunkZ; ++z) {
						Vec3i chunk = new Vec3i(x, y, z);
						curveToChunk.put(curve, chunk);
						chunkToCurve.put(chunk, curve);
					}
				}
			}
		}
		return true;
	}

	public boolean removeCurve(BezierConnection curve) {
		if(!curve.isPrimary()) {
			curve = curve.secondary();
		}
		if(!curveToChunk.containsKey(curve)) {
			return false;
		}
		for(Vec3i chunk : curveToChunk.removeAll(curve)) {
			chunkToCurve.get(chunk).remove(curve);
		}
		return true;
	}

	private ObjectDoublePair<CurvedTrackSegment> findSegment(Level level, SubLevel subLevel, Vector3dc globalPos, Vector3dc globalDir, Vector3dc globalVert, boolean inverted, Set<TrackType> validTypes) {
		Vector3d scale = subLevel.logicalPose().scale();
		if(!Mth.equal(scale.x, scale.y) || !Mth.equal(scale.x, scale.z)) {
			return null;
		}

		double score = Double.POSITIVE_INFINITY;
		CurvedTrackSegment segment = null;

		Vector3d checkLocalPos = new Vector3d();
		Vector3d checkLocalDir = new Vector3d();
		Vector3d checkLocalVert = new Vector3d();

		for(SubLevel checkSubLevel : SubLevelUtil.getIntersectingSubLevels(level, globalPos, 1)) {
			if(subLevel == checkSubLevel) {
				continue;
			}
			Pose3dc checkPose = checkSubLevel == null ? SimurailMath.POSE_I : checkSubLevel.logicalPose();
			Vector3dc checkScale = checkPose.scale();
			if(!Mth.equal(checkScale.x(), checkScale.y()) || !Mth.equal(checkScale.y(), checkScale.z()) || !Mth.equal(checkScale.x(), scale.x)) {
				continue;
			}
			checkPose.transformPositionInverse(globalPos, checkLocalPos);
			checkPose.transformNormalInverse(globalDir, checkLocalDir);
			checkPose.transformNormalInverse(globalVert, checkLocalVert);
			int minChunkX = Mth.floor((checkLocalPos.x - 1) / CHUNK_FACTOR);
			int minChunkY = Mth.floor((checkLocalPos.y - 2) / CHUNK_FACTOR);
			int minChunkZ = Mth.floor((checkLocalPos.z - 1) / CHUNK_FACTOR);
			int maxChunkX = Mth.ceil((checkLocalPos.x + 1) / CHUNK_FACTOR);
			int maxChunkY = Mth.ceil((checkLocalPos.y + 2) / CHUNK_FACTOR);
			int maxChunkZ = Mth.ceil((checkLocalPos.z + 1) / CHUNK_FACTOR);
			for(int x = minChunkX; x < maxChunkX; ++x) {
				for(int y = minChunkY; y < maxChunkY; ++y) {
					for(int z = minChunkZ; z < maxChunkZ; ++z) {
						Vec3i chunk = new Vec3i(x, y, z);
						for(BezierConnection checkCurve : chunkToCurve.get(chunk)) {
							TrackType type = TrackTypeOverrides.getTrackType(checkCurve.getMaterial());
							if(!validTypes.contains(type) && !TrackTypeOverrides.isUniversal(type)) {
								continue;
							}
							for(int i = 0; i < checkCurve.getSegmentCount(); ++i) {
								CurvedTrackSegment checkSegment = new CurvedTrackSegment(dimension, checkCurve, i);
								if(!checkSegment.inSegmentRange(checkLocalPos, checkLocalVert, inverted)) {
									continue;
								}
								double orientScore = 1 - Math.abs(checkLocalDir.dot(checkSegment.basis().direction()));
								if(orientScore > 0.9) {
									continue;
								}
								double distScore = checkSegment.distanceSquared(checkLocalPos);
								double checkScore = distScore + TrackSegmentHelper.ORIENT_SCORE_WEIGHT * orientScore;
								if(checkScore < score) {
									score = checkScore;
									segment = checkSegment;
								}
							}
						}
					}
				}
			}
		}
		if(segment == null) {
			return null;
		}
		BezierConnection curve = segment.curve();
		BlockPos curvePos1 = curve.bePositions.getFirst();
		BlockPos curvePos2 = curve.bePositions.getSecond();
		if(level.isLoaded(curvePos1) && !(level.getBlockState(curvePos1).getBlock() instanceof ITrackBlock) ||
				level.isLoaded(curvePos2) && !(level.getBlockState(curvePos2).getBlock() instanceof ITrackBlock)) {
			// Broken curve, remove
			removeCurve(curve);
			return null;
		}
		return ObjectDoublePair.of(segment, score);
	}

	private void clear() {
		chunkToCurve.clear();
		curveToChunk.clear();
	}
}
