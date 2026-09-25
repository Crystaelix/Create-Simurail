package com.crystaelix.simurail.api.signal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SignalNameRegistry {

	private static final List<SignalNameExtractor> EXTRACTORS = new ArrayList<>();

	public static void register(SignalNameExtractor extracor) {
		EXTRACTORS.add(extracor);
	}

	public static List<SignalNameExtractor> getExtractors() {
		return Collections.unmodifiableList(EXTRACTORS);
	}
}
