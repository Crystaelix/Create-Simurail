package com.crystaelix.simurail.content.bogey;

import java.util.List;

import com.crystaelix.simurail.content.SimurailBlocks;
import com.crystaelix.simurail.content.SimurailGuiTextures;
import com.crystaelix.simurail.content.SimurailItems;
import com.crystaelix.simurail.gui.SLabel;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;

import foundry.veil.api.network.VeilPacketManager;
import net.createmod.catnip.gui.TextureSheetSegment;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class PhysicsBogeyOptionsScreen extends PhysicsBogeyBaseScreen {

	public static final SimurailGuiTextures BACKGROUND = SimurailGuiTextures.PHYSICS_BOGEY_OPTIONS;

	public static final Component PHYSICS_TITLE = Component.translatable("gui.simurail.physics_bogey.physics");
	public static final Component ROTATION_TITLE = Component.translatable("gui.simurail.physics_bogey.rotation");
	public static final Component OFFSET_TITLE = Component.translatable("gui.simurail.physics_bogey.offset");
	public static final Component VERTICAL_TITLE = Component.translatable("gui.simurail.physics_bogey.vertical");
	public static final Component AXLE_TITLE = Component.translatable("gui.simurail.physics_bogey.axle");
	public static final Component CONTROL_TITLE = Component.translatable("gui.simurail.physics_bogey.control");
	public static final Component STRESS_TITLE = Component.translatable("gui.simurail.physics_bogey.stress");
	public static final Component TILT_TITLE = Component.translatable("gui.simurail.physics_bogey.tilt");
	public static final Component CONNECTOR_TITLE = Component.translatable("gui.simurail.physics_bogey.connector");

	public static final List<Component> PHYSICS_OPTIONS = List.of(
			Component.translatable("gui.simurail.physics_bogey.physics.enabled"),
			Component.translatable("gui.simurail.physics_bogey.physics.disabled"));
	public static final List<Component> ROTATION_OPTIONS = List.of(
			Component.translatable("gui.simurail.physics_bogey.rotation.disallow"),
			Component.translatable("gui.simurail.physics_bogey.rotation.allow"),
			Component.translatable("gui.simurail.physics_bogey.rotation.yaw"),
			Component.translatable("gui.simurail.physics_bogey.rotation.pitch"));
	public static final List<Component> OFFSET_OPTIONS = List.of(
			Component.translatable("gui.simurail.physics_bogey.offset.disallow"),
			Component.translatable("gui.simurail.physics_bogey.offset.allow"),
			Component.translatable("gui.simurail.physics_bogey.offset.lateral"),
			Component.translatable("gui.simurail.physics_bogey.offset.vertical"));
	public static final List<Component> VERTICAL_OPTIONS = List.of(
			Component.translatable("gui.simurail.physics_bogey.vertical.disallow"),
			Component.translatable("gui.simurail.physics_bogey.vertical.allow"));
	public static final List<Component> VERTICAL_OPTIONS_INVERTED = List.of(
			Component.translatable("gui.simurail.physics_bogey.vertical.disallow"));
	public static final List<Component> CONTROL_OPTIONS = List.of(
			Component.translatable("gui.simurail.physics_bogey.control.braking"),
			Component.translatable("gui.simurail.physics_bogey.control.braking_inverted"),
			Component.translatable("gui.simurail.physics_bogey.control.strength"),
			Component.translatable("gui.simurail.physics_bogey.control.strength_inverted"),
			Component.translatable("gui.simurail.physics_bogey.control.none"));
	public static final List<Component> CONNECTOR_OPTIONS = List.of(
			Component.translatable("gui.simurail.physics_bogey.connector.visible"),
			Component.translatable("gui.simurail.physics_bogey.connector.invisible"),
			Component.translatable("gui.simurail.physics_bogey.connector.front"),
			Component.translatable("gui.simurail.physics_bogey.connector.back"));

	public static final Component COMPUTER_TOOLTIP = Component.translatable("gui.simurail.physics_bogey.controlled_by_computer");
	public static final Component TYPE_TOOLTIP = Component.translatable("gui.simurail.physics_bogey.type");
	public static final Component CONFIRM_TOOLTIP = Component.translatable("create.action.confirm");

	final BlockPos pos;
	final PhysicsBogeyOptions options;
	final boolean inverted;

	private SLabel physicsLabel;
	private SLabel rotationLabel;
	private SLabel offsetLabel;
	private SLabel verticalLabel;
	private SLabel axleLabel;
	private SLabel controlLabel;
	private SLabel stressLabel;
	private SLabel tiltLabel;
	private SLabel connectorLabel;

	private SelectionScrollInput physicsInput;
	private SelectionScrollInput rotationInput;
	private SelectionScrollInput offsetInput;
	private SelectionScrollInput verticalInput;
	private ScrollInput axleInput;
	private SelectionScrollInput controlInput;
	private ScrollInput stressInput;
	private ScrollInput tiltInput;
	private SelectionScrollInput connectorInput;

	private IconButton bogeyButton;
	private IconButton confirmButton;

	public PhysicsBogeyOptionsScreen(PhysicsBogeyMenu menu, Component title) {
		super(menu, title);
		pos = menu.pos;
		options = menu.options;
		inverted = menu.inverted;
	}

	@Override
	protected void init() {
		setWindowSize(BACKGROUND.w, BACKGROUND.h);
		super.init();

		int x = guiLeft;
		int y = guiTop;

		physicsLabel = new SLabel(x + 31, y + 20, 109, 18);
		physicsLabel.withMargin(5);
		physicsLabel.withShadow();

		rotationLabel = new SLabel(x + 31, y + 42, 109, 18);
		rotationLabel.withMargin(5);
		rotationLabel.withShadow();

		offsetLabel = new SLabel(x + 171, y + 42, 109, 18);
		offsetLabel.withMargin(5);
		offsetLabel.withShadow();

		verticalLabel = new SLabel(x + 31, y + 64, 109, 18);
		verticalLabel.withMargin(5);
		verticalLabel.withShadow();

		axleLabel = new SLabel(x + 171, y + 64, 109, 18);
		axleLabel.withMargin(5);
		axleLabel.withShadow();

		controlLabel = new SLabel(x + 31, y + 86, 109, 18);
		controlLabel.withMargin(5);
		controlLabel.withShadow();

		stressLabel = new SLabel(x + 31, y + 108, 109, 18);
		stressLabel.withMargin(5);
		stressLabel.withShadow();

		tiltLabel = new SLabel(x + 171, y + 108, 109, 18);
		tiltLabel.withMargin(5);
		tiltLabel.withShadow();

		connectorLabel = new SLabel(x + 31, y + 130, 109, 18);
		connectorLabel.withMargin(5);
		connectorLabel.withShadow();

		physicsInput = new SelectionScrollInput(x + 31, y + 20, 109, 18);
		physicsInput.forOptions(PHYSICS_OPTIONS);
		physicsInput.titled(PHYSICS_TITLE.plainCopy());
		physicsInput.writingTo(physicsLabel);
		physicsInput.setState(options.enabled ? 0 : 1);
		physicsInput.calling(i -> options.enabled = i != 1);

		rotationInput = new SelectionScrollInput(x + 31, y + 42, 109, 18);
		rotationInput.forOptions(ROTATION_OPTIONS);
		rotationInput.titled(ROTATION_TITLE.plainCopy());
		rotationInput.writingTo(rotationLabel);
		rotationInput.setState(options.getAngularType());
		rotationInput.calling(options::setAngularType);

		offsetInput = new SelectionScrollInput(x + 171, y + 42, 109, 18);
		offsetInput.forOptions(OFFSET_OPTIONS);
		offsetInput.titled(OFFSET_TITLE.plainCopy());
		offsetInput.writingTo(offsetLabel);
		offsetInput.setState(options.getLinearType());
		offsetInput.calling(options::setLinearType);

		verticalInput = new SelectionScrollInput(x + 31, y + 64, 109, 18);
		verticalInput.forOptions(menu.inverted ? VERTICAL_OPTIONS_INVERTED : VERTICAL_OPTIONS);
		verticalInput.titled(VERTICAL_TITLE.plainCopy());
		verticalInput.writingTo(verticalLabel);
		verticalInput.setState(!menu.inverted && options.allowVerticalMovement ? 1 : 0);
		verticalInput.calling(i -> options.allowVerticalMovement = !menu.inverted && i == 1);

		axleInput = new ScrollInput(x + 171, y + 64, 109, 18);
		axleInput.withRange(-16, 16 + 1);
		axleInput.withShiftStep(4);
		axleInput.titled(AXLE_TITLE.plainCopy());
		axleInput.format(i -> Component.literal(String.valueOf(i * 0.0625F)));
		axleInput.writingTo(axleLabel);
		axleInput.setState((int)(options.getAxleOffset() * 16));
		axleInput.calling(i -> options.setAxleOffset(i * 0.0625F));

		controlInput = new SelectionScrollInput(x + 31, y + 86, 109, 18);
		controlInput.forOptions(CONTROL_OPTIONS);
		controlInput.titled(CONTROL_TITLE.plainCopy());
		controlInput.writingTo(controlLabel);
		controlInput.setState(options.controlMode.ordinal());
		controlInput.calling(i -> options.controlMode = PhysicsBogeyControlMode.BY_ID.apply(i));

		stressInput = new ScrollInput(x + 31, y + 108, 109, 18);
		stressInput.withRange(0, 64 * 2 + 1);
		stressInput.withShiftStep(4);
		stressInput.titled(STRESS_TITLE.plainCopy());
		stressInput.format(i -> Component.literal(String.valueOf(i * 0.5F)));
		stressInput.writingTo(stressLabel);
		stressInput.setState((int)(options.getStress() * 2));
		stressInput.calling(i -> options.setStress(i * 0.5F));

		tiltInput = new ScrollInput(x + 171, y + 108, 109, 18);
		tiltInput.withRange(0, 100 + 1);
		tiltInput.withShiftStep(5);
		tiltInput.titled(TILT_TITLE.plainCopy());
		tiltInput.format(i -> Component.literal(i + "%"));
		tiltInput.writingTo(tiltLabel);
		tiltInput.setState((int)(options.getTiltStrength() * 100));
		tiltInput.calling(i -> options.setTiltStrength(i * 0.01F));

		connectorInput = new SelectionScrollInput(x + 31, y + 130, 109, 18);
		connectorInput.forOptions(CONNECTOR_OPTIONS);
		connectorInput.titled(CONNECTOR_TITLE.plainCopy());
		connectorInput.writingTo(connectorLabel);
		connectorInput.setState(options.getConnectorType());
		connectorInput.calling(options::setConnectorType);

		bogeyButton = new IconButton(x + 7, y + 159, SimurailGuiTextures.PHYSICS_BOGEY_OPTIONS_BOGEY_ICON);
		bogeyButton.setToolTip(TYPE_TOOLTIP);
		bogeyButton.withCallback(this::openTypeScreen);

		confirmButton = new IconButton(x + 262, y + 159, AllIcons.I_CONFIRM);
		confirmButton.setToolTip(CONFIRM_TOOLTIP);
		confirmButton.withCallback(this::onConfirm);

		if(!menu.hasComputer) {
			addRenderableWidget(physicsInput);
			addRenderableWidget(rotationInput);
			addRenderableWidget(offsetInput);
			addRenderableWidget(verticalInput);
			addRenderableWidget(axleInput);
			addRenderableWidget(controlInput);
			addRenderableWidget(stressInput);
			addRenderableWidget(tiltInput);
		}
		else {
			physicsLabel.withTooltip(List.of(
					PHYSICS_TITLE.plainCopy().withColor(0x5391E1),
					COMPUTER_TOOLTIP.plainCopy().withColor(0x96B7E0)));
			rotationLabel.withTooltip(List.of(
					ROTATION_TITLE.plainCopy().withColor(0x5391E1),
					COMPUTER_TOOLTIP.plainCopy().withColor(0x96B7E0)));
			offsetLabel.withTooltip(List.of(
					OFFSET_TITLE.plainCopy().withColor(0x5391E1),
					COMPUTER_TOOLTIP.plainCopy().withColor(0x96B7E0)));
			verticalLabel.withTooltip(List.of(
					VERTICAL_TITLE.plainCopy().withColor(0x5391E1),
					COMPUTER_TOOLTIP.plainCopy().withColor(0x96B7E0)));
			axleLabel.withTooltip(List.of(
					AXLE_TITLE.plainCopy().withColor(0x5391E1),
					COMPUTER_TOOLTIP.plainCopy().withColor(0x96B7E0)));
			controlLabel.withTooltip(List.of(
					CONTROL_TITLE.plainCopy().withColor(0x5391E1),
					COMPUTER_TOOLTIP.plainCopy().withColor(0x96B7E0)));
			stressLabel.withTooltip(List.of(
					STRESS_TITLE.plainCopy().withColor(0x5391E1),
					COMPUTER_TOOLTIP.plainCopy().withColor(0x96B7E0)));
			tiltLabel.withTooltip(List.of(
					TILT_TITLE.plainCopy().withColor(0x5391E1),
					COMPUTER_TOOLTIP.plainCopy().withColor(0x96B7E0)));
		}

		addRenderableWidget(connectorInput);

		addRenderableWidget(physicsLabel);
		addRenderableWidget(rotationLabel);
		addRenderableWidget(offsetLabel);
		addRenderableWidget(verticalLabel);
		addRenderableWidget(axleLabel);
		addRenderableWidget(controlLabel);
		addRenderableWidget(stressLabel);
		addRenderableWidget(tiltLabel);
		addRenderableWidget(connectorLabel);

		addRenderableWidget(bogeyButton);
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
		GuiGameElement.GuiRenderBuilder builder = GuiGameElement.of(inverted ? SimurailItems.INVERTED_PHYSICS_BOGEY : SimurailBlocks.PHYSICS_BOGEY);
		builder.at(guiLeft + background.getWidth() + 6, guiTop + background.getHeight() - 56, -200);
		builder.scale(5);
		builder.render(graphics);
	}

	private void openTypeScreen() {
		minecraft.setScreen(new PhysicsBogeyMenuScreen(menu, title));
	}

	private void onConfirm() {
		if(minecraft.level.getBlockEntity(pos) instanceof PhysicsBogeyBlockEntity be) {
			be.renderPivotOffset.step();
			be.renderPivotRot.step();
		}
		VeilPacketManager.server().sendPacket(new PhysicsBogeyOptionsPacket(pos, options));
		onClose();
	}
}
