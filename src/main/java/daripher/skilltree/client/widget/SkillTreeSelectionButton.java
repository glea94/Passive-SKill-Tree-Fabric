package daripher.skilltree.client.widget;

import daripher.skilltree.client.screen.SkillTreeScreen;
import net.minecraft.client.Minecraft;
<<<<<<< Updated upstream
<<<<<<< Updated upstream
import net.minecraft.client.gui.GuiGraphics;
=======
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
>>>>>>> Stashed changes
=======
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
>>>>>>> Stashed changes
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class SkillTreeSelectionButton extends Button {
    private final Identifier skillTreeId;

    public SkillTreeSelectionButton(int x, int y, int width, int height, Identifier skillTreeId) {
        super(x, y, width, height, Component.translatable(skillTreeId.toString()));
        setPressFunc(b -> onPress(skillTreeId));
        this.skillTreeId = skillTreeId;
    }

    private static void onPress(Identifier skillTreeId) {
        getMinecraft().setScreen(new SkillTreeScreen(skillTreeId));
    }

<<<<<<< Updated upstream
<<<<<<< Updated upstream
    protected void renderBackground(@NotNull GuiGraphics graphics) {
=======
    @Override
    protected void renderBackground(@NotNull GuiGraphicsExtractor graphics) {
>>>>>>> Stashed changes
=======
    @Override
    protected void renderBackground(@NotNull GuiGraphicsExtractor graphics) {
>>>>>>> Stashed changes
        String texturesFolder = "textures/icons/skill_tree/";
        Identifier texture = skillTreeId.withPrefix(texturesFolder).withSuffix(".png");
        int v = getTextureVariant() * 19;
<<<<<<< Updated upstream
        graphics.blit(texture, getX(), getY(), 0, v, width, height, 19, 57);
    }

    protected void renderText(@NotNull GuiGraphics graphics) {
=======

        int currentWidth = this.getWidth();
        int currentHeight = this.getHeight();

        // Fix 1.21.8 : RenderPipelines.GUI_TEXTURED remplace le wrapper RenderType::guiTextured (supprimé)
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), 0F, (float) v, currentWidth, currentHeight, 19, 57);
    }

    @Override
    protected void renderText(@NotNull GuiGraphicsExtractor graphics) {
        // Keeps empty implementation to prevent drawing text directly over custom graphics icons
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
    }

    private static Minecraft getMinecraft() {
        return Minecraft.getInstance();
    }
}