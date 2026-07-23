package com.crystaelix.simurail.content.connector;

import org.lwjgl.glfw.GLFW;

import com.crystaelix.simurail.api.math.SimurailMath;
import com.crystaelix.simurail.content.SimurailItems;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.util.SimColors;
import dev.simulated_team.simulated.util.click_interactions.InteractCallback;
import foundry.veil.api.network.VeilPacketManager;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ConnectorInteractCallback implements InteractCallback {

	public static final double MAX_LENGTH_SAME = 32;
	public static final double MAX_LENGTH_DIFF = 12;

	public BlockPos startPos;
	public Direction startDir;

	public boolean tryStart(UseOnContext context) {
		if(startPos != null) {
			return false;
		}
		LocalPlayer player = Minecraft.getInstance().player;
		Level level = player.level();
		BlockPos pos = context.getClickedPos();
		BlockEntity be = level.getBlockEntity(pos);
		if(!(be instanceof ConnectorConnectable connectable)) {
			return false;
		}
		startPos = pos;
		startDir = getDirection(connectable, context.getClickLocation());
		return true;
	}

	@Override
	public Result onUse(int modifiers, int action, KeyMapping rightKey) {
		LocalPlayer player = Minecraft.getInstance().player;
		Level level = player.level();

		if(action == GLFW.GLFW_PRESS) {
			InteractionHand hand = getHand(player);

			if(hand == null) {
				reset(true);
				return Result.empty();
			}

			if(startPos != null) {
				if(player.isShiftKeyDown()) {
					player.swing(hand);
					reset(true);
					return new Result(true);
				}
				if(level.getBlockEntity(startPos) instanceof ConnectorConnectable start) {
					HitResult clientHit = Minecraft.getInstance().hitResult;
					if(clientHit instanceof BlockHitResult hit && hit.getType() != HitResult.Type.MISS && startPos != null) {
						BlockPos endPos = hit.getBlockPos();
						if(level.getBlockEntity(endPos) instanceof ConnectorConnectable end) {
							Direction endDir = getDirection(end, hit.getLocation());
							if(start == end && startDir != endDir) {
								sendMessage("item.simurail.connector.same_block", SimColors.NUH_UH_RED);
								return new Result(true);
							}
							if(start != end) {
								if(!start.canConnectTo(startDir, end, endDir)) {
									sendMessage("item.simurail.connector.cannot_connect", SimColors.NUH_UH_RED);
									return new Result(true);
								}
								if(testRange(level, start, end)) {
									sendMessage("item.simurail.connector.out_of_range", SimColors.NUH_UH_RED);
									return new Result(true);
								}
							}

							if(start == end) {
								sendMessage("item.simurail.connector.disconnected", SimColors.SUCCESS_LIME);
							}
							else {
								sendMessage("item.simurail.connector.connected", SimColors.SUCCESS_LIME);
							}

							boolean startFront = start.getFacing() == startDir;
							boolean endFront = end.getFacing() == endDir;

							player.swing(hand);
							VeilPacketManager.server().sendPacket(new ConnectorConnectPacket(startPos, startFront, endPos, endFront));
							reset(false);
							return new Result(true);
						}
					}
				}
			}
		}

		return Result.empty();
	}

	private boolean testRange(Level level, ConnectorConnectable start, ConnectorConnectable end) {
		double range = start.connectionRange(end);
		BlockPos pos1 = start.getBlockPos();
		BlockPos pos2 = end.getBlockPos();
		SubLevel subLevel1 = Sable.HELPER.getContaining(level, pos1);
		SubLevel subLevel2 = Sable.HELPER.getContaining(level, pos2);
		if(subLevel1 == subLevel2) {
			return pos1.distSqr(pos2) > Mth.square(range);
		}
		else {
			Pose3dc pose1 = subLevel1 == null ? SimurailMath.POSE_I : subLevel1.logicalPose();
			Pose3dc pose2 = subLevel2 == null ? SimurailMath.POSE_I : subLevel2.logicalPose();
			Vec3 globalPos1 = pose1.transformPosition(pos1.getCenter());
			Vec3 globalPos2 = pose2.transformPosition(pos2.getCenter());
			return globalPos1.distanceToSqr(globalPos2) > Mth.square(range);
		}
	}

	public InteractionHand getHand(LocalPlayer player) {
		InteractionHand hand = null;
		if(player.getMainHandItem().is(SimurailItems.CONNECTOR)) {
			hand = InteractionHand.MAIN_HAND;
		}
		else if(player.getOffhandItem().is(SimurailItems.CONNECTOR)) {
			hand = InteractionHand.OFF_HAND;
		}
		return hand;
	}

	public Direction getDirection(ConnectorConnectable connectable, Vec3 clickLocation) {
		Vec3 offset = clickLocation.subtract(connectable.getBlockPos().getCenter());
		Direction direction = connectable.getFacing();
		return offset.x() * direction.getStepX() + offset.z * direction.getStepZ() >= 0 ? direction : direction.getOpposite();
	}

	public void reset(boolean sayMessage) {
		if(sayMessage && startPos != null) {
			sendMessage("item.simurail.connector.terminated", SimColors.NUH_UH_RED);
		}
		startPos = null;
		startDir = null;
	}

	public void sendMessage(String message, int color) {
		Component component = Component.translatable(message).withColor(color);
		Minecraft.getInstance().player.displayClientMessage(component, true);
	}

	@Override
	public void clientTick(Level level, LocalPlayer player) {
		if(!player.getMainHandItem().is(SimurailItems.CONNECTOR) && !player.getOffhandItem().is(SimurailItems.CONNECTOR)) {
			reset(true);
			return;
		}

		if(startPos != null) {
			if(level.getBlockEntity(startPos) instanceof ConnectorConnectable start) {
				AABB aabb = start.getOutline(startDir);
				Outliner.getInstance().showAABB("simurail.connector", aabb).colored(SimColors.SUCCESS_LIME).lineWidth(0.0625F);
			}
			else {
				reset(true);
			}
		}

		HitResult clientHit = Minecraft.getInstance().hitResult;
		if(clientHit != null && clientHit.getType() != HitResult.Type.MISS && clientHit instanceof BlockHitResult hit) {
			BlockPos pos = hit.getBlockPos();
			BlockEntity be = level.getBlockEntity(pos);
			if(be instanceof ConnectorConnectable end) {
				Direction dir = getDirection(end, hit.getLocation());
				int color = SimColors.SUCCESS_LIME;
				if(startPos == null) {
					color = SimColors.ACTIVE_YELLOW;
				}
				if(startPos != null && (pos.equals(startPos) && dir != startDir ||
						level.getBlockEntity(startPos) instanceof ConnectorConnectable start && start != end &&
						(testRange(level, start, end) || !start.canConnectTo(startDir, end, dir)))) {
					color = SimColors.NUH_UH_RED;
				}

				AABB aabb = end.getOutline(dir);
				Outliner.getInstance().showAABB("simurail.connector.selection", aabb).colored(color).lineWidth(0.0625F);

				if(startPos != null) {
					Vec3 globalStart = Sable.HELPER.projectOutOfSubLevel(level, startPos.getCenter().add(startDir.getStepX() * 0.5, 0, startDir.getStepZ() * 0.5));
					Vec3 globalEnd = Sable.HELPER.projectOutOfSubLevel(level, pos.getCenter().add(dir.getStepX() * 0.5, 0, dir.getStepZ() * 0.5));

					DustParticleOptions data = new DustParticleOptions(new Color(color).asVectorF(), 1);
					for(int i = 0; i < 4; i++) {
						Vec3 vec = globalStart.lerp(globalEnd, level.getRandom().nextFloat());
						level.addParticle(data, vec.x, vec.y, vec.z, 0, 0, 0);
					}
				}
			}
		}
	}
}
