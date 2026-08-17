package com.crystaelix.simurail.content.probe_reader;

import com.crystaelix.simurail.api.bogey.BogeyLinkData;
import com.crystaelix.simurail.content.SimurailDataComponents;
import com.crystaelix.simurail.content.bogey.PhysicsBogeyBlockEntity;
import com.simibubi.create.AllSoundEvents;

import dev.ryanhcode.sable.Sable;
import dev.simulated_team.simulated.util.SimColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ProbeReaderBlockItem extends BlockItem {

	public ProbeReaderBlockItem(ProbeReaderBlock block, Properties properties) {
		super(block, properties);
	}

	public double getMaxDistance() {
		return 64;
	}

	public static Direction getDirection(PhysicsBogeyBlockEntity bogey, Vec3 clickLocation) {
		Vec3 offset = clickLocation.subtract(bogey.getBlockPos().getCenter());
		Direction direction = bogey.getFacing();
		return offset.x() * direction.getStepX() + offset.z * direction.getStepZ() >= 0 ? direction : direction.getOpposite();
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		BlockPos pos = context.getClickedPos();
		Player player = context.getPlayer();
		if(player == null) {
			return InteractionResult.FAIL;
		}
		ItemStack stack = context.getItemInHand();
		Level level = context.getLevel();
		if(player.isSecondaryUseActive() && stack.has(SimurailDataComponents.BOGEY_LINK_DATA)) {
			if(level.isClientSide()) {
				return InteractionResult.SUCCESS;
			}
			player.displayClientMessage(Component.translatable("block.simurail.probe_reader.clear"), true);
			stack.remove(SimurailDataComponents.BOGEY_LINK_DATA);
			return InteractionResult.SUCCESS;
		}
		ResourceLocation placedDim = level.dimension().location();
		if(!stack.has(SimurailDataComponents.BOGEY_LINK_DATA)) {
			if(level.getBlockEntity(pos) instanceof PhysicsBogeyBlockEntity bogey) {
				if(level.isClientSide()) {
					return InteractionResult.SUCCESS;
				}
				Direction direction = getDirection(bogey, context.getClickLocation());
				player.displayClientMessage(Component.translatable("block.simurail.probe_reader.set"), true);
				stack.set(SimurailDataComponents.BOGEY_LINK_DATA, new BogeyLinkData(pos, direction, placedDim));
				return InteractionResult.SUCCESS;
			}
			if(level.isClientSide()) {
				AllSoundEvents.DENY.playFrom(player);
			}
			player.displayClientMessage(Component.translatable("block.simurail.probe_reader.invalid").withColor(SimColors.NUH_UH_RED), true);
			return InteractionResult.FAIL;
		}
		BlockState state = level.getBlockState(pos);
		double maxDistance = getMaxDistance();
		BogeyLinkData data = stack.get(SimurailDataComponents.BOGEY_LINK_DATA);
		BlockPos selectedPos = data.position();
		ResourceLocation selectedDim = data.dimension();
		BlockPos placedPos = pos.relative(context.getClickedFace(), state.canBeReplaced() ? 0 : 1);
		if(!selectedDim.equals(placedDim) || Sable.HELPER.distanceSquaredWithSubLevels(level, selectedPos.getCenter(), placedPos.getCenter()) > maxDistance) {
			player.displayClientMessage(Component.translatable("block.simurail.probe_reader.too_far").withColor(SimColors.NUH_UH_RED), true);
			return InteractionResult.FAIL;
		}
		InteractionResult useOn = super.useOn(context);
		if(level.isClientSide() || useOn == InteractionResult.FAIL) {
			return useOn;
		}
		ItemStack itemInHand = player.getItemInHand(context.getHand());
		if(!itemInHand.isEmpty()) {
			stack.remove(SimurailDataComponents.BOGEY_LINK_DATA);
		}
		player.displayClientMessage(Component.translatable("block.simurail.probe_reader.success").withColor(SimColors.SUCCESS_LIME), true);
		return useOn;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);
		if(player.isSecondaryUseActive() && stack.has(SimurailDataComponents.BOGEY_LINK_DATA)) {
			if(level.isClientSide()) {
				return InteractionResultHolder.success(stack);
			}
			player.displayClientMessage(Component.translatable("block.simurail.probe_reader.clear"), true);
			stack.remove(SimurailDataComponents.BOGEY_LINK_DATA);
			return InteractionResultHolder.success(stack);
		}
		return super.use(level, player, usedHand);
	}

	@Override
	protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
		BogeyLinkData data = context.getItemInHand().get(SimurailDataComponents.BOGEY_LINK_DATA);
		if(data != null) {
			boolean result = super.placeBlock(context, state);
			Level level = context.getLevel();
			BlockPos pos = context.getClickedPos();
			if(level.getBlockEntity(pos) instanceof ProbeReaderBlockEntity reader) {
				BlockPos selectedPos = data.position();
				Direction selectedDir = data.direction();
				boolean selectedFront = true;
				if(level.getBlockEntity(selectedPos) instanceof PhysicsBogeyBlockEntity bogey) {
					selectedFront = selectedDir != bogey.getFacing().getOpposite();
				}
				reader.setTargetPos(selectedPos);
				reader.targetFront = selectedFront;
			}
			return result;
		}
		return super.placeBlock(context, state);
	}
}
