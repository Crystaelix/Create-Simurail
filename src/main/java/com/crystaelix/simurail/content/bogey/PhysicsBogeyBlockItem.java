package com.crystaelix.simurail.content.bogey;

import com.crystaelix.simurail.api.bogey.BogeyRenderedType;
import com.crystaelix.simurail.api.bogey.BogeyType;
import com.crystaelix.simurail.api.track.TrackTypeOverrides;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.CommonLevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class PhysicsBogeyBlockItem extends BlockItem {

	public PhysicsBogeyBlockItem(PhysicsBogeyBlock block, Properties properties) {
		super(block, properties);
	}

	@Override
	protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
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
			return placeSubLevel(context, state, trackType);
		}
		boolean result = context.getLevel().setBlock(pos, state, Block.UPDATE_ALL_IMMEDIATE);
		if(context.getLevel().getBlockEntity(pos) instanceof PhysicsBogeyBlockEntity bogey) {
			if(trackType != null) {
				bogey.options.type = BogeyRenderedType.getDefault(trackType, state.getValue(PhysicsBogeyBlock.INVERTED));
			}
			ItemStack stack = context.getItemInHand();
			if(stack.has(DataComponents.CUSTOM_NAME)) {
				bogey.setCustomName(stack.get(DataComponents.CUSTOM_NAME));
			}
		}
		return result;
	}

	protected boolean placeSubLevel(BlockPlaceContext context, BlockState state, TrackType trackType) {
		Level level = context.getLevel();

		if(level.isClientSide()) {
			return true;
		}

		SubLevelContainer container = SubLevelContainer.getContainer(context.getLevel());
		BlockPos pos = context.getClickedPos();
		SubLevel containingSubLevel = Sable.HELPER.getContaining(level, pos);

		Pose3d pose = new Pose3d();
		JOMLConversion.atCenterOf(pos, pose.position());
		if(containingSubLevel != null) {
			Pose3d containingPose = containingSubLevel.logicalPose();
			containingPose.transformPosition(pose.position());
			pose.orientation().set(containingPose.orientation());
		}

		SubLevel subLevel = container.allocateNewSubLevel(pose);
		LevelPlot plot = subLevel.getPlot();
		plot.newEmptyChunk(plot.getCenterChunk());
		CommonLevelAccessor plotAccessor = plot.getEmbeddedLevelAccessor();
		boolean result = plotAccessor.setBlock(BlockPos.ZERO, state, Block.UPDATE_ALL_IMMEDIATE);
		subLevel.updateLastPose();

		if(result && plotAccessor.getBlockEntity(BlockPos.ZERO) instanceof PhysicsBogeyBlockEntity bogey) {
			bogey.options.setAngularType(0);
			if(trackType != null) {
				bogey.options.type = BogeyRenderedType.getDefault(trackType, state.getValue(PhysicsBogeyBlock.INVERTED));
			}
			ItemStack stack = context.getItemInHand();
			if(stack.has(DataComponents.CUSTOM_NAME)) {
				bogey.setCustomName(stack.get(DataComponents.CUSTOM_NAME));
			}
		}

		return result;
	}
}
