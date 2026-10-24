package com.crystaelix.simurail.content.gangway_frame;

import java.util.Iterator;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import org.joml.Vector3d;
import org.joml.Vector3dc;

import com.crystaelix.simurail.api.math.CubicBezier3d;
import com.crystaelix.simurail.api.math.SimurailMath;
import com.google.common.collect.AbstractIterator;

import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import net.minecraft.core.Direction;

public class GangwayFrameBlockCollisions implements Iterable<BoundingBox3dc> {

	private static final int SEGMENTS = 8;

	private final Supplier<GangwayFrameBlockShape> shape;
	private final Supplier<Direction> facing;
	private final DoubleSupplier restLength;
	private final BooleanSupplier hasPartner;
	private final Pose3dc endPose;

	public GangwayFrameBlockCollisions(Supplier<GangwayFrameBlockShape> shape, Supplier<Direction> facing, DoubleSupplier restLength, BooleanSupplier hasPartner, Pose3dc endPose) {
		this.shape = shape;
		this.facing = facing;
		this.restLength = restLength;
		this.hasPartner = hasPartner;
		this.endPose = endPose;
	}

	@Override
	public Iterator<BoundingBox3dc> iterator() {
		return new Itr();
	}

	private class Itr extends AbstractIterator<BoundingBox3dc> {

		private final GangwayFrameBlockShape shape = GangwayFrameBlockCollisions.this.shape.get();
		private final Direction facing = GangwayFrameBlockCollisions.this.facing.get();
		private final double restLength = GangwayFrameBlockCollisions.this.restLength.getAsDouble();
		private final boolean hasPartner = GangwayFrameBlockCollisions.this.hasPartner.getAsBoolean();
		private int index = 0;

		@Override
		protected BoundingBox3dc computeNext() {
			int count = shape.colliderPointCount();
			Direction.Axis axis = facing.getAxis();
			double xExpand = axis.choose(0, 0, 0.0625);
			double zExpand = axis.choose(0.0625, 0, 0);
			if(hasPartner) {
				endPose.transformNormal(SimurailMath.DIR_XN, partnerDir);
				shape.anchor(Direction.EAST, rootAnchor);
				int colliderIndex = index / SEGMENTS;
				if(colliderIndex < count) {
					int segmentIndex = index % SEGMENTS;
					shape.colliderPoint(colliderIndex, facing, selfColliderPoint);
					shape.colliderPoint(colliderIndex, Direction.EAST, partnerColliderPoint).sub(rootAnchor);
					endPose.transformPosition(partnerColliderPoint);
					colliderCurve.setHermite(selfColliderPoint, partnerColliderPoint, getDirection(), partnerDir);
					colliderCurve.position(segmentIndex * 0.5625 / SEGMENTS, point0);
					colliderCurve.position((segmentIndex + 1) * 0.5625 / SEGMENTS, point1);
					box.set(point0.x, point0.y, point0.z, point1.x, point1.y, point1.z).expand(xExpand, 0.0625, zExpand);
					index++;
					return box;
				}
			}
			else if(restLength > 0 && index < count) {
				shape.colliderPoint(index, facing, point0);
				point1.set(point0).fma(restLength, getDirection());
				box.set(point0.x, point0.y, point0.z, point1.x, point1.y, point1.z).expand(xExpand, 0.0625, zExpand);
				index++;
				return box;
			}
			return endOfData();
		}

		Vector3dc getDirection() {
			return switch(facing) {
			case EAST -> SimurailMath.DIR_XP; case WEST -> SimurailMath.DIR_XN;
			case SOUTH -> SimurailMath.DIR_ZP; case NORTH -> SimurailMath.DIR_ZN;
			case null, default -> throw new IllegalArgumentException("Unexpected value: " + facing);
			};
		}
	}

	private Vector3d partnerDir = new Vector3d();
	private Vector3d selfColliderPoint = new Vector3d();
	private Vector3d partnerColliderPoint = new Vector3d();
	private Vector3d rootAnchor = new Vector3d();
	private CubicBezier3d colliderCurve = new CubicBezier3d();
	private Vector3d point0 = new Vector3d();
	private Vector3d point1 = new Vector3d();
	private BoundingBox3d box = new BoundingBox3d();
}
