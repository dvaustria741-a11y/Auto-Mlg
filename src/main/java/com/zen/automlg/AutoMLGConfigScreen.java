package com.zen.automlg;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class AutoMLGConfigScreen extends Screen {

    private final Screen parent;

    public AutoMLGConfigScreen(Screen parent) {
        super(Text.literal("Auto MLG Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int spacing = 24;
        int y = this.height / 2 - (spacing * 6) / 2;
        int leftX = this.width / 2 - 105;
        int rightX = this.width / 2 + 105;

        addToggle(leftX, y, "Boat MLG", () -> AutoMLGConfig.boatEnabled, v -> AutoMLGConfig.boatEnabled = v);
        addToggle(leftX, y + spacing, "Horse MLG", () -> AutoMLGConfig.horseEnabled, v -> AutoMLGConfig.horseEnabled = v);
        addToggle(leftX, y + spacing * 2, "Water Bucket MLG", () -> AutoMLGConfig.waterEnabled, v -> AutoMLGConfig.waterEnabled = v);
        addToggle(leftX, y + spacing * 3, "Water Pickup", () -> AutoMLGConfig.waterPickupEnabled, v -> AutoMLGConfig.waterPickupEnabled = v);
        addToggle(leftX, y + spacing * 4, "Powder Snow MLG", () -> AutoMLGConfig.snowEnabled, v -> AutoMLGConfig.snowEnabled = v);
        addToggle(leftX, y + spacing * 5, "Snow Pickup", () -> AutoMLGConfig.snowPickupEnabled, v -> AutoMLGConfig.snowPickupEnabled = v);

        addToggle(rightX, y, "Hay Bale MLG", () -> AutoMLGConfig.hayBaleEnabled, v -> AutoMLGConfig.hayBaleEnabled = v);
        addToggle(rightX, y + spacing, "Hay Bale Pickup", () -> AutoMLGConfig.hayBalePickupEnabled, v -> AutoMLGConfig.hayBalePickupEnabled = v);
        addToggle(rightX, y + spacing * 2, "Cobweb MLG", () -> AutoMLGConfig.cobwebEnabled, v -> AutoMLGConfig.cobwebEnabled = v);
        addToggle(rightX, y + spacing * 3, "Cobweb Pickup", () -> AutoMLGConfig.cobwebPickupEnabled, v -> AutoMLGConfig.cobwebPickupEnabled = v);
        addToggle(rightX, y + spacing * 4, "Sweet Berries MLG", () -> AutoMLGConfig.sweetBerryEnabled, v -> AutoMLGConfig.sweetBerryEnabled = v);
        addToggle(rightX, y + spacing * 5, "Sweet Berries Pickup", () -> AutoMLGConfig.sweetBerryPickupEnabled, v -> AutoMLGConfig.sweetBerryPickupEnabled = v);

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button -> this.close())
                .dimensions(this.width / 2 - 75, y + spacing * 6 + 12, 150, 20)
                .build());
    }

    private void addToggle(int centerX, int y, String label, java.util.function.BooleanSupplier getter, java.util.function.Consumer<Boolean> setter) {
        this.addDrawableChild(ButtonWidget.builder(toggleText(label, getter.getAsBoolean()), button -> {
            boolean newValue = !getter.getAsBoolean();
            setter.accept(newValue);
            button.setMessage(toggleText(label, newValue));
        }).dimensions(centerX - 100, y, 200, 20).build());
    }

    private Text toggleText(String label, boolean value) {
        return Text.literal(label + ": " + (value ? "ON" : "OFF"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
    }

    @Override
    public void close() {
        AutoMLGConfig.save();
        this.client.setScreen(parent);
    }
}
