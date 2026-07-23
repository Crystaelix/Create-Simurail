package com.crystaelix.simurail.content.connector;

import com.crystaelix.simurail.content.SimurailInteractCallbacks;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class ConnectorItem extends Item {

	public ConnectorItem(Properties properties) {
		super(properties);
	}

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if(context.getPlayer().isLocalPlayer() && SimurailInteractCallbacks.CONNECTOR.tryStart(context)) {
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }
}
