package com.crystaelix.simurail.content.gangway_frame;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.DoubleFunction;
import java.util.function.IntFunction;

import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector3d;

import com.crystaelix.simurail.api.math.HorizontalRelativeDirection;
import com.crystaelix.simurail.api.math.Quad2d;
import com.crystaelix.simurail.api.math.Quad2dc;
import com.crystaelix.simurail.api.math.Quad3d;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.ByIdMap.OutOfBoundsStrategy;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public enum GangwayFrameBlockShape implements GangwayFrameShape, StringRepresentable {
	// Basic
	D(
			HorizontalRelativeDirection.CW,
			HorizontalRelativeDirection.ACW,
			new Quad2d(v(0, 2), v(16, 2), v(16, 4), v(0, 4)),
			List.of(v(1, 3), v(3, 3), v(5, 3), v(7, 3), v(9, 3), v(11, 3), v(13, 3), v(15, 3)),
			z -> VoxelShaper.forHorizontal(Block.box(0, 2, 0, 16, 4, z), Direction.SOUTH),
			0),
	U(
			HorizontalRelativeDirection.CW,
			HorizontalRelativeDirection.ACW,
			new Quad2d(v(0, 12), v(16, 12), v(16, 14), v(0, 14)),
			List.of(v(1, 13), v(3, 13), v(5, 13), v(7, 13), v(9, 13), v(11, 13), v(13, 13), v(15, 13)),
			z -> VoxelShaper.forHorizontal(Block.box(0, 12, 0, 16, 14, z), Direction.SOUTH),
			1),
	L(
			HorizontalRelativeDirection.D,
			HorizontalRelativeDirection.U,
			new Quad2d(v(2, 16), v(2, 0), v(4, 0), v(4, 16)),
			List.of(v(3, 1), v(3, 2), v(3, 5), v(3, 7), v(3, 9), v(3, 11), v(3, 13), v(3, 15)),
			z -> VoxelShaper.forHorizontal(Block.box(2, 0, 0, 4, 16, z), Direction.SOUTH),
			2),
	R(
			HorizontalRelativeDirection.D,
			HorizontalRelativeDirection.U,
			new Quad2d(v(12, 16), v(12, 0), v(14, 0), v(14, 16)),
			List.of(v(13, 1), v(13, 2), v(13, 5), v(13, 7), v(13, 9), v(13, 11), v(13, 13), v(13, 15)),
			z -> VoxelShaper.forHorizontal(Block.box(12, 0, 0, 14, 16, z), Direction.SOUTH),
			3),
	// Short diagonal
	DL(
			HorizontalRelativeDirection.CW,
			HorizontalRelativeDirection.D,
			new Quad2d(v(0, 2), v(2, 0), v(4, 0), v(0, 4)),
			List.of(v(1, 2), v(2, 1)),
			GangwayFrameBlockShape::dlShape,
			4),
	RD(
			HorizontalRelativeDirection.ACW,
			HorizontalRelativeDirection.D,
			new Quad2d(v(14, 0), v(16, 2), v(16, 4), v(12, 0)),
			List.of(v(14, 1), v(15, 2)),
			GangwayFrameBlockShape::rdShape,
			5),
	LU(
			HorizontalRelativeDirection.CW,
			HorizontalRelativeDirection.U,
			new Quad2d(v(0, 12), v(4, 16), v(2, 16), v(0, 14)),
			List.of(v(1, 14), v(2, 15)),
			GangwayFrameBlockShape::luShape,
			6),
	UR(
			HorizontalRelativeDirection.ACW,
			HorizontalRelativeDirection.U,
			new Quad2d(v(12, 16), v(16, 12), v(16, 14), v(14, 16)),
			List.of(v(14, 15), v(15, 14)),
			GangwayFrameBlockShape::urShape,
			7),
	// Long diagonal
	LD(
			HorizontalRelativeDirection.ACW,
			HorizontalRelativeDirection.U,
			new Quad2d(v(2, 16), v(16, 2), v(16, 4), v(4, 16)),
			List.of(v(4, 15), v(5.375, 13.625), v(6.75, 12.25), v(8.125, 10.875), v(9.5, 9.5), v(10.875, 8.125), v(12.25, 6.75), v(13.625, 5.375), v(15, 4)),
			GangwayFrameBlockShape::ldShape,
			0),
	DR(
			HorizontalRelativeDirection.CW,
			HorizontalRelativeDirection.U,
			new Quad2d(v(0, 2), v(14, 16), v(12, 16), v(0, 4)),
			List.of(v(1, 4), v(2.375, 5.375), v(3.75, 6.75), v(5.125, 8.125), v(6.5, 9.5), v(7.875, 10.875), v(9.25, 12.25), v(10.625, 13.625), v(12, 15)),
			GangwayFrameBlockShape::drShape,
			0),
	UL(
			HorizontalRelativeDirection.ACW,
			HorizontalRelativeDirection.D,
			new Quad2d(v(4, 0), v(16, 12), v(16, 14), v(2, 0)),
			List.of(v(4, 1), v(5.375, 2.375), v(6.75, 3.75), v(8.125, 5.125), v(9.5, 6.5), v(10.875, 7.875), v(12.25, 9.25), v(13.625, 10.625), v(15, 12)),
			GangwayFrameBlockShape::ulShape,
			0),
	RU(
			HorizontalRelativeDirection.CW,
			HorizontalRelativeDirection.D,
			new Quad2d(v(0, 12), v(12, 0), v(14, 0), v(0, 14)),
			List.of(v(1, 12), v(2.375, 10.625), v(3.75, 9.25), v(5.125, 7.875), v(6.5, 6.5), v(7.875, 5.125), v(9.25, 3.75), v(10.625, 2.375), v(12, 1)),
			GangwayFrameBlockShape::ruShape,
			0),
	// Two-segment horizontal
	DRA(
			HorizontalRelativeDirection.CW,
			HorizontalRelativeDirection.D,
			List.of(new Quad2d(v(0, 2), v(10, 2), v(10, 4), v(0, 4)),
					new Quad2d(v(10, 2), v(12, 0), v(14, 0), v(10, 4))),
			List.of(v(1, 3), v(3, 3), v(5, 3), v(7, 3), v(9, 3), v(11, 2), v(12, 1)),
			GangwayFrameBlockShape::draShape,
			8),
	DLA(
			HorizontalRelativeDirection.ACW,
			HorizontalRelativeDirection.D,
			List.of(new Quad2d(v(4, 0), v(6, 2), v(6, 4), v(2, 0)),
					new Quad2d(v(6, 2), v(16, 2), v(16, 4), v(6, 4))),
			List.of(v(4, 1), v(5, 2), v(7, 3), v(9, 3), v(11, 3), v(13, 3), v(15, 3)),
			GangwayFrameBlockShape::dlaShape,
			9),
	URA(
			HorizontalRelativeDirection.CW,
			HorizontalRelativeDirection.U,
			List.of(new Quad2d(v(0, 12), v(10, 12), v(10, 14), v(0, 14)),
					new Quad2d(v(10, 12), v(14, 16), v(12, 16), v(10, 14))),
			List.of(v(1, 13), v(3, 13), v(5, 13), v(7, 13), v(9, 13), v(11, 14), v(12, 15)),
			GangwayFrameBlockShape::uraShape,
			10),
	ULA(
			HorizontalRelativeDirection.ACW,
			HorizontalRelativeDirection.U,
			List.of(new Quad2d(v(2, 16), v(6, 12), v(6, 14), v(4, 16)),
					new Quad2d(v(6, 12), v(16, 12), v(16, 14), v(6, 14))),
			List.of(v(4, 15), v(5, 14), v(7, 13), v(9, 13), v(11, 13), v(13, 13), v(15, 13)),
			GangwayFrameBlockShape::ulaShape,
			11),
	// None
	NONE(
			HorizontalRelativeDirection.I,
			HorizontalRelativeDirection.I,
			List.of(),
			List.of(),
			z -> VoxelShaper.forHorizontal(Shapes.empty(), Direction.SOUTH),
			0),
	;

	public static final IntFunction<GangwayFrameBlockShape> BY_ID = ByIdMap.continuous(GangwayFrameBlockShape::ordinal, values(), OutOfBoundsStrategy.ZERO);
	public static final StreamCodec<ByteBuf, GangwayFrameBlockShape> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, GangwayFrameBlockShape::ordinal);

	public static final Set<GangwayFrameBlockShape> NORMAL = EnumSet.complementOf(EnumSet.of(NONE));
	public static final Set<GangwayFrameBlockShape> COUPLER = EnumSet.of(D, U, NONE);
	public static final Set<GangwayFrameBlockShape> COPYCAT = EnumSet.complementOf(EnumSet.of(LD, DR, UL, RU, NONE));

	public static final IntFunction<GangwayFrameBlockShape> BY_ID_COPYCAT = ByIdMap.continuous(shape -> shape.copycatIndex, COPYCAT.toArray(GangwayFrameBlockShape[]::new), OutOfBoundsStrategy.ZERO);

	public static final Set<GangwayFrameBlockShape> D_CW = EnumSet.of(D, RD, LD, DLA);
	public static final Set<GangwayFrameBlockShape> D_ACW = EnumSet.of(D, DL, DR, DRA);
	public static final Set<GangwayFrameBlockShape> U_CW = EnumSet.of(U, UR, UL, ULA);
	public static final Set<GangwayFrameBlockShape> U_ACW = EnumSet.of(U, LU, RU, URA);

	public static final Set<GangwayFrameBlockShape> L_D = EnumSet.of(L, LU, LD, ULA);
	public static final Set<GangwayFrameBlockShape> L_U = EnumSet.of(L, DL, UL, DLA);
	public static final Set<GangwayFrameBlockShape> R_D = EnumSet.of(R, UR, DR, URA);
	public static final Set<GangwayFrameBlockShape> R_U = EnumSet.of(R, RD, RU, DRA);

	private final Couple<HorizontalRelativeDirection> adjacentOffsets;

	private final List<Quad2dc> quads;
	private final Vector2dc anchor;
	private final List<Vector2dc> colliderPoints;

	public final VoxelShaper shapes;
	public final VoxelShaper subLevelShapes;

	public final int copycatIndex;

	GangwayFrameBlockShape(HorizontalRelativeDirection offset1, HorizontalRelativeDirection offset2, List<Quad2dc> quads, List<Vector2dc> colliderPoints, DoubleFunction<VoxelShaper> shapes, int copycatIndex) {
		this.adjacentOffsets = Couple.create(offset1, offset2);

		this.quads = quads;
		Vector2d anchor = new Vector2d();
		double weight = 0.25 / quads.size();
		for(Quad2dc quad : quads) {
			anchor.fma(weight, quad.v0());
			anchor.fma(weight, quad.v1());
			anchor.fma(weight, quad.v2());
			anchor.fma(weight, quad.v3());
		}
		this.anchor = anchor;
		this.colliderPoints = colliderPoints;

		this.shapes = shapes.apply(3);
		this.subLevelShapes = shapes.apply(0.25);

		this.copycatIndex = copycatIndex;
	}

	GangwayFrameBlockShape(HorizontalRelativeDirection offset1, HorizontalRelativeDirection offset2, Quad2dc quad, List<Vector2dc> colliderPoints, DoubleFunction<VoxelShaper> shapes, int copycatIndex) {
		this(offset1, offset2, List.of(quad), colliderPoints, shapes, copycatIndex);
	}

	@Override
	public String getSerializedName() {
		return name().toLowerCase(Locale.ROOT);
	}

	public Component getDisplayName() {
		return Component.translatable("block.simurail.gangway_frame." + getSerializedName());
	}

	public int quadCount() {
		return quads.size();
	}

	public Quad3d quad(int index, Direction facing, Quad3d dest) {
		rotate(quads.get(index).v0(), facing, dest.v0);
		rotate(quads.get(index).v1(), facing, dest.v1);
		rotate(quads.get(index).v2(), facing, dest.v2);
		rotate(quads.get(index).v3(), facing, dest.v3);
		return dest;
	}

	@Override
	public Vector3d anchor(Direction facing, Vector3d dest) {
		return rotate(anchor, facing, dest);
	}

	public int colliderPointCount() {
		return colliderPoints.size();
	}

	public Vector3d colliderPoint(int index, Direction facing, Vector3d dest) {
		return rotate(colliderPoints.get(index), facing, dest);
	}

	@Override
	public boolean canConnectTo(GangwayFrameShape other) {
		return other == switch(this) {
		case D -> D;
		case U -> U;
		case L -> R;
		case R -> L;
		case DL -> RD;
		case RD -> DL;
		case LU -> UR;
		case UR -> LU;
		case LD -> DR;
		case DR -> LD;
		case UL -> RU;
		case RU -> UL;
		case DLA -> DRA;
		case DRA -> DLA;
		case ULA -> URA;
		case URA -> ULA;
		case NONE -> NONE;
		};
	}

	@Override
	public HorizontalRelativeDirection adjacentOffset(boolean first) {
		return adjacentOffsets.get(first);
	}

	@Override
	public boolean isAdjacentTo(HorizontalRelativeDirection offset, GangwayFrameShape other) {
		Set<GangwayFrameBlockShape> adjacent = Set.of();
		switch(offset) {
		case CW -> {
			switch(this) {
			case D, RD, LD, DRA -> adjacent = D_CW;
			case U, UR, UL, URA -> adjacent = U_CW;
			default -> {}
			}
		}
		case ACW -> {
			switch(this) {
			case D, RD, LD, DLA -> adjacent = D_ACW;
			case U, UR, UL, ULA -> adjacent = U_ACW;
			default -> {}
			}
		}
		case D -> {
			switch(this) {
			case L, DL, UL, DLA -> adjacent = L_D;
			case R, RD, RU, DRA -> adjacent = R_D;
			default -> {}
			}
		}
		case U -> {
			switch(this) {
			case L, LU, LD, ULA -> adjacent = L_U;
			case R, UR, DR, URA -> adjacent = R_U;
			default -> {}
			}
		}
		case null, default -> {}
		}
		return adjacent.contains(other);
	}

	private static Vector2d v(double x, double y) {
		return new Vector2d(x / 16, y / 16);
	}

	private static VoxelShape createShortShape(double x0, double y0, double x1, double y1, double yInc, double zSize) {
		VoxelShape shape = Shapes.empty();
		for(int i = 0; i < 2; ++i) {
			shape = Shapes.or(shape, Block.box(x0, y0, 0, x0 + 2, y0 + 2, zSize));
			x0 += 2;
			y0 += yInc;
		}
		shape = Shapes.or(shape, Block.box(x1, y1, 0, x1 + 2, y1 + 2, zSize));
		return shape;
	}

	private static VoxelShape createLongShape(double x0, double y0, double x1, double y1, double yInc, double zSize) {
		VoxelShape shape = Shapes.empty();
		for(int i = 0; i < 7; ++i) {
			shape = Shapes.or(shape, Block.box(x0, y0, 0, x0 + 2, y0 + 2, zSize));
			x0 += 2;
			y0 += yInc;
		}
		for(int i = 0; i < 6; ++i) {
			shape = Shapes.or(shape, Block.box(x1, y1, 0, x1 + 2, y1 + 2, zSize));
			x1 += 2;
			y1 += yInc;
		}
		return shape;
	}

	private static Vector3d rotate(Vector2dc vec, Direction facing, Vector3d dest) {
		double z = 0.125;
		return switch(facing) {
		case SOUTH -> dest.set(vec, z);
		case EAST -> dest.set(z, vec.y(), 1 - vec.x());
		case NORTH -> dest.set(1 - vec.x(), vec.y(), 1 - z);
		case WEST -> dest.set(1 - z, vec.y(), vec.x());
		case null, default -> throw new IllegalArgumentException("Unexpected value: " + facing);
		};
	}

	private static VoxelShape clampedBox(double x1, double y1, double z1, double x2, double y2, double z2) {
		return Block.box(Math.clamp(x1, 0, 16), Math.clamp(y1, 0, 16), Math.clamp(z1, 0, 16), Math.clamp(x2, 0, 16), Math.clamp(y2, 0, 16), Math.clamp(z2, 0, 16));
	}

	private static VoxelShaper dlShape(double z) {
		VoxelShape shape = Shapes.empty();
		for(int i = 0; i < 4; ++i) {
			shape = Shapes.joinUnoptimized(
					shape, clampedBox(0+i, 1-i, 0, 1+i, 4-i, z),
					BooleanOp.OR);
		}
		return VoxelShaper.forHorizontal(shape.optimize(), Direction.SOUTH);
	}

	private static VoxelShaper rdShape(double z) {
		VoxelShape shape = Shapes.empty();
		for(int i = 0; i < 4; ++i) {
			shape = Shapes.joinUnoptimized(
					shape, clampedBox(12+i, -2+i, 0, 13+i, 1+i, z),
					BooleanOp.OR);
		}
		return VoxelShaper.forHorizontal(shape.optimize(), Direction.SOUTH);
	}

	private static VoxelShaper luShape(double z) {
		VoxelShape shape = Shapes.empty();
		for(int i = 0; i < 4; ++i) {
			shape = Shapes.joinUnoptimized(
					shape, clampedBox(0+i, 12+i, 0, 1+i, 15+i, z),
					BooleanOp.OR);
		}
		return VoxelShaper.forHorizontal(shape.optimize(), Direction.SOUTH);
	}

	private static VoxelShaper urShape(double z) {
		VoxelShape shape = Shapes.empty();
		for(int i = 0; i < 4; ++i) {
			shape = Shapes.joinUnoptimized(
					shape, clampedBox(12+i, 15-i, 0, 13+i, 18-i, z),
					BooleanOp.OR);
		}
		return VoxelShaper.forHorizontal(shape.optimize(), Direction.SOUTH);
	}

	private static VoxelShaper ldShape(double z) {
		VoxelShape shape = Shapes.empty();
		for(int i = 0; i < 14; ++i) {
			shape = Shapes.joinUnoptimized(
					shape, clampedBox(2+i, 15-i, 0, 3+i, 18-i, z),
					BooleanOp.OR);
		}
		return VoxelShaper.forHorizontal(shape.optimize(), Direction.SOUTH);
	}

	private static VoxelShaper drShape(double z) {
		VoxelShape shape = Shapes.empty();
		for(int i = 0; i < 14; ++i) {
			shape = Shapes.joinUnoptimized(
					shape, clampedBox(0+i, 2+i, 0, 1+i, 5+i, z),
					BooleanOp.OR);
		}
		return VoxelShaper.forHorizontal(shape.optimize(), Direction.SOUTH);
	}

	private static VoxelShaper ulShape(double z) {
		VoxelShape shape = Shapes.empty();
		for(int i = 0; i < 14; ++i) {
			shape = Shapes.joinUnoptimized(
					shape, clampedBox(2+i, -2+i, 0, 3+i, 1+i, z),
					BooleanOp.OR);
		}
		return VoxelShaper.forHorizontal(shape.optimize(), Direction.SOUTH);
	}

	private static VoxelShaper ruShape(double z) {
		VoxelShape shape = Shapes.empty();
		for(int i = 0; i < 14; ++i) {
			shape = Shapes.joinUnoptimized(
					shape, clampedBox(0+i, 11-i, 0, 1+i, 14-i, z),
					BooleanOp.OR);
		}
		return VoxelShaper.forHorizontal(shape.optimize(), Direction.SOUTH);
	}

	private static VoxelShaper draShape(double z) {
		VoxelShape shape = Block.box(0, 2, 0, 10, 4, z);
		for(int i = 0; i < 4; ++i) {
			shape = Shapes.joinUnoptimized(
					shape, clampedBox(10+i, 1-i, 0, 11+i, 4-i, z),
					BooleanOp.OR);
		}
		return VoxelShaper.forHorizontal(shape.optimize(), Direction.SOUTH);
	}

	private static VoxelShaper dlaShape(double z) {
		VoxelShape shape = Block.box(6, 2, 0, 16, 4, z);
		for(int i = 0; i < 4; ++i) {
			shape = Shapes.joinUnoptimized(
					shape, clampedBox(2+i, -2+i, 0, 3+i, 1+i, z),
					BooleanOp.OR);
		}
		return VoxelShaper.forHorizontal(shape.optimize(), Direction.SOUTH);
	}

	private static VoxelShaper uraShape(double z) {
		VoxelShape shape = Block.box(0, 12, 0, 10, 14, z);
		for(int i = 0; i < 4; ++i) {
			shape = Shapes.joinUnoptimized(
					shape, clampedBox(10+i, 12+i, 0, 11+i, 15+i, z),
					BooleanOp.OR);
		}
		return VoxelShaper.forHorizontal(shape.optimize(), Direction.SOUTH);
	}

	private static VoxelShaper ulaShape(double z) {
		VoxelShape shape = Block.box(6, 12, 0, 16, 14, z);
		for(int i = 0; i < 4; ++i) {
			shape = Shapes.joinUnoptimized(
					shape, clampedBox(2+i, 15-i, 0, 3+i, 18-i, z),
					BooleanOp.OR);
		}
		return VoxelShaper.forHorizontal(shape.optimize(), Direction.SOUTH);
	}
}
