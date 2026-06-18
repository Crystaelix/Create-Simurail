package com.crystaelix.simurail.api.util;

import java.util.UUID;

import dev.ryanhcode.sable.api.schematic.SubLevelSchematicSerializationContext;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.BlockPos;

public class SchematicContextUtil {

	public static Pair<BlockPos, UUID> writeTransform(BlockPos position, UUID subLevelID) {
		SubLevelSchematicSerializationContext schematicContext = SubLevelSchematicSerializationContext.getCurrentContext();
		if(schematicContext != null) {
			if(position != null) {
				if(schematicContext.getType() == SubLevelSchematicSerializationContext.Type.PLACE) {
					if(subLevelID == null) {
						position = schematicContext.getSetupTransform().apply(position);
					}
				}
				else if(subLevelID != null) {
					SubLevelSchematicSerializationContext.SchematicMapping mapping = schematicContext.getMapping(subLevelID);
					if(mapping != null) {
						position = mapping.transform().apply(position);
						subLevelID = mapping.newUUID();
					}
					else {
						position = null;
						subLevelID = null;
					}
				}
				else {
					if(schematicContext.getBoundingBox().contains(position.getX(), position.getY(), position.getZ())) {
						position = schematicContext.getPlaceTransform().apply(position);
					}
					else {
						position = null;
					}
				}
			}
		}
		return Pair.of(position, subLevelID);
	}

	public static Pair<BlockPos, UUID> readTransform(BlockPos position, UUID subLevelID) {
		SubLevelSchematicSerializationContext schematicContext = SubLevelSchematicSerializationContext.getCurrentContext();
		if(schematicContext != null && schematicContext.getType() == SubLevelSchematicSerializationContext.Type.PLACE) {
			if(position != null) {
				if(subLevelID != null) {
					SubLevelSchematicSerializationContext.SchematicMapping mapping = schematicContext.getMapping(subLevelID);
					if(mapping != null) {
						position = mapping.transform().apply(position);
						subLevelID = mapping.newUUID();
					}
					else {
						position = null;
						subLevelID = null;
					}
				}
				else {
					position = schematicContext.getPlaceTransform().apply(position);
				}
			}
		}
		return Pair.of(position, subLevelID);
	}
}
