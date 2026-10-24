package com.crystaelix.simurail.content.gangway_frame;

import java.util.Iterator;

import com.google.common.collect.AbstractIterator;

import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GangwayFrameCollisions extends AbstractIterator<VoxelShape> {

	private final CollisionGetter collisionGetter;
	private final AABB box;
	private final MutableBlockPos pos;
	private final Cursor3D cursor;

	private Iterator<BoundingBox3dc> currGangway;
	private BlockGetter cachedChunk;
	private long cachedChunkPos;

	public GangwayFrameCollisions(CollisionGetter collisionGetter, AABB box) {
		this.collisionGetter = collisionGetter;
		this.box = box;
		pos = new MutableBlockPos();
		cursor = new Cursor3D(
				Mth.floor(box.minX - 1E-7) - 1, Mth.floor(box.minY - 1E-7) - 1, Mth.floor(box.minZ - 1E-7) - 1,
				Mth.floor(box.maxX + 1E-7) + 1, Mth.floor(box.maxY + 1E-7) + 1, Mth.floor(box.maxZ + 1E-7) + 1);
	}

	private BlockGetter getChunk(int x, int z) {
		int chunkX = SectionPos.blockToSectionCoord(x);
		int chunkZ = SectionPos.blockToSectionCoord(z);
		long pos = ChunkPos.asLong(chunkX, chunkZ);
		if(cachedChunk != null && cachedChunkPos == pos) {
			return cachedChunk;
		}
		BlockGetter chunk = collisionGetter.getChunkForCollisions(chunkX, chunkZ);
		cachedChunk = chunk;
		cachedChunkPos = pos;
		return chunk;
	}

	@Override
	protected VoxelShape computeNext() {
		if(currGangway != null) {
			int x = cursor.nextX();
			int y = cursor.nextY();
			int z = cursor.nextZ();
			while(currGangway.hasNext()) {
				BoundingBox3dc shape = currGangway.next();
				if(shape.intersects(box.minX - x, box.minY - y, box.minZ - z, box.maxX - x, box.maxY - y, box.maxZ - z)) {
					return Shapes.create(shape.minX() + x, shape.minY() + y, shape.minZ() + z, shape.maxX() + x, shape.maxY() + y, shape.maxZ() + z);
				}
			}
		}
		while(cursor.advance()) {
			int x = cursor.nextX();
			int y = cursor.nextY();
			int z = cursor.nextZ();
			BlockGetter chunk = getChunk(x, z);
			if(chunk != null && chunk.getBlockEntity(pos.set(x, y, z)) instanceof GangwayFrame gangway) {
				currGangway = gangway.getGangwayCollisions().iterator();
				while(currGangway.hasNext()) {
					BoundingBox3dc shape = currGangway.next();
					if(shape.intersects(box.minX - x, box.minY - y, box.minZ - z, box.maxX - x, box.maxY - y, box.maxZ - z)) {
						return Shapes.create(shape.minX() + x, shape.minY() + y, shape.minZ() + z, shape.maxX() + x, shape.maxY() + y, shape.maxZ() + z);
					}
				}
			}
		}
		return endOfData();
	}
}
