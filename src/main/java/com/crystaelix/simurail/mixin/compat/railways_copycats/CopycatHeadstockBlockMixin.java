package com.crystaelix.simurail.mixin.compat.railways_copycats;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.copycatsplus.copycats.foundation.copycat.CopycatExternalContext;
import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.railwayteam.railways.content.buffer.headstock.CopycatHeadstockBlock;
import com.simibubi.create.content.decoration.copycat.CopycatBlockEntity;
import com.simibubi.create.content.decoration.copycat.WaterloggedCopycatBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

@Mixin(CopycatHeadstockBlock.class)
public abstract class CopycatHeadstockBlockMixin extends WaterloggedCopycatBlock implements ICopycatBlock {

	public CopycatHeadstockBlockMixin(Properties properties) {
		super(properties);
	}

	@Override
	public BlockState getAppearance(BlockState state, BlockAndTintGetter level, BlockPos pos, Direction side, BlockState queryState, BlockPos queryPos) {
		if(!isCTEnabled(state, level, queryPos)) {
			return state;
		}
		return super.getAppearance(state, level, pos, side, queryState, queryPos);
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		return toggleCT(state, level, pos, player, hitResult);
	}

	@Override
	@Unique
	public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	@Unique
	public CopycatBlockEntity getBlockEntity(BlockGetter level, BlockPos pos) {
		return super.getBlockEntity(level, pos);
	}

	@WrapMethod(method = "isIgnoredConnectivitySide")
	private boolean simurail$copycats$isIgnoredConnectivitySide(BlockAndTintGetter level, BlockState state, Direction face, BlockPos fromPos, BlockPos toPos, Operation<Boolean> original) {
		boolean originalValue = original.call(level, state, face, fromPos, toPos);
		if(!originalValue) {
			return false;
		}
		if(CopycatExternalContext.isForBlockingLogic()) {
			return false;
		}
		if(toPos == null) {
			return true;
		}
		return !checkConnection(level, toPos, fromPos, level.getBlockState(toPos));
	}

	@WrapMethod(method = "canConnectTexturesToward")
	private boolean simurail$copycats$canConnectTexturesToward(BlockAndTintGetter level, BlockPos fromPos, BlockPos toPos, BlockState fromState, Operation<Boolean> original) {
		boolean originalValue = original.call(level, fromPos, toPos, fromState);
		if(originalValue) {
			return true;
		}
		BlockState toState = level.getBlockState(toPos);
		if(!toState.is(this) && toState.getBlock() instanceof ICopycatBlock) {
			return true;
		}
		return checkConnection(level, fromPos, toPos, fromState);
	}

	@WrapMethod(method = "hidesNeighborFace")
	private boolean simurail$copycats$hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir, Operation<Boolean> original) {
		return ICopycatBlock.hidesNeighborFace(level, pos, state, neighborState, dir);
	}

	@Unique
	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return super.mirror(state, mirror);
	}

	@Unique
	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return super.rotate(state, rot);
	}
}
