package com.crystaelix.simurail.content.bogey;

import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import com.crystaelix.simurail.api.bogey.BogeyRenderedType;
import com.crystaelix.simurail.api.bogey.BogeyType;
import com.crystaelix.simurail.api.math.SimurailMath;
import com.crystaelix.simurail.api.track.TrackTypeOverrides;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;
import com.simibubi.create.content.trains.track.TrackShape;

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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class PhysicsBogeyBlockItem extends BlockItem {

	public PhysicsBogeyBlockItem(PhysicsBogeyBlock block, Properties properties) {
		super(block, properties);
	}

	@Override
	protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		Direction clickedFace = context.getClickedFace();
		BlockPos relativePos = pos.relative(clickedFace.getOpposite());
		BlockState relativeState = context.getLevel().getBlockState(relativePos);
		boolean inverted = state.getValue(BlockStateProperties.INVERTED);
		TrackType trackType = null;

		if(relativeState.getBlock() instanceof ITrackBlock track) {
			TrackType type = TrackTypeOverrides.getTrackType(track.getMaterial());
			if(BogeyType.hasDefault(type, inverted)) {
				trackType = type;
			}
		}
		a:if(trackType != null && context.isSecondaryUseActive() && !context.replacingClickedOnBlock()) {
			state = state.setValue(BlockStateProperties.WATERLOGGED, false);
			Vector3dc position;
			Quaterniondc orientation;
			TrackShape shape = relativeState.getOptionalValue(TrackBlock.SHAPE).orElse(null);
			double offset = inverted ? -0.625 : 1.625;
			switch(shape) {
			case AN -> {
				if(inverted ? clickedFace != Direction.DOWN && clickedFace != Direction.NORTH :
					clickedFace != Direction.UP && clickedFace != Direction.SOUTH) break a;
				offset *= SimurailMath.SQRT_2/2;
				position = JOMLConversion.atCenterOf(relativePos).add(0, offset, offset);
				orientation = new Quaterniond().rotateX(Math.PI / 4);
			}
			case AS -> {
				if(inverted ? clickedFace != Direction.DOWN && clickedFace != Direction.SOUTH :
					clickedFace != Direction.UP && clickedFace != Direction.NORTH) break a;
				offset *= SimurailMath.SQRT_2/2;
				position = JOMLConversion.atCenterOf(relativePos).add(0, offset, -offset);
				orientation = new Quaterniond().rotateX(-Math.PI / 4);
			}
			case AE -> {
				if(inverted ? clickedFace != Direction.DOWN && clickedFace != Direction.EAST :
					clickedFace != Direction.UP && clickedFace != Direction.WEST) break a;
				offset *= SimurailMath.SQRT_2/2;
				position = JOMLConversion.atCenterOf(relativePos).add(-offset, offset, 0);
				orientation = new Quaterniond().rotateZ(Math.PI / 4);
			}
			case AW -> {
				if(inverted ? clickedFace != Direction.DOWN && clickedFace != Direction.WEST :
					clickedFace != Direction.UP && clickedFace != Direction.EAST) break a;
				offset *= SimurailMath.SQRT_2/2;
				position = JOMLConversion.atCenterOf(relativePos).add(offset, offset, 0);
				orientation = new Quaterniond().rotateZ(-Math.PI / 4);
			}
			case PD, CR_D -> {
				if(inverted ? clickedFace != Direction.DOWN : clickedFace != Direction.UP) break a;
				Direction direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
				position = JOMLConversion.atBottomCenterOf(relativePos).add(0, offset, 0);
				orientation = new Quaterniond().rotateY(direction.getAxis() == Direction.Axis.X ? -Math.PI / 4 : Math.PI / 4);
			}
			case ND -> {
				if(inverted ? clickedFace != Direction.DOWN : clickedFace != Direction.UP) break a;
				Direction direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
				position = JOMLConversion.atBottomCenterOf(relativePos).add(0, offset, 0);
				orientation = new Quaterniond().rotateY(direction.getAxis() == Direction.Axis.X ? Math.PI / 4 : -Math.PI / 4);
			}
			case ZO, XO, TN, TS, TE, TW, CR_O, CR_PDX, CR_PDZ, CR_NDX, CR_NDZ -> {
				if(inverted ? clickedFace != Direction.DOWN : clickedFace != Direction.UP) break a;
				position = JOMLConversion.atBottomCenterOf(relativePos).add(0, offset, 0);
				orientation = SimurailMath.ROT_I;
			}
			case null, default -> {
				if(relativeState.getBlock() instanceof ITrackBlock track) {
					// Assume FlexiTrack-like
					Vector3d normal = JOMLConversion.toJOML(track.getUpNormal(level, relativePos, relativeState));
					double dot = normal.x * clickedFace.getStepX() + normal.y * clickedFace.getStepY() + normal.z * clickedFace.getStepZ();
					if(inverted ? dot > -0.5 : dot < 0.5) break a;
					state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST);
					Vector3d direction = JOMLConversion.toJOML(track.getTrackAxes(level, relativePos, relativeState).get(0));
					position = JOMLConversion.atBottomCenterOf(relativePos).fma(offset, normal);
					orientation = SimurailMath.rot(direction, normal, new Quaterniond());
				}
				else break a;
			}
			}
			return placeSubLevel(level, position, orientation, context.getPlayer(), context.getItemInHand(), state, trackType);
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
		pose.position().set(position);
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
