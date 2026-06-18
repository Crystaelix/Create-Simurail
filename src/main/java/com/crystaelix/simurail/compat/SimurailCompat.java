package com.crystaelix.simurail.compat;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import com.crystaelix.simurail.Simurail;
import com.simibubi.create.compat.Mods;

import net.neoforged.fml.loading.LoadingModList;

public enum SimurailCompat {
	COMPUTERCRAFT("computercraft"),
	ELECTROENERGETICS("electroenergetics"),
	BLOCKSBOGIES("create_bb"),
	RAILWAYS(() -> isClassLoaded("com.railwayteam.railways.Railways"))
	;
	
	public final BooleanSupplier isLoaded;
	
	SimurailCompat(BooleanSupplier isLoaded) {
		this.isLoaded = isLoaded;
	}

	SimurailCompat(String modId) {
		this(() -> LoadingModList.get().getModFileById("electroenergetics") != null);
	}
	
	public boolean isLoaded() {
		return isLoaded.getAsBoolean();
	}

	public void ifLoaded(Supplier<Runnable> toExecute) {
		if(isLoaded()) {
			toExecute.get().run();
		}
	}

	public static boolean isClassLoaded(String className) {
		try {
			Class.forName(className, false, Simurail.class.getClassLoader());
			return true;
		}
		catch(ClassNotFoundException e) {
			return false;
		}
	}
}
