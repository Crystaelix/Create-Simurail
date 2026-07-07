package com.crystaelix.simurail.content.gangway_frame;

import java.util.function.Function;

import org.joml.Vector3d;
import org.joml.Vector3dc;

import com.crystaelix.simurail.Simurail;
import com.crystaelix.simurail.api.math.CubicBezier3d;
import com.crystaelix.simurail.api.math.Quad3d;
import com.crystaelix.simurail.api.math.SimurailMath;
import com.crystaelix.simurail.api.util.RenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

public class GangwayFrameRenderer extends SmartBlockEntityRenderer<GangwayFrameBlockEntity> {

	public static final ResourceLocation BELLOWS = Simurail.id("block/gangway_frame/bellows");
	public static final ResourceLocation BELLOWS_SIDE = Simurail.id("block/gangway_frame/bellows_side");
	public static final int SEGMENTS = 16;

	public GangwayFrameRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected void renderSafe(GangwayFrameBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
		super.renderSafe(be, partialTick, poseStack, bufferSource, light, overlay);

		GangwayFrame partner = be.getGangwayPartner();
		if(!be.hasPartner && be.timer <= 0 && be.restLength <= 0) {
			return;
		}

		BlockPos pos = be.getBlockPos();
		Direction facing = be.getFacing();
		GangwayFrameBlockShape shape = be.getGangwayShape();
		Vector3dc selfDir = be.getDirection();

		shape.quad(facing, selfQuad);
		shape.center(facing, restPose.position());
		restPose.position().fma(be.restLength * 2, selfDir);
		restPose.orientation().set(be.getOrientation());

		if(partner == null) {
			endPose.set(be.endPose);
		}
		else {
			BlockPos partnerPos = partner.getBlockPos();
			ClientSubLevel selfSubLevel = Sable.HELPER.getContainingClient(pos);
			ClientSubLevel partnerSubLevel = Sable.HELPER.getContainingClient(partnerPos);
			Pose3dc selfPose = selfSubLevel == null ? SimurailMath.POSE_I : selfSubLevel.renderPose(partialTick);
			Pose3dc partnerPose = partnerSubLevel == null ? SimurailMath.POSE_I : partnerSubLevel.renderPose(partialTick);

			partner.getGangwayCenter(endPose.position());
			partnerPose.transformPosition(endPose.position());
			selfPose.transformPositionInverse(endPose.position());
			endPose.position().sub(pos.getX(), pos.getY(), pos.getZ());

			selfPose.orientation().conjugate(endPose.orientation());
			endPose.orientation().mul(partnerPose.orientation());
			endPose.orientation().mul(partner.getOrientation()).rotateY(Math.PI);
		}

		float endF;
		if(be.hasPartner) {
			endF = be.timer > 0 ? (10 - be.timer + partialTick) * 0.1F : 1;
		}
		else {
			endF = be.timer > 0 ? (be.timer - partialTick) * 0.1F : 0;
		}

		restPose.lerp(endPose, endF, interpPose);

		shape.quad(Direction.EAST, partnerQuad);
		partnerQuad.sub(shape.center(Direction.EAST, dir0));
		partnerQuad.transformPosition(interpPose);
		interpPose.transformNormal(SimurailMath.DIR_XN, partnerDir);

		curve0.setHermite(selfQuad.v0, partnerQuad.v0, selfDir, partnerDir);
		curve1.setHermite(selfQuad.v1, partnerQuad.v1, selfDir, partnerDir);
		curve2.setHermite(selfQuad.v2, partnerQuad.v2, selfDir, partnerDir);
		curve3.setHermite(selfQuad.v3, partnerQuad.v3, selfDir, partnerDir);
		curve0.lengthLUT(0, 0.5, lut0);
		curve1.lengthLUT(0, 0.5, lut1);
		curve2.lengthLUT(0, 0.5, lut2);
		curve3.lengthLUT(0, 0.5, lut3);

		VertexConsumer vb = bufferSource.getBuffer(RenderType.solid());

		poseStack.pushPose();
		renderBellows(poseStack, vb, 0xFF000000 | be.color, light, overlay);
		poseStack.popPose();
	}

