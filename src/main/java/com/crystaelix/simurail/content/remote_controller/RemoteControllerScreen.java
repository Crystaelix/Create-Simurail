package com.crystaelix.simurail.content.remote_controller;

import java.util.List;

import com.crystaelix.simurail.content.SimurailBlocks;
import com.crystaelix.simurail.content.SimurailGuiTextures;
import com.crystaelix.simurail.gui.SLabel;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;

import foundry.veil.api.network.VeilPacketManager;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.TextureSheetSegment;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class RemoteControllerScreen extends AbstractSimiScreen implements MenuAccess<RemoteControllerMenu> {

	public static final SimurailGuiTextures BACKGROUND = SimurailGuiTextures.REMOTE_CONTROLLER;

	public static final Component MODE_TITLE = Component.translatable("gui.simurail.remote_controller.mode");

	public static final List<Component> MODE_OPTIONS = List.of(
			Component.translatable("gui.simurail.remote_controller.mode.braking"),
			Component.translatable("gui.simurail.remote_controller.mode.braking_inverted"),
			Component.translatable("gui.simurail.remote_controller.mode.steering_left"),
			Component.translatable("gui.simurail.remote_controller.mode.steering_right"),
			Component.translatable("gui.simurail.remote_controller.mode.vertical_movement"),
			Component.translatable("gui.simurail.remote_controller.mode.enabled"));

	public static final Component COMPUTER_TOOLTIP = Component.translatable("gui.simurail.controlled_by_computer");
	public static final Component CONFIRM_TOOLTIP = Component.translatable("create.action.confirm");

	final RemoteControllerMenu menu;

	final BlockPos pos;
	RemoteControllerMode mode;

	private SLabel modeLabel;

	private SelectionScrollInput modeInput;

	private IconButton confirmButton;

	public RemoteControllerScreen(RemoteControllerMenu menu, Inventory inv, Component title) {
		super(title);
		this.menu = menu;
		pos = menu.pos;
		mode = menu.mode;
	}

	@Override
	public RemoteControllerMenu getMenu() {
		return menu;
	}

	@Override
	protected void init() {
		setWindowSize(BACKGROUND.w, BACKGROUND.h);
		super.init();

		int x = guiLeft;
		int y = guiTop;

		modeLabel = new SLabel(x + 45, y + 23, 109, 18);
		modeLabel.withMargin(5);
		modeLabel.withShadow();

		modeInput = new SelectionScrollInput(x + 45, y + 23, 109, 18);
		modeInput.forOptions(MODE_OPTIONS);
		modeInput.titled(MODE_TITLE.plainCopy());
		modeInput.writingTo(modeLabel);
		modeInput.setState(mode.ordinal());
		modeInput.calling(i -> mode = RemoteControllerMode.BY_ID.apply(i));

		confirmButton = new IconButton(x + 155, y + 55, AllIcons.I_CONFIRM);
		confirmButton.setToolTip(CONFIRM_TOOLTIP);
		confirmButton.withCallback(this::onConfirm);

		if(!menu.hasComputer) {
			addRenderableWidget(modeInput);
		}
		else {
			modeLabel.withTooltip(List.of(
					MODE_TITLE.plainCopy().withColor(0x5391E1),
					COMPUTER_TOOLTIP.plainCopy().withColor(0x96B7E0)));
		}

		addRenderableWidget(modeLabel);

		addRenderableWidget(confirmButton);
	}

	@Override
	protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		int x = guiLeft;
		int y = guiTop;

		BACKGROUND.render(graphics, x, y);
		graphics.drawString(font, title, x + (BACKGROUND.w - 8) / 2 - font.width(title) / 2, y + 4, 0x592424, false);
		renderBlock(graphics, mouseX, mouseY, partialTicks, guiLeft, guiTop, BACKGROUND);
	}

	private void renderBlock(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, int guiLeft, int guiTop, TextureSheetSegment background) {
		GuiGameElement.GuiRenderBuilder builder = GuiGameElement.of(SimurailBlocks.REMOTE_CONTROLLER);
		builder.at(guiLeft + background.getWidth() + 6, guiTop + background.getHeight() - 56, -200);
		builder.scale(5);
		builder.render(graphics);
	}

	private void onConfirm() {
		VeilPacketManager.server().sendPacket(new RemoteControllerModePacket(mode));
		onClose();
	}
}
