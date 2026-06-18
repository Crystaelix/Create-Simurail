package com.crystaelix.simurail.content.bogey;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import org.lwjgl.opengl.GL11;

import com.crystaelix.simurail.api.bogey.BogeyRenderedType;
import com.crystaelix.simurail.api.bogey.menu.BogeyCategory;
import com.crystaelix.simurail.api.bogey.menu.BogeyDataNumericOption;
import com.crystaelix.simurail.api.bogey.menu.BogeyDataOption;
import com.crystaelix.simurail.api.bogey.menu.BogeyDataOptionValue;
import com.crystaelix.simurail.api.bogey.menu.BogeyDataSelectionOption;
import com.crystaelix.simurail.api.bogey.menu.BogeyDataTextOption;
import com.crystaelix.simurail.api.bogey.menu.BogeyEntry;
import com.crystaelix.simurail.api.bogey.menu.BogeyEntryCategory;
import com.crystaelix.simurail.api.bogey.menu.BogeyMenuItem;
import com.crystaelix.simurail.api.bogey.menu.BogeyMenuManager;
import com.crystaelix.simurail.api.bogey.menu.BogeyMenuSelection;
import com.crystaelix.simurail.api.bogey.menu.BogeyParentCategory;
import com.crystaelix.simurail.api.bogey.menu.TrackTypeEntry;
import com.crystaelix.simurail.content.SimurailBlocks;
import com.crystaelix.simurail.content.SimurailGuiTextures;
import com.crystaelix.simurail.content.SimurailItems;
import com.crystaelix.simurail.gui.SLabel;
import com.crystaelix.simurail.gui.SafeSelectionScrollInput;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.trains.bogey.BogeySizes.BogeySize;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.ScrollInput;

import foundry.veil.api.network.VeilPacketManager;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.TextureSheetSegment;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class PhysicsBogeyMenuScreen extends AbstractSimiScreen {

	public static final SimurailGuiTextures BACKGROUND = SimurailGuiTextures.PHYSICS_BOGEY_MENU;

	public static final Component TITLE = Component.translatable("gui.simurail.physics_bogey.title");
	public static final Component INVERTED_TITLE = Component.translatable("gui.simurail.physics_bogey.title.inverted");

	public static final Component CATEGORY_TITLE = Component.translatable("gui.simurail.physics_bogey.category");
	public static final Component SUBCATEGORY_TITLE = Component.translatable("gui.simurail.physics_bogey.subcategory");
	public static final Component PAGE_TITLE = Component.translatable("gui.simurail.physics_bogey.page");
	public static final Component DATA_OPTION_TITLE = Component.translatable("gui.simurail.physics_bogey.data_option");

	public static final Component AXLE_SPACING_TOOLTIP = Component.translatable("gui.simurail.physics_bogey.axle_spacing").withColor(0x5391E1);
	public static final Component TRACK_TYPE_TOOLTIP = Component.translatable("gui.simurail.physics_bogey.track_type").withColor(0x5391E1);
	public static final Component CONFIRM_TOOLTIP = Component.translatable("create.action.confirm");
	public static final Component OPTIONS_TOOLTIP = Component.translatable("gui.simurail.physics_bogey.options");

	final PhysicsBogeyBlockEntity be;
	final PhysicsBogeyOptions options;
	final boolean inverted;
	final List<BogeyCategory<?>> mainCategories;

	private SafeSelectionScrollInput mainCategoryInput;
	private SLabel mainCategoryLabel;

	private SLabel[] subCategoryLabels = new SLabel[3];
	private SafeSelectionScrollInput[] subCategoryInputs = new SafeSelectionScrollInput[3];

	private SLabel[] entryLabels = new SLabel[6];

	private SafeSelectionScrollInput pageInput;
	private SLabel pageLabel;

	private SLabel selectedEntryLabel;

	private SLabel axleSpacingLabel;

	private SLabel trackTypeLabel;

	private SafeSelectionScrollInput dataOptionInput;
	private SLabel dataOptionLabel;

	private SafeSelectionScrollInput dataOptionValueSelectionInput;
	private ScrollInput dataOptionValueNumericInput;
	private SLabel dataOptionValueLabel;

	private IconButton optionsButton;
	private IconButton confirmButton;

	private ObjectList<BogeyCategory<?>> path = new ObjectArrayList<>();
	private List<BogeyEntry> entries = List.of();
	private int subPathDepth = 0;
	private int entriesPerPage = 6;
	private int pageCount = 1;
	private int currentPage = 0;

	private Optional<BogeyEntry> selectedEntry = Optional.empty();
	private List<BogeyDataOptionValue<?>> optionValues = List.of();
	private int currentOption = 0;

	public PhysicsBogeyMenuScreen(PhysicsBogeyBlockEntity be) {
		this(be, be.getOptions());
	}

	public PhysicsBogeyMenuScreen(PhysicsBogeyBlockEntity be, PhysicsBogeyOptions options) {
		super(be.hasCustomName() ? be.getCustomName() : be.isInverted() ? INVERTED_TITLE : TITLE);
		this.be = be;
		this.options = new PhysicsBogeyOptions().set(options);
		inverted = be.isInverted();
		mainCategories = BogeyMenuManager.getCategories(inverted);

		BogeyMenuSelection menuState = BogeyMenuManager.findEntry(options.type, inverted);
		if(BogeyMenuSelection.EMPTY.equals(menuState)) {
			menuState = BogeyMenuManager.defaultEntry(inverted);
		}

		path.addAll(menuState.path());
		entries = !path.isEmpty() && path.getLast() instanceof BogeyEntryCategory e ? e.children(inverted) : List.of();

		selectedEntry = menuState.entry();
		optionValues = menuState.optionValues();
	}

	@Override
	protected void init() {
		setWindowSize(BACKGROUND.w, BACKGROUND.h);
		super.init();

		int x = guiLeft;
		int y = guiTop;

		mainCategoryLabel = new SLabel(x + 7, y + 20, 120, 18);
		mainCategoryLabel.withMargin(5);
		mainCategoryLabel.withAlignCenter();
		mainCategoryLabel.withShadow();

		mainCategoryInput = new SafeSelectionScrollInput(x + 7, y + 20, 120, 18);
		mainCategoryInput.titled(CATEGORY_TITLE.plainCopy());
		mainCategoryInput.forOptions(Lists.transform(mainCategories, BogeyMenuItem::displayName));
		mainCategoryInput.writingTo(mainCategoryLabel);
		mainCategoryInput.calling(this::changeMainCategory);

		for(int i = 0; i < 3; ++i) {
			subCategoryLabels[i] = new SLabel(x + 7, y + 38 + 18 * i, 120, 18);
			subCategoryLabels[i].withMargin(5);
			subCategoryLabels[i].withAlignCenter();
			subCategoryLabels[i].withShadow();

			subCategoryInputs[i] = new SafeSelectionScrollInput(x + 7, y + 38 + 18 * i, 120, 18);
			subCategoryInputs[i].titled(SUBCATEGORY_TITLE.plainCopy());
			subCategoryInputs[i].writingTo(subCategoryLabels[i]);
			subCategoryInputs[i].calling(changeSubCategory(i));
		}

		for(int i = 0; i < 6; ++i) {
			entryLabels[i] = new SLabel(x + 7, y + 40 + 18 * i, 120, 18);
			entryLabels[i].withMargin(3);
			entryLabels[i].withAlignCenter();
			entryLabels[i].withShadow();
			entryLabels[i].withOverflowTooltip();
			entryLabels[i].withClickSound();
		}

		pageLabel = new SLabel(x + 25, y + 150, 84, 18);
		pageLabel.withMargin(5);
		pageLabel.withAlignCenter();
		pageLabel.withShadow();

		pageInput = new SafeSelectionScrollInput(x + 25, y + 150, 84, 18);
		pageInput.titled(PAGE_TITLE.plainCopy());
		pageInput.writingTo(pageLabel);
		pageInput.calling(this::changePage);

		selectedEntryLabel = new SLabel(x + 138, y + 20, 150, 18);
		selectedEntryLabel.withMargin(3);
		selectedEntryLabel.withAlignCenter();
		selectedEntryLabel.withShadow();
		selectedEntryLabel.withOverflowTooltip();
		selectedEntryLabel.text = CommonComponents.EMPTY;

		axleSpacingLabel = new SLabel(x + 156, y + 38, 42, 18);
		axleSpacingLabel.withMargin(3);
		axleSpacingLabel.withAlignCenter();
		axleSpacingLabel.withShadow();
		axleSpacingLabel.withTooltip(List.of(AXLE_SPACING_TOOLTIP));
		axleSpacingLabel.text = CommonComponents.EMPTY;

		trackTypeLabel = new SLabel(x + 216, y + 38, 72, 18);
		trackTypeLabel.withMargin(3);
		trackTypeLabel.withAlignCenter();
		trackTypeLabel.withShadow();
		trackTypeLabel.withTooltip(List.of(TRACK_TYPE_TOOLTIP));
		trackTypeLabel.text = CommonComponents.EMPTY;

		dataOptionLabel = new SLabel(x + 138, y + 150, 42, 18);
		dataOptionLabel.withMargin(5);
		dataOptionLabel.withAlignCenter();
		dataOptionLabel.withShadow();

		dataOptionInput = new SafeSelectionScrollInput(x + 138, y + 150, 42, 18);
		dataOptionInput.titled(DATA_OPTION_TITLE.plainCopy());
		dataOptionInput.writingTo(dataOptionLabel);
		dataOptionInput.calling(this::changeDataOption);

		dataOptionValueLabel = new SLabel(x + 186, y + 150, 102, 18);
		dataOptionValueLabel.withMargin(5);
		dataOptionValueLabel.withAlignCenter();
		dataOptionValueLabel.withShadow();

		dataOptionValueSelectionInput = new SafeSelectionScrollInput(x + 186, y + 150, 102, 18);
		dataOptionValueSelectionInput.writingTo(dataOptionValueLabel);

		dataOptionValueNumericInput = new ScrollInput(x + 186, y + 150, 102, 18);
		dataOptionValueNumericInput.writingTo(dataOptionValueLabel);

		optionsButton = new IconButton(x + 7, y + 179, AllIcons.I_CONFIG_OPEN);
		optionsButton.setToolTip(OPTIONS_TOOLTIP);
		optionsButton.withCallback(this::openOptionsScreen);

		confirmButton = new IconButton(x + 270, y + 179, AllIcons.I_CONFIRM);
		confirmButton.setToolTip(CONFIRM_TOOLTIP);
		confirmButton.withCallback(this::onConfirm);

		addRenderableWidget(mainCategoryInput);
		addRenderableWidget(mainCategoryLabel);

		addRenderableWidget(pageInput);
		addRenderableWidget(pageLabel);

		addRenderableWidget(selectedEntryLabel);

		addRenderableWidget(axleSpacingLabel);

		addRenderableWidget(trackTypeLabel);

		addRenderableWidget(optionsButton);
		addRenderableWidget(confirmButton);

		updateMenuItemWidgets();
		updateSelectedEntryWidgets();
	}

	private void updateMenuItemWidgets() {
		removeWidgets(subCategoryInputs);
		removeWidgets(subCategoryLabels);

		subPathDepth = Math.clamp(path.size() - 1, 0, 3);
		entriesPerPage = 6 - subPathDepth;
		pageCount = Math.max(1, Math.ceilDiv(entries.size(), entriesPerPage));
		currentPage = 0;

		pageInput.forOptions(IntStream.rangeClosed(1, pageCount).
				mapToObj(i -> Component.translatable("gui.simurail.physics_bogey.page_format", i)).
				toList());
		pageInput.setState(currentPage);

		mainCategoryInput.setState(mainCategories.indexOf(path.get(0)));

		for(int i = 0; i < subPathDepth; ++i) {
			List<? extends BogeyMenuItem> children = path.get(i).children(inverted);
			subCategoryInputs[i].forOptions(Lists.transform(children, BogeyMenuItem::displayName));
			subCategoryInputs[i].setState(children.indexOf(path.get(i + 1)));
			addRenderableWidget(subCategoryInputs[i]);
			addRenderableWidget(subCategoryLabels[i]);
		}

		updateEntryWidgets();
	}

	private void updateEntryWidgets() {
		removeWidgets(entryLabels);

		for(int i = 0; i < entriesPerPage; ++i) {
			int n = currentPage * entriesPerPage + i;
			if(n >= entries.size()) {
				break;
			}
			int j = subPathDepth + i;
			entryLabels[j].withCallback(changeSelectedEntry(n));
			entryLabels[j].text = entries.get(n).displayName();
			addRenderableWidget(entryLabels[j]);
		}
	}

	private void updateSelectedEntryWidgets() {
		removeWidget(dataOptionInput);
		removeWidget(dataOptionLabel);

		if(selectedEntry.isPresent()) {
			BogeyEntry entry = selectedEntry.get();

			selectedEntryLabel.text = entry.displayName();
			axleSpacingLabel.text = Component.literal(String.valueOf(entry.type().wheelSpacing()));

			List<TrackTypeEntry> trackTypes = BogeyMenuManager.getTrackTypeEntries(entry.type());
			trackTypeLabel.text = ComponentUtils.formatList(trackTypes, Component.literal("/"), trackTypes.size() > 1 ? TrackTypeEntry::shortName : TrackTypeEntry::displayName);
			List<Component> trackTypeTooltip = new ArrayList<>(trackTypes.size() + 1);
			trackTypeTooltip.add(TRACK_TYPE_TOOLTIP);
			for(TrackTypeEntry trackType : trackTypes) {
				trackTypeTooltip.add(trackType.displayName().plainCopy().withStyle(ChatFormatting.GRAY));
			}
			trackTypeLabel.withTooltip(trackTypeTooltip);

			if(!optionValues.isEmpty()) {
				dataOptionInput.forOptions(Lists.transform(entry.options(), BogeyDataOption::displayName));
				dataOptionInput.setState(currentOption);
				addRenderableWidget(dataOptionInput);
				addRenderableWidget(dataOptionLabel);
			}
		}
		else {
			selectedEntryLabel.text = CommonComponents.EMPTY;
			axleSpacingLabel.text = CommonComponents.EMPTY;
			trackTypeLabel.text = CommonComponents.EMPTY;
			trackTypeLabel.withTooltip(List.of(TRACK_TYPE_TOOLTIP));
		}

		updateDataOptionWidgets();
	}

	private void updateDataOptionWidgets() {
		removeWidget(dataOptionValueSelectionInput);
		removeWidget(dataOptionValueNumericInput);
		removeWidget(dataOptionValueLabel);

		if(!optionValues.isEmpty()) {
			BogeyDataOptionValue<?> optionValue = optionValues.get(currentOption);
			switch(optionValue.option()) {
			case BogeyDataSelectionOption selection -> {
				BogeyDataOptionValue<Integer> selectionValue = (BogeyDataOptionValue<Integer>)optionValue;
				dataOptionValueSelectionInput.titled(selection.displayName().plainCopy());
				dataOptionValueSelectionInput.forOptions(selection.options());
				dataOptionValueSelectionInput.setState(selectionValue.value());
				dataOptionValueSelectionInput.calling(i -> {
					selectionValue.value(i);
					updateType();
				});
				addRenderableWidget(dataOptionValueSelectionInput);
				addRenderableWidget(dataOptionValueLabel);
			}
			case BogeyDataNumericOption numeric -> {
				BogeyDataOptionValue<Integer> numericValue = (BogeyDataOptionValue<Integer>)optionValue;
				dataOptionValueNumericInput.titled(numeric.displayName().plainCopy());
				dataOptionValueNumericInput.withRange(numeric.min(), numeric.max() + 1);
				dataOptionValueNumericInput.withStepFunction(numeric::applyStep);
				dataOptionValueNumericInput.setState(numericValue.value());
				dataOptionValueNumericInput.calling(i -> {
					numericValue.value(i);
					updateType();
				});
				addRenderableWidget(dataOptionValueNumericInput);
				addRenderableWidget(dataOptionValueLabel);
			}
			case BogeyDataTextOption text -> {
				// TODO not used by anything yet
			}
			}
		}
	}

	private void updateCategoryPath() {
		while(path.getLast() instanceof BogeyParentCategory p) {
			path.add(p.children(inverted).get(0));
		}
		if(path.getLast() instanceof BogeyEntryCategory e) {
			entries = e.children(inverted);
		}
		else {
			entries = List.of();
		}
		updateMenuItemWidgets();
	}

	private void changeMainCategory(int index) {
		path.clear();
		path.add(BogeyMenuManager.getCategories().get(index));
		updateCategoryPath();
	}

	private Consumer<Integer> changeSubCategory(int depth) {
		return i -> {
			path.removeElements(depth + 1, path.size());
			if(path.getLast() instanceof BogeyParentCategory p) {
				path.add(p.children(inverted).get(i));
			}
			updateCategoryPath();
		};
	}

	private void changePage(int page) {
		currentPage = page;
		updateEntryWidgets();
	}

	private Runnable changeSelectedEntry(int index) {
		return () -> {
			BogeyEntry entry = entries.get(index);
			selectedEntry = Optional.of(entry);
			optionValues = entry.options().stream().<BogeyDataOptionValue<?>>map(BogeyDataOptionValue::new).toList();
			currentOption = 0;
			updateSelectedEntryWidgets();
			updateType();
		};
	}

	private void changeDataOption(int index) {
		currentOption = index;
		updateDataOptionWidgets();
	}

	@Override
	protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		int x = guiLeft;
		int y = guiTop;

		BACKGROUND.render(graphics, x, y);

		for(int i = 0; i < subPathDepth; ++i) {
			SimurailGuiTextures.PHYSICS_BOGEY_MENU_CATEGORY.render(graphics, x + 7, y + 38 + i * 18);
		}

		for(int i = 0; i < entriesPerPage; ++i) {
			int n = currentPage * entriesPerPage + i;
			if(n >= entries.size()) {
				break;
			}
			int j = subPathDepth + i;
			SimurailGuiTextures.PHYSICS_BOGEY_MENU_ENTRY.render(graphics, x + 7, y + 40 + j * 18);
		}

		if(!optionValues.isEmpty()) {
			SimurailGuiTextures.PHYSICS_BOGEY_MENU_OPTION.render(graphics, x + 137, y + 149);
			if(optionValues.get(currentOption).option() instanceof BogeyDataTextOption) {
				SimurailGuiTextures.PHYSICS_BOGEY_MENU_OPTION_TEXT_VALUE.render(graphics, x + 185, y + 149);
			}
			else {
				SimurailGuiTextures.PHYSICS_BOGEY_MENU_OPTION_SCROLL_VALUE.render(graphics, x + 185, y + 149);
			}
		}

		graphics.drawString(font, title, x + (BACKGROUND.w - 8) / 2 - font.width(title) / 2, y + 4, 0x592424, false);
		renderBlock(graphics, mouseX, mouseY, partialTicks, guiLeft, guiTop, BACKGROUND);

		graphics.enableScissor(x + 139, y + 67, x + 287, y + 139);
		renderPreview(graphics, partialTicks);
		graphics.disableScissor();
	}

	private void renderBlock(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, int guiLeft, int guiTop, TextureSheetSegment background) {
		GuiGameElement.GuiRenderBuilder builder = GuiGameElement.of(inverted ? SimurailItems.INVERTED_PHYSICS_BOGEY : SimurailBlocks.PHYSICS_BOGEY);
		builder.at(guiLeft + background.getWidth() + 6, guiTop + background.getHeight() - 56, -200);
		builder.scale(5);
		builder.render(graphics);
	}

	private CompoundTag data;

	// Rendering code from Blocks & Bogies
	private void renderPreview(GuiGraphics graphics, float partialTicks) {
		if(selectedEntry.isEmpty()) {
			return;
		}

		BogeyRenderedType type = options.type;

		if(data == null) {
			data = type.data(inverted);
		}

		BogeyStyle style = type.style();
		BogeySize size = type.size();

		PoseStack poseStack = graphics.pose();
		poseStack.pushPose();

		float scale = 20;
		poseStack.translate(guiLeft + 213, guiTop + 103, 1500);
		poseStack.scale(1, 1, -1);
		poseStack.translate(0, 0, 1000);
		poseStack.scale(scale, scale, scale);

		float rotationX = inverted ? 20 : -20;
		float rotationY = -135;
		poseStack.mulPose(Axis.ZP.rotationDegrees(180));
		poseStack.mulPose(Axis.XP.rotationDegrees(rotationX));
		poseStack.mulPose(Axis.YP.rotationDegrees(rotationY));

		poseStack.translate(0, inverted ? -0.5F : 0.5F, 0);

		Lighting.setupForEntityInInventory(Axis.XP.rotationDegrees(-30));

		MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
		int light = 0xF000F0;
		int overlay = OverlayTexture.NO_OVERLAY;

		float time = Util.getMillis() * 0.001F;

		float speed = 2;
		float distance = time * speed;
		double wheelAngle = distance / type.wheelRadius();
		float wheelAngleDeg = (float)Math.toDegrees(wheelAngle) % 360;

		CachedBuffers.block(be.getBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)).
		translate(-0.5F, -0.5F, -0.5F).
		light(light).overlay(overlay).
		renderInto(poseStack, bufferSource.getBuffer(RenderType.cutoutMipped()));

		CachedBuffers.block(KineticBlockEntityRenderer.shaft(Direction.Axis.Z)).
		translate(-0.5F, -0.5F, -0.5F).
		light(light).overlay(overlay).
		renderInto(poseStack, bufferSource.getBuffer(RenderType.cutoutMipped()));

		TrackTypeEntry renderEntry = BogeyMenuManager.getRenderTrackTypeEntry(type.type(), inverted);
		BlockState trackState = renderEntry.trackState().get();
		for(int i = -8; i < 8; ++i) {
			CachedBuffers.block(trackState).
			translate(-0.5F, inverted ? 0.5F : -1.5F, i - 0.5F - distance % 1).
			light(light).overlay(overlay).
			renderInto(poseStack, bufferSource.getBuffer(RenderType.cutoutMipped()));
		}

		style.render(size, partialTicks, poseStack, bufferSource, light, overlay, wheelAngleDeg, data, false);

		bufferSource.endBatch();
		poseStack.popPose();

		RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, false);
	}

	private void updateType() {
		if(selectedEntry.isPresent()) {
			BogeyEntry entry = selectedEntry.get();
			CompoundTag extra = new CompoundTag();
			for(BogeyDataOptionValue<?> optionValue : optionValues) {
				optionValue.write(extra);
			}
			options.type = new BogeyRenderedType(entry.type(), extra);
			data = null;
		}
	}

	private void openOptionsScreen() {
		minecraft.setScreen(new PhysicsBogeyOptionsScreen(be, options));
	}

	private void onConfirm() {
		be.renderPivotOffset.step();
		be.renderPivotRot.step();
		be.setOptions(options);
		VeilPacketManager.server().sendPacket(new PhysicsBogeySetOptionsPacket(be.getBlockPos(), options));
		onClose();
	}
}
