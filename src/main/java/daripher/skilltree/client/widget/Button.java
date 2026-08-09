package daripher.skilltree.client.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
<<<<<<< Updated upstream
=======
import net.minecraft.client.renderer.RenderPipelines;
>>>>>>> Stashed changes
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class Button extends net.minecraft.client.gui.components.Button {
    protected net.minecraft.client.gui.components.Button.OnPress pressFunc;

    public Button(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message, b -> {
        }, Supplier::get);
        this.pressFunc = b -> {
        };
    }

    public void setPressFunc(net.minecraft.client.gui.components.Button.OnPress pressFunc) {
        this.pressFunc = pressFunc;
    }

    @Override
    public void onPress() {
        pressFunc.onPress(this);
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        renderText(graphics);
    }

    protected void renderBackground(@NotNull GuiGraphics graphics) {
        ResourceLocation texture = new ResourceLocation("skilltree:textures/screen/widgets.png");
        int v = getTextureVariant() * 14;
<<<<<<< Updated upstream
        graphics.blit(texture, getX(), getY(), 0, v, width / 2, height);
        graphics.blit(texture, getX() + width / 2, getY(), -width / 2, v, width / 2, height);
=======
        int currentWidth = this.getWidth();
        int currentHeight = this.getHeight();

        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), 0F, v, currentWidth / 2, currentHeight, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, getX() + currentWidth / 2, getY(), (256 - currentWidth / 2F), v, currentWidth / 2, currentHeight, 256, 256);
>>>>>>> Stashed changes
    }

    protected void renderText(@NotNull GuiGraphics graphics) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        int textColor = 0xFFFFFF; // couleur de texte par défaut vanilla (setFGColor n'est jamais appelé dans le mod)
        textColor |= Mth.ceil(alpha * 255F) << 24;
        graphics.drawCenteredString(font, getMessage(), getX() + width / 2, getY() + (height - 8) / 2, textColor);
    }

    protected int getTextureVariant() {
        return !isActive() ? 0 : isHoveredOrFocused() ? 2 : 1;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return visible && mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
    }
}
