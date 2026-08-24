package daripher.skilltree.client.screen;

import daripher.skilltree.client.widget.SkillTreeSelectionButton;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.skill.PassiveSkillTree;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SkillTreeSelectionScreen extends Screen {
    public static final int BUTTONS_SIZE = 19;
    public static final int BUTTONS_SPACING = 5;

    public SkillTreeSelectionScreen() {
        super(Component.empty());
    }

    @Override
    protected void init() {
        clearWidgets();
        addSkillTreeButtons();
    }

    private void addSkillTreeButtons() {
        List<PassiveSkillTree> skillTrees = getNonEmptySkillTrees();
        int buttonCount = skillTrees.size();
        int buttonRowWidth = buttonCount * BUTTONS_SIZE - (buttonCount - 1) * BUTTONS_SPACING;
        int x = this.width / 2 - buttonRowWidth / 2;
        int y = this.height / 2 - BUTTONS_SIZE / 2;
        for (PassiveSkillTree skillTree : skillTrees) {
            Button button = new SkillTreeSelectionButton(x, y, BUTTONS_SIZE, BUTTONS_SIZE, skillTree.getId());
            x += BUTTONS_SIZE + BUTTONS_SPACING;
            addRenderableWidget(button);
        }
    }

    @NotNull
    private static List<PassiveSkillTree> getNonEmptySkillTrees() {
        return SkillTreesReloader.getSkillTrees().values().stream().filter(skillTree -> !skillTree.getSkillIds().isEmpty()).toList();
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        
        renderBackground(guiGraphics);
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        for (var widget : children()) {
            if (!(widget instanceof SkillTreeSelectionButton button)) {
                continue;
            }
            if (!button.isMouseOver(mouseX, mouseY)) {
                continue;
            }
            guiGraphics.setTooltipForNextFrame(font, button.getMessage(), mouseX, mouseY);
        }
    }

    public void renderBackground(@NotNull GuiGraphicsExtractor guiGraphics) {
        Identifier texture = Identifier.parse("skilltree:textures/screen/skill_tree_background.png");
        int size = SkillTreeScreen.BACKGROUND_SIZE;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, (this.width - size) / 2, (this.height - size) / 2, 0F, 0F, size, size, size, size);
    }
}