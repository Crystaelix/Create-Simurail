package com.crystaelix.simurail.content.probe_reader;

import java.util.List;

import com.crystaelix.simurail.content.SimurailBlocks;
import com.crystaelix.simurail.content.SimurailGuiTextures;
import com.crystaelix.simurail.gui.SLabel;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;

import foundry.veil.api.network.VeilPacketManager;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.TextureSheetSegment;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ProbeReaderScreen extends AbstractSimiScreen implements MenuAccess<ProbeReaderMenu> {

	public static final SimurailGuiTextures BACKGROUND = SimurailGuiTextures.PROBE_READER;

	public static final Component MODE_TITLE = Component.translatable("gui.simurail.probe_reader.mode");
	public static final Component FILTER_TITLE = Component.translatable("gui.simurail.probe_reader.filter");
	public static final Component MIN_TITLE = Component.translatable("gui.simurail.probe_reader.min");
	public static final Component MAX_TITLE = Component.translatable("gui.simurail.probe_reader.max");

	public static final List<Component> MODE_OPTIONS = List.of(
			Component.translatable("gui.simurail.probe_reader.mode.occupied_signal"),
			Component.translatable("gui.simurail.probe_reader.mode.aligned_signal"),
			Component.translatable("gui.simurail.probe_reader.mode.opposite_signal"),
			Component.translatable("gui.simurail.probe_reader.mode.signal"),
			Component.translatable("gui.simurail.probe_reader.mode.station"),
			Component.translatable("gui.simurail.probe_reader.mode.powered_station"),
			Component.translatable("gui.simurail.probe_reader.mode.discontinuity"));

	public static final Component COMPUTER_TOOLTIP = Component.translatable("gui.simurail.controlled_by_computer");
	public static final Component CONFIRM_TOOLTIP = Component.translatable("create.action.confirm");

	final ProbeReaderMenu menu;

	final BlockPos pos;
	final ProbeReaderOptions options;

	private SLabel modeLabel;
	private SLabel filterLabel;
	private SLabel minLabel;
	private SLabel maxLabel;

	private SelectionScrollInput modeInput;
	private EditBox filterBox;
	private ScrollInput minInput;
	private ScrollInput maxInput;

	private IconButton confirmButton;

	public ProbeReaderScreen(ProbeReaderMenu menu, Inventory inv, Component title) {
		super(title);
		this.menu = menu;
		pos = menu.pos;
		options = menu.options;
	}

	@Override
	public ProbeReaderMenu getMenu() {
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

		filterLabel = new SLabel(x + 45, y + 45, 109, 18);
		filterLabel.text = CommonComponents.EMPTY;

		minLabel = new SLabel(x + 45, y + 67, 39, 18);
		minLabel.withMargin(5);
		minLabel.withShadow();

		maxLabel = new SLabel(x + 115, y + 67, 39, 18);
		maxLabel.withMargin(5);
		maxLabel.withShadow();

		modeInput = new SelectionScrollInput(x + 45, y + 23, 109, 18);
		modeInput.forOptions(MODE_OPTIONS);
		modeInput.titled(MODE_TITLE.plainCopy());
		modeInput.writingTo(modeLabel);
		modeInput.setState(options.mode.ordinal());
		modeInput.calling(i -> options.mode = ProbeReaderMode.BY_ID.apply(i));

		filterBox = new EditBox(font, x + 50, y + 50, 99, 10, CommonComponents.EMPTY);
		filterBox.setTextColor(-1);
		filterBox.setBordered(false);
		filterBox.setFocused(false);
		filterBox.setHint(Component.literal("*"));
		filterBox.setValue(options.getFilter());
		filterBox.setResponder(options::setFilter);

		minInput = new ScrollInput(x + 45, y + 67, 39, 18);
		minInput.withRange(0, 256 * 2 + 1);
		minInput.withShiftStep(8);
		minInput.titled(MIN_TITLE.plainCopy());
		minInput.format(i -> Component.literal(String.valueOf(i * 0.5F)));
		minInput.writingTo(minLabel);
		minInput.setState(Math.round(options.getMinDistance() * 2));
		minInput.calling(i -> {
			float distance = i * 0.5F;
			options.setMinDistance(distance);
			if(options.getMaxDistance() < distance) {
				options.setMaxDistance(distance);
				maxInput.setState(i);
			}
		});

		maxInput = new ScrollInput(x + 115, y + 67, 39, 18);
		maxInput.withRange(0, 256 * 2 + 1);
		maxInput.withShiftStep(8);
		maxInput.titled(MAX_TITLE.plainCopy());
		maxInput.format(i -> Component.literal(String.valueOf(i * 0.5F)));
		maxInput.writingTo(maxLabel);
		maxInput.setState(Math.round(options.getMaxDistance() * 2));
		maxInput.calling(i -> {
			float distance = i * 0.5F;
			options.setMaxDistance(i * 0.5F);
			if(options.getMinDistance() > distance) {
				options.setMinDistance(distance);
				minInput.setState(i);
			}
		});

		confirmButton = new IconButton(x + 155, y + 99, AllIcons.I_CONFIRM);
		confirmButton.setToolTip(CONFIRM_TOOLTIP);
		confirmButton.withCallback(this::onConfirm);

		if(!menu.hasComputer) {
			addRenderableWidget(modeInput);
			addRenderableWidget(minInput);
			addRenderableWidget(maxInput);
			filterLabel.withTooltip(List.of(FILTER_TITLE.plainCopy().withColor(0x5391E1)));
		}
		else {
			filterBox.setEditable(false);
			modeLabel.withTooltip(List.of(
					MODE_TITLE.plainCopy().withColor(0x5391E1),
					COMPUTER_TOOLTIP.plainCopy().withColor(0x96B7E0)));
			filterLabel.withTooltip(List.of(
					FILTER_TITLE.plainCopy().withColor(0x5391E1),
					COMPUTER_TOOLTIP.plainCopy().withColor(0x96B7E0)));
			minLabel.withTooltip(List.of(
					MIN_TITLE.plainCopy().withColor(0x5391E1),
					COMPUTER_TOOLTIP.plainCopy().withColor(0x96B7E0)));
			maxLabel.withTooltip(List.of(
					MAX_TITLE.plainCopy().withColor(0x5391E1),
					COMPUTER_TOOLTIP.plainCopy().withColor(0x96B7E0)));
		}

		addRenderableWidget(filterBox);

		addRenderableWidget(modeLabel);
		addRenderableWidget(filterLabel);
		addRenderableWidget(minLabel);
		addRenderableWidget(maxLabel);

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
		GuiGameElement.GuiRenderBuilder builder = GuiGameElement.of(SimurailBlocks.PROBE_READER);
		builder.at(guiLeft + background.getWidth() + 6, guiTop + background.getHeight() - 56, -200);
		builder.scale(5);
		builder.render(graphics);
	}

	private void onConfirm() {
		VeilPacketManager.server().sendPacket(new ProbeReaderOptionsPacket(options));
		onClose();
	}
}
