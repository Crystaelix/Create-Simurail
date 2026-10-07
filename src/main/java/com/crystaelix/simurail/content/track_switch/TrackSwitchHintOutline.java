package com.crystaelix.simurail.content.track_switch;

import java.util.Map;

import org.joml.Vector3d;

import com.crystaelix.simurail.api.math.SimurailMath;
import com.crystaelix.simurail.content.SimurailBlocks;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;

import dev.ryanhcode.sable.companion.math.JOMLConversion;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Based on {@link com.railwayteam.railways.content.switches.TrackSwitchDebugVisualizer TrackSwitchDebugVisualizer}
 */
public class TrackSwitchHintOutline {

	public static void clientTick() {
		Player player = Minecraft.getInstance().player;
		if(player == null) {
			return;
		}
		showPotentialLocationOutlines();
		showTargetOutlines();
	}

	private static void showPotentialLocationOutlines() {
		Player player = Minecraft.getInstance().player;
		if(!player.isHolding(SimurailBlocks.TRACK_SWITCH.asItem())) {
			return;
		}

		Level level = Minecraft.getInstance().level;
		int range = 64;
		int rangeSqr = range * range;

		for(TrackGraph graph : CreateClient.RAILWAYS.trackNetworks.values()) {
			for(TrackNodeLocation nodeLoc : graph.getNodes()) {
				if(!level.dimension().equals(nodeLoc.dimension) || nodeLoc.getLocation().distanceToSqr(player.position()) > rangeSqr) {
					continue;
				}

				TrackNode node = graph.locateNode(nodeLoc);
				Map<TrackNode, TrackEdge> connections = graph.getConnectionsFrom(node);
				if(connections.size() > 2) {
					for(TrackEdge edge : connections.values()) {
						int exitCount = (int)connections.values().
								stream().
								filter(e -> e != edge).
								filter(e -> !e.node2.getLocation().equals(edge.node2.getLocation())).
								filter(e -> edge.getDirection(false).dot(e.getDirection(true)) < -0.875).
								count();
						if(exitCount > 1) {
							Vec3 offset = edge.getDirection(false).normalize().scale(0.5).add(0, 0.5, 0);
							Outliner.getInstance().showAABB(edge, AABB.ofSize(
									nodeLoc.getLocation().add(offset),
									1, 1, 1)).
							colored(graph.color).lineWidth(0.0625F);
						}
					}
				}
			}
		}
	}

	private static void showTargetOutlines() {
		Minecraft mc = Minecraft.getInstance();
		if(GogglesItem.isWearingGoggles(mc.player) && mc.hitResult instanceof BlockHitResult hitResult) {
			Level level = mc.level;
			if(hitResult.getType() != HitResult.Type.MISS && level.getBlockEntity(hitResult.getBlockPos()) instanceof TrackSwitchBlockEntity be) {
				TrackSwitch sw = be.getTrackSwitch();
				if(sw == null) {
					return;
				}
				Vec3 nodeLoc = sw.edgeLocation.getSecond().getLocation();
				be.trackRot.transform(SimurailMath.DIR_YP, trackVert);
				be.trackRot.transform(SimurailMath.DIR_ZP, trackLat);
				if(be.hasStraightExit()) {
					boolean active = be.state == TrackSwitchState.STRAIGHT;
					Vec3 from = JOMLConversion.toMojang(
							JOMLConversion.toJOML(nodeLoc, fromPos).fma(0.375, trackVert));
					Vec3 to = be.straightExit.getLocation().add(0, 0.375, 0);
					Outliner.getInstance().showLine("simurail.track_switch.straight", from, to).colored(active ? 0x8CBA51 : 0xFF5D6C).lineWidth(0.0625F);
				}
				if(be.hasLeftExit()) {
					boolean active = be.state == TrackSwitchState.LEFT;
					Vec3 from = JOMLConversion.toMojang(
							JOMLConversion.toJOML(nodeLoc, fromPos).fma(0.375, trackVert).fma(-0.25, trackLat));
					Vec3 to = be.leftExit.getLocation().add(0, 0.375, 0);
					Outliner.getInstance().showLine("simurail.track_switch.left", from, to).colored(active ? 0x8CBA51 : 0xFF5D6C).lineWidth(0.0625F);
				}
				if(be.hasRightExit()) {
					boolean active = be.state == TrackSwitchState.RIGHT;
					Vec3 from = JOMLConversion.toMojang(
							JOMLConversion.toJOML(nodeLoc, fromPos).fma(0.375, trackVert).fma(0.25, trackLat));
					Vec3 to = be.rightExit.getLocation().add(0, 0.375, 0);
					Outliner.getInstance().showLine("simurail.track_switch.right", from, to).colored(active ? 0x8CBA51 : 0xFF5D6C).lineWidth(0.0625F);
				}
			}
		}
	}

	private static Vector3d trackVert = new Vector3d();
	private static Vector3d trackLat = new Vector3d();
	private static Vector3d fromPos = new Vector3d();
}
