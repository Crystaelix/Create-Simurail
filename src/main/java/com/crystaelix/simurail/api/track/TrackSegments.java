package com.crystaelix.simurail.api.track;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import com.google.common.collect.Collections2;

import it.unimi.dsi.fastutil.objects.ObjectIntPair;

public class TrackSegments {

	public static final double ORIENT_SCORE_WEIGHT = 0.04;

	private static final Map<String, TrackSegmentReader<?>> READERS = new HashMap<>();
	private static final TreeSet<ObjectIntPair<TrackSegmentFinder>> FINDERS = new TreeSet<>(Comparator.comparing(ObjectIntPair::secondInt));

	public static void addSegmentReader(String type, TrackSegmentReader<?> reader) {
		READERS.put(type, reader);
	}

	public static void addSegmentFinder(TrackSegmentFinder finder, int order) {
		FINDERS.add(ObjectIntPair.of(finder, order));
	}

	public static Collection<TrackSegmentFinder> getSegmentFinders() {
		return Collections2.transform(FINDERS, ObjectIntPair::first);
	}

	public static TrackSegmentReader<?> getSegmentReader(String type) {
		return READERS.get(type);
	}
}
