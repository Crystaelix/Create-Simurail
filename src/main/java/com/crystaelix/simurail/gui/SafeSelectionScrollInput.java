package com.crystaelix.simurail.gui;

import java.util.List;

import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class SafeSelectionScrollInput extends SelectionScrollInput {

	private static final Component NO_OPTIONS = Component.translatable("simurail.gui.no_options");

	public SafeSelectionScrollInput(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	@Override
	public SafeSelectionScrollInput forOptions(List<? extends Component> options) {
		super.forOptions(options);
		setState(0);
		return this;
	}

	@Override
	protected void updateTooltip() {
		if(options.isEmpty()) {
			toolTip.clear();
			if(title == null) {
				return;
			}
			toolTip.add(title.plainCopy().withStyle(s -> s.withColor(HEADER_RGB.getRGB())));
			toolTip.add(NO_OPTIONS.plainCopy().withStyle(ChatFormatting.GRAY));
		}
		else {
			super.updateTooltip();
		}
	}
}
