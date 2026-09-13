package com.crystaelix.simurail.content.copycat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.crystaelix.simurail.content.copycat.SimurailCopycatModel.CropTransform;
import com.simibubi.create.content.decoration.copycat.CopycatModel;
import com.simibubi.create.foundation.model.BakedModelHelper;
import com.simibubi.create.foundation.model.BakedQuadHelper;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;

public abstract class SimurailCopycatModel extends CopycatModel {

	public static final ChunkRenderTypeSet ALL = ChunkRenderTypeSet.of(RenderType.solid(), RenderType.cutout(), RenderType.cutoutMipped(), RenderType.translucent());

	public SimurailCopycatModel(BakedModel originalModel) {
		super(originalModel);
	}

	@Override
	public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
		ChunkRenderTypeSet renderTypes = super.getRenderTypes(state, rand, data);
		BlockState material = getMaterial(data);
		if(material != null) {
			BakedModel model = getModelOf(material);
			renderTypes = ChunkRenderTypeSet.union(renderTypes, model.getRenderTypes(material, rand, data));
		}
		return renderTypes;
	}

	@Override
	protected List<BakedQuad> getCroppedQuads(BlockState state, Direction side, RandomSource rand, BlockState material, ModelData wrappedData, RenderType renderType) {
		BakedModel model = getModelOf(material);
		List<BakedQuad> templateQuads = model.getQuads(material, side, rand, wrappedData, renderType);
		List<BakedQuad> quads = new ArrayList<>();
		for(CropTransform component : getComponents(state)) {
			for(BakedQuad quad : templateQuads) {
				if(!component.removedDirections().contains(quad.getDirection())) {
					quads.add(component.cloneWithCustomGeometry(quad));
				}
			}
		}
		if(side == null) {
			List<CropTransform> unculled = getUnculledComponents(state);
			for(Direction dir : Direction.values()) {
				List<BakedQuad> sidedTemplateQuads = model.getQuads(material, dir, rand, wrappedData, renderType);
				for(CropTransform component : unculled) {
					for(BakedQuad quad : sidedTemplateQuads) {
						if(!component.removedDirections().contains(quad.getDirection())) {
							quads.add(component.cloneWithCustomGeometry(quad));
						}
					}
				}
			}
		}
		return quads;
	}

	protected abstract List<CropTransform> getComponents(BlockState state);
	
	protected List<CropTransform> getUnculledComponents(BlockState state) {
		return List.of();
	}

	public record CropTransform(AABB crop, Vec3 move, Set<Direction> removedDirections) {
		public BakedQuad cloneWithCustomGeometry(BakedQuad quad) {
			return BakedQuadHelper.cloneWithCustomGeometry(quad, BakedModelHelper.cropAndMove(quad.getVertices(), quad.getSprite(), crop, move));
		}
	}

	public static CropTransform cropForHorizontal(
			Direction facing,
			double x1, double y1, double z1,
			double x2, double y2, double z2,
			double mx, double my, double mz,
			Set<Direction> removedDirections) {
		x1 *= 0.0625; y1 *= 0.0625; z1 *= 0.0625;
		x2 *= 0.0625; y2 *= 0.0625; z2 *= 0.0625;
		mx *= 0.0625; my *= 0.0625; mz *= 0.0625;
		AABB crop = switch(facing) {
		case null, default -> new AABB(x1, y1, z1, x2, y2, z2);
		case EAST -> new AABB(z1, y1, 1 - x1, z2, y2, 1 - x2);
		case NORTH -> new AABB(1 - x1, y1, 1 - z1, 1 - x2, y2, 1 - z2);
		case WEST -> new AABB(1 - z1, y1, x1, 1 - z2, y2, x2);
		};
		Vec3 move = switch(facing) {
		case null, default -> new Vec3(mx, my, mz);
		case EAST -> new Vec3(mz, my, -mx);
		case NORTH -> new Vec3(-mx, my, -mz);
		case WEST -> new Vec3(-mz, my, mx);
		};
		return new CropTransform(crop, move, removedDirections);
	}
}
