package daripher.skilltree.client.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
    public void onPress() {
        value ^= true;
        responder.accept(value);
    }

    @Override
    protected void renderBackground(@NotNull GuiGraphics graphics) {
        super.renderBackground(graphics);
        ResourceLocation texture = ResourceLocation.parse("skilltree:textures/screen/widgets.png");
        if (value) {
            // Factual Fix 1.21.4: Refactored legacy blit calls to use the mandatory RenderType layer and size metadata
            graphics.blit(RenderType::guiTextured, texture, getX(), getY(), 0F, 242F, this.getWidth(), this.getHeight(), 256, 256);
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
