package com.crystaelix.simurail.mixin.compat.electroenergetics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;

import com.crystaelix.simurail.compat.electroenergetics.SimurailDeviceTypes;
import com.crystaelix.simurail.compat.electroenergetics.SimurailNodeConfigurations;
import com.crystaelix.simurail.compat.electroenergetics.device.AutomaticCouplerDevice;
import com.crystaelix.simurail.content.automatic_coupler.AutomaticCouplerBlock;
import com.george_vi.electroenergetics.CEEItems;
import com.george_vi.electroenergetics.config.CEEConfigs;
import com.george_vi.electroenergetics.devices.device.SimulatedDeviceType;
import com.george_vi.electroenergetics.foundation.device.ElectricalDeviceBlock;
import com.george_vi.electroenergetics.foundation.nodes.InWorldNodeConnection;
import com.george_vi.electroenergetics.simulation.infrastructure.InWorldNodeData;
import com.george_vi.electroenergetics.simulation.infrastructure.InfrastructureSavedData;
import com.george_vi.electroenergetics.simulation.infrastructure.WireData;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.LevelTickAccess;

@Mixin(AutomaticCouplerBlock.class)
public abstract class AutomaticCouplerBlockMixin extends HorizontalDirectionalBlock implements ElectricalDeviceBlock<AutomaticCouplerDevice> {

	protected AutomaticCouplerBlockMixin(Properties properties) {
		super(properties);
	}

	@Override
	public SimulatedDeviceType<AutomaticCouplerDevice> getDevice() {
		return SimurailDeviceTypes.AUTOMATIC_COUPLER.get();
	}

	@Override
	public Map<Integer, Vec3> getNodePositions(Level level, BlockPos pos, BlockState state) {
		return SimurailNodeConfigurations.AUTOMATIC_COUPLER.getNodes(state.getValue(FACING));
	}

	@Override
	public Vec3 getNodePosition(Level level, BlockPos pos, BlockState state, int id) {
		return SimurailNodeConfigurations.AUTOMATIC_COUPLER.getNodePos(state.getValue(FACING), id);
	}

	@WrapMethod(method = "onPlace")
	private void simurail$electroenergetics$onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston, Operation<Void> original) {
		original.call(state, level, pos, oldState, movedByPiston);
		LevelTickAccess<Block> blockTicks = level.getBlockTicks();
		if(!blockTicks.hasScheduledTick(pos, this)) {
			level.scheduleTick(pos, this, 1);
		}
	}

	@WrapMethod(method = "tick")
	private void simurail$electroenergetics$tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, Operation<Void> original) {
		List<Integer> nodes = new ArrayList<>(getNodePositions(level, pos, state).keySet());
		InfrastructureSavedData sd = InfrastructureSavedData.load(level);
		sd.registerOrUpdateNodes(pos, nodes);
		original.call(state, level, pos, random);
	}

	@WrapMethod(method = "playerWillDestroy")
	private BlockState simurail$electroenergetics$playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player, Operation<BlockState> original) {
		if(level instanceof ServerLevel sl && player.isCreative()) {
			InfrastructureSavedData sd = InfrastructureSavedData.load(sl);
			for(InWorldNodeData nodeData : sd.getNodesAt(pos)) {
				for(InWorldNodeConnection connection : sd.getConnections(nodeData)) {
					sd.removeConnection(connection);
				}
			}
		}
		return original.call(level, pos, state, player);
	}

	@WrapMethod(method = "onSneakWrenched")
	private InteractionResult simurail$electroenergetics$onSneakWrenched(BlockState state, UseOnContext context, Operation<InteractionResult> original) {
		BlockPos pos = context.getClickedPos();
		double hitY = context.getClickLocation().y - pos.getY();
		boolean removedCoupler = hitY > 0.3 && hitY < 0.7;
		Player player = context.getPlayer();
		if(removedCoupler && context.getLevel() instanceof ServerLevel sl && player != null) {
			InfrastructureSavedData sd = InfrastructureSavedData.load(sl);
			for(InWorldNodeData nodeData : sd.getNodesAt(context.getClickedPos())) {
				for(InWorldNodeConnection connection : sd.getConnections(nodeData)) {
					WireData wireData = sd.removeConnection(connection);
					boolean found = false;
					for(int i = 0; i < player.getInventory().items.size(); i++) {
						ItemStack stack = player.getInventory().items.get(i);
						if(CEEItems.EMPTY_SPOOL.isIn(stack)) {
							stack.shrink(1);
							player.getInventory().placeItemBackInInventory(wireData.wireType().getSpooledItem().getDefaultInstance());
							found = true;
							break;
						}
					}
					if(!found) {
						player.getInventory().placeItemBackInInventory(new ItemStack(wireData.wireType().getDrops(), CEEConfigs.server().wiresPerSpool.get()));
					}
				}
			}
		}
		return original.call(state, context);
	}
}
