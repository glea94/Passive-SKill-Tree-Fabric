package daripher.skilltree.client.widget;

import net.minecraft.client.gui.GuiGraphics;
<<<<<<< Updated upstream
<<<<<<< Updated upstream
=======
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
>>>>>>> Stashed changes
=======
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
>>>>>>> Stashed changes
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
    public void onPress() {
        value ^= true;
        responder.accept(value);
    }

    @Override
    protected void renderBackground(@NotNull GuiGraphics graphics) {
        super.renderBackground(graphics);
<<<<<<< Updated upstream
<<<<<<< Updated upstream
        ResourceLocation texture = new ResourceLocation("skilltree:textures/screen/widgets.png");
        if (value) {
            graphics.blit(texture, getX(), getY(), 0, 242, width, height);
=======
=======
>>>>>>> Stashed changes
        Identifier texture = Identifier.parse("skilltree:textures/screen/widgets.png");
        if (value) {
            // Fix 1.21.8 : blit(RenderType::guiTextured, ...) supprimé, remplacé par blit(RenderPipeline, ...) confirmé par décompilation de GuiGraphics (RenderPipelines.GUI_TEXTURED = équivalent direct)
            graphics.blit(RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), 0F, 242F, this.getWidth(), this.getHeight(), 256, 256);
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
        }
    }

    protected int getTextureVariant() {
        return isHoveredOrFocused() ? 3 : 4;
    }

    public void setResponder(Consumer<Boolean> responder) {
        this.responder = responder;
    }
}