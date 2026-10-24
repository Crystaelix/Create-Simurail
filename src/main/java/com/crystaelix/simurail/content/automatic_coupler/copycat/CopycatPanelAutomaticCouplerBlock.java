package com.crystaelix.simurail.content.automatic_coupler.copycat;

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

public class CopycatPanelAutomaticCouplerBlock extends CopycatAutomaticCouplerBlock {

	public static final VoxelShaper SHAPES = VoxelShaper.forHorizontal(box(0, 0, 0, 16, 16, 3), Direction.SOUTH);
	public static final VoxelShaper TOP_SHAPES = VoxelShaper.forHorizontal(box(0, 4, 0, 16, 16, 3), Direction.SOUTH);
	public static final VoxelShaper BOTTOM_SHAPES = VoxelShaper.forHorizontal(box(0, 0, 0, 16, 12, 3), Direction.SOUTH);
	public static final VoxelShaper TOP_GANGWAY_SHAPES = combine(TOP_SHAPES, GangwayFrameBlockShape.D.shapes);
	public static final VoxelShaper BOTTOM_GANGWAY_SHAPES = combine(BOTTOM_SHAPES, GangwayFrameBlockShape.U.shapes);

	public static VoxelShaper combine(VoxelShaper shapes1, VoxelShaper shapes2) {
		return VoxelShaper.forHorizontal(Shapes.or(shapes1.get(Direction.SOUTH), shapes2.get(Direction.SOUTH)), Direction.SOUTH);
	}

	public CopycatPanelAutomaticCouplerBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		Direction facing = state.getValue(FACING);
		return switch(state.getValue(SHAPE)) {
		case TOP -> (state.getValue(GANGWAY) ? TOP_GANGWAY_SHAPES : TOP_SHAPES).get(facing);
		case BOTTOM -> (state.getValue(GANGWAY) ? BOTTOM_GANGWAY_SHAPES : BOTTOM_SHAPES).get(facing);
		case null, default -> SHAPES.get(facing);
		};
	}

	@Override
	protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
		Direction facing = state.getValue(FACING);
		return switch(state.getValue(SHAPE)) {
		case TOP -> TOP_SHAPES.get(facing);
		case BOTTOM -> BOTTOM_SHAPES.get(facing);
		case null, default -> SHAPES.get(facing);
		};
	}

	@Override
	public VoxelShape getSubLevelCollisionShape(BlockGetter blockGetter, BlockState state) {
		Direction facing = state.getValue(FACING);
		return switch(state.getValue(SHAPE)) {
		case TOP -> TOP_SHAPES.get(facing);
		case BOTTOM -> BOTTOM_SHAPES.get(facing);
		case null, default -> SHAPES.get(facing);
		};
	}

	@Override
	public boolean isAcceptedRegardless(BlockState material) {
		return CopycatSpecialCases.isBarsMaterial(material);
	}
}
