package daripher.skilltree.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
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
        // Fix 1.21.5 : la méthode renderBackground prend uniquement GuiGraphics en paramètre (pattern déjà validé dans SkillTreeEditorScreen.java / SkillTreeSelectionScreen.java)
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (skillTreeWidgets.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return skillButtons.mouseClicked(mouseX, mouseY, button);
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
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (skillTreeWidgets.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
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
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return skillTreeWidgets.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char character, int keyCode) {
        return skillTreeWidgets.charTyped(character, keyCode);
    }

    private void renderOverlay(GuiGraphics graphics) {
        // CORRECTION 1.21.1: Modern factory constructor pattern
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("skilltree", "textures/screen/skill_tree_overlay.png");
        // Fix 1.21.5 : RenderSystem.enableBlend()/disableBlend() supprimés - RenderType.guiTextured gère déjà le blending
        // (pattern déjà validé ailleurs dans le mod : SkillTreeEditorScreen.renderOverlay(), blit identique sans ces appels)
        graphics.blit(RenderType::guiTextured, texture, 0, 0, 0F, 0F, width, height, width, height);
    }

    // Fix 1.21.5 : renderBackground n'est plus surchargeable avec l'ancienne signature (GuiGraphics, int, int, float) -> plus de @Override, signature réduite à (GuiGraphics)
    public void renderBackground(@NotNull GuiGraphics graphics) {
        // CORRECTION 1.21.1: Modern factory constructor pattern
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("skilltree", "textures/screen/skill_tree_background.png");
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        float x = skillButtons.getScrollX();
        float y = skillButtons.getScrollY();
        if (ClientConfig.skill_tree_background_parallax) {
            x /= 3f;
            y /= 3f;
        }
        poseStack.translate(x, y, 0);
        int size = BACKGROUND_SIZE;
        graphics.blit(RenderType::guiTextured, texture, (width - size) / 2, (height - size) / 2, 0F, 0F, size, size, size, size);
        poseStack.popPose();
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragAmountX, double dragAmountY) {
        return skillButtons.mouseDragged(mouseX, mouseY, mouseButton, dragAmountX, dragAmountY);
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