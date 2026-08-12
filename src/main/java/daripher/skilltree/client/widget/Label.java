package daripher.skilltree.client.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
<<<<<<< Updated upstream
<<<<<<< Updated upstream
=======
import net.minecraft.client.renderer.RenderPipelines;
>>>>>>> Stashed changes
=======
import net.minecraft.client.renderer.RenderPipelines;
>>>>>>> Stashed changes
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class Label extends AbstractWidget {
    public static final ResourceLocation WIDGETS_TEXTURE = new ResourceLocation("skilltree:textures/screen/widgets.png");
    private boolean hasBackground;

    public Label(int x, int y, Component message) {
        super(x, y, 0, 14, message);
    }

    public Label(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        setHasBackground(true);
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int m, int pMouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        if (hasBackground) {
<<<<<<< Updated upstream
<<<<<<< Updated upstream
            graphics.blit(WIDGETS_TEXTURE, getX(), getY(), 0, 14, width / 2, height);
            graphics.blit(WIDGETS_TEXTURE, getX() + width / 2, getY(), 256 - width / 2, 14, width / 2, height);
            int textColor = 0xFFFFFF | Mth.ceil(alpha * 255F) << 24; // couleur par défaut vanilla
            graphics.drawCenteredString(font, getMessage(), getX() + width / 2, getY() + (height - 8) / 2, textColor);
=======
=======
>>>>>>> Stashed changes
            // Fix 1.21.8 : blit(RenderType::guiTextured, ...) supprimé, remplacé par blit(RenderPipeline, ...) confirmé par décompilation de GuiGraphics (RenderPipelines.GUI_TEXTURED = équivalent direct)
            graphics.blit(RenderPipelines.GUI_TEXTURED, WIDGETS_TEXTURE, getX(), getY(), 0F, 14F, currentWidth / 2, currentHeight, 256, 256);
            graphics.blit(RenderPipelines.GUI_TEXTURED, WIDGETS_TEXTURE, getX() + currentWidth / 2, getY(), 256F - currentWidth / 2F, 14F, currentWidth / 2, currentHeight, 256, 256);

            // Factual Fix 1.21.4: Replaced legacy getAlpha() method with direct public field access
            int textColor = 0xFFFFFF | Mth.ceil(this.alpha * 255F) << 24;
            graphics.drawCenteredString(font, getMessage(), getX() + currentWidth / 2, getY() + (currentHeight - 8) / 2, textColor);
>>>>>>> Stashed changes
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