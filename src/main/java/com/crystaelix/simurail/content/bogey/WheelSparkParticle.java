package com.crystaelix.simurail.content.bogey;

import org.joml.Vector3f;

import com.crystaelix.simurail.config.SimurailConfig;
import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.ryanhcode.sable.api.particle.ParticleSubLevelKickable;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;


public class WheelSparkParticle extends TextureSheetParticle implements ParticleSubLevelKickable {

	public static final double MIN_STREAK_SPEED = 0.01;
	public static final float STREAK_STRETCH = 1.2F;
	public static final float MAX_STREAK_LENGTH = 1F;

	protected final float heat;

	protected WheelSparkParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SpriteSet sprites) {
		super(level, x, y, z);
		this.xd = xd;
		this.yd = yd;
		this.zd = zd;
		heat = 0.6F + random.nextFloat() * 0.4F;
		gravity = 0.6F;
		friction = 0.92F;
		lifetime = 5 + random.nextInt(9);
		quadSize = (0.08F + random.nextFloat() * 0.08F) * heat * SimurailConfig.client().wheelSlipSparkScale.get().floatValue();
		pickSprite(sprites);
		setColor(1, 0.95F, 0.75F);
	}

	@Override
	public void tick() {
		super.tick();
		float age = (float)this.age / lifetime;
		setColor(
			1,
			Mth.lerp(age, 0.95F, 0.3F),
			Mth.lerp(age * age, 0.75F, 0.05F)
		);
		alpha = 1 - age * age;
	}

	@Override
	public void render(VertexConsumer buffer, Camera camera, float partialTick) {
		double speedSq = xd * xd + yd * yd + zd * zd;
		if(speedSq < MIN_STREAK_SPEED * MIN_STREAK_SPEED) {
			super.render(buffer, camera, partialTick);
			return;
		}

		Vec3 cameraPos = camera.getPosition();
		float px = (float)(Mth.lerp(partialTick, xo, x) - cameraPos.x());
		float py = (float)(Mth.lerp(partialTick, yo, y) - cameraPos.y());
		float pz = (float)(Mth.lerp(partialTick, zo, z) - cameraPos.z());

		double speed = Math.sqrt(speedSq);
		Vector3f head = new Vector3f((float)(xd / speed), (float)(yd / speed), (float)(zd / speed));
		Vector3f side = new Vector3f(head).cross(px, py, pz);
		if(side.lengthSquared() < 1E-8F) {
			// flying straight at/away from the camera => no direction left to stretch along
			super.render(buffer, camera, partialTick);
			return;
		}

		float halfWidth = getQuadSize(partialTick);
		side.normalize(halfWidth);
		head.mul(Math.min(halfWidth * 2 + (float)speed * STREAK_STRETCH, MAX_STREAK_LENGTH));

		// left edge of the texture is the origin of the particle
		int light = getLightColor(partialTick);
		renderVertex(buffer, px + head.x - side.x, py + head.y - side.y, pz + head.z - side.z, getU1(), getV1(), light);
		renderVertex(buffer, px + head.x + side.x, py + head.y + side.y, pz + head.z + side.z, getU1(), getV0(), light);
		renderVertex(buffer, px + side.x, py + side.y, pz + side.z, getU0(), getV0(), light);
		renderVertex(buffer, px - side.x, py - side.y, pz - side.z, getU0(), getV1(), light);
	}

	protected void renderVertex(VertexConsumer buffer, float x, float y, float z, float u, float v, int light) {
		buffer.addVertex(x, y, z).setUv(u, v).setColor(rCol, gCol, bCol, alpha).setLight(light);
	}

	@Override
	protected int getLightColor(float partialTick) {
		return 0xF000F0;
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public boolean sable$shouldKickFromTracking() {
		return true;
	}

	@Override
	public boolean sable$shouldCollideWithTrackingSubLevel() {
		return false;
	}

	public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {

		@Override
		public Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
			return new WheelSparkParticle(level, x, y, z, xd, yd, zd, sprites);
		}
	}
}
