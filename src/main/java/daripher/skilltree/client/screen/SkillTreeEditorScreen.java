package daripher.skilltree.client.screen;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.mixin.AbstractWidgetAccessor;
import daripher.skilltree.data.client.SkillTreeEditorData;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.editor.menu.SkillNodeEditor;
import daripher.skilltree.client.widget.skill.SkillButtons;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;


public class SkillTreeEditorScreen extends Screen {
    private final PassiveSkillTree skillTree;
    private final SkillButtons skillButtons;
    private final SkillTreeEditor editorWidgets;
    private boolean shouldCloseOnEsc = true;
    private int prevMouseX;
    private int prevMouseY;
    private boolean statsUpdated;

    public SkillTreeEditorScreen(Identifier skillTreeId) {
        super(Component.empty());
<<<<<<< Updated upstream
        
=======
>>>>>>> Stashed changes
        this.skillTree = SkillTreeEditorData.getOrCreateEditorTree(skillTreeId);
        this.skillButtons = new SkillButtons(skillTree, () -> 0f);
        this.editorWidgets = new SkillTreeEditor(skillButtons);
    }

    @Override
    public void init() {
        if (!statsUpdated) {
            ClientPacketListener connection = this.minecraft.getConnection();
            Objects.requireNonNull(connection);
            connection.send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.REQUEST_STATS));
        }
        if (skillTree == null) {
            this.minecraft.gui.setScreen(null);
            return;
        }
        clearWidgets();
        ((AbstractWidgetAccessor) (Object) skillButtons).setWidth(this.width);
        ((AbstractWidgetAccessor) (Object) skillButtons).setHeight(this.height);
        ((AbstractWidgetAccessor) (Object) editorWidgets).setWidth(210);
        ((AbstractWidgetAccessor) (Object) editorWidgets).setHeight(10);
<<<<<<< Updated upstream

        
=======
>>>>>>> Stashed changes
        editorWidgets.setX(this.width - editorWidgets.getWidth());
        editorWidgets.init();
        editorWidgets.increaseHeight(5);
        editorWidgets.setRebuildFunc(this::rebuildWidgets);
        skillButtons.setRebuildFunc(this::rebuildWidgets);
        skillButtons.clearWidgets();
        editorWidgets.getSkills().forEach(editorWidgets::addSkillButton);
        skillButtons.updateSkillConnections();
        calculateMaxScroll();
        addRenderableWidget(skillButtons);
        addRenderableWidget(editorWidgets);
    }

    @Override
    protected void rebuildWidgets() {
        this.minecraft.execute(super::rebuildWidgets);
    }

    private void calculateMaxScroll() {
        skillButtons.setMaxScrollX(Math.min(0, this.width / 2 - 350));
        skillButtons.setMaxScrollY(Math.min(0, this.height / 2 - 350));
        skillButtons.getWidgets().forEach(button -> {
            float skillX = button.skill.getPositionX();
            float skillY = button.skill.getPositionY();
            int maxScrollX = (int) Math.max(skillButtons.getMaxScrollX(), Mth.abs(skillX));
            int maxScrollY = (int) Math.max(skillButtons.getMaxScrollY(), Mth.abs(skillY));
            skillButtons.setMaxScrollX(maxScrollX);
            skillButtons.setMaxScrollY(maxScrollY);
        });
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
<<<<<<< Updated upstream
        
=======
>>>>>>> Stashed changes
        renderBackground(graphics);
        skillButtons.extractRenderState(graphics, mouseX, mouseY, partialTick);
        renderOverlay(graphics);
        editorWidgets.extractRenderState(graphics, mouseX, mouseY, partialTick);
        if (mouseX < editorWidgets.getX() || mouseY > editorWidgets.getHeight()) {
            float tooltipX = mouseX + (prevMouseX - mouseX) * partialTick;
            float tooltipY = mouseY + (prevMouseY - mouseY) * partialTick;
            skillButtons.renderTooltip(graphics, tooltipX, tooltipY);
        }
        prevMouseX = mouseX;
        prevMouseY = mouseY;
    }
    private void createBlankSkill() {
        Identifier background = Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "textures/icons/background/lesser.png");
        Identifier icon = Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "textures/icons/void.png");
        Identifier border = Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "textures/tooltip/lesser.png");
        Identifier skillId = SkillNodeEditor.createNewSkillId(skillTree.getId());
        PassiveSkill skill = new PassiveSkill(skillId, 16, background, icon, border, false);
        skill.setPosition(0, 0);
        SkillTreeEditorData.saveEditorSkill(skill);
        SkillTreeEditorData.loadEditorSkill(skill.getId());
        editorWidgets.getSkillTree().getSkillIds().add(skill.getId());
        SkillTreeEditorData.saveEditorSkillTree(editorWidgets.getSkillTree());
    }

    @Override
    public boolean shouldCloseOnEsc() {
        if (!shouldCloseOnEsc) {
            shouldCloseOnEsc = true;
            return false;
        }
        return super.shouldCloseOnEsc();
    }

    @Override
    public void tick() {
        if (!statsUpdated) {
            statsUpdated = true;
            init();
        }
        editorWidgets.onWidgetTick();
    }

    private void renderOverlay(GuiGraphicsExtractor graphics) {
        Identifier texture = Identifier.fromNamespaceAndPath("skilltree", "textures/screen/skill_tree_overlay.png");
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, 0, 0, 0F, 0F, this.width, this.height, this.width, this.height);
    }

    public void renderBackground(@NotNull GuiGraphicsExtractor graphics) {
        Identifier texture = Identifier.fromNamespaceAndPath("skilltree", "textures/screen/skill_tree_background.png");
        Matrix3x2fStack poseStack = graphics.pose();
        poseStack.pushMatrix();
        poseStack.translate(skillButtons.getScrollX() / 3F, skillButtons.getScrollY() / 3F);
        int size = SkillTreeScreen.BACKGROUND_SIZE;
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, (this.width - size) / 2, (this.height - size) / 2, 0F, 0F, size, size, size, size);
        poseStack.popMatrix();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean doubleClick) {
        return editorWidgets.mouseClicked(mouseButtonEvent, doubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent mouseButtonEvent) {
        return editorWidgets.mouseReleased(mouseButtonEvent);
    }
<<<<<<< Updated upstream

    
=======
>>>>>>> Stashed changes
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return editorWidgets.mouseScrolled(mouseX, mouseY, 0, scrollY) || skillButtons.mouseScrolled(mouseX, mouseY, 0, scrollY);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent mouseButtonEvent, double dragX, double dragY) {
        return editorWidgets.mouseDragged(mouseButtonEvent, dragX, dragY) | skillButtons.mouseDragged(mouseButtonEvent, dragX, dragY);
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        if (editorWidgets.keyPressed(keyEvent)) {
            if (keyEvent.isEscape()) {
                shouldCloseOnEsc = false;
            }
            return true;
        }
        if (keyEvent.isEscape()) {
            if (shouldCloseOnEsc()) {
                onClose();
                return true;
            }
        }
        if (keyEvent.key() == GLFW.GLFW_KEY_N && keyEvent.hasControlDown()) {
            createBlankSkill();
            rebuildWidgets();
            return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(CharacterEvent characterEvent) {
        return editorWidgets.charTyped(characterEvent);
    }
}