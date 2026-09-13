package com.crystaelix.simurail.content.automatic_coupler.copycat;

import java.util.List;

import com.crystaelix.simurail.content.SimurailBlocks;
import com.crystaelix.simurail.content.SimurailGuiTextures;
import com.crystaelix.simurail.content.gangway_frame.GangwayFrameBlockShape;
import com.crystaelix.simurail.gui.SLabel;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.ScrollInput;

import foundry.veil.api.network.VeilPacketManager;
import net.createmod.catnip.gui.TextureSheetSegment;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class CopycatAutomaticCouplerGangwayScreen extends CopycatAutomaticCouplerBaseScreen {

	public static final SimurailGuiTextures BACKGROUND = SimurailGuiTextures.GANGWAY_FRAME_NO_SHAPE;

	public static final Component SHAPE_TITLE = Component.translatable("gui.simurail.gangway_frame.shape");
	public static final Component REST_LENGTH_TITLE = Component.translatable("gui.simurail.gangway_frame.rest_length");

	public static final Component CONFIRM_TOOLTIP = Component.translatable("create.action.confirm");

	public static final List<GangwayFrameBlockShape> OPTIONS = List.of(GangwayFrameBlockShape.D, GangwayFrameBlockShape.U);

	final BlockPos pos;
	float restLength;

	private SLabel restLengthLabel;

	private ScrollInput restLengthInput;

	private IconButton confirmButton;

	public CopycatAutomaticCouplerGangwayScreen(CopycatAutomaticCouplerMenu menu, Component title) {
		super(menu, title);
		pos = menu.pos;
		restLength = menu.gangwayRestLength;
	}

	@Override
	protected void init() {
		setWindowSize(BACKGROUND.w, BACKGROUND.h);
		super.init();

		int x = guiLeft;
		int y = guiTop;

		restLengthLabel = new SLabel(x + 45, y + 23, 109, 18);
		restLengthLabel.withMargin(5);
		restLengthLabel.withShadow();

		restLengthInput = new ScrollInput(x + 45, y + 23, 109, 18);
		restLengthInput.withRange(0, 15);
		restLengthInput.withShiftStep(4);
		restLengthInput.titled(REST_LENGTH_TITLE.plainCopy());
		restLengthInput.format(i -> Component.literal(String.valueOf(i * 0.0625 + 0.125)));
		restLengthInput.writingTo(restLengthLabel);
		restLengthInput.setState((int)(restLength * 16));
		restLengthInput.calling(i -> restLength = i * 0.0625F);

		confirmButton = new IconButton(x + 155, y + 55, AllIcons.I_CONFIRM);
		confirmButton.setToolTip(CONFIRM_TOOLTIP);
		confirmButton.withCallback(this::onConfirm);

		addRenderableWidget(restLengthInput);

		addRenderableWidget(restLengthLabel);

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
		GuiGameElement.GuiRenderBuilder builder = GuiGameElement.of(SimurailBlocks.GANGWAY_FRAME);
		builder.at(guiLeft + background.getWidth() + 6, guiTop + background.getHeight() - 56, -200);
		builder.scale(5);
		builder.render(graphics);
	}

	private void onConfirm() {
		if(minecraft.level.getBlockEntity(pos) instanceof CopycatAutomaticCouplerBlockEntity be) {
			be.gangwayRestLength = restLength;
		}
		VeilPacketManager.server().sendPacket(new CopycatAutomaticCouplerGangwayOptionsPacket(pos, restLength));
		onClose();
	}
}
