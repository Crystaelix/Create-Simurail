package com.crystaelix.simurail.content.probe_reader;

import com.google.common.base.Strings;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Utf8String;
import net.minecraft.network.codec.StreamCodec;

public class ProbeReaderOptions {

	public static final StreamCodec<ByteBuf, ProbeReaderOptions> STREAM_CODEC = StreamCodec.of(
			(b, v) -> v.encode(b), b -> new ProbeReaderOptions().decode(b));

	public ProbeReaderMode mode = ProbeReaderMode.OCCUPIED_SIGNAL;

	private String filter = "";
	private float minDistance = 4;
	private float maxDistance = 12;

	public ProbeReaderOptions() {}

	public ProbeReaderOptions set(ProbeReaderOptions other) {
		mode = other.mode;
		filter = other.filter;
		minDistance = other.minDistance;
		maxDistance = other.maxDistance;
		return this;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String stationFilter) {
		this.filter = Strings.nullToEmpty(stationFilter);
	}

	public float getMinDistance() {
		return minDistance;
	}

	public void setMinDistance(float minDistance) {
		this.minDistance = Math.clamp(minDistance, 0, maxDistance);
	}

	public float getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(float maxDistance) {
		this.maxDistance = Math.clamp(maxDistance, minDistance, 256);
	}

	public CompoundTag write() {
		CompoundTag tag = new CompoundTag();
		tag.putByte("mode", (byte)mode.ordinal());
		tag.putString("station_filter", filter);
		tag.putFloat("min_distance", minDistance);
		tag.putFloat("max_distance", maxDistance);
		return tag;
	}

	public ProbeReaderOptions read(CompoundTag tag) {
		mode = ProbeReaderMode.BY_ID.apply(tag.getByte("mode"));
		filter = tag.getString("station_filter");
		minDistance = Math.clamp(tag.getFloat("min_distance"), 0, 256);
		maxDistance = Math.clamp(tag.getFloat("max_distance"), 0, 256);
		return this;
	}

	public void encode(ByteBuf buf) {
		ProbeReaderMode.STREAM_CODEC.encode(buf, mode);
		Utf8String.write(buf, filter, Short.MAX_VALUE);
		buf.writeFloat(minDistance);
		buf.writeFloat(maxDistance);
	}

	public ProbeReaderOptions decode(ByteBuf buf) {
		mode = ProbeReaderMode.STREAM_CODEC.decode(buf);
		filter = Utf8String.read(buf, Short.MAX_VALUE);
		minDistance = buf.readFloat();
		maxDistance = buf.readFloat();
		return this;
	}
}
