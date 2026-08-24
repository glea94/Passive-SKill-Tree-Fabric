package daripher.skilltree.client.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
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
    protected void extractContents(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        renderText(graphics);
    }

    protected void renderBackground(@NotNull GuiGraphicsExtractor graphics) {
        Identifier texture = Identifier.parse("skilltree:textures/screen/widgets.png");
        int v = getTextureVariant() * 14;
        int currentWidth = this.getWidth();
        int currentHeight = this.getHeight();

        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), 0F, v, currentWidth / 2, currentHeight, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, getX() + currentWidth / 2, getY(), (256 - currentWidth / 2F), v, currentWidth / 2, currentHeight, 256, 256);
    }

    protected void renderText(@NotNull GuiGraphicsExtractor graphics) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        int textColor = 0xFFFFFF;
        textColor |= Mth.ceil(this.alpha * 255F) << 24;
        graphics.centeredText(font, getMessage(), getX() + this.getWidth() / 2, getY() + (this.getHeight() - 8) / 2, textColor);
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