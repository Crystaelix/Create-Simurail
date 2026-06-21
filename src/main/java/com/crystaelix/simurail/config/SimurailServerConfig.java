package com.crystaelix.simurail.config;

public class SimurailServerConfig extends SimurailBaseConfig {

	public final SimurailBlocksConfig blocks;
	public final SimurailPhysicsConfig physics;
	public final SimurailCompatConfig compat;

	public SimurailServerConfig() {
		blocks = nested(0, SimurailBlocksConfig::new, Comments.blocks);
		physics = nested(0, SimurailPhysicsConfig::new, Comments.physics);
		compat = nested(0, SimurailCompatConfig::new, Comments.compat);
	}

	@Override
	public String getName() {
		return "server";
	}

	static class Comments {
		static String blocks = "Parameters of Simurail block behaviors";
		static String physics = "Parameters of Simurail physics behaviors";
		static String compat = "Parameters of Simurail compat behaviors";
	}
}
