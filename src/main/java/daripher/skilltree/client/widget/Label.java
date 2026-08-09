package daripher.skilltree.client.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class Label extends AbstractWidget {
    public static final ResourceLocation WIDGETS_TEXTURE = ResourceLocation.parse("skilltree:textures/screen/widgets.png");
    private boolean hasBackground;

    public Label(int x, int y, Component message) {
        // Factual Fix 1.21.4: AbstractWidget constructor requires x, y, width, height, and message component
        super(x, y, Minecraft.getInstance().font.width(message), 14, message);
    }

    public Label(int x, int y, int width, int height, Component message) {
        // Factual Fix 1.21.4: AbstractWidget constructor requires x, y, width, height, and message component
        super(x, y, width, height, message);
        setHasBackground(true);
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int m, int pMouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        int currentWidth = this.getWidth();
        int currentHeight = this.getHeight();

        if (hasBackground) {
            // Factual Fix 1.21.4: Refactored legacy blit calls to use the mandatory RenderType layer pipeline
            graphics.blit(RenderType::guiTextured, WIDGETS_TEXTURE, getX(), getY(), 0F, 14F, currentWidth / 2, currentHeight, 256, 256);
            graphics.blit(RenderType::guiTextured, WIDGETS_TEXTURE, getX() + currentWidth / 2, getY(), 256F - currentWidth / 2F, 14F, currentWidth / 2, currentHeight, 256, 256);

            // Factual Fix 1.21.4: Replaced legacy getAlpha() method with direct public field access
            int textColor = 0xFFFFFF | Mth.ceil(this.alpha * 255F) << 24;
            graphics.drawCenteredString(font, getMessage(), getX() + currentWidth / 2, getY() + (currentHeight - 8) / 2, textColor);
        } else {
            graphics.drawString(font, getMessage(), getX(), getY() + 3, 0xFFFFFF);
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
    }

    public void setHasBackground(boolean hasBackground) {
        this.hasBackground = hasBackground;
    }
}
