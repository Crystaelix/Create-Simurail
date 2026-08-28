package com.crystaelix.simurail.content.bogey;


import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.crystaelix.simurail.api.bogey.BogeyRenderedType;
import com.crystaelix.simurail.api.math.Basis3f;
import com.crystaelix.simurail.config.SimurailClientConfig;
import com.crystaelix.simurail.config.SimurailConfig;
import com.crystaelix.simurail.content.SimurailParticles;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.animation.LerpedFloat.Chaser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class PhysicsBogeyEffects {

	public static final double SLIP_DEADZONE = 0.4;

	public static final double SLIP_SATURATION = 5;
	public static final double SPARKS_PER_TICK = 6;
	public static final double SPARK_RANGE = 48;

	public static final float RAIL_CLEARANCE = 1 / 32F;
	public static final float CONTACT_JITTER_ALONG = 0.2F;
	public static final float CONTACT_JITTER_ACROSS = 0.1F;

	protected final PhysicsBogeyBlockEntity bogey;

	protected final LerpedFloat slip = LerpedFloat.linear();
	protected double sparkBudget;

	protected PhysicsBogeyEffects(PhysicsBogeyBlockEntity bogey) {
		this.bogey = bogey;
	}

	public void tick() {
		slip.chase(bogey.slipSpeed, 0.4, Chaser.exp(4));
		slip.tickChaser();

		SimurailClientConfig config = SimurailConfig.client();
		float slipSpeed = slip.getValue();
		if(Math.abs(slipSpeed) <= SLIP_DEADZONE || !config.wheelSlipSparkEnabled.get()) {
			// the wheels have their grip back, so the next slip starts sparking from nothing
			sparkBudget = 0;
			return;
		}

		Minecraft mc = Minecraft.getInstance();
		ParticleStatus particleStatus = mc.options.particles().get();
		if(particleStatus == ParticleStatus.MINIMAL || mc.player == null || !inSparkRange(mc.player)) {
			return;
		}

		int sparks = takeSparks(config, particleStatus, slipSpeed);
		if(sparks > 0) {
			spawnSparks(slipSpeed, sparks);
		}
	}

	protected boolean inSparkRange(LocalPlayer player) {
		double distanceSq = Sable.HELPER.distanceSquaredWithSubLevels(bogey.getLevel(), bogey.localCenter, JOMLConversion.toJOML(player.getEyePosition()));
		return distanceSq <= SPARK_RANGE * SPARK_RANGE;
	}

	protected int takeSparks(SimurailClientConfig config, ParticleStatus particleStatus, float slipSpeed) {
		double density = config.wheelSlipSparkDensity.get();
		if(particleStatus == ParticleStatus.DECREASED) {
			density *= 0.5;
		}
		double intensity = Mth.clamp((Math.abs(slipSpeed) - SLIP_DEADZONE) / (SLIP_SATURATION - SLIP_DEADZONE), 0, 1);
		sparkBudget = Math.min(sparkBudget + intensity * SPARKS_PER_TICK * density, SPARKS_PER_TICK);
		int sparks = (int)sparkBudget;
		sparkBudget -= sparks;
		return sparks;
	}

	protected void spawnSparks(float slipSpeed, int sparks) {
		BogeyRenderedType type = bogey.options.type;
		double[] axlePositions = type.axlePositions();
		int axleCount = axlePositions != null ? axlePositions.length : type.axleCount();
		if(axleCount <= 0) {
			return;
		}
		updatePivotBasis();

		Level level = bogey.getLevel();
		RandomSource random = level.getRandom();
		for(int i = 0; i < sparks; i++) {
			int axle = random.nextInt(axleCount);
			boolean left = random.nextBoolean();
			spawnSpark(level, random, slipSpeed, axlePosition(axlePositions, axle, axleCount), left);
		}
	}

	protected void updatePivotBasis() {
		bogey.getRenderPivotOffset(1, pivotOffset);
		bogey.getRenderPivotRot(1, pivotRot);
		Basis3f.I.transform(pivotRot, basis);
		if(bogey.isInverted()) basis.vertical.mul(-1);
	}

	protected float axlePosition(double[] axlePositions, int axle, int axleCount) {
		if(axlePositions != null) {
			return (float)axlePositions[axle];
		}
		if(axleCount <= 1) {
			return 0;
		}
		return (float)bogey.options.type.visualAxleSpacing() * ((float)axle / (axleCount - 1) - 0.5F);
	}

	protected void spawnSpark(Level level, RandomSource random, float slipSpeed, float axlePos, boolean left) {
		contactPoint(random, axlePos, left, contact);
		scrubVelocity(random, slipSpeed, velocity);

		level.addParticle(SimurailParticles.PHYSICS_BOGEY_WHEEL_SPARK.get(),
				bogey.localCenter.x() + contact.x,
				bogey.localCenter.y() + contact.y,
				bogey.localCenter.z() + contact.z,
				velocity.x, velocity.y, velocity.z);
	}

	protected void contactPoint(RandomSource random, float axlePos, boolean left, Vector3f dest) {
		BogeyRenderedType type = bogey.options.type;
		boolean inverted = bogey.isInverted();
		float axleOffset = bogey.options.getAxleOffset();
		float halfGauge = (float)type.trackWidth() * 0.5F;
		float wheelReach = (float)type.trackHeight() + RAIL_CLEARANCE;
		float trackOffset = inverted ? 0.5F + axleOffset : -1.5F - axleOffset;

		dest.set(
				axlePos + jitter(random, CONTACT_JITTER_ALONG),
				(inverted ? trackOffset + 1 - wheelReach : trackOffset + wheelReach),
				(left ? halfGauge : -halfGauge) + jitter(random, CONTACT_JITTER_ACROSS));
		pivotRot.transform(dest).add(pivotOffset);
	}

	protected void scrubVelocity(RandomSource random, float slipSpeed, Vector3f dest) {
		float scrub = -slipSpeed * 0.05F;
		dest.zero();
		dest.fma(scrub * (0.5F + random.nextFloat() * 0.7F), basis.direction);
		dest.fma(Math.abs(scrub) * (0.1F + random.nextFloat() * 0.35F), basis.vertical);
		dest.fma(Math.abs(scrub) * (random.nextFloat() - 0.5F) * 0.4F, basis.lateral);
	}

	protected static float jitter(RandomSource random, float width) {
		return (random.nextFloat() - 0.5F) * width;
	}

	protected final Vector3f pivotOffset = new Vector3f();
	protected final Quaternionf pivotRot = new Quaternionf();
	protected final Vector3f contact = new Vector3f();
	protected final Basis3f basis = new Basis3f();
	protected final Vector3f velocity = new Vector3f();
}
