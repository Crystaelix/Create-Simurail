package com.crystaelix.simurail.compat.ip_aeronautics;

import com.crystaelix.simurail.api.track.TrackSegments;

public class SimurailIPAeronauticsCompat {

	public static void onCommonSetupLate() {
		TrackSegments.addSegmentReader("ipa_projected", IPAProjectedTrackSegment::read);
		TrackSegments.addSegmentFinder(IPAProjectedTrackSegmentFinder.INSTANCE, -1000);
	}
}