	private void renderBellows(PoseStack poseStack, VertexConsumer vb, int color, int light, int overlay) {
		Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
		TextureAtlasSprite bellowsSprite = atlas.apply(BELLOWS);
		TextureAtlasSprite bellowsSideSprite = atlas.apply(BELLOWS_SIDE);

		float texSize = 1F / SEGMENTS;

		for(float i = 0; i < SEGMENTS; ++i) {
			float f0 = i / SEGMENTS;
			float f1 = (i + 1) / SEGMENTS;
			float texOffset = i / SEGMENTS;

			double t00 = SimurailMath.f2t(f0, lut0) * 0.5;
			double t01 = SimurailMath.f2t(f1, lut0) * 0.5;
			double t10 = SimurailMath.f2t(f0, lut1) * 0.5;
			double t11 = SimurailMath.f2t(f1, lut1) * 0.5;
			double t20 = SimurailMath.f2t(f0, lut2) * 0.5;
			double t21 = SimurailMath.f2t(f1, lut2) * 0.5;
			double t30 = SimurailMath.f2t(f0, lut3) * 0.5;
			double t31 = SimurailMath.f2t(f1, lut3) * 0.5;

			curve1.position(t10, vt0);
			curve1.position(t11, vt1);
			curve0.position(t01, vt2);
			curve0.position(t00, vt3);
			vt1.sub(vt0, dir0);
			vt3.sub(vt0, dir1);
			dir0.cross(dir1, normal).normalize();
			RenderUtil.renderSpriteQuad(poseStack, vb, bellowsSprite, vt0, vt1, vt2, vt3, 0, texOffset, 1, texSize, color, light, overlay, normal);

			curve2.position(t21, vt0);
			curve2.position(t20, vt1);
			curve3.position(t30, vt2);
			curve3.position(t31, vt3);
			vt1.sub(vt0, dir0);
			vt3.sub(vt0, dir1);
			dir0.cross(dir1, normal).normalize();
			RenderUtil.renderSpriteQuad(poseStack, vb, bellowsSprite, vt0, vt1, vt2, vt3, 0, texOffset, 1, texSize, color, light, overlay, normal);

			curve1.position(t10, vt0);
			curve2.position(t20, vt1);
			curve2.position(t21, vt2);
			curve1.position(t11, vt3);
			vt1.sub(vt0, dir0);
			vt3.sub(vt0, dir1);
			dir0.cross(dir1, normal).normalize();
			RenderUtil.renderSpriteQuad(poseStack, vb, bellowsSideSprite, vt0, vt1, vt2, vt3, texOffset, 0, texSize, 0.125F, color, light, overlay, normal);

			curve3.position(t30, vt0);
			curve0.position(t00, vt1);
			curve0.position(t01, vt2);
			curve3.position(t31, vt3);
			vt1.sub(vt0, dir0);
			vt3.sub(vt0, dir1);
			dir0.cross(dir1, normal).normalize();
			RenderUtil.renderSpriteQuad(poseStack, vb, bellowsSideSprite, vt0, vt1, vt2, vt3, texOffset, 0, texSize, 0.125F, color, light, overlay, normal);
		}

		curve1.position(0.5, vt0);
		curve2.position(0.5, vt1);
		curve3.position(0.5, vt2);
		curve0.position(0.5, vt3);
		vt1.sub(vt0, dir0);
		vt3.sub(vt0, dir1);
		dir0.cross(dir1, normal).normalize();
		RenderUtil.renderSpriteQuad(poseStack, vb, bellowsSideSprite, vt0, vt1, vt2, vt3, 0, 0.125F, 1, 0.125F, color, light, overlay, normal);
	}

	private final Pose3d endPose = new Pose3d();
	private final Pose3d restPose = new Pose3d();
	private final Pose3d interpPose = new Pose3d();
	private final Quad3d selfQuad = new Quad3d();
	private final Quad3d partnerQuad = new Quad3d();
	private final Vector3d partnerDir = new Vector3d();
	private final CubicBezier3d curve0 = new CubicBezier3d();
	private final CubicBezier3d curve1 = new CubicBezier3d();
	private final CubicBezier3d curve2 = new CubicBezier3d();
	private final CubicBezier3d curve3 = new CubicBezier3d();
	private final double[] lut0 = new double[9];
	private final double[] lut1 = new double[9];
	private final double[] lut2 = new double[9];
	private final double[] lut3 = new double[9];
	private final Vector3d vt0 = new Vector3d();
	private final Vector3d vt1 = new Vector3d();
	private final Vector3d vt2 = new Vector3d();
	private final Vector3d vt3 = new Vector3d();
	private final Vector3d dir0 = new Vector3d();
	private final Vector3d dir1 = new Vector3d();
	private final Vector3d normal = new Vector3d();
}
