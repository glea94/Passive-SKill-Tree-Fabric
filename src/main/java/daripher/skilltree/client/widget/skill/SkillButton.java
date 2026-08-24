package daripher.skilltree.client.widget.skill;

import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.config.ClientConfig;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.player.BrokenSkillBonus;
import daripher.skilltree.skill.requirement.SkillRequirement;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class SkillButton extends Button {
    private static final Style DESCRIPTION_STYLE = Style.EMPTY.withColor(0x7B7BE5);
    private static final Style ID_STYLE = Style.EMPTY.withColor(0x545454);
    private final Supplier<Float> animationFunction;
    public final PassiveSkill skill;
    public float x;
    public float y;
    public boolean skillLearned;
    public boolean canLearn;
    public boolean searched;
    public boolean selected;
    public boolean hasBrokenBonuses;

    public SkillButton(Supplier<Float> animationFunc, float x, float y, PassiveSkill skill) {
        
        super((int) x, (int) y, skill.getSkillSize(), skill.getSkillSize(), Component.empty(), b -> {}, DEFAULT_NARRATION);
        this.x = x;
        this.y = y;
        this.skill = skill;
        this.animationFunction = animationFunc;
        
        this.active = false;
        this.hasBrokenBonuses = skill.getBonuses().stream().anyMatch(bonus -> bonus instanceof BrokenSkillBonus);
    }

    private static final int WHITE = 0xFFFFFFFF;

    private static int argb(float r, float g, float b, float a) {
        return ((int) (Mth.clamp(a, 0F, 1F) * 255) << 24)
                | ((int) (Mth.clamp(r, 0F, 1F) * 255) << 16)
                | ((int) (Mth.clamp(g, 0F, 1F) * 255) << 8)
                | (int) (Mth.clamp(b, 0F, 1F) * 255);
    }

    @Override
    protected void extractContents(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {

        graphics.pose().pushMatrix();
        graphics.pose().translate(x, y);

        int currentWidth = this.getWidth();
        int currentHeight = this.getHeight();

        if (hasBrokenBonuses) {
            Identifier brokenTexture = Identifier.parse("skilltree:textures/icons/broken_skill.png");
            graphics.blit(RenderPipelines.GUI_TEXTURED, brokenTexture, 0, 0, 0F, 0F, currentWidth, currentHeight, currentWidth, currentHeight, currentWidth, currentHeight);
            graphics.pose().popMatrix();
            return;
        }
        renderFavoriteSkillHighlight(graphics);
        renderBackground(graphics, WHITE);
        graphics.pose().pushMatrix();
        graphics.pose().translate(currentWidth / 2f, currentHeight / 2f);
        graphics.pose().scale(0.5F, 0.5F);
        if (currentWidth == 32) {
            graphics.pose().scale(0.75F, 0.75F);
        }
        graphics.pose().translate(-currentWidth / 2f, -currentHeight / 2f);
        renderIcon(graphics, WHITE);
        graphics.pose().popMatrix();
        float animation = (Mth.sin(animationFunction.get() / 3F) + 1) / 2;
        float rb = searched ? 0.1f : 1f;
        int darkeningColor = (canLearn || searched) ? argb(rb, 1F, rb, 1 - animation) : WHITE;
        if (!skillLearned) {
            renderDarkening(graphics, darkeningColor);
        }
        int frameColor = (canLearn || searched) ? argb(rb, 1F, rb, animation) : WHITE;
        if (skillLearned || canLearn || searched) {
            renderFrame(graphics, frameColor);
        }
        graphics.pose().popMatrix();
    }

    private void renderFavoriteSkillHighlight(GuiGraphicsExtractor graphics) {
        if (!ClientConfig.favorite_skills.contains(skill.getId())) {
            return;
        }
        Identifier texture = Identifier.parse("skilltree:textures/screen/favorite_skill.png");
        int color;
        if (ClientConfig.favorite_color_is_rainbow) {
            color = Color.getHSBColor(animationFunction.get() / 240f, 1f, 1f).getRGB();
        } else {
            color = ClientConfig.favorite_color;
        }
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = ((color) & 0xFF) / 255f;
        int tint = argb(r, g, b, 1f);

        int currentWidth = this.getWidth();
        int currentHeight = this.getHeight();
        int size = (int) (currentWidth * 1.4);

        graphics.pose().pushMatrix();
        graphics.pose().translate(currentWidth / 2f, currentHeight / 2f);
        float animation = 1 + 0.3f * (Mth.sin(animationFunction.get() / 3F) + 1) / 2;
        graphics.pose().scale(animation, animation);
        graphics.pose().rotate((float) Math.toRadians(animationFunction.get()));
        graphics.pose().translate(-size / 2f, -size / 2f);
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, 0, 0, 0F, 0F, size, size, 80, 80, 80, 80, tint);
        graphics.pose().popMatrix();
    }

    private void renderFrame(GuiGraphicsExtractor graphics, int color) {
        Identifier texture = skill.getFrameTexture();
        int currentWidth = this.getWidth();
        int currentHeight = this.getHeight();
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, 0, 0, currentWidth * 2, 0F, currentWidth, currentHeight, currentWidth, currentHeight, currentWidth * 3, currentHeight, color);
    }
    private void renderDarkening(GuiGraphicsExtractor graphics, int color) {
        Identifier texture = skill.getFrameTexture();
        int currentWidth = this.getWidth();
        int currentHeight = this.getHeight();
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, 0, 0, currentWidth, 0F, currentWidth, currentHeight, currentWidth, currentHeight, currentWidth * 3, currentHeight, color);
    }

    private void renderIcon(GuiGraphicsExtractor graphics, int color) {
        Identifier texture = skill.getIconTexture();
        int currentWidth = this.getWidth();
        int currentHeight = this.getHeight();
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, 0, 0, 0F, 0F, currentWidth, currentHeight, currentWidth, currentHeight, currentWidth, currentHeight, color);
    }

    private void renderBackground(GuiGraphicsExtractor graphics, int color) {
        Identifier texture = skill.getFrameTexture();
        int currentWidth = this.getWidth();
        int currentHeight = this.getHeight();
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, 0, 0, 0F, 0F, currentWidth, currentHeight, currentWidth, currentHeight, currentWidth * 3, currentHeight, color);
    }

    public void setButtonSize(int size) {
        
        this.setWidth(size);
        this.setHeight(size);
    }

    public List<MutableComponent> getSkillTooltip(PassiveSkillTree skillTree) {
        ArrayList<MutableComponent> tooltip = new ArrayList<>();
        addTitleTooltip(tooltip);
        addLimitationsTooltip(skillTree, tooltip);
        List<MutableComponent> description = skill.getDescription();
        if (description != null) {
            tooltip.addAll(description);
        } else {
            addSkillBonusTooltip(tooltip);
        }
        addRequirementsTooltip(tooltip);
        addAdvancedTooltip(tooltip);
        return tooltip;
    }

    public void addRequirementsTooltip(ArrayList<MutableComponent> tooltip) {
        if (skill.getRequirements().isEmpty()) {
            return;
        }
        if (tooltip.size() > 1) {
            tooltip.add(Component.empty());
        }
        MutableComponent requirementsComponent = Component.translatable("skill.requirements");
        requirementsComponent = requirementsComponent.withStyle(TooltipHelper.getSkillBonusStyle(true));
        tooltip.add(requirementsComponent);
        skill.getRequirements().forEach(requirement -> addRequirementTooltip(tooltip, requirement));
    }

    private void addRequirementTooltip(ArrayList<MutableComponent> tooltip, SkillRequirement<?> requirement) {
        MutableComponent requirementTooltip = requirement.getTooltip();
        Player localPlayer = Minecraft.getInstance().player;
        Style style = TooltipHelper.getSkillRequirementStyle(requirement.test(localPlayer));
        requirementTooltip = requirementTooltip.withStyle(style);
        tooltip.add(Component.literal("  ").append(requirementTooltip));
    }

    public void addSkillBonusTooltip(List<MutableComponent> tooltip) {
        addDescriptionTooltip(tooltip);
        addInfoTooltip(tooltip);
    }

    private void addInfoTooltip(List<MutableComponent> tooltip) {
        
        
        
        
        
        boolean altDown = InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), GLFW.GLFW_KEY_LEFT_ALT)
                || InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), GLFW.GLFW_KEY_RIGHT_ALT);
        if (!altDown) {
            return;
        }
        List<MutableComponent> info = new ArrayList<>();
        for (SkillBonus<?> skillBonus : skill.getBonuses()) {
            skillBonus.gatherInfo(component -> {
                component = component.withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY);
                info.add(component);
            });
        }
        if (!info.isEmpty()) {
            tooltip.add(Component.empty());
            tooltip.addAll(info);
        }
    }

    protected void addAdvancedTooltip(List<MutableComponent> tooltip) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.options.advancedItemTooltips) {
            return;
        }
        addIdTooltip(tooltip);
    }

    protected void addDescriptionTooltip(List<MutableComponent> tooltip) {
        skill.getBonuses().stream().map(SkillBonus::getFullTooltip).forEach(tooltip::addAll);
        String descriptionId = getSkillId() + ".description";
        String description = Component.translatable(descriptionId).getString();
        if (!description.equals(descriptionId)) {
            List<String> descriptionStrings = Arrays.asList(description.split("/n"));
            descriptionStrings.stream().map(Component::translatable).map(this::applyDescriptionStyle).forEach(tooltip::add);
        }
    }

    private void addLimitationsTooltip(PassiveSkillTree skillTree, ArrayList<MutableComponent> tooltips) {
        boolean addedLimitTooltip = false;
        for (String tag : skill.getTags()) {
            int limit = skillTree.getSkillLimitations().getOrDefault(tag, 0);
            if (limit <= 0) {
                continue;
            }
            addedLimitTooltip = true;
            AtomicReference<MutableComponent> tagTooltip = new AtomicReference<>(Component.literal(tag));
            TooltipHelper.consumeTranslated("skill.tag.%s.name".formatted(tag), tagTooltip::set);
            tagTooltip.set(Component.literal(limit + " " + tagTooltip.get().getString()));
            tagTooltip.set(tagTooltip.get().withStyle(TooltipHelper.getSkillBonusSecondStyle(true)));
            MutableComponent tooltip = Component.translatable("skill.limitation", tagTooltip.get());
            tooltip = tooltip.withStyle(TooltipHelper.getSkillBonusStyle(true));
            tooltips.add(tooltip);
        }
        if (addedLimitTooltip) {
            tooltips.add(Component.empty());
        }
    }

    protected void addTitleTooltip(List<MutableComponent> tooltip) {
        tooltip.add(TooltipHelper.getSkillTitle(skill));
    }

    protected void addIdTooltip(List<MutableComponent> tooltip) {
        MutableComponent idComponent = Component.literal(skill.getId().toString()).withStyle(ID_STYLE);
        tooltip.add(idComponent);
    }

    protected MutableComponent applyDescriptionStyle(MutableComponent component) {
        return component.withStyle(DESCRIPTION_STYLE);
    }

    public void setCanLearn() {
        canLearn = true;
    }

    public void setActive() {
        
        this.active = true;
    }

    private String getSkillId() {
        return "skill." + skill.getId().getNamespace() + "." + skill.getId().getPath();
    }
}