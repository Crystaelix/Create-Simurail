package com.crystaelix.simurail.api.bogey;

import java.util.Objects;
import java.util.Set;

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

public final class BogeyRenderedType {

	public static final StreamCodec<ByteBuf, BogeyRenderedType> STREAM_CODEC = StreamCodec.composite(
			BogeyType.STREAM_CODEC, t -> t.type,
			ByteBufCodecs.COMPOUND_TAG, t -> t.extra,
			BogeyRenderedType::new);

	private final BogeyType type;
	private final CompoundTag extra;

	public BogeyRenderedType(BogeyType type, CompoundTag extra) {
		this.type = type;
		this.extra = extra;
	}

	public BogeyRenderedType(BogeyType type) {
		this.type = type;
		this.extra = new CompoundTag();
	}

	public BogeyType type() {
		return this.type;
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

	public double axleSpacing() {
		return type.wheelSpacing();
	}

	public double wheelRadius() {
		return type.wheelRadius();
	}

	public Vector3f connectorAnchorOffset(boolean inverted, Vector3f dest) {
		return type.connectorAnchorOffset(inverted, dest);
	}

	public Set<TrackType> trackTypes() {
		return type.trackTypes();
	}

	public boolean groundDrivable() {
		return type.groundDrivable();
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

	public static BogeyRenderedType read(CompoundTag tag) {
		BogeyType type = BogeyType.read(tag);
		CompoundTag extra = tag.getCompound("extra");
		return new BogeyRenderedType(type, extra);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, extra);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof BogeyRenderedType other) {
			return type == other.type && extra.equals(other.extra);
		}
		return false;
	}

	public static BogeyRenderedType getFallback() {
		return new BogeyRenderedType(BogeyType.getFallback());
	}

	public static BogeyRenderedType getFallback(boolean inverted) {
		return new BogeyRenderedType(BogeyType.getFallback(inverted));
	}

	public static BogeyRenderedType getDefault(TrackType trackType, boolean inverted) {
		return new BogeyRenderedType(BogeyType.getDefault(trackType, inverted));
	}
}
