package com.crystaelix.simurail.gui;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.foundation.gui.widget.Label;

import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;

public class SLabel extends Label {

	protected int margin;
	protected Alignment alignment = Alignment.LEFT;
	protected boolean overflowTooltip = false;
	protected boolean doClickSound = false;

	private int textWidth;

	public SLabel(int x, int y, int width) {
		super(x, y, CommonComponents.EMPTY);
		this.width = width;
		this.height = font.lineHeight;
	}

	public SLabel(int x, int y, int width, int height) {
		super(x, y, CommonComponents.EMPTY);
		this.width = width;
		this.height = height;
	}

	public SLabel withMargin(int margin) {
		this.margin = margin;
		return this;
	}

	public SLabel withAlignment(Alignment alignment) {
		this.alignment = alignment;
		return this;
	}

	public SLabel withAlignLeft() {
		return withAlignment(Alignment.LEFT);
	}

	public SLabel withAlignCenter() {
		return withAlignment(Alignment.CENTER);
	}

	public SLabel withAlignRight() {
		return withAlignment(Alignment.RIGHT);
	}

	public SLabel withTooltip(List<Component> tooltip) {
		overflowTooltip = false;
		this.toolTip = tooltip;
		return this;
	}

	public SLabel withOverflowTooltip() {
		overflowTooltip = true;
		this.toolTip = List.of();
		return this;
	}

	public SLabel withClickSound() {
		doClickSound = true;
		return this;
	}

	@Override
	public void playDownSound(SoundManager handler) {
		if(doClickSound) {
			super.playDownSound(handler);
		}
	}

	@Override
	public List<Component> getToolTip() {
		if(overflowTooltip && textWidth > width - margin * 2) {
			MutableComponent copy = text.copy();
			if(suffix != null && !suffix.isEmpty()) {
				copy.append(suffix);
			}
			return List.of(copy);
		}
		return super.getToolTip();
	}

	@Override
	protected void doRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		if(text == null || text.getString().isEmpty()) {
			return;
		}

		RenderSystem.setShaderColor(1, 1, 1, 1);
		MutableComponent copy = text.copy();
		if(suffix != null && !suffix.isEmpty()) {
			copy.append(suffix);
		}
		textWidth = font.width(copy);

		drawScrollingString(graphics, font, copy, alignment, getX() + margin, getX() + width - margin, getY() + height / 2 - font.lineHeight / 2, color, hasShadow);
	}

	public static void drawScrollingString(GuiGraphics graphics, Font font, Component text, Alignment alignment, int minX, int maxX, int y, int color, boolean dropShadow) {
		int width = font.width(text);
		int maxWidth = maxX - minX;
		if(width > maxWidth) {
			int d = width - maxWidth;
			double s = Util.getMillis() * 0.001;
			double l = Math.max(d * 0.5, 3);
			double t = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * s / l)) / 2 + 0.5;
			double o = Mth.lerp(t, 0, d);
			graphics.drawString(font, "", 0, 0, 0, false); // why is this needed what
			graphics.enableScissor(minX, y, maxX, y + font.lineHeight);
			graphics.pose().pushPose();
			graphics.pose().translate(-o, 0, 0);
			graphics.drawString(font, text, minX, y, color, dropShadow);
			graphics.pose().popPose();
			graphics.disableScissor();
		}
		else switch(alignment) {
		case LEFT -> graphics.drawString(font, text, minX, y, color, dropShadow);
		case CENTER -> graphics.drawString(font, text, minX + maxWidth / 2 - width / 2, y, color, dropShadow);
		case RIGHT -> graphics.drawString(font, text, maxX - width, y, color, dropShadow);
		}
	}

	public enum Alignment {
		LEFT, CENTER, RIGHT;
	}
}
