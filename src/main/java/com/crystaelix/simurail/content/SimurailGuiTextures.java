package com.crystaelix.simurail.content;

import com.crystaelix.simurail.Simurail;

import net.createmod.catnip.gui.TextureSheetSegment;
import net.createmod.catnip.gui.element.ScreenElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public enum SimurailGuiTextures implements ScreenElement, TextureSheetSegment {

	PHYSICS_BOGEY_OPTIONS("physics_bogey_options", 0, 0, 295, 183, 512, 256),
	PHYSICS_BOGEY_OPTIONS_BOGEY_ICON("physics_bogey_options", 295, 0, 16, 16, 512, 256),

	PHYSICS_BOGEY_MENU("physics_bogey_menu", 0, 0, 303, 203, 512, 256),
	PHYSICS_BOGEY_MENU_CATEGORY("physics_bogey_menu", 303, 0, 123, 18, 512, 256),
	PHYSICS_BOGEY_MENU_ENTRY("physics_bogey_menu", 303, 18, 120, 18, 512, 256),
	PHYSICS_BOGEY_MENU_OPTION("physics_bogey_menu", 303, 36, 46, 20, 512, 256),
	PHYSICS_BOGEY_MENU_OPTION_SCROLL_VALUE("physics_bogey_menu", 303, 56, 106, 20, 512, 256),
	PHYSICS_BOGEY_MENU_OPTION_TEXT_VALUE("physics_bogey_menu", 303, 76, 104, 20, 512, 256),

	GANGWAY_FRAME("gangway_frame", 0, 0, 188, 101),
	;

	public final ResourceLocation location;
	public final int u;
	public final int v;
	public final int w;
	public final int h;
	public final int tW;
	public final int tH;

	SimurailGuiTextures(String location, int u, int v, int w, int h, int tW, int tH) {
		this.location = Simurail.id("textures/gui/" + location + ".png");
		this.u = u;
		this.v = v;
		this.w = w;
		this.h = h;
		this.tW = tW;
		this.tH = tH;
	}

	SimurailGuiTextures(String location, int u, int v, int w, int h) {
		this(location, u, v, w, h, 256, 256);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void render(GuiGraphics graphics, int x, int y) {
		graphics.blit(location, x, y, u, v, w, h, tW, tH);
	}

	@Override
	public ResourceLocation getLocation() {
		return location;
	}

	@Override
	public int getStartX() {
		return u;
	}

	@Override
	public int getStartY() {
		return v;
	}

	@Override
	public int getWidth() {
		return w;
	}

	@Override
	public int getHeight() {
		return h;
	}
}
