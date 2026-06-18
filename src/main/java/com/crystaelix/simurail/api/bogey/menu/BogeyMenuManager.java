package com.crystaelix.simurail.api.bogey.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.mutable.MutableObject;

import com.crystaelix.simurail.api.bogey.BogeyRenderedType;
import com.crystaelix.simurail.api.bogey.BogeyType;
import com.crystaelix.simurail.content.SimurailBogeys;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;

import net.minecraft.resources.ResourceLocation;

public class BogeyMenuManager {

	private static final List<BogeyCategory<?>> CATEGORIES = new ArrayList<>();
	private static final Map<ResourceLocation, BogeyEntry> ENTRIES = new HashMap<>();

	private static final Map<TrackType, TrackTypeEntry> TRACK_TYPES = new HashMap<>();

	public static void addBogeyCategory(BogeyCategory<?> category) {
		CATEGORIES.add(category);
		addBogeyEntries(category, new ArrayList<>());
	}

	private static void addBogeyEntries(BogeyCategory<?> category, List<BogeyCategory<?>> path) {
		if(path.size() >= 4) {
			throw new IllegalArgumentException("Cannot have sub-categories of more than depth 4");
		}
		path.addLast(category);
		if(!category.isDynamic()) {
			if(category instanceof BogeyEntryCategory entryCategory) {
				for(BogeyEntry e : entryCategory.children()) {
					ENTRIES.putIfAbsent(e.id(), e);
				}
			}
			else if(category instanceof BogeyParentCategory parent) {
				for(BogeyCategory<?> c : parent.children()) {
					if(path.contains(c)) {
						throw new IllegalArgumentException("Cannot have cyclic sub-categories");
					}
					addBogeyEntries(c, path);
				}
			}
		}
		path.removeLast();
	}

	public static void addTrackTypeEntry(TrackTypeEntry entry) {
		TRACK_TYPES.put(entry.trackType(), entry);
	}

	public static List<BogeyCategory<?>> getCategories() {
		return Collections.unmodifiableList(CATEGORIES);
	}

	public static List<BogeyCategory<?>> getCategories(boolean inverted) {
		return inverted ? CATEGORIES.stream().filter(BogeyCategory::containsInvertible).toList() : getCategories();
	}

	public static BogeyEntry getEntry(ResourceLocation id) {
		return ENTRIES.get(id);
	}

	public static BogeyMenuSelection defaultEntry(boolean inverted) {
		return findEntry(BogeyRenderedType.getFallback(inverted), inverted);
	}

	public static BogeyMenuSelection findEntry(BogeyRenderedType type, boolean inverted) {
		List<BogeyCategory<?>> path = new ArrayList<>();
		MutableObject<BogeyEntry> entry = new MutableObject<>();
		for(BogeyCategory<?> c : getCategories(inverted)) {
			if(search(c, type.type(), inverted, path, entry)) {
				break;
			}
			path.clear();
		}
		if(path.isEmpty() || entry.getValue() == null) {
			return BogeyMenuSelection.EMPTY;
		}
		List<BogeyDataOptionValue<?>> optionValues = entry.getValue().options().stream().<BogeyDataOptionValue<?>>map(o -> o.readToValue(type.extra())).toList();
		return new BogeyMenuSelection(List.copyOf(path), Optional.ofNullable(entry.getValue()), optionValues);
	}

	private static boolean search(BogeyCategory<?> category, BogeyType type, boolean inverted, List<BogeyCategory<?>> path, MutableObject<BogeyEntry> entry) {
		path.addLast(category);
		if(category instanceof BogeyEntryCategory entryCategory) {
			for(BogeyEntry e : entryCategory.children(inverted)) {
				if(e.type().equals(type)) {
					entry.setValue(e);
					return true;
				}
			}
		}
		else if(category instanceof BogeyParentCategory parent) {
			for(BogeyCategory<?> c : parent.children(inverted)) {
				if(!path.contains(c) && search(c, type, inverted, path, entry)) {
					return true;
				}
			}
		}
		path.removeLast();
		return false;
	}

	public static TrackTypeEntry getTrackTypeEntry(TrackType trackType) {
		if(TRACK_TYPES.containsKey(trackType)) {
			return TRACK_TYPES.get(trackType);
		}
		return SimurailBogeys.STANDARD;
	}

	public static List<TrackTypeEntry> getTrackTypeEntries(BogeyType bogeyType) {
		return bogeyType.trackTypes().stream().map(BogeyMenuManager::getTrackTypeEntry).distinct().toList();
	}

	public static TrackTypeEntry getRenderTrackTypeEntry(BogeyType bogeyType, boolean inverted) {
		if(inverted) {
			Optional<TrackTypeEntry> trackType = bogeyType.trackTypes().stream().
					map(BogeyMenuManager::getTrackTypeEntry).
					filter(TrackTypeEntry::allowInverted).
					findFirst();
			if(trackType.isPresent()) {
				return trackType.get();
			}
			return getRenderTrackTypeEntry(bogeyType, false);
		}
		else if(bogeyType.trackTypes().contains(TrackType.STANDARD)) {
			return SimurailBogeys.STANDARD;
		}
		return getTrackTypeEntry(bogeyType.trackTypes().stream().
				filter(t -> t != TrackType.STANDARD).
				findFirst().orElse(TrackType.STANDARD));
	}
}
