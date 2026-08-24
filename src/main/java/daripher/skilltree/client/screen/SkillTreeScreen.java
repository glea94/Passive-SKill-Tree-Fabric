package daripher.skilltree.client.screen;

import daripher.skilltree.client.widget.NavigationArrowButton;
import daripher.skilltree.client.widget.SkillTreeWidgets;
import daripher.skilltree.mixin.AbstractWidgetAccessor;
import daripher.skilltree.client.widget.skill.SkillButtons;
import daripher.skilltree.config.ClientConfig;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;


public class SkillTreeScreen extends Screen {
    public static final int BACKGROUND_SIZE = 2048;
    private static final int NAV_ARROW_SIZE = 16;
    private final PassiveSkillTree skillTree;
    private final SkillButtons skillButtons;
    private final SkillTreeWidgets skillTreeWidgets;
    public float renderAnimation;
    private int prevMouseX;
    private int prevMouseY;
    private boolean statsUpdated;
    private @Nullable Identifier prevTreeId;
    private @Nullable Identifier nextTreeId;
    private @Nullable NavigationArrowButton prevButton;
    private @Nullable NavigationArrowButton nextButton;

    public SkillTreeScreen(Identifier skillTreeId) {
        super(Component.empty());
        this.skillTree = SkillTreesReloader.getSkillTreeById(skillTreeId);

        this.skillButtons = new SkillButtons(skillTree, () -> renderAnimation);
        this.skillTreeWidgets = new SkillTreeWidgets(getLocalPlayer(), skillButtons, skillTree);
        this.skillButtons.setRebuildFunc(this::rebuildWidgets);
        this.skillTreeWidgets.setRebuildFunc(this::rebuildWidgets);
        updateNavigationTreeIds();
    }



    private void updateNavigationTreeIds() {
        List<Identifier> orderedIds = SkillTreesReloader.getOrderedSkillTreeIds();
        int index = orderedIds.indexOf(skillTree.getId());
        if (index < 0) {
            prevTreeId = null;
            nextTreeId = null;
            return;
        }
        prevTreeId = index > 0 ? orderedIds.get(index - 1) : null;
        nextTreeId = index < orderedIds.size() - 1 ? orderedIds.get(index + 1) : null;
    }

    private void switchToTree(Identifier skillTreeId) {
        Objects.requireNonNull(this.minecraft).setScreen(new SkillTreeScreen(skillTreeId));
    }



    private void addNavigationArrows() {
        prevButton = null;
        nextButton = null;
        if (prevTreeId != null) {
            Identifier targetId = prevTreeId;
            prevButton = new NavigationArrowButton(4, height / 2 - NAV_ARROW_SIZE / 2, NAV_ARROW_SIZE, NAV_ARROW_SIZE, false);
            prevButton.setPressFunc(b -> switchToTree(targetId));
        }
        if (nextTreeId != null) {
            Identifier targetId = nextTreeId;
            nextButton = new NavigationArrowButton(width - 4 - NAV_ARROW_SIZE, height / 2 - NAV_ARROW_SIZE / 2, NAV_ARROW_SIZE, NAV_ARROW_SIZE, true);
            nextButton.setPressFunc(b -> switchToTree(targetId));
        }
    }

    @Override
    public void init() {
        Minecraft minecraft = Objects.requireNonNull(this.minecraft);
        if (!statsUpdated) {
            ClientPacketListener connection = minecraft.getConnection();
            Objects.requireNonNull(connection);
            connection.send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.REQUEST_STATS));
        }
        clearWidgets();
        skillTreeWidgets.clearWidgets();
        ((AbstractWidgetAccessor) skillTreeWidgets).setWidth(width);
        ((AbstractWidgetAccessor) skillTreeWidgets).setHeight(height);
        ((AbstractWidgetAccessor) skillButtons).setWidth(width);
        ((AbstractWidgetAccessor) skillButtons).setHeight(height);
        skillButtons.clearWidgets();
        addSkillButtons();
        skillTreeWidgets.init();
        calculateMaxScroll();
        addRenderableWidget(skillTreeWidgets);
        addRenderableWidget(skillButtons);
        addNavigationArrows();
    }

    private void addSkillButtons() {
        Stream<PassiveSkill> passiveSkills = skillTree.getSkillIds().stream().map(SkillsReloader::getSkillById).filter(Objects::nonNull);
        passiveSkills.forEach(skill -> skillTreeWidgets.addSkillButton(skill, () -> renderAnimation));
        skillButtons.updateSkillConnections();
    }

    @Override
    protected void rebuildWidgets() {
        Objects.requireNonNull(this.minecraft).execute(super::rebuildWidgets);
    }

    private void calculateMaxScroll() {
        skillButtons.setMaxScrollX(Math.min(0, width / 2 - 350));
        skillButtons.setMaxScrollY(Math.min(0, height / 2 - 350));
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
        renderAnimation += partialTick;

        renderBackground(graphics);
        skillButtons.extractRenderState(graphics, mouseX, mouseY, partialTick);
        renderOverlay(graphics);
        skillTreeWidgets.extractRenderState(graphics, mouseX, mouseY, partialTick);

        if (prevButton != null) {
            prevButton.extractRenderState(graphics, mouseX, mouseY, partialTick);
        }
        if (nextButton != null) {
            nextButton.extractRenderState(graphics, mouseX, mouseY, partialTick);
        }
        float tooltipX = mouseX + (prevMouseX - mouseX) * partialTick;
        float tooltipY = mouseY + (prevMouseY - mouseY) * partialTick;
        skillButtons.renderTooltip(graphics, tooltipX, tooltipY);
        prevMouseX = mouseX;
        prevMouseY = mouseY;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean doubleClick) {
        if (skillTreeWidgets.mouseClicked(mouseButtonEvent, doubleClick)) {
            return true;
        }
        if (skillButtons.mouseClicked(mouseButtonEvent, doubleClick)) {
            return true;
        }

        if (prevButton != null && prevButton.isMouseOver(mouseButtonEvent.x(), mouseButtonEvent.y())) {
            return prevButton.mouseClicked(mouseButtonEvent, doubleClick);
        }
        if (nextButton != null && nextButton.isMouseOver(mouseButtonEvent.x(), mouseButtonEvent.y())) {
            return nextButton.mouseClicked(mouseButtonEvent, doubleClick);
        }
        return false;
    }


    @Override
    public void tick() {
        if (!statsUpdated) {
            statsUpdated = true;
            init();
        }
        skillTreeWidgets.onWidgetTick();
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        if (skillTreeWidgets.keyPressed(keyEvent)) {
            return true;
        }
        if (keyEvent.key() == GLFW.GLFW_KEY_ESCAPE) {
            if (SkillTreesReloader.getSkillTrees().size() == 1) {
                onClose();
            } else {
                Objects.requireNonNull(this.minecraft).setScreen(new SkillTreeSelectionScreen());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean keyReleased(KeyEvent keyEvent) {
        return skillTreeWidgets.keyPressed(keyEvent);
    }

    @Override
    public boolean charTyped(CharacterEvent characterEvent) {
        return skillTreeWidgets.charTyped(characterEvent);
    }

    private void renderOverlay(GuiGraphicsExtractor graphics) {

        Identifier texture = Identifier.fromNamespaceAndPath("skilltree", "textures/screen/skill_tree_overlay.png");

        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, 0, 0, 0F, 0F, width, height, width, height);
    }


    public void renderBackground(@NotNull GuiGraphicsExtractor graphics) {

        Identifier texture = Identifier.fromNamespaceAndPath("skilltree", "textures/screen/skill_tree_background.png");
        Matrix3x2fStack poseStack = graphics.pose();
        poseStack.pushMatrix();
        float x = skillButtons.getScrollX();
        float y = skillButtons.getScrollY();
        if (ClientConfig.skill_tree_background_parallax) {
            x /= 3f;
            y /= 3f;
        }
        poseStack.translate(x, y);
        int size = BACKGROUND_SIZE;
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, (width - size) / 2, (height - size) / 2, 0F, 0F, size, size, size, size);
        poseStack.popMatrix();
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent mouseButtonEvent, double dragAmountX, double dragAmountY) {
        return skillButtons.mouseDragged(mouseButtonEvent, dragAmountX, dragAmountY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return skillButtons.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private @NotNull LocalPlayer getLocalPlayer() {
        return Objects.requireNonNull(Objects.requireNonNull(this.minecraft).player);
    }

    public void updateSkillPoints(int skillPoints) {
        skillTreeWidgets.updateSkillPoints(skillPoints);
    }
}