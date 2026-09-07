package com.crystaelix.simurail.api.bogey;

import java.util.Objects;
import java.util.Set;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlockEntity;
import com.simibubi.create.content.trains.bogey.BogeySizes.BogeySize;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;

public final class BogeySubtype {

	public static final StreamCodec<ByteBuf, BogeySubtype> STREAM_CODEC = StreamCodec.composite(
			BogeyType.STREAM_CODEC, t -> t.type,
			ByteBufCodecs.COMPOUND_TAG, t -> t.extra,
			BogeySubtype::new);

	private final BogeyType type;
	private final CompoundTag extra;

	public BogeySubtype(BogeyType type, CompoundTag extra) {
		this.type = type;
		this.extra = extra;
	}

	public BogeySubtype(BogeyType type) {
		this.type = type;
		this.extra = new CompoundTag();
	}

	public BogeyType type() {
		return type;
	}

	public BogeyStyle style() {
		return type.style();
	}

	public BogeySize size() {
		return type.size();
	}

	public AbstractBogeyBlock<?> block() {
		return type.block();
	}

	public int logicalAxleSpacing() {
		return type.logicalAxleSpacing(extra);
	}

	public double visualAxleSpacing() {
		return type.visualAxleSpacing(extra);
	}

	public int axleCount() {
		return type.axleCount(extra);
	}

	@Nullable
	public double[] axlePositions() {
		return type.axlePositions(extra);
	}

	public double wheelRadius() {
		return type.wheelRadius(extra);
	}

	public double trackWidth() {
		return type.trackWidth();
	}

	public double trackHeight() {
		return type.trackHeight();
	}

	public Vector3f connectorAnchorOffset(boolean inverted, Vector3f dest) {
		return type.connectorAnchorOffset(inverted, dest);
	}

	public Set<TrackType> trackTypes() {
		return type.trackTypes();
	}

	public boolean groundDrivable() {
		return type.groundDrivable(extra);
	}

	public SoundEvent soundEvent() {
		return type.soundEvent();
	}

	public CompoundTag extra() {
		return extra;
	}

	public CompoundTag data(boolean upsideDown) {
		CompoundTag tag = new CompoundTag();
		tag.putBoolean(CarriageBogey.UPSIDE_DOWN_KEY, upsideDown);
		NBTHelper.writeResourceLocation(tag, AbstractBogeyBlockEntity.BOGEY_STYLE_KEY, type.style().id);
		tag.merge(extra);
		return tag;
	}

	public CompoundTag write() {
		CompoundTag tag = type.write();
		if(!extra.isEmpty()) {
			tag.put("extra", extra);
		}
		return tag;
	}

	public static BogeySubtype read(CompoundTag tag) {
		BogeyType type = BogeyType.read(tag);
		CompoundTag extra = tag.getCompound("extra");
		return new BogeySubtype(type, extra);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, extra);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof BogeySubtype other) {
			return type == other.type && extra.equals(other.extra);
		}
		return false;
	}

	public static BogeySubtype getFallback() {
		return new BogeySubtype(BogeyType.getFallback());
	}

	public static BogeySubtype getFallback(boolean inverted) {
		return new BogeySubtype(BogeyType.getFallback(inverted));
	}

	public static BogeySubtype getDefault(TrackType trackType, boolean inverted) {
		return new BogeySubtype(BogeyType.getDefault(trackType, inverted));
	}
}
