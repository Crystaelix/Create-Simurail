package com.crystaelix.simurail.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.crystaelix.simurail.api.extension.BezierConnectionExtension;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(TrackBlockEntity.class)
public abstract class TrackBlockEntityMixin extends SmartBlockEntity {

	public TrackBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Shadow
	Map<BlockPos, BezierConnection> connections;

	@Inject(method = "addConnection", at = @At("RETURN"))
	public void simurail$onAddConnection(BezierConnection connection, CallbackInfo ci) {
		if(level == null) {
			return;
		}
		BezierConnection stored = connections.getOrDefault(connection.getKey(), connection);
		((BezierConnectionExtension)stored).simurail$invalidateCurve();
	}
}
