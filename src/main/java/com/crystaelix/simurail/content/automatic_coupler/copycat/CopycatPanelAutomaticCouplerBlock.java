package com.crystaelix.simurail.content.automatic_coupler.copycat;

import java.util.stream.IntStream;

import com.crystaelix.simurail.content.gangway_frame.GangwayFrameBlockShape;

import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CopycatPanelAutomaticCouplerBlock extends CopycatAutomaticCouplerBlock {

	public static final VoxelShaper SHAPES = VoxelShaper.forHorizontal(box(0, 0, 0, 16, 16, 3), Direction.SOUTH);
	public static final VoxelShaper TOP_SHAPES = VoxelShaper.forHorizontal(box(0, 4, 0, 16, 16, 3), Direction.SOUTH);
	public static final VoxelShaper BOTTOM_SHAPES = VoxelShaper.forHorizontal(box(0, 0, 0, 16, 12, 3), Direction.SOUTH);
	public static final VoxelShaper[] TOP_GANGWAY_SHAPES = IntStream.range(0, 30).
			mapToObj(i -> VoxelShaper.forHorizontal(Shapes.or(box(0, 4, 0, 16, 16, 3), GangwayFrameBlockShape.D.getShape(Direction.SOUTH, i)), Direction.SOUTH)).
			toArray(VoxelShaper[]::new);
	public static final VoxelShaper[] BOTTOM_GANGWAY_SHAPES = IntStream.range(0, 30).
			mapToObj(i -> VoxelShaper.forHorizontal(Shapes.or(box(0, 0, 0, 16, 12, 3), GangwayFrameBlockShape.U.getShape(Direction.SOUTH, i)), Direction.SOUTH)).
			toArray(VoxelShaper[]::new);

	public CopycatPanelAutomaticCouplerBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return switch(state.getValue(SHAPE)) {
		case TOP -> (state.getValue(GANGWAY) ? TOP_GANGWAY_SHAPES[0] : TOP_SHAPES).get(state.getValue(FACING));
		case BOTTOM -> (state.getValue(GANGWAY) ? BOTTOM_GANGWAY_SHAPES[0] : BOTTOM_SHAPES).get(state.getValue(FACING));
		case null, default -> SHAPES.get(state.getValue(FACING));
		};
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, int shapeIndex) {
		return switch(state.getValue(SHAPE)) {
		case TOP -> TOP_GANGWAY_SHAPES[shapeIndex].get(state.getValue(FACING));
		case BOTTOM -> BOTTOM_GANGWAY_SHAPES[shapeIndex].get(state.getValue(FACING));
		case null, default -> SHAPES.get(state.getValue(FACING));
		};
	}

	@Override
	public VoxelShape getSubLevelCollisionShape(BlockGetter blockGetter, BlockState state) {
		return switch(state.getValue(SHAPE)) {
		case TOP -> TOP_SHAPES.get(state.getValue(FACING));
		case BOTTOM -> BOTTOM_SHAPES.get(state.getValue(FACING));
		case null, default -> SHAPES.get(state.getValue(FACING));
		};
	}

	@Override
	public boolean isAcceptedRegardless(BlockState material) {
		return false;
	}

	@Override
	public BlockState prepareMaterial(Level level, BlockPos pos, BlockState state, Player player, InteractionHand hand, BlockHitResult hit, BlockState material) {
		return super.prepareMaterial(level, pos, state, player, hand, hit, material);
	}

	@Override
	public boolean canConnectTexturesToward(BlockAndTintGetter level, BlockPos fromPos, BlockPos toPos, BlockState state) {
		Direction facing = state.getValue(FACING);
		BlockState toState = level.getBlockState(toPos);
		if(toPos.equals(fromPos.relative(facing))) {
			return false;
		}
		BlockPos diff = fromPos.subtract(toPos);
		int coord = facing.getAxis().choose(diff.getX(), diff.getY(), diff.getZ());
		if(!toState.is(this)) {
			return coord != -facing.getAxisDirection().getStep();
		}
		if(isOccluded(state, toState, facing)) {
			return true;
		}
		if(coord == 0 && toState.getValue(FACING) == state.getValue(FACING) && toState.getValue(SHAPE) == state.getValue(SHAPE)) {
			return true;
		}
		return false;
	}
}
