package daripher.skilltree.client.screen;

<<<<<<< Updated upstream
<<<<<<< Updated upstream
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
=======
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
import daripher.skilltree.client.widget.SkillTreeWidgets;
import daripher.skilltree.mixin.AbstractWidgetAccessor;
import daripher.skilltree.client.widget.skill.SkillButtons;
import daripher.skilltree.config.ClientConfig;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
<<<<<<< Updated upstream
<<<<<<< Updated upstream
=======
import net.minecraft.client.renderer.RenderPipelines;
>>>>>>> Stashed changes
=======
import net.minecraft.client.renderer.RenderPipelines;
>>>>>>> Stashed changes
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;
import java.util.stream.Stream;

// CORRECTION 1.21.1: Removed 'implements StatsUpdateListener' completely
public class SkillTreeScreen extends Screen {
    public static final int BACKGROUND_SIZE = 2048;
    private final PassiveSkillTree skillTree;
    private final SkillButtons skillButtons;
    private final SkillTreeWidgets skillTreeWidgets;
    public float renderAnimation;
    private int prevMouseX;
    private int prevMouseY;
    private boolean statsUpdated;

    public SkillTreeScreen(ResourceLocation skillTreeId) {
        super(Component.empty());
        this.skillTree = SkillTreesReloader.getSkillTreeById(skillTreeId);
        this.minecraft = Minecraft.getInstance();
        this.skillButtons = new SkillButtons(skillTree, () -> renderAnimation);
        this.skillTreeWidgets = new SkillTreeWidgets(getLocalPlayer(), skillButtons, skillTree);
        this.skillButtons.setRebuildFunc(this::rebuildWidgets);
        this.skillTreeWidgets.setRebuildFunc(this::rebuildWidgets);
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
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderAnimation += partialTick;
<<<<<<< Updated upstream
<<<<<<< Updated upstream
=======
        // Fix 1.21.5 : la méthode renderBackground prend uniquement GuiGraphicsExtractor en paramètre (pattern déjà validé dans SkillTreeEditorScreen.java / SkillTreeSelectionScreen.java)
>>>>>>> Stashed changes
=======
        // Fix 1.21.5 : la méthode renderBackground prend uniquement GuiGraphicsExtractor en paramètre (pattern déjà validé dans SkillTreeEditorScreen.java / SkillTreeSelectionScreen.java)
>>>>>>> Stashed changes
        renderBackground(graphics);
        skillButtons.render(graphics, mouseX, mouseY, partialTick);
        renderOverlay(graphics);
        skillTreeWidgets.render(graphics, mouseX, mouseY, partialTick);
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
        return skillButtons.mouseClicked(mouseButtonEvent, doubleClick);
    }

    // CORRECTION 1.21.1: Replaces legacy onStatsUpdated updates inline during widget ticks safely
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
                Objects.requireNonNull(this.minecraft).gui.setScreen(new SkillTreeSelectionScreen());
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

<<<<<<< Updated upstream
<<<<<<< Updated upstream
    private void renderOverlay(GuiGraphics graphics) {
        ResourceLocation texture = new ResourceLocation("skilltree:textures/screen/skill_tree_overlay.png");
        RenderSystem.enableBlend();
        graphics.blit(texture, 0, 0, 0, 0F, 0F, width, height, width, height);
        RenderSystem.disableBlend();
    }

    @Override
    public void renderBackground(GuiGraphics graphics) {
        ResourceLocation texture = new ResourceLocation("skilltree:textures/screen/skill_tree_background.png");
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
=======
=======
>>>>>>> Stashed changes
    private void renderOverlay(GuiGraphicsExtractor graphics) {
        // CORRECTION 1.21.1: Modern factory constructor pattern
        Identifier texture = Identifier.fromNamespaceAndPath("skilltree", "textures/screen/skill_tree_overlay.png");
        // Fix 1.21.8 : RenderPipelines.GUI_TEXTURED gère déjà le blending, pas besoin de RenderSystem.enableBlend()/disableBlend()
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, 0, 0, 0F, 0F, width, height, width, height);
    }

    // Fix 1.21.5 : renderBackground n'est plus surchargeable avec l'ancienne signature (GuiGraphicsExtractor, int, int, float) -> plus de @Override, signature réduite à (GuiGraphicsExtractor)
    public void renderBackground(@NotNull GuiGraphicsExtractor graphics) {
        // CORRECTION 1.21.1: Modern factory constructor pattern
        Identifier texture = Identifier.fromNamespaceAndPath("skilltree", "textures/screen/skill_tree_background.png");
        Matrix3x2fStack poseStack = graphics.pose();
        poseStack.pushMatrix();
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
        float x = skillButtons.getScrollX();
        float y = skillButtons.getScrollY();
        if (ClientConfig.skill_tree_background_parallax) {
            x /= 3f;
            y /= 3f;
        }
        poseStack.translate(x, y, 0);
        int size = BACKGROUND_SIZE;
<<<<<<< Updated upstream
<<<<<<< Updated upstream
        graphics.blit(texture, (width - size) / 2, (height - size) / 2, 0, 0F, 0F, size, size, size, size);
        poseStack.popPose();
=======
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, (width - size) / 2, (height - size) / 2, 0F, 0F, size, size, size, size);
        poseStack.popMatrix();
>>>>>>> Stashed changes
=======
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, (width - size) / 2, (height - size) / 2, 0F, 0F, size, size, size, size);
        poseStack.popMatrix();
>>>>>>> Stashed changes
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