package daripher.skilltree.client.screen;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.skill.SkillButton;
import daripher.skilltree.client.widget.skill.SkillConnection;
import daripher.skilltree.skill.PassiveSkillTree;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import java.util.ArrayList;
import java.util.List;
public class ScreenHelper {
    public static void drawCenteredOutlinedText(GuiGraphicsExtractor graphics, String text, int x, int y, int color) {
        Font font = Minecraft.getInstance().font;
        x -= font.width(text) / 2;
        int outlineColor = ARGB.opaque(0);
        int textColor = ARGB.opaque(color);
        graphics.text(font, text, x + 1, y, outlineColor, false);
        graphics.text(font, text, x - 1, y, outlineColor, false);
        graphics.text(font, text, x, y + 1, outlineColor, false);
        graphics.text(font, text, x, y - 1, outlineColor, false);
        graphics.text(font, text, x, y, textColor, false);
    }
    public static void drawRectangle(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color) {
        graphics.fill(x, y, x + width, y + 1, color);
        graphics.fill(x, y + height - 1, x + width, y + height, color);
        graphics.fill(x, y + 1, x + 1, y + height - 1, color);
        graphics.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
    }
    public static float getAngleBetweenButtons(Button button1, Button button2) {
        float x1 = button1.getX() + button1.getWidth() / 2F;
        float y1 = button1.getY() + button1.getHeight() / 2F;
        float x2 = button2.getX() + button2.getWidth() / 2F;
        float y2 = button2.getY() + button2.getHeight() / 2F;
        return (float) Mth.atan2(y2 - y1, x2 - x1);
    }
    public static float getDistanceBetweenButtons(Button button1, Button button2) {
        float x1 = button1.getX() + button1.getWidth() / 2F;
        float y1 = button1.getY() + button1.getHeight() / 2F;
        float x2 = button2.getX() + button2.getWidth() / 2F;
        float y2 = button2.getY() + button2.getHeight() / 2F;
        return Mth.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }
    public static void renderSkillTooltip(PassiveSkillTree skillTree, SkillButton button, GuiGraphicsExtractor graphics, float x, float y, int width, int height) {
        Font font = Minecraft.getInstance().font;
        int maxWidth = width - 10;
        // Les descriptions longues (nœuds combinant plusieurs enchantements/effets
        // spéciaux, ex. Mace Mastery) tenaient auparavant sur une seule ligne géante
        // car le seuil de wrap était la largeur de l'écran entière (maxWidth) - jamais
        // atteint en pratique. On ne réduit le wrap qu'à moitié de maxWidth, et
        // seulement pour les lignes qui dépassent déjà ce seuil réduit : les
        // descriptions courtes (majorité des nœuds blacksmith/hunter/etc.) restent sur
        // une seule ligne, inchangées.
        int wrapWidth = maxWidth / 2;
        List<MutableComponent> tooltip = new ArrayList<>();
        for (MutableComponent component : button.getSkillTooltip(skillTree)) {
            if (font.width(component) > wrapWidth) {
                tooltip.addAll(TooltipHelper.split(component, font, wrapWidth));
            } else {
                tooltip.add(component);
            }
        }
        if (tooltip.isEmpty()) {
            return;
        }
        int tooltipWidth = 0;
        int tooltipHeight = tooltip.size() == 1 ? 8 : 10;
        for (MutableComponent component : tooltip) {
            int k = font.width(component);
            if (k > tooltipWidth) {
                tooltipWidth = k;
            }
            tooltipHeight += font.lineHeight + 2;
        }
        tooltipWidth += 42;
        float tooltipX = x + 12;
        float tooltipY = y - 12;
        if (tooltipX + tooltipWidth > width) {
            tooltipX -= 28 + tooltipWidth;
        }
        if (tooltipY + tooltipHeight + 6 > height) {
            tooltipY = height - tooltipHeight - 6;
        }
        if (tooltipX < 5) {
            tooltipX = 5;
        }
        if (tooltipY < 5) {
            tooltipY = 5;
        }
        graphics.pose().pushMatrix();
        graphics.pose().translate(tooltipX, tooltipY);
        graphics.fill(1, 4, (int) (tooltipWidth - 1), tooltipHeight + 4, 0xDD000000);
        int textX = 5;
        int textY = 2;
        Identifier texture = button.skill.getTooltipFrameTexture();
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, -4, -4, 0F, 0F, 21, 20, 110, 20);
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, (int) (tooltipWidth + 4 - 21), -4, 89F, 0F, 21, 20, 110, 20);
        int centerWidth = (int) (tooltipWidth + 8 - 42);
        int centerX = -4 + 21;
        while (centerWidth > 0) {
            int partWidth = Math.min(centerWidth, 68);
            graphics.blit(RenderPipelines.GUI_TEXTURED, texture, centerX, -4, 21F, 0F, partWidth, 20, 110, 20);
            centerX += partWidth;
            centerWidth -= partWidth;
        }
        MutableComponent title = tooltip.remove(0);
        graphics.centeredText(font, title, (int) (tooltipWidth / 2), textY, ARGB.opaque(0xFFFFFF));
        textY += 19;
        for (MutableComponent component : tooltip) {
            graphics.text(font, component, textX, textY, ARGB.opaque(0xFFFFFF), false);
            textY += font.lineHeight + 2;
        }
        graphics.pose().popMatrix();
    }
    public static void renderGatewayConnection(GuiGraphicsExtractor graphics, SkillConnection connection, boolean highlighted, float zoom, float animation) {
        Identifier texture = Identifier.parse("skilltree:textures/screen/long_connection.png");
        graphics.pose().pushMatrix();
        SkillButton button1 = connection.getFirstButton();
        SkillButton button2 = connection.getSecondButton();
        double connectionX = button1.getX() + button1.getWidth() / 2F;
        double connectionY = button1.getY() + button1.getHeight() / 2F;
        graphics.pose().translate((float) connectionX, (float) connectionY);
        float rotation = ScreenHelper.getAngleBetweenButtons(button1, button2);
        graphics.pose().rotate(rotation);
        int length = (int) (ScreenHelper.getDistanceBetweenButtons(button1, button2) / zoom);
        graphics.pose().scale(zoom, zoom);
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, 0, -8, -animation, highlighted ? 0F : 6F, length, 6, 30, 12);
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, 0, 2, animation, highlighted ? 0F : 6F, length, 6, 30, 12);
        graphics.pose().popMatrix();
    }
    public static void renderOneWayConnection(GuiGraphicsExtractor graphics, SkillConnection connection, boolean highlighted, float zoom, float animation) {
        Identifier texture = Identifier.parse("skilltree:textures/screen/one_way_connection.png");
        graphics.pose().pushMatrix();
        SkillButton button1 = connection.getFirstButton();
        SkillButton button2 = connection.getSecondButton();
        double connectionX = button1.getX() + button1.getWidth() / 2F;
        double connectionY = button1.getY() + button1.getHeight() / 2F;
        graphics.pose().translate((float) connectionX, (float) connectionY);
        float rotation = ScreenHelper.getAngleBetweenButtons(button1, button2);
        graphics.pose().rotate(rotation);
        int length = (int) (ScreenHelper.getDistanceBetweenButtons(button1, button2) / zoom);
        graphics.pose().scale(zoom, zoom);
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, 0, -3, -animation, highlighted ? 0F : 6F, length, 6, 30, 12);
        graphics.pose().popMatrix();
    }
    public static void renderConnection(GuiGraphicsExtractor graphics, SkillConnection connection, float zoom, float animation) {
        Identifier texture = Identifier.parse("skilltree:textures/screen/direct_connection.png");
        graphics.pose().pushMatrix();
        SkillButton button1 = connection.getFirstButton();
        SkillButton button2 = connection.getSecondButton();
        double connectionX = button1.getX() + button1.getWidth() / 2F;
        double connectionY = button1.getY() + button1.getHeight() / 2F;
        graphics.pose().translate((float) connectionX, (float) connectionY);
        float rotation = ScreenHelper.getAngleBetweenButtons(button1, button2);
        graphics.pose().rotate(rotation);
        int length = (int) ScreenHelper.getDistanceBetweenButtons(button1, button2);
        boolean highlighted = button1.skillLearned && button2.skillLearned;
        graphics.pose().scale(1F, zoom);
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, 0, -3, 0F, highlighted ? 0F : 6F, length, 6, 50, 12);
        boolean shouldAnimate = button1.skillLearned && button2.canLearn || button2.skillLearned && button1.canLearn;
        if (!highlighted && shouldAnimate) {
            int tintColor = ARGB.color((Mth.sin(animation / 3F) + 1) / 2, -1);
            graphics.blit(RenderPipelines.GUI_TEXTURED, texture, 0, -3, 0F, 0F, length, 6, 50, 12, tintColor);
        }
        graphics.pose().popMatrix();
    }
}