package com.crystaelix.simurail.content;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.crystaelix.simurail.api.signal.SignalNameExtractor;
import com.crystaelix.simurail.api.signal.SignalNameRegistry;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.equipment.clipboard.ClipboardBlock;
import com.simibubi.create.content.equipment.clipboard.ClipboardBlockEntity;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class SimurailSignalNames {

	public static final SignalNameExtractor
	SIGN = (state, level, pos, direction) -> {
		Optional<? extends SignBlockEntity> optional = Optional.empty();
		Block block = state.getBlock();
		switch(direction) {
		case UP -> {
			if(block instanceof StandingSignBlock) {
				optional = level.getBlockEntity(pos, BlockEntityType.SIGN);
			}
		}
		case DOWN -> {
			if(block instanceof CeilingHangingSignBlock || block instanceof WallHangingSignBlock) {
				optional = level.getBlockEntity(pos, BlockEntityType.HANGING_SIGN);
			}
		}
		default -> {
			if(block instanceof WallSignBlock && direction == state.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
				optional = level.getBlockEntity(pos, BlockEntityType.SIGN);
			}
			else if(block instanceof WallHangingSignBlock && direction.getAxis() != state.getValue(BlockStateProperties.HORIZONTAL_FACING).getAxis()) {
				optional = level.getBlockEntity(pos, BlockEntityType.HANGING_SIGN);
			}
		}
		}
		return optional.stream().
				flatMap(sign -> Stream.of(sign.getFrontText(), sign.getBackText())).
				flatMap(text -> Arrays.stream(text.getMessages(false))).
				map(Component::getString);
	},
	CLIPBOARD = (state, level, pos, direction) -> {
		Optional<ClipboardBlockEntity> optional = Optional.empty();
		if(state.getBlock() instanceof ClipboardBlock) {
			switch(direction) {
			case UP -> {
				if(AttachFace.FLOOR == state.getValue(BlockStateProperties.ATTACH_FACE)) {
					optional = level.getBlockEntity(pos, AllBlockEntityTypes.CLIPBOARD.get());
				}
			}
			case DOWN -> {
				if(AttachFace.CEILING == state.getValue(BlockStateProperties.ATTACH_FACE)) {
					optional = level.getBlockEntity(pos, AllBlockEntityTypes.CLIPBOARD.get());
				}
			}
			default -> {
				if(direction == state.getValue(BlockStateProperties.HORIZONTAL_FACING) &&
						AttachFace.WALL == state.getValue(BlockStateProperties.ATTACH_FACE)) {
					optional = level.getBlockEntity(pos, AllBlockEntityTypes.CLIPBOARD.get());
				}
			}
			}
		}
		return optional.stream().
				flatMap(board -> Stream.ofNullable(board.components().get(AllDataComponents.CLIPBOARD_CONTENT))).
				flatMap(content -> content.pages().stream()).
				flatMap(List::stream).
				map(entry -> entry.text.getString());
	};

	public static void register() {
		SignalNameRegistry.register(SIGN);
		SignalNameRegistry.register(CLIPBOARD);
	}
}
