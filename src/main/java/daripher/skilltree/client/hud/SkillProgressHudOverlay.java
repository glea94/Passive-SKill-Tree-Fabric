package daripher.skilltree.client.hud;

import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.client.screen.ScreenHelper;
import daripher.skilltree.config.ClientConfig;
import daripher.skilltree.config.ServerConfig;
import daripher.skilltree.exp.ExpHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudStatusBarHeightRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class SkillProgressHudOverlay {
    private static final Identifier TEXTURE = Identifier.parse("skilltree:textures/screen/progress_bars.png");
    private static final int BLOCK_WIDTH = 235;
    private static final int BAR_WIDTH = 182;
    private static final int BAR_HEIGHT = 5;
    private static final float TEXT_SCALE = 0.7F;
    private static final int GAP_ABOVE_ARMOR_ROW = 2;

    public static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        if (!ClientConfig.show_hud_progress_bar) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }

        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();
        int blockX = screenWidth / 2 - BLOCK_WIDTH / 2;

        
        
        int armorBarTop = screenHeight - HudStatusBarHeightRegistry.getHeight(VanillaHudElements.ARMOR_BAR);
        
        int textY = armorBarTop - GAP_ABOVE_ARMOR_ROW - 2 - BAR_HEIGHT;

        int currentLevel = getCurrentLevel(player);
        boolean maxLevel = currentLevel >= ServerConfig.max_skill_points;
        if (maxLevel) {
            currentLevel--;
        }
        int nextLevel = currentLevel + 1;

        renderBar(graphics, player, blockX, textY);
        drawScaledCenteredText(graphics, "" + currentLevel, blockX + 17, textY, 0xFCE266);
        drawScaledCenteredText(graphics, "" + nextLevel, blockX + BLOCK_WIDTH - 17, textY, 0xFCE266);
        float progress = getExperienceProgress(player);
        String progressText = (int) (progress * 100) + "%";
        drawScaledCenteredText(graphics, progressText, blockX + BLOCK_WIDTH / 2, textY, 0xFCE266);
    }

    private static void renderBar(GuiGraphicsExtractor graphics, LocalPlayer player, int blockX, int textY) {
        float progress = getExperienceProgress(player);
        int filledWidth = (int) (progress * BAR_WIDTH);
        int barX = blockX + 26;
        int barY = textY + 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, barX, barY, 0F, 0F, BAR_WIDTH, BAR_HEIGHT, 256, 256);
        if (filledWidth > 0) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, barX, barY, 0F, 5F, filledWidth, BAR_HEIGHT, 256, 256);
        }
    }

    private static void drawScaledCenteredText(GuiGraphicsExtractor graphics, String text, int centerX, int centerY, int color) {
        graphics.pose().pushMatrix();
        graphics.pose().translate(centerX, centerY);
        graphics.pose().scale(TEXT_SCALE, TEXT_SCALE);
        ScreenHelper.drawCenteredOutlinedText(graphics, text, 0, 0, color);
        graphics.pose().popMatrix();
    }

    private static int getCurrentLevel(LocalPlayer player) {
        IPlayerSkills capability = PlayerSkillsProvider.get(player);
        return capability.getPlayerSkills().size() + capability.getSkillPoints();
    }

    private static float getExperienceProgress(LocalPlayer player) {
        int level = getCurrentLevel(player);
        if (level >= ServerConfig.max_skill_points) {
            return 1F;
        }
        int levelupCost = ServerConfig.getSkillPointCost(level);
        float progress = (float) ExpHelper.getPlayerExp(player) / levelupCost;
        return Math.min(1F, progress);
    }
}