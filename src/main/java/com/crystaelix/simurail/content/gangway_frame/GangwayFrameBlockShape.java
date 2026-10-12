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
			z -> VoxelShaper.forHorizontal(Block.box(0, 2, 0, 16, 4, z), Direction.SOUTH)),
	U(
			HorizontalRelativeDirection.CW,
			HorizontalRelativeDirection.ACW,
			new Quad2d(v(0, 12), v(16, 12), v(16, 14), v(0, 14)),
			z -> VoxelShaper.forHorizontal(Block.box(0, 12, 0, 16, 14, z), Direction.SOUTH)),
	L(
			HorizontalRelativeDirection.D,
			HorizontalRelativeDirection.U,
			new Quad2d(v(2, 16), v(2, 0), v(4, 0), v(4, 16)),
			z -> VoxelShaper.forHorizontal(Block.box(2, 0, 0, 4, 16, z), Direction.SOUTH)),
	R(
			HorizontalRelativeDirection.D,
			HorizontalRelativeDirection.U,
			new Quad2d(v(12, 16), v(12, 0), v(14, 0), v(14, 16)),
			z -> VoxelShaper.forHorizontal(Block.box(12, 0, 0, 14, 16, z), Direction.SOUTH)),
	// Short diagonal
	DL(
			HorizontalRelativeDirection.CW,
			HorizontalRelativeDirection.D,
			new Quad2d(v(0, 2), v(2, 0), v(4, 0), v(0, 4)),
			GangwayFrameBlockShape::dlShape),
	RD(
			HorizontalRelativeDirection.ACW,
			HorizontalRelativeDirection.D,
			new Quad2d(v(14, 0), v(16, 2), v(16, 4), v(12, 0)),
			GangwayFrameBlockShape::rdShape),
	LU(
			HorizontalRelativeDirection.CW,
			HorizontalRelativeDirection.U,
			new Quad2d(v(0, 12), v(4, 16), v(2, 16), v(0, 14)),
			GangwayFrameBlockShape::luShape),
	UR(
			HorizontalRelativeDirection.ACW,
			HorizontalRelativeDirection.U,
			new Quad2d(v(12, 16), v(16, 12), v(16, 14), v(14, 16)),
			GangwayFrameBlockShape::urShape),
	// Long diagonal
	LD(
			HorizontalRelativeDirection.ACW,
			HorizontalRelativeDirection.U,
			new Quad2d(v(2, 16), v(16, 2), v(16, 4), v(4, 16)),
			GangwayFrameBlockShape::ldShape),
	DR(
			HorizontalRelativeDirection.CW,
			HorizontalRelativeDirection.U,
			new Quad2d(v(0, 2), v(14, 16), v(12, 16), v(0, 4)),
			GangwayFrameBlockShape::drShape),
	UL(
			HorizontalRelativeDirection.ACW,
			HorizontalRelativeDirection.D,
			new Quad2d(v(4, 0), v(16, 12), v(16, 14), v(2, 0)),
			GangwayFrameBlockShape::ulShape),
	RU(
			HorizontalRelativeDirection.CW,
			HorizontalRelativeDirection.D,
			new Quad2d(v(0, 12), v(12, 0), v(14, 0), v(0, 14)),
			GangwayFrameBlockShape::ruShape),
	// None
	NONE(
			HorizontalRelativeDirection.I,
			HorizontalRelativeDirection.I,
			List.of(),
			z -> VoxelShaper.forHorizontal(Shapes.empty(), Direction.SOUTH)),
	;

	public static final IntFunction<GangwayFrameBlockShape> BY_ID = ByIdMap.continuous(GangwayFrameBlockShape::ordinal, values(), OutOfBoundsStrategy.ZERO);
	public static final StreamCodec<ByteBuf, GangwayFrameBlockShape> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, GangwayFrameBlockShape::ordinal);

	public static final Set<GangwayFrameBlockShape> NORMAL = EnumSet.of(D, U, L, R, DL, RD, LU, UR, LD, DR, UL, RU);
	public static final Set<GangwayFrameBlockShape> COUPLER = EnumSet.of(D, U, NONE);

	public static final Set<GangwayFrameBlockShape> D_CW = EnumSet.of(D, RD, LD);
	public static final Set<GangwayFrameBlockShape> D_ACW = EnumSet.of(D, DL, DR);
	public static final Set<GangwayFrameBlockShape> U_CW = EnumSet.of(U, UR, UL);
	public static final Set<GangwayFrameBlockShape> U_ACW = EnumSet.of(U, LU, RU);

	public static final Set<GangwayFrameBlockShape> L_D = EnumSet.of(L, LU, LD);
	public static final Set<GangwayFrameBlockShape> L_U = EnumSet.of(L, DL, UL);
	public static final Set<GangwayFrameBlockShape> R_D = EnumSet.of(R, UR, DR);
	public static final Set<GangwayFrameBlockShape> R_U = EnumSet.of(R, RD, RU);

	private final Couple<HorizontalRelativeDirection> adjacentOffsets;

	private final List<Quad2dc> quads;
	private final Vector2dc anchor;

	private final VoxelShaper[] shapes;
	private final VoxelShaper subLevelShapes;

	GangwayFrameBlockShape(HorizontalRelativeDirection offset1, HorizontalRelativeDirection offset2, List<Quad2dc> quads, DoubleFunction<VoxelShaper> shapes) {
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

		this.shapes = new VoxelShaper[30];
		for(int i = 0; i < 30; ++i) {
			this.shapes[i] = shapes.apply(i + 3);
		}
		this.subLevelShapes = shapes.apply(0.25);
	}

	GangwayFrameBlockShape(HorizontalRelativeDirection offset1, HorizontalRelativeDirection offset2, Quad2dc quad, DoubleFunction<VoxelShaper> shapes) {
		this(offset1, offset2, List.of(quad), shapes);
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
			case D, RD, LD -> adjacent = D_CW;
			case U, UR, UL -> adjacent = U_CW;
			default -> {}
			}
		}
		case ACW -> {
			switch(this) {
			case D, RD, LD -> adjacent = D_ACW;
			case U, UR, UL -> adjacent = U_ACW;
			default -> {}
			}
		}
		case D -> {
			switch(this) {
			case L, DL, UL -> adjacent = L_D;
			case R, RD, RU -> adjacent = R_D;
			default -> {}
			}
		}
		case U -> {
			switch(this) {
			case L, LU, LD -> adjacent = L_U;
			case R, UR, DR -> adjacent = R_U;
			default -> {}
			}
		}
		case null, default -> {}
		}
		return adjacent.contains(other);
	}

	public VoxelShape getShape(Direction facing) {
		return getShape(facing, 0);
	}

	public VoxelShape getShape(Direction facing, int index) {
		return shapes[Math.clamp(index, 0, 29)].get(facing);
	}

	public VoxelShape getShapeForLength(Direction facing, double length) {
		return getShape(facing, (int)Math.round(length) - 1);
	}

	public VoxelShape getSubLevelShape(Direction facing) {
		return subLevelShapes.get(facing);
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
}
