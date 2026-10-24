package com.crystaelix.simurail.content.gangway_frame.copycat;

import com.crystaelix.simurail.content.gangway_frame.GangwayFrameBlockShape;
import com.simibubi.create.content.decoration.copycat.CopycatSpecialCases;

import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CopycatPanelGangwayFrameBlock extends CopycatGangwayFrameBlock {

	public static final VoxelShaper D_SHAPES = VoxelShaper.forHorizontal(box(0, 4, 0, 16, 16, 3), Direction.SOUTH);
	public static final VoxelShaper U_SHAPES = VoxelShaper.forHorizontal(box(0, 0, 0, 16, 12, 3), Direction.SOUTH);
	public static final VoxelShaper L_SHAPES = VoxelShaper.forHorizontal(box(4, 0, 0, 16, 16, 3), Direction.SOUTH);
	public static final VoxelShaper R_SHAPES = VoxelShaper.forHorizontal(box(0, 0, 0, 12, 16, 3), Direction.SOUTH);
	public static final VoxelShaper DL_SHAPES = combine(D_SHAPES, L_SHAPES);
	public static final VoxelShaper RD_SHAPES = combine(R_SHAPES, D_SHAPES);
	public static final VoxelShaper LU_SHAPES = combine(L_SHAPES, U_SHAPES);
	public static final VoxelShaper UR_SHAPES = combine(U_SHAPES, R_SHAPES);
	public static final VoxelShaper DRA_SHAPES = D_SHAPES;
	public static final VoxelShaper DLA_SHAPES = D_SHAPES;
	public static final VoxelShaper URA_SHAPES = U_SHAPES;
	public static final VoxelShaper ULA_SHAPES = U_SHAPES;
	public static final VoxelShaper D_GANGWAY_SHAPES = combine(D_SHAPES, GangwayFrameBlockShape.D.shapes);
	public static final VoxelShaper U_GANGWAY_SHAPES = combine(U_SHAPES, GangwayFrameBlockShape.U.shapes);
	public static final VoxelShaper L_GANGWAY_SHAPES = combine(L_SHAPES, GangwayFrameBlockShape.L.shapes);
	public static final VoxelShaper R_GANGWAY_SHAPES = combine(R_SHAPES, GangwayFrameBlockShape.R.shapes);
	public static final VoxelShaper DL_GANGWAY_SHAPES = combine(DL_SHAPES, GangwayFrameBlockShape.DL.shapes);
	public static final VoxelShaper RD_GANGWAY_SHAPES = combine(RD_SHAPES, GangwayFrameBlockShape.RD.shapes);
	public static final VoxelShaper LU_GANGWAY_SHAPES = combine(LU_SHAPES, GangwayFrameBlockShape.LU.shapes);
	public static final VoxelShaper UR_GANGWAY_SHAPES = combine(UR_SHAPES, GangwayFrameBlockShape.UR.shapes);
	public static final VoxelShaper DRA_GANGWAY_SHAPES = combine(DRA_SHAPES, GangwayFrameBlockShape.DRA.shapes);
	public static final VoxelShaper DLA_GANGWAY_SHAPES = combine(DLA_SHAPES, GangwayFrameBlockShape.DLA.shapes);
	public static final VoxelShaper URA_GANGWAY_SHAPES = combine(URA_SHAPES, GangwayFrameBlockShape.URA.shapes);
	public static final VoxelShaper ULA_GANGWAY_SHAPES = combine(ULA_SHAPES, GangwayFrameBlockShape.ULA.shapes);

	public static VoxelShaper combine(VoxelShaper shapes1, VoxelShaper shapes2) {
		return VoxelShaper.forHorizontal(Shapes.or(shapes1.get(Direction.SOUTH), shapes2.get(Direction.SOUTH)), Direction.SOUTH);
	}

	public CopycatPanelGangwayFrameBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		Direction facing = state.getValue(FACING);
		return switch(state.getValue(SHAPE)) {
		case D -> D_GANGWAY_SHAPES.get(facing);
		case U -> U_GANGWAY_SHAPES.get(facing);
		case L -> L_GANGWAY_SHAPES.get(facing);
		case R -> R_GANGWAY_SHAPES.get(facing);
		case DL -> DL_GANGWAY_SHAPES.get(facing);
		case RD -> RD_GANGWAY_SHAPES.get(facing);
		case LU -> LU_GANGWAY_SHAPES.get(facing);
		case UR -> UR_GANGWAY_SHAPES.get(facing);
		case DRA -> DRA_GANGWAY_SHAPES.get(facing);
		case DLA -> DLA_GANGWAY_SHAPES.get(facing);
		case URA -> URA_GANGWAY_SHAPES.get(facing);
		case ULA -> ULA_GANGWAY_SHAPES.get(facing);
		default -> Shapes.empty();
		};
	}

	@Override
	public VoxelShape getSubLevelCollisionShape(BlockGetter blockGetter, BlockState state) {
		Direction facing = state.getValue(FACING);
		return switch(state.getValue(SHAPE)) {
		case D -> D_SHAPES.get(facing);
		case U -> U_SHAPES.get(facing);
		case L -> L_SHAPES.get(facing);
		case R -> R_SHAPES.get(facing);
		case DL -> DL_SHAPES.get(facing);
		case RD -> RD_SHAPES.get(facing);
		case LU -> LU_SHAPES.get(facing);
		case UR -> UR_SHAPES.get(facing);
		case DRA -> DRA_SHAPES.get(facing);
		case DLA -> DLA_SHAPES.get(facing);
		case URA -> URA_SHAPES.get(facing);
		case ULA -> ULA_SHAPES.get(facing);
		default -> Shapes.empty();
		};
	}

	@Override
	protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
		Direction facing = state.getValue(FACING);
		return switch(state.getValue(SHAPE)) {
		case D -> D_SHAPES.get(facing);
		case U -> U_SHAPES.get(facing);
		case L -> L_SHAPES.get(facing);
		case R -> R_SHAPES.get(facing);
		case DL -> DL_SHAPES.get(facing);
		case RD -> RD_SHAPES.get(facing);
		case LU -> LU_SHAPES.get(facing);
		case UR -> UR_SHAPES.get(facing);
		case DRA -> DRA_SHAPES.get(facing);
		case DLA -> DLA_SHAPES.get(facing);
		case URA -> URA_SHAPES.get(facing);
		case ULA -> ULA_SHAPES.get(facing);
		default -> Shapes.empty();
		};
	}

	@Override
	public boolean isAcceptedRegardless(BlockState material) {
		return CopycatSpecialCases.isBarsMaterial(material);
	}
}
