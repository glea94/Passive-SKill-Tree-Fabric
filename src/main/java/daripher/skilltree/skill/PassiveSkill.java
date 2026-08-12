package daripher.skilltree.skill;

import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.requirement.SkillRequirement;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PassiveSkill {
    private final Identifier id;
    private final List<SkillBonus<?>> bonuses = new ArrayList<>();
    private @Nullable List<SkillRequirement<?>> requirements;
    private @Nullable List<Identifier> directConnections = new ArrayList<>();
    private @Nullable List<Identifier> longConnections = new ArrayList<>();
    private @Nullable List<Identifier> oneWayConnections = new ArrayList<>();
    private @Nullable List<String> tags = new ArrayList<>();
    private Identifier backgroundTexture;
    private Identifier iconTexture;
    private Identifier borderTexture;
    private @Nullable String title;
    private @Nullable String titleColor;
    private float positionX, positionY;
    private int buttonSize;
    private boolean isStartingPoint;
    private boolean isAlwaysStartingPoint;
    private @Nullable List<MutableComponent> description;

    public PassiveSkill(Identifier id, int buttonSize, Identifier backgroundTexture, Identifier iconTexture, Identifier borderTexture, boolean isStartingPoint) {
        this.id = id;
        this.backgroundTexture = backgroundTexture;
        this.iconTexture = iconTexture;
        this.borderTexture = borderTexture;
        this.buttonSize = buttonSize;
        this.isStartingPoint = isStartingPoint;
    }

    public Identifier getId() {
        return id;
    }

    public int getSkillSize() {
        return buttonSize;
    }

    public void setButtonSize(int buttonSize) {
        this.buttonSize = buttonSize;
    }

    public Identifier getFrameTexture() {
        return backgroundTexture;
    }

    public void setBackgroundTexture(Identifier texture) {
        this.backgroundTexture = texture;
    }

    public Identifier getIconTexture() {
        return iconTexture;
    }

    public void setIconTexture(Identifier texture) {
        this.iconTexture = texture;
    }

    public Identifier getTooltipFrameTexture() {
        return borderTexture;
    }

    public void setBorderTexture(Identifier texture) {
        this.borderTexture = texture;
    }

    public boolean isStartingPoint() {
        return isStartingPoint || isAlwaysStartingPoint;
    }

    public void setStartingPoint(boolean isStartingPoint) {
        this.isStartingPoint = isStartingPoint;
    }

    public boolean isAlwaysStartingPoint() {
        return isAlwaysStartingPoint;
    }

    public void setAlwaysStartingPoint(boolean alwaysStartingPoint) {
        isAlwaysStartingPoint = alwaysStartingPoint;
    }

    public List<SkillBonus<?>> getBonuses() {
        return bonuses;
    }

    public @NotNull List<SkillRequirement<?>> getRequirements() {
        if (requirements == null) {
            return requirements = new ArrayList<>();
        }
        return requirements;
    }
    public void addSkillBonus(SkillBonus<?> bonus) {
        bonuses.add(bonus);
    }

    public void addSkillRequirement(SkillRequirement<?> requirement) {
        getRequirements().add(requirement);
    }

    public void connect(PassiveSkill otherSkill) {
        getDirectConnections().add(otherSkill.getId());
    }

    public void setPosition(float x, float y) {
        positionX = x;
        positionY = y;
    }

    public float getPositionX() {
        return positionX;
    }

    public float getPositionY() {
        return positionY;
    }

    @NotNull
    public List<Identifier> getDirectConnections() {
        if (directConnections == null) {
            return directConnections = new ArrayList<>();
        }
        return directConnections;
    }

    @NotNull
    public List<Identifier> getLongConnections() {
        if (longConnections == null) {
            return longConnections = new ArrayList<>();
        }
        return longConnections;
    }

    @NotNull
    public List<Identifier> getOneWayConnections() {
        if (oneWayConnections == null) {
            return oneWayConnections = new ArrayList<>();
        }
        return oneWayConnections;
    }

    @NotNull
    public List<String> getTags() {
        if (tags == null) {
            return tags = new ArrayList<>();
        }
        return tags;
    }

    public @NotNull String getTitle() {
        return title == null ? "" : title;
    }

    public void setTitle(@NotNull String title) {
        this.title = title.isEmpty() ? null : title;
    }

    public void learn(ServerPlayer player, boolean firstTime) {
        getBonuses().forEach(bonus -> bonus.onSkillLearned(player, firstTime));
    }

    public void setTitleColor(@Nullable String color) {
        this.titleColor = color;
    }

    public @NotNull String getTitleColor() {
        return titleColor == null ? "" : titleColor;
    }

    public @Nullable List<MutableComponent> getDescription() {
        return description;
    }

    public void setDescription(@Nullable List<MutableComponent> description) {
        this.description = description;
    }

    public void remove(ServerPlayer player) {
        getBonuses().forEach(bonus -> bonus.onSkillRemoved(player));
    }

    public boolean isInvalid() {
        return getId() == null || getBonuses() == null || getFrameTexture() == null || getIconTexture() == null || getTooltipFrameTexture() == null;
    }
}
