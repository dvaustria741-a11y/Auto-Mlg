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
        int centerX = this.width / 2;
        int y = this.height / 2 - 80;
        int spacing = 24;

        addToggle(centerX, y, "Auto Boat MLG", () -> AutoMLGConfig.boatEnabled, v -> AutoMLGConfig.boatEnabled = v);
        addToggle(centerX, y + spacing, "Auto Water Bucket MLG", () -> AutoMLGConfig.waterEnabled, v -> AutoMLGConfig.waterEnabled = v);
        addToggle(centerX, y + spacing * 2, "Auto Water Pickup", () -> AutoMLGConfig.waterPickupEnabled, v -> AutoMLGConfig.waterPickupEnabled = v);
        addToggle(centerX, y + spacing * 3, "Auto Powder Snow MLG", () -> AutoMLGConfig.snowEnabled, v -> AutoMLGConfig.snowEnabled = v);
        addToggle(centerX, y + spacing * 4, "Auto Snow Pickup", () -> AutoMLGConfig.snowPickupEnabled, v -> AutoMLGConfig.snowPickupEnabled = v);

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button -> this.close())
                .dimensions(centerX - 75, y + spacing * 5 + 12, 150, 20)
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
