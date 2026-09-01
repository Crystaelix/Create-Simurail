package com.crystaelix.simurail.content.bogey;

import org.joml.Matrix3d;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class PhysicsBogeyBlockItem extends BlockItem {

	public PhysicsBogeyBlockItem(PhysicsBogeyBlock block, Properties properties) {
		super(block, properties);
	}

	public BlockState defaultBlockState() {
		return getBlock().defaultBlockState();
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
			if(trackType != null && context.isSecondaryUseActive() && !context.replacingClickedOnBlock()) {
				Direction direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
				Vector3dc bogeyDirection = switch(direction) {
				case EAST -> SimurailMath.DIR_XP; case WEST -> SimurailMath.DIR_XN;
				case SOUTH -> SimurailMath.DIR_ZP; case NORTH -> SimurailMath.DIR_ZN;
				case null, default -> throw new IllegalArgumentException("Unexpected value: " + direction);
				};
				Vector3dc bogeyLateral = switch(direction) {
				case EAST -> SimurailMath.DIR_ZP; case WEST -> SimurailMath.DIR_ZN;
				case SOUTH -> SimurailMath.DIR_XN; case NORTH -> SimurailMath.DIR_XP;
				case null, default -> throw new IllegalArgumentException("Unexpected value: " + direction);
				};

				Vector3d trackDirection = JOMLConversion.toJOML(track.getTrackAxes(level, relativePos, relativeState).get(0));
				if(bogeyDirection.dot(trackDirection) < 0) trackDirection.mul(-1);
				Vector3d trackNormal = JOMLConversion.toJOML(track.getUpNormal(level, relativePos, relativeState));
				Vector3d trackLateral = trackDirection.cross(trackNormal, new Vector3d());

				Matrix3d bogeyMatrix = new Matrix3d(bogeyDirection, SimurailMath.DIR_YP, bogeyLateral).transpose();
				Matrix3d trackMatrix = new Matrix3d(trackDirection, trackNormal, trackLateral).mul(bogeyMatrix);

				double offset = inverted ? -0.625 : 1.625;
				Vector3d position = JOMLConversion.atBottomCenterOf(relativePos).
						add(0, track.getElevationAtCenter(level, relativePos, relativeState), 0).
						fma(offset, trackNormal);
				Quaterniond orientation = new Quaterniond().setFromUnnormalized(trackMatrix);

				return placeSubLevel(level, position, orientation, context.getPlayer(), context.getItemInHand(), state.setValue(BlockStateProperties.WATERLOGGED, false), trackType);
			}
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
		else {
			disableRotation = false;
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
