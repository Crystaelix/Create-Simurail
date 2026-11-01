package com.crystaelix.simurail.compat.ip_aeronautics;


public class SimurailIPAeronauticsCompat {

	public static void onCommonSetupLate() {
		IPAProjectedTrackSegmentFinder.register();
	}
}
