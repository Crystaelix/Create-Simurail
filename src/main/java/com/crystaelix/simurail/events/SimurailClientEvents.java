package com.crystaelix.simurail.events;

import com.crystaelix.simurail.api.bogey.BogeyType;
import com.crystaelix.simurail.content.bogey.PhysicsBogeyBlockItem;
import com.crystaelix.simurail.content.bogey.PhysicsBogeyCurvePlacementPacket;
import com.crystaelix.simurail.content.probe_reader.ProbeReaderOutline;
import com.crystaelix.simurail.content.remote_controller.RemoteControllerOutline;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackBlockOutline;
import com.simibubi.create.content.trains.track.TrackBlockOutline.BezierPointSelection;

import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;

@EventBusSubscriber(Dist.CLIENT)
public class SimurailClientEvents {

	protected static boolean isGameActive() {
		return !(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null);
	}

	@SubscribeEvent
	public static void onClientTickPre(ClientTickEvent.Pre event) {
		onClientTick(true);
	}

	@SubscribeEvent
	public static void onClientTickPost(ClientTickEvent.Post event) {
		onClientTick(false);
	}

	public static void onClientTick(boolean pre) {
		if(!isGameActive()) {
			return;
		}
		if(pre) {
			return;
		}
		ProbeReaderOutline.clientTick();
		RemoteControllerOutline.clientTick();
	}

	@SubscribeEvent
	public static void onInteractionKey(InputEvent.InteractionKeyMappingTriggered event) {
		if(Minecraft.getInstance().screen != null) {
			return;
		}
		{
			BezierPointSelection result = TrackBlockOutline.result;
			if(result != null && event.isUseItem()) {
				Minecraft mc = Minecraft.getInstance();
				ClientLevel level = mc.level;
				LocalPlayer player = mc.player;
				ItemStack stack = player.getMainHandItem();
				if(player.isSecondaryUseActive() && stack.getItem() instanceof PhysicsBogeyBlockItem bogeyItem) {
					BlockState state = bogeyItem.defaultBlockState();
					BezierConnection connection = result.blockEntity().getConnections().get(result.loc().curveTarget());
					if(state != null && connection != null && BogeyType.hasDefault(connection.getMaterial().trackType, state.getValue(BlockStateProperties.INVERTED))) {
						VeilPacketManager.server().sendPacket(new PhysicsBogeyCurvePlacementPacket(result.blockEntity().getBlockPos(), result.loc()));

						state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST);
						player.swing(InteractionHand.MAIN_HAND);
						SoundType soundType = state.getSoundType();
						level.playSound(player, result.vec().x, result.vec().y, result.vec().z, soundType.getPlaceSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1) / 2, soundType.getPitch() * 0.8F);
						stack.consume(1, player);

						event.setCanceled(true);
						return;
					}
				}
			}
		}
	}
}
