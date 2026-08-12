package daripher.skilltree.client.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
<<<<<<< Updated upstream
<<<<<<< Updated upstream
=======
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
>>>>>>> Stashed changes
=======
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
>>>>>>> Stashed changes
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class Button extends net.minecraft.client.gui.components.Button {
    protected net.minecraft.client.gui.components.Button.OnPress pressFunc;

    public Button(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message, b -> {}, DEFAULT_NARRATION);
        this.pressFunc = b -> {};
    }

    public void setPressFunc(net.minecraft.client.gui.components.Button.OnPress pressFunc) {
        this.pressFunc = pressFunc;
    }

    @Override
    public void onPress(InputWithModifiers inputWithModifiers) {
        pressFunc.onPress(this);
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        renderText(graphics);
    }

    protected void renderBackground(@NotNull GuiGraphics graphics) {
<<<<<<< Updated upstream
        ResourceLocation texture = new ResourceLocation("skilltree:textures/screen/widgets.png");
        int v = getTextureVariant() * 14;
        graphics.blit(texture, getX(), getY(), 0, v, width / 2, height);
        graphics.blit(texture, getX() + width / 2, getY(), -width / 2, v, width / 2, height);
=======
        ResourceLocation texture = ResourceLocation.parse("skilltree:textures/screen/widgets.png");
        int v = getTextureVariant() * 14;
        int currentWidth = this.getWidth();
        int currentHeight = this.getHeight();

        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), 0F, v, currentWidth / 2, currentHeight, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, getX() + currentWidth / 2, getY(), (256 - currentWidth / 2F), v, currentWidth / 2, currentHeight, 256, 256);
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
    }

    protected void renderText(@NotNull GuiGraphics graphics) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        int textColor = 0xFFFFFF;
        textColor |= Mth.ceil(this.alpha * 255F) << 24;
        graphics.drawCenteredString(font, getMessage(), getX() + this.getWidth() / 2, getY() + (this.getHeight() - 8) / 2, textColor);
    }

    protected int getTextureVariant() {
        return !this.active ? 0 : isHoveredOrFocused() ? 2 : 1;
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.visible && mouseX >= getX() && mouseY >= getY() && mouseX < getX() + this.getWidth() && mouseY < getY() + this.getHeight();
    }
}