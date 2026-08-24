package daripher.skilltree.client.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class CheckBox extends Button {
    private boolean value;
    private Consumer<Boolean> responder = b -> {
    };

    public CheckBox(int x, int y, boolean defaultValue) {
        super(x, y, 14, 14, Component.empty());
        this.value = defaultValue;
    }

    @Override
    public void onPress(InputWithModifiers inputWithModifiers) {
        value ^= true;
        responder.accept(value);
    }

    @Override
    protected void renderBackground(@NotNull GuiGraphicsExtractor graphics) {
        super.renderBackground(graphics);
        Identifier texture = Identifier.parse("skilltree:textures/screen/widgets.png");
        if (value) {

            graphics.blit(RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), 0F, 242F, this.getWidth(), this.getHeight(), 256, 256);
        }
    }

    @Override
    protected int getTextureVariant() {
        return isHoveredOrFocused() ? 3 : 4;
    }

    public void setResponder(Consumer<Boolean> responder) {
        this.responder = responder;
    }
}