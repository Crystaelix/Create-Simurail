package com.crystaelix.simurail.content.track_switch;

/**
 * Variables for tracking if track switches should be applied or not.
 * Based on {@link com.railwayteam.railways.util.MixinVariables MixinVariables}
 */
public class TrackSwitchVariables {
	public static boolean skipSwitches = false;
	public static int signalPropagatorCallDepth = 0;
}
