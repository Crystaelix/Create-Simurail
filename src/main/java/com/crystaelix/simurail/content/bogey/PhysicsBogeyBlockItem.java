package com.crystaelix.simurail.content.bogey;

import org.joml.Quaterniondc;
import org.joml.Vector3dc;

import com.crystaelix.simurail.api.bogey.BogeyRenderedType;
import com.crystaelix.simurail.api.bogey.BogeyType;
import com.crystaelix.simurail.api.math.SimurailMath;
import com.crystaelix.simurail.api.track.TrackTypeOverrides;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class PhysicsBogeyBlockItem extends BlockItem {

	public PhysicsBogeyBlockItem(PhysicsBogeyBlock block, Properties properties) {
		super(block, properties);
	}

	@Override
	protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockPos relativePos = pos.relative(context.getClickedFace().getOpposite());
		BlockState relativeState = context.getLevel().getBlockState(relativePos);
		TrackType trackType = null;

		if(relativeState.getBlock() instanceof ITrackBlock track) {
			TrackType type = TrackTypeOverrides.getTrackType(track.getMaterial());
			if(BogeyType.hasDefault(type, state.getValue(PhysicsBogeyBlock.INVERTED))) {
				trackType = type;
			}
		}
		if(trackType != null && context.isSecondaryUseActive() && !context.replacingClickedOnBlock() && context.getClickedFace().getAxis() == Direction.Axis.Y) {
			return placeSubLevel(level, JOMLConversion.atBottomCenterOf(pos), SimurailMath.ROT_I, context.getPlayer(), context.getItemInHand(), state, trackType);
		}

		boolean disableRotation = true;
		boolean enableOffset = false;
		if(Sable.HELPER.getContaining(level, pos) instanceof ServerSubLevel subLevel) {
			for(BlockEntitySubLevelActor actor : subLevel.getPlot().getBlockEntityActors()) {
				if(actor instanceof PhysicsBogeyBlockEntity) {
					disableRotation = false;
					break;
				}
			}
		}

		boolean result = level.setBlock(pos, state, Block.UPDATE_ALL_IMMEDIATE);
		if(context.getLevel().getBlockEntity(pos) instanceof PhysicsBogeyBlockEntity bogey) {
			if(disableRotation) {
				bogey.options.setAngularType(0);
			}
			if(enableOffset) {
				bogey.options.setLinearType(1);
			}
			if(trackType != null) {
				bogey.options.type = BogeyRenderedType.getDefault(trackType, state.getValue(PhysicsBogeyBlock.INVERTED));
			}
			bogey.setChanged();
		}

		return result;
	}

	public boolean placeSubLevel(Level level, Vector3dc position, Quaterniondc orientation, Player player, ItemStack stack, BlockState state, TrackType trackType) {
		if(level.isClientSide()) {
			return true;
		}

		SubLevelContainer container = SubLevelContainer.getContainer(level);
		SubLevel containingSubLevel = Sable.HELPER.getContaining(level, position);

		Pose3d pose = new Pose3d();
		pose.position().set(position).add(0, 0.5, 0);
		pose.orientation().set(orientation);
		if(containingSubLevel != null) {
			Pose3d containingPose = containingSubLevel.logicalPose();
			containingPose.transformPosition(pose.position());
			pose.orientation().set(containingPose.orientation());
		}

		SubLevel subLevel = container.allocateNewSubLevel(pose);
		LevelPlot plot = subLevel.getPlot();
		plot.newEmptyChunk(plot.getCenterChunk());
		BlockPos pos = plot.getCenterBlock();
		boolean result = level.setBlock(pos, state, Block.UPDATE_ALL_IMMEDIATE);
		subLevel.updateLastPose();

		if(result) {
			BlockState placedState = level.getBlockState(pos);
			if(placedState.is(state.getBlock())) {
				updateCustomBlockEntityTag(pos, level, player, stack, placedState);
				if(level.getBlockEntity(pos) instanceof PhysicsBogeyBlockEntity bogey) {
					bogey.options.setAngularType(0);
					if(trackType != null) {
						bogey.options.type = BogeyRenderedType.getDefault(trackType, state.getValue(PhysicsBogeyBlock.INVERTED));
					}
					bogey.applyComponentsFromItemStack(stack);
					bogey.setChanged();
				}
				placedState.getBlock().setPlacedBy(level, pos, placedState, player, stack);
				CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)player, pos, stack);
			}
		}

		return result;
	}
}
