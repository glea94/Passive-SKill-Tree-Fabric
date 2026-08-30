package daripher.skilltree.client.widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;
public class NavigationArrowButton extends Button {
    private static final Component ARROW_LEFT = Component.literal("<");
    private static final Component ARROW_RIGHT = Component.literal(">");
    private static final float SCALE = 1.6F;
    public NavigationArrowButton(int x, int y, int width, int height, boolean pointingRight) {
        super(x, y, width, height, pointingRight ? ARROW_RIGHT : ARROW_LEFT);
    }
    @Override
    protected void renderBackground(@NotNull GuiGraphicsExtractor graphics) {
    }
    @Override
    protected void renderText(@NotNull GuiGraphicsExtractor graphics) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        int baseColor = isHoveredOrFocused() ? 0xFFFFFF : 0xAAAAAA;
        int textColor = baseColor | (Mth.ceil(this.alpha * 255F) << 24);
        float centerX = getX() + this.getWidth() / 2F;
        float centerY = getY() + this.getHeight() / 2F;
        Matrix3x2fStack poseStack = graphics.pose();
        poseStack.pushMatrix();
        poseStack.translate(centerX, centerY);
        poseStack.scale(SCALE, SCALE);
        poseStack.translate(-centerX, -centerY);
        graphics.centeredText(font, getMessage(), (int) centerX, (int) (centerY - 4), textColor);
        poseStack.popMatrix();
    }
}