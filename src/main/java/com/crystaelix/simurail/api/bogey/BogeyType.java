package com.crystaelix.simurail.api.bogey;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.joml.Vector3f;

import com.crystaelix.simurail.api.network.SimurailStreamCodecs;
import com.simibubi.create.AllBogeyStyles;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeySizes.BogeySize;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectBooleanPair;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public final class BogeyType {

	public static final StreamCodec<ByteBuf, BogeyType> STREAM_CODEC = StreamCodec.composite(
			SimurailStreamCodecs.BOGEY_STYLE, BogeyType::style,
			SimurailStreamCodecs.BOGEY_SIZE, BogeyType::size,
			BogeyType::new);

	private final BogeyStyle style;
	private final BogeySize size;

	private final AbstractBogeyBlock<?> block;

	public BogeyType(BogeyStyle style, BogeySize size) {
		this.style = style;
		this.size = size;

		block = style.getBlockForSize(size);
	}

	public BogeyStyle style() {
		return style;
	}

	public BogeySize size() {
		return size;
	}

	public AbstractBogeyBlock<?> block() {
		return block;
	}

	public double wheelSpacing() {
		if(BogeyPropertyOverrides.WHEEL_SPACING_OVERRIDE.containsKey(this)) {
			return BogeyPropertyOverrides.WHEEL_SPACING_OVERRIDE.getDouble(this);
		}
		return block.getWheelPointSpacing();
	}

	public double wheelRadius() {
		if(BogeyPropertyOverrides.WHEEL_RADIUS_OVERRIDE.containsKey(this)) {
			return BogeyPropertyOverrides.WHEEL_RADIUS_OVERRIDE.getDouble(this);
		}
		return block.getWheelRadius();
	}

	public Vector3f connectorAnchorOffset(boolean inverted, Vector3f dest) {
		Vec3 offset = block.getConnectorAnchorOffset(inverted);
		return dest.set(offset.z, offset.y - 0.5, offset.x);
	}

	public Set<TrackType> trackTypes() {
		if(BogeyPropertyOverrides.TRACK_TYPES_OVERRIDE.containsKey(this)) {
			return BogeyPropertyOverrides.TRACK_TYPES_OVERRIDE.get(this);
		}
		return block.getValidPathfindingTypes(style);
	}

	public boolean invertible() {
		return block.canBeUpsideDown();
	}

	public boolean groundDrivable() {
		if(BogeyPropertyOverrides.GROUND_DRIVABLE_OVERRIDE.containsKey(this)) {
			return BogeyPropertyOverrides.GROUND_DRIVABLE_OVERRIDE.getBoolean(this);
		}
		return !invertible();
	}

	public CompoundTag write() {
		CompoundTag tag = new CompoundTag();
		NBTHelper.writeResourceLocation(tag, "style", style.id);
		NBTHelper.writeResourceLocation(tag, "size", size.id());
		return tag;
	}

	public static BogeyType read(CompoundTag tag) {
		BogeyStyle style = AllBogeyStyles.BOGEY_STYLES.getOrDefault(NBTHelper.readResourceLocation(tag, "style"), AllBogeyStyles.STANDARD);
		BogeySize size = BogeySizes.all().getOrDefault(NBTHelper.readResourceLocation(tag, "size"), BogeySizes.SMALL);
		return new BogeyType(style, size);
	}

	@Override
	public int hashCode() {
		return Objects.hash(style, size);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof BogeyType other) {
			return style == other.style && size == other.size;
		}
		return false;
	}

	private static final BogeyType FALLBACK = new BogeyType(AllBogeyStyles.STANDARD, BogeySizes.SMALL);
	private static BogeyType fallbackInverted;
	private static final Map<ObjectBooleanPair<TrackType>, BogeyType> DEFAULTS = new Object2ObjectOpenHashMap<>();

	public static BogeyType getFallback() {
		return FALLBACK;
	}

	public static BogeyType getFallback(boolean inverted) {
		if(inverted) {
			createFallbackInverted();
			return fallbackInverted;
		}
		return FALLBACK;
	}

	public static BogeyType getDefault(TrackType trackType, boolean inverted) {
		return DEFAULTS.getOrDefault(ObjectBooleanPair.of(trackType, inverted), getFallback(inverted));
	}

	public static boolean hasDefault(TrackType trackType, boolean inverted) {
		return DEFAULTS.containsKey(ObjectBooleanPair.of(trackType, inverted));
	}

	public static void setDefault(TrackType trackType, boolean inverted, BogeyType type) {
		DEFAULTS.put(ObjectBooleanPair.of(trackType, inverted), type);
	}

	private static void createFallbackInverted() {
		if(fallbackInverted == null) {
			if(isRailwaysLoaded()) {
				ResourceLocation id = ResourceLocation.parse("railways:monobogey");
				fallbackInverted = new BogeyType(AllBogeyStyles.BOGEY_STYLES.get(id), BogeySizes.SMALL);
				return;
			}
			for(BogeyStyle style : AllBogeyStyles.BOGEY_STYLES.values()) {
				Set<BogeySize> validSizes = style.validSizes();
				for(BogeySize size : BogeySizes.allSortedIncreasing()) {
					if(validSizes.contains(size) && style.getBlockForSize(size).canBeUpsideDown()) {
						fallbackInverted = new BogeyType(style, size);
						return;
					}
				}
			}
			fallbackInverted = FALLBACK;
		}
	}

	private static boolean isRailwaysLoaded() {
		try {
			Class.forName("com.railwayteam.railways.Railways");
			return true;
		}
		catch(ClassNotFoundException e) {
			return false;
		}
	}
}
