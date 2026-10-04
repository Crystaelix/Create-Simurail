package com.crystaelix.simurail.extension;

import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;

public interface PonderSceneRegistrationHelperExtension<T> extends PonderSceneRegistrationHelper<T> {

	PonderSceneRegistrationHelper<T> simurail$withNamespace(String namespace);

	static <T> PonderSceneRegistrationHelperExtension<T> cast(PonderSceneRegistrationHelper<T> registry) {
		return (PonderSceneRegistrationHelperExtension<T>)registry;
	}
}
