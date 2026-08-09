package daripher.skilltree.client.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class TextArea extends MultiLineEditBox implements TickingWidget {
    public TextArea(int x, int y, int width, int height, String defaultValue) {
        // Factual Fix 1.21.4: MultiLineEditBox super constructor strictly requires font, x, y, width, height, placeholder, message
        super(Minecraft.getInstance().font, x, y, width, height, Component.empty(), Component.empty());
        this.setValue(defaultValue);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.isFocused() && super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return this.isFocused() && super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.setFocused(this.isMouseOver(mouseX, mouseY));
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public TextArea setResponder(@NotNull Consumer<String> responder) {
        super.setValueListener(responder);
        return this;
    }

    @Override
    public void onWidgetTick() {
        // MultiLineEditBox ticks automatically internally via focus state animations; no manual tick task needed.
    }
}
