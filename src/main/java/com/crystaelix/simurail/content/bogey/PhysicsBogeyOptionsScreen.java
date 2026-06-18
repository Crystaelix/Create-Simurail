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
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.TextureSheetSegment;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class PhysicsBogeyOptionsScreen extends AbstractSimiScreen {

	public static final SimurailGuiTextures BACKGROUND = SimurailGuiTextures.PHYSICS_BOGEY_OPTIONS;

	public static final Component TITLE = Component.translatable("gui.simurail.physics_bogey.title");
	public static final Component INVERTED_TITLE = Component.translatable("gui.simurail.physics_bogey.title.inverted");

	public static final Component ENABLED_TITLE = Component.translatable("gui.simurail.physics_bogey.behaviour");
	public static final Component ROTATION_TITLE = Component.translatable("gui.simurail.physics_bogey.rotation");
	public static final Component OFFSET_TITLE = Component.translatable("gui.simurail.physics_bogey.offset");
	public static final Component VERTICAL_TITLE = Component.translatable("gui.simurail.physics_bogey.vertical");
	public static final Component CONTROL_TITLE = Component.translatable("gui.simurail.physics_bogey.control");
	public static final Component STRESS_TITLE = Component.translatable("gui.simurail.physics_bogey.stress");
	public static final Component CONNECTOR_TITLE = Component.translatable("gui.simurail.physics_bogey.connector");

	public static final List<Component> ENABLED_OPTIONS = List.of(
			Component.translatable("gui.simurail.physics_bogey.behaviour.enabled"),
			Component.translatable("gui.simurail.physics_bogey.behaviour.disabled"));
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
			Component.translatable("gui.simurail.physics_bogey.control.strength_inverted"));
	public static final List<Component> CONNECTOR_OPTIONS = List.of(
			Component.translatable("gui.simurail.physics_bogey.connector.visible"),
			Component.translatable("gui.simurail.physics_bogey.connector.invisible"),
			Component.translatable("gui.simurail.physics_bogey.connector.front"),
			Component.translatable("gui.simurail.physics_bogey.connector.back"));

	public static final Component TYPE_TOOLTIP = Component.translatable("gui.simurail.physics_bogey.type");
	public static final Component CONFIRM_TOOLTIP = Component.translatable("create.action.confirm");

	final PhysicsBogeyBlockEntity be;
	final PhysicsBogeyOptions options;

	private SelectionScrollInput behaviourInput;
	private SLabel behaviourLabel;
	private SelectionScrollInput rotationInput;
	private SLabel rotationLabel;
	private SelectionScrollInput offsetInput;
	private SLabel offsetLabel;
	private SelectionScrollInput verticalInput;
	private SLabel verticalLabel;
	private SelectionScrollInput controlInput;
	private SLabel controlLabel;
	private ScrollInput stressInput;
	private SLabel stressLabel;
	private SelectionScrollInput connectorInput;
	private SLabel connectorLabel;

	private IconButton bogeyButton;
	private IconButton confirmButton;

	public PhysicsBogeyOptionsScreen(PhysicsBogeyBlockEntity be) {
		this(be, be.getOptions());
	}

	public PhysicsBogeyOptionsScreen(PhysicsBogeyBlockEntity be, PhysicsBogeyOptions options) {
		super(be.hasCustomName() ? be.getCustomName() : be.isInverted() ? INVERTED_TITLE : TITLE);
		this.be = be;
		this.options = new PhysicsBogeyOptions().set(options);
	}

	@Override
	protected void init() {
		// TODO computer
		//if(be.computerBehaviour.hasAttachedComputer()) {
		//	minecraft.setScreen(new PhysicsBogeyComputerScreen(be, options));
		//	return;
		//}

		setWindowSize(BACKGROUND.w, BACKGROUND.h);
		super.init();

		int x = guiLeft;
		int y = guiTop;

		behaviourLabel = new SLabel(x + 45, y + 23, 109, 18);
		behaviourLabel.withMargin(5);
		behaviourLabel.withShadow();

		behaviourInput = new SelectionScrollInput(x + 45, y + 23, 109, 18);
		behaviourInput.forOptions(ENABLED_OPTIONS);
		behaviourInput.titled(ENABLED_TITLE.plainCopy());
		behaviourInput.writingTo(behaviourLabel);
		behaviourInput.setState(options.enabled ? 0 : 1);
		behaviourInput.calling(i -> options.enabled = i != 1);

		rotationLabel = new SLabel(x + 45, y + 45, 109, 18);
		rotationLabel.withMargin(5);
		rotationLabel.withShadow();

		rotationInput = new SelectionScrollInput(x + 45, y + 45, 109, 18);
		rotationInput.forOptions(ROTATION_OPTIONS);
		rotationInput.titled(ROTATION_TITLE.plainCopy());
		rotationInput.writingTo(rotationLabel);
		rotationInput.setState(options.getAngularType());
		rotationInput.calling(options::setAngularType);

		offsetLabel = new SLabel(x + 45, y + 67, 109, 18);
		offsetLabel.withMargin(5);
		offsetLabel.withShadow();

		offsetInput = new SelectionScrollInput(x + 45, y + 67, 109, 18);
		offsetInput.forOptions(OFFSET_OPTIONS);
		offsetInput.titled(OFFSET_TITLE.plainCopy());
		offsetInput.writingTo(offsetLabel);
		offsetInput.setState(options.getLinearType());
		offsetInput.calling(options::setLinearType);

		verticalLabel = new SLabel(x + 45, y + 89, 109, 18);
		verticalLabel.withMargin(5);
		verticalLabel.withShadow();

		verticalInput = new SelectionScrollInput(x + 45, y + 89, 109, 18);
		verticalInput.forOptions(be.isInverted() ? VERTICAL_OPTIONS_INVERTED : VERTICAL_OPTIONS);
		verticalInput.titled(VERTICAL_TITLE.plainCopy());
		verticalInput.writingTo(verticalLabel);
		verticalInput.setState(!be.isInverted() && options.allowVerticalMovement ? 1 : 0);
		verticalInput.calling(i -> options.allowVerticalMovement = !be.isInverted() && i == 1);

		controlLabel = new SLabel(x + 45, y + 111, 109, 18);
		controlLabel.withMargin(5);
		controlLabel.withShadow();

		controlInput = new SelectionScrollInput(x + 45, y + 111, 109, 18);
		controlInput.forOptions(CONTROL_OPTIONS);
		controlInput.titled(CONTROL_TITLE.plainCopy());
		controlInput.writingTo(controlLabel);
		controlInput.setState(options.controlMode.ordinal());
		controlInput.calling(i -> options.controlMode = PhysicsBogeyControlMode.BY_ID.apply(i));

		stressLabel = new SLabel(x + 45, y + 133, 109, 18);
		stressLabel.withMargin(5);
		stressLabel.withShadow();

		stressInput = new ScrollInput(x + 45, y + 133, 109, 18);
		stressInput.withRange(0, 32 * 2 + 1);
		stressInput.withShiftStep(4);
		stressInput.titled(STRESS_TITLE.plainCopy());
		stressInput.format(i -> Component.literal(String.valueOf(i * 0.5F)));
		stressInput.writingTo(stressLabel);
		stressInput.setState((int)(options.stress * 2));
		stressInput.calling(i -> options.setStress(i * 0.5F));

		connectorLabel = new SLabel(x + 45, y + 155, 109, 18);
		connectorLabel.withMargin(5);
		connectorLabel.withShadow();

		connectorInput = new SelectionScrollInput(x + 45, y + 155, 109, 18);
		connectorInput.forOptions(CONNECTOR_OPTIONS);
		connectorInput.titled(CONNECTOR_TITLE.plainCopy());
		connectorInput.writingTo(connectorLabel);
		connectorInput.setState(options.getConnectorType());
		connectorInput.calling(options::setConnectorType);

		bogeyButton = new IconButton(x + 7, y + 187, SimurailGuiTextures.PHYSICS_BOGEY_OPTIONS_BOGEY_ICON);
		bogeyButton.setToolTip(TYPE_TOOLTIP);
		bogeyButton.withCallback(this::openTypeScreen);

		confirmButton = new IconButton(x + 155, y + 187, AllIcons.I_CONFIRM);
		confirmButton.setToolTip(CONFIRM_TOOLTIP);
		confirmButton.withCallback(this::onConfirm);

		addRenderableWidget(behaviourInput);
		addRenderableWidget(behaviourLabel);
		addRenderableWidget(rotationInput);
		addRenderableWidget(rotationLabel);
		addRenderableWidget(offsetInput);
		addRenderableWidget(offsetLabel);
		addRenderableWidget(verticalInput);
		addRenderableWidget(verticalLabel);
		addRenderableWidget(controlInput);
		addRenderableWidget(controlLabel);
		addRenderableWidget(stressInput);
		addRenderableWidget(stressLabel);
		addRenderableWidget(connectorInput);
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
		GuiGameElement.GuiRenderBuilder builder = GuiGameElement.of(be.isInverted() ? SimurailItems.INVERTED_PHYSICS_BOGEY : SimurailBlocks.PHYSICS_BOGEY);
		builder.at(guiLeft + background.getWidth() + 6, guiTop + background.getHeight() - 56, -200);
		builder.scale(5);
		builder.render(graphics);
	}

	private void openTypeScreen() {
		minecraft.setScreen(new PhysicsBogeyMenuScreen(be, options));
	}

	private void onConfirm() {
		be.renderPivotOffset.step();
		be.renderPivotRot.step();
		be.setOptions(options);
		VeilPacketManager.server().sendPacket(new PhysicsBogeySetOptionsPacket(be.getBlockPos(), options));
		onClose();
	}
}
