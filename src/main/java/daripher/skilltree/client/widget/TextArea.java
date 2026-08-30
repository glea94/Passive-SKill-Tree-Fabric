package daripher.skilltree.client.widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import java.util.function.Consumer;
public class TextArea extends MultiLineEditBox implements TickingWidget {
    public TextArea(int x, int y, int width, int height, String defaultValue) {
        super(Minecraft.getInstance().font, x, y, width, height, Component.empty(), Component.empty(), -2039584, true, -3092272, true, true);
        this.setValue(defaultValue);
    }
    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        return this.isFocused() && super.keyPressed(keyEvent);
    }
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return this.isFocused() && super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean doubleClick) {
        this.setFocused(this.isMouseOver(mouseButtonEvent.x(), mouseButtonEvent.y()));
        return super.mouseClicked(mouseButtonEvent, doubleClick);
    }
    public TextArea setResponder(@NotNull Consumer<String> responder) {
        super.setValueListener(responder);
        return this;
    }
    @Override
    public void onWidgetTick() {
    }
}