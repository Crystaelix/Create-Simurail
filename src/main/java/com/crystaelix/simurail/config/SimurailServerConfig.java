package com.crystaelix.simurail.config;

import net.createmod.catnip.config.ConfigBase;

public class SimurailServerConfig extends ConfigBase {

	public final SimurailBlocksConfig blocks = nested(0, SimurailBlocksConfig::new, Comments.blocks);
	public final SimurailPhysicsConfig physics = nested(0, SimurailPhysicsConfig::new, Comments.physics);

	@Override
	public String getName() {
		return "server";
	}

	static class Comments {
        static String blocks = "Parameters of Simurail block behaviors";
        static String physics = "Parameters of Simurail physics behaviors";
	}
}
