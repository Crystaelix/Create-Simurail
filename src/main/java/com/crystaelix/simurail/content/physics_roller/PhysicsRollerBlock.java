package com.crystaelix.simurail.content.physics_roller;

import java.util.function.Predicate;

import com.crystaelix.simurail.content.SimurailBlockEntities;
import com.crystaelix.simurail.content.SimurailBlocks;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.contraptions.actors.AttachedActorBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.placement.PoleHelper;

import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PhysicsRollerBlock extends AttachedActorBlock implements IBE<PhysicsRollerBlockEntity> {

	private static final int PLACEMENT_HELPER_ID = PlacementHelpers.register(new PlacementHelper());

	public static final MapCodec<PhysicsRollerBlock> CODEC = simpleCodec(PhysicsRollerBlock::new);

	public PhysicsRollerBlock(Properties properties) {
		super(properties);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return withWater(defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()), context);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Shapes.block();
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return true;
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		IPlacementHelper placementHelper = PlacementHelpers.get(PLACEMENT_HELPER_ID);
		if(!player.isShiftKeyDown() && player.mayBuild() && placementHelper.matchesItem(stack)) {
			placementHelper.getOffset(player, level, state, pos, hitResult).
					placeInWorld(level, (BlockItem)stack.getItem(), player, hand, hitResult);
			return ItemInteractionResult.SUCCESS;
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		IBE.onRemove(state, level, pos, newState);
	}

	@Override
	public Class<PhysicsRollerBlockEntity> getBlockEntityClass() {
		return PhysicsRollerBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends PhysicsRollerBlockEntity> getBlockEntityType() {
		return SimurailBlockEntities.PHYSICS_ROLLER.get();
	}

	@Override
	protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
		return CODEC;
	}

	private static class PlacementHelper extends PoleHelper<Direction> {

		public PlacementHelper() {
			super(SimurailBlocks.PHYSICS_ROLLER::has, state -> state.getValue(FACING).getClockWise().getAxis(), FACING);
		}

		@Override
		public Predicate<ItemStack> getItemPredicate() {
			return SimurailBlocks.PHYSICS_ROLLER::isIn;
		}
	}
}
