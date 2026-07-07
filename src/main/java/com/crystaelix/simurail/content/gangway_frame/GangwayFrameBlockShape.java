package com.crystaelix.simurail.content.gangway_frame;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.DoubleFunction;
import java.util.function.IntFunction;

import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector3d;

import com.crystaelix.simurail.api.math.Quad3d;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.ByIdMap.OutOfBoundsStrategy;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public enum GangwayFrameBlockShape implements GangwayFrameShape, StringRepresentable {
	D(
			new Vector2d(0, 0.125), new Vector2d(1, 0.125),
			new Vector2d(1, 0.25), new Vector2d(0, 0.25),
			z -> VoxelShaper.forHorizontal(Block.box(0, 2, 0, 16, 4, z), Direction.SOUTH)),
	U(
			new Vector2d(1, 0.875), new Vector2d(0, 0.875),
			new Vector2d(0, 0.75), new Vector2d(1, 0.75),
			z -> VoxelShaper.forHorizontal(Block.box(0, 12, 0, 16, 14, z), Direction.SOUTH)),
	L(
			new Vector2d(0.125, 1), new Vector2d(0.125, 0),
			new Vector2d(0.25, 0), new Vector2d(0.25, 1),
			z -> VoxelShaper.forHorizontal(Block.box(2, 0, 0, 4, 16, z), Direction.SOUTH)),
	R(
			new Vector2d(0.875, 0), new Vector2d(0.875, 1),
			new Vector2d(0.75, 1), new Vector2d(0.75, 0),
			z -> VoxelShaper.forHorizontal(Block.box(12, 0, 0, 14, 16, z), Direction.SOUTH)),

	DL(
			new Vector2d(0, 0.125), new Vector2d(0.125, 0),
			new Vector2d(0.25, 0), new Vector2d(0, 0.25),
			z -> VoxelShaper.forHorizontal(createShortShape(0, 2, 0, 0, -2, z), Direction.SOUTH)),
	RD(
			new Vector2d(0.875, 0), new Vector2d(1, 0.125),
			new Vector2d(1, 0.25), new Vector2d(0.75, 0),
			z -> VoxelShaper.forHorizontal(createShortShape(12, 0, 14, 0, 2, z), Direction.SOUTH)),
	LU(
			new Vector2d(0.125, 1), new Vector2d(0, 0.875),
			new Vector2d(0, 0.75), new Vector2d(0.25, 1),
			z -> VoxelShaper.forHorizontal(createShortShape(0, 12, 0, 14, 2, z), Direction.SOUTH)),
	UR(
			new Vector2d(1, 0.875), new Vector2d(0.875, 1),
			new Vector2d(0.75, 1), new Vector2d(1, 0.75),
			z -> VoxelShaper.forHorizontal(createShortShape(12, 14, 14, 14, -2, z), Direction.SOUTH)),

	LD(
			new Vector2d(0.125, 1), new Vector2d(1, 0.125),
			new Vector2d(1, 0.25), new Vector2d(0.25, 1),
			z -> VoxelShaper.forHorizontal(createLongShape(2, 14, 4, 14, -2, z), Direction.SOUTH)),
	DR(
			new Vector2d(0, 0.125), new Vector2d(0.875, 1),
			new Vector2d(0.75, 1), new Vector2d(0, 0.25),
			z -> VoxelShaper.forHorizontal(createLongShape(0, 2, 0, 4, 2, z), Direction.SOUTH)),
	UL(
			new Vector2d(1, 0.875), new Vector2d(0.125, 0),
			new Vector2d(0.25, 0), new Vector2d(1, 0.75),
			z -> VoxelShaper.forHorizontal(createLongShape(2, 0, 4, 0, 2, z), Direction.SOUTH)),
	RU(
			new Vector2d(0.875, 0), new Vector2d(0, 0.875),
			new Vector2d(0, 0.75), new Vector2d(0.75, 0),
			z -> VoxelShaper.forHorizontal(createLongShape(0, 12, 0, 10, -2, z), Direction.SOUTH)),
	NONE(
			new Vector2d(), new Vector2d(),
			new Vector2d(), new Vector2d(),
			z -> VoxelShaper.forHorizontal(Shapes.empty(), Direction.SOUTH)),
	;

	public static final IntFunction<GangwayFrameBlockShape> BY_ID = ByIdMap.continuous(GangwayFrameBlockShape::ordinal, values(), OutOfBoundsStrategy.ZERO);
	public static final StreamCodec<ByteBuf, GangwayFrameBlockShape> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, GangwayFrameBlockShape::ordinal);

	public static final Set<GangwayFrameBlockShape> D_CW = EnumSet.of(D, DL, DR);
	public static final Set<GangwayFrameBlockShape> U_CW = EnumSet.of(U, UR, UL);
	public static final Set<GangwayFrameBlockShape> L_CW = EnumSet.of(L, LU, LD);
	public static final Set<GangwayFrameBlockShape> R_CW = EnumSet.of(R, RD, RU);

	public static final Set<GangwayFrameBlockShape> D_CCW = EnumSet.of(D, RD, LD);
	public static final Set<GangwayFrameBlockShape> U_CCW = EnumSet.of(U, LU, RU);
	public static final Set<GangwayFrameBlockShape> L_CCW = EnumSet.of(L, DL, UL);
	public static final Set<GangwayFrameBlockShape> R_CCW = EnumSet.of(R, UR, DR);

	public static final Set<GangwayFrameBlockShape> NORMAL = EnumSet.of(D, U, L, R, DL, RD, LU, UR, LD, DR, UL, RU);
	public static final Set<GangwayFrameBlockShape> COUPLER = EnumSet.of(D, U, NONE);

	public static final Set<GangwayFrameBlockShape> ORTHO = EnumSet.of(D, U, L, R);
	public static final Set<GangwayFrameBlockShape> DIAG_SHORT = EnumSet.of(DL, RD, LU, UR);
	public static final Set<GangwayFrameBlockShape> DIAG_LONG = EnumSet.of(LD, DR, UL, RU);

	private final Vector2dc innerStart;
	private final Vector2dc innerEnd;
	private final Vector2dc outerStart;
	private final Vector2dc outerEnd;
	private final Vector2dc center;

	private final VoxelShaper[] shapes;
	private final VoxelShaper subLevelShapes;

	GangwayFrameBlockShape(
			Vector2dc innerStart, Vector2dc innerEnd,
			Vector2dc outerStart, Vector2dc outerEnd,
			DoubleFunction<VoxelShaper> shapes) {
		this.innerStart = innerStart;
		this.innerEnd = innerEnd;
		this.outerStart = outerStart;
		this.outerEnd = outerEnd;
		this.center = new Vector2d().
				fma(0.25, innerStart).
				fma(0.25, innerEnd).
				fma(0.25, outerStart).
				fma(0.25, outerEnd);
		this.shapes = new VoxelShaper[30];
		for(int i = 0; i < 30; ++i) {
			this.shapes[i] = shapes.apply(i + 3);
		}
		this.subLevelShapes = shapes.apply(0.25);
	}

	@Override
	public GangwayFrameBlockShape connectsTo() {
		return switch(this) {
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
	public String getSerializedName() {
		return name().toLowerCase(Locale.ROOT);
	}

	public Component getDisplayName() {
		return Component.translatable("block.simurail.gangway_frame." + getSerializedName());
	}

	public Vector3d innerStart(Direction facing, Vector3d dest) {
		return rotate(innerStart, facing, dest);
	}

	public Vector3d innerEnd(Direction facing, Vector3d dest) {
		return rotate(innerEnd, facing, dest);
	}

	public Vector3d outerStart(Direction facing, Vector3d dest) {
		return rotate(outerStart, facing, dest);
	}

	public Vector3d outerEnd(Direction facing, Vector3d dest) {
		return rotate(outerEnd, facing, dest);
	}

	public Vector3d center(Direction facing, Vector3d dest) {
		return rotate(center, facing, dest);
	}

	public Quad3d quad(Direction facing, Quad3d dest) {
		innerStart(facing, dest.v0);
		innerEnd(facing, dest.v1);
		outerStart(facing, dest.v2);
		outerEnd(facing, dest.v3);
		return dest;
	}

	@Override
	public Direction adjacentOffset(Direction facing, boolean clockwise) {
		Direction direction;
		if(clockwise) {
			direction = switch(this) {
			case D, RD, LD -> Direction.EAST;
			case U, LU, RU -> Direction.WEST;
			case L, DL, UL -> Direction.DOWN;
			case R, UR, DR -> Direction.UP;
			case NONE -> null;
			};
		}
		else {
			direction = switch(this) {
			case D, DL, DR -> Direction.WEST;
			case U, UR, UL -> Direction.EAST;
			case L, LU, LD -> Direction.UP;
			case R, RD, RU -> Direction.DOWN;
			case NONE -> null;
			};
		}
		if(direction == null) {
			return null;
		}
		if(direction.getAxis() == Direction.Axis.Y) {
			return direction;
		}
		return switch(facing) {
		case SOUTH -> direction;
		case EAST -> direction.getCounterClockWise();
		case NORTH -> direction.getOpposite();
		case WEST -> direction.getClockWise();
		case null, default -> throw new IllegalArgumentException("Unexpected value: " + facing);
		};
	}

	@Override
	public int adjacentTo(GangwayFrameShape other, boolean clockwise) {
		return adjacentTo(clockwise).contains(other) ? 1 : 0;
	}

	public Set<GangwayFrameBlockShape> adjacentTo(boolean clockwise) {
		if(clockwise) {
			return switch(this) {
			case D, RD, LD -> D_CW;
			case U, LU, RU -> U_CW;
			case L, DL, UL -> L_CW;
			case R, UR, DR -> R_CW;
			case NONE -> Set.of();
			};
		}
		else {
			return switch(this) {
			case D, DL, DR -> D_CCW;
			case U, UR, UL -> U_CCW;
			case L, LU, LD -> L_CCW;
			case R, RD, RU -> R_CCW;
			case NONE -> Set.of();
			};
		}
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
}
