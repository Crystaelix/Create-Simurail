package com.crystaelix.simurail.content.bogey;

import com.crystaelix.simurail.api.bogey.BogeyRenderedType;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.StreamCodec;

public class PhysicsBogeyOptions {

	public static final StreamCodec<ByteBuf, PhysicsBogeyOptions> STREAM_CODEC = StreamCodec.of(
			(b, v) -> v.encode(b), b -> new PhysicsBogeyOptions().decode(b));

	public BogeyRenderedType type = BogeyRenderedType.getFallback();
	public boolean enabled = true;
	public boolean allowYawOffset = true;
	public boolean allowPitchOffset = true;
	public boolean allowVerticalOffset = false;
	public boolean allowLateralOffset = false;
	public boolean allowVerticalMovement = false;
	public boolean renderFrontConnector = true;
	public boolean renderBackConnector = true;
	public PhysicsBogeyControlMode controlMode = PhysicsBogeyControlMode.BRAKING;

	private float axleOffset = 0;
	private float stress = 8;
	private float tiltStrength = 0;

	public PhysicsBogeyOptions() {}

	public PhysicsBogeyOptions(boolean inverted) {
		type = BogeyRenderedType.getFallback(inverted);
	}

	public PhysicsBogeyOptions set(PhysicsBogeyOptions other) {
		type = other.type;
		enabled = other.enabled;
		allowYawOffset = other.allowYawOffset;
		allowPitchOffset = other.allowPitchOffset;
		allowVerticalOffset = other.allowVerticalOffset;
		allowLateralOffset = other.allowLateralOffset;
		allowVerticalMovement = other.allowVerticalMovement;
		renderFrontConnector = other.renderFrontConnector;
		renderBackConnector = other.renderBackConnector;
		controlMode = other.controlMode;
		axleOffset = other.axleOffset;
		stress = other.stress;
		tiltStrength = other.tiltStrength;
		return this;
	}

	public PhysicsBogeyOptions setNonComputer(PhysicsBogeyOptions other) {
		type = other.type;
		renderFrontConnector = other.renderFrontConnector;
		renderBackConnector = other.renderBackConnector;
		return this;
	}

	public float getAxleOffset() {
		return axleOffset;
	}

	public PhysicsBogeyOptions setAxleOffset(float axleOffset) {
		this.axleOffset = Math.clamp(axleOffset, -1, 1);
		return this;
	}

	public float getStress() {
		return stress;
	}

	public PhysicsBogeyOptions setStress(float stress) {
		this.stress = Math.clamp(stress, 0, 64);
		return this;
	}

	public float getTiltStrength() {
		return tiltStrength;
	}

	public PhysicsBogeyOptions setTiltStrength(float tiltStrength) {
		this.tiltStrength = Math.clamp(tiltStrength, 0, 1);
		return this;
	}

	public int getAngularType() {
		return allowYawOffset ? allowPitchOffset ? 1 : 2 : allowPitchOffset ? 3 : 0;
	}

	public PhysicsBogeyOptions setAngularType(int typeIndex) {
		switch(typeIndex) {
		case 0 -> {
			allowYawOffset = false;
			allowPitchOffset = false;
		}
		case 1 -> {
			allowYawOffset = true;
			allowPitchOffset = true;
		}
		case 2 -> {
			allowYawOffset = true;
			allowPitchOffset = false;
		}
		case 3 -> {
			allowYawOffset = false;
			allowPitchOffset = true;
		}
		}
		return this;
	}

	public int getLinearType() {
		return allowVerticalOffset ? allowLateralOffset ? 1 : 3 : allowLateralOffset ? 2 : 0;
	}

	public PhysicsBogeyOptions setLinearType(int typeIndex) {
		switch(typeIndex) {
		case 0 -> {
			allowVerticalOffset = false;
			allowLateralOffset = false;
		}
		case 1 -> {
			allowVerticalOffset = true;
			allowLateralOffset = true;
		}
		case 2 -> {
			allowVerticalOffset = false;
			allowLateralOffset = true;
		}
		case 3 -> {
			allowVerticalOffset = true;
			allowLateralOffset = false;
		}
		}
		return this;
	}

	public int getConnectorType() {
		return renderFrontConnector ? renderBackConnector ? 0 : 2 : allowLateralOffset ? 3 : 1;
	}

	public PhysicsBogeyOptions setConnectorType(int typeIndex) {
		switch(typeIndex) {
		case 0 -> {
			renderFrontConnector = true;
			renderBackConnector = true;
		}
		case 1 -> {
			renderFrontConnector = false;
			renderBackConnector = false;
		}
		case 2 -> {
			renderFrontConnector = true;
			renderBackConnector = false;
		}
		case 3 -> {
			renderFrontConnector = false;
			renderBackConnector = true;
		}
		}
		return this;
	}

	public short getFlags() {
		short flags = 0;
		if(enabled)               flags |= 1;
		if(allowYawOffset)        flags |= 2;
		if(allowPitchOffset)      flags |= 4;
		if(allowVerticalOffset)   flags |= 8;
		if(allowLateralOffset)    flags |= 16;
		if(allowVerticalMovement) flags |= 32;
		if(renderFrontConnector)  flags |= 64;
		if(renderBackConnector)   flags |= 128;
		return flags;
	}

	public PhysicsBogeyOptions setFlags(short flags) {
		enabled               = (flags & 1)   != 0;
		allowYawOffset        = (flags & 2)   != 0;
		allowPitchOffset      = (flags & 4)   != 0;
		allowVerticalOffset   = (flags & 8)   != 0;
		allowLateralOffset    = (flags & 16)  != 0;
		allowVerticalMovement = (flags & 32)  != 0;
		renderFrontConnector  = (flags & 64)  != 0;
		renderBackConnector   = (flags & 128) != 0;
		return this;
	}

	public CompoundTag write() {
		CompoundTag tag = new CompoundTag();
		tag.put("type", type.write());
		tag.putShort("flags", getFlags());
		tag.putByte("control_mode", (byte)controlMode.ordinal());
		tag.putFloat("axle_offset", axleOffset);
		tag.putFloat("stress", stress);
		tag.putDouble("tilt_strength", tiltStrength);
		return tag;
	}

	public PhysicsBogeyOptions read(CompoundTag tag) {
		type = BogeyRenderedType.read(tag.getCompound("type"));
		setFlags(tag.getShort("flags"));
		controlMode = PhysicsBogeyControlMode.BY_ID.apply(tag.getByte("control_mode"));
		axleOffset = Math.clamp(tag.getFloat("axle_offset"), -1, 1);
		stress = Math.clamp(tag.getFloat("stress"), 0, 32);
		tiltStrength = Math.clamp(tag.getFloat("tilt_strength"), 0, 1);
		return this;
	}

	public void encode(ByteBuf buf) {
		BogeyRenderedType.STREAM_CODEC.encode(buf, type);
		buf.writeShort(getFlags());
		PhysicsBogeyControlMode.STREAM_CODEC.encode(buf, controlMode);
		buf.writeFloat(axleOffset);
		buf.writeFloat(stress);
		buf.writeFloat(tiltStrength);
	}

	public PhysicsBogeyOptions decode(ByteBuf buf) {
		type = BogeyRenderedType.STREAM_CODEC.decode(buf);
		setFlags(buf.readShort());
		controlMode = PhysicsBogeyControlMode.STREAM_CODEC.decode(buf);
		axleOffset = buf.readFloat();
		stress = buf.readFloat();
		tiltStrength = buf.readFloat();
		return this;
	}
}
