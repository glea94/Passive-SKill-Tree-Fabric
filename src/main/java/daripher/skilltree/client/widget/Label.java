package daripher.skilltree.client.widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
public class Label extends AbstractWidget {
    public static final Identifier WIDGETS_TEXTURE = Identifier.parse("skilltree:textures/screen/widgets.png");
    private boolean hasBackground;
    public Label(int x, int y, Component message) {
        super(x, y, Minecraft.getInstance().font.width(message), 14, message);
    }
    public Label(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        setHasBackground(true);
    }
    @Override
    public void extractWidgetRenderState(@NotNull GuiGraphicsExtractor graphics, int m, int pMouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        int currentWidth = this.getWidth();
        int currentHeight = this.getHeight();
        if (hasBackground) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, WIDGETS_TEXTURE, getX(), getY(), 0F, 14F, currentWidth / 2, currentHeight, 256, 256);
            graphics.blit(RenderPipelines.GUI_TEXTURED, WIDGETS_TEXTURE, getX() + currentWidth / 2, getY(), 256F - currentWidth / 2F, 14F, currentWidth / 2, currentHeight, 256, 256);
            int textColor = 0xFFFFFF | Mth.ceil(this.alpha * 255F) << 24;
            graphics.centeredText(font, getMessage(), getX() + currentWidth / 2, getY() + (currentHeight - 8) / 2, textColor);
        } else {
            graphics.text(font, getMessage(), getX(), getY() + 3, ARGB.opaque(0xFFFFFF));
        }
    }
    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
    }
    public void setHasBackground(boolean hasBackground) {
        this.hasBackground = hasBackground;
    }
}