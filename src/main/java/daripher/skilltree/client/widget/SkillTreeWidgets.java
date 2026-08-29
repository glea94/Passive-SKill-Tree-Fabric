package daripher.skilltree.client.widget;
import com.google.common.collect.Streams;
import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.group.WidgetGroup;
import daripher.skilltree.client.widget.skill.SkillButton;
import daripher.skilltree.client.widget.skill.SkillButtons;
import daripher.skilltree.client.widget.skill.SkillConnection;
import daripher.skilltree.client.screen.SkillTreeScreen;
import daripher.skilltree.config.ClientConfig;
import daripher.skilltree.config.ServerConfig;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.event.MaceMasteryEvents;
import daripher.skilltree.exp.ExpHelper;
import daripher.skilltree.client.network.ClientNetworking;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.player.ExecuteCommandBonus;
import daripher.skilltree.skill.bonus.player.RecipeUnlockBonus;
import daripher.skilltree.skill.bonus.player.VanillaRecipeUnlockBonus;
import daripher.skilltree.skill.requirement.SkillRequirement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import java.util.function.Supplier;
public class SkillTreeWidgets extends WidgetGroup<AbstractWidget> {
    private static final Identifier BLACKSMITH_TREE_ID = Identifier.fromNamespaceAndPath("skilltree", "blacksmith");
    private static final Identifier MACE_MASTERY_TREE_ID = Identifier.fromNamespaceAndPath("skilltree", "mace_mastery");
    private static final List<Identifier> MACE_MASTERY_UNLOCK_SKILLS = List.of(
            Identifier.fromNamespaceAndPath("skilltree", "blacksmith_56"),
            Identifier.fromNamespaceAndPath("skilltree", "blacksmith_57"),
            Identifier.fromNamespaceAndPath("skilltree", "blacksmith_58")
    );
    private final SkillButtons skills;
    private final PassiveSkillTree skillTree;
    private final List<Identifier> learnedSkills = new ArrayList<>();
    public final List<Identifier> newlyLearnedSkills = new ArrayList<>();
    private final List<SkillButton> startingPoints = new ArrayList<>();
    private final List<SkillButton> alwaysStartingPoints = new ArrayList<>();
    private Button buyButton;
    private Label pointsInfo;
    private Label treeNameLabel;
    private ProgressBar progressBar;
    private ScrollableComponentList statsInfo;
    public int skillPoints;
    private boolean showStats;
    private boolean showProgressInNumbers;
    private String search = "";
    private final LocalPlayer player;
    public SkillTreeWidgets(LocalPlayer player, SkillButtons skills, PassiveSkillTree skillTree) {
        super(0, 0, 0, 0);
        this.setX(0);
        this.setY(0);
        this.skills = skills;
        this.skillTree = skillTree;
        this.player = player;
        readPlayerData(player);
    }
    public void init() {
        int currentWidth = this.getWidth();
        int currentHeight = this.getHeight();
        progressBar = new ProgressBar(currentWidth / 2 - 235 / 2, currentHeight - 17, b -> toggleProgressDisplayMode());
        progressBar.showProgressInNumbers = showProgressInNumbers;
        addWidget(progressBar);
        addTreeNameLabel(currentWidth, currentHeight);
        addTopWidgets();
        addMaceMasteryButton(currentWidth, currentHeight);
        if (!ServerConfig.enable_exp_exchange) {
            progressBar.visible = false;
            buyButton.visible = false;
        }
        statsInfo = new ScrollableComponentList(48, currentHeight - 60);
        List<Component> statsTooltip = new ArrayList<>(getMergedSkillBonusesTooltips());
        statsTooltip.addAll(getMaceMasteryStatsTooltip());
        statsInfo.setComponents(statsTooltip);
        addWidget(statsInfo);
        startingPoints.clear();
        skills.getWidgets().stream().filter(button -> button.skill.isStartingPoint()).forEach(startingPoints::add);
        skills.getWidgets().stream().filter(button -> button.skill.isAlwaysStartingPoint()).forEach(alwaysStartingPoints::add);
        highlightSkills();
        updateSearch();
    }
    @Override
    protected void extractWidgetRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        updateBuyPointButton();
        Style pointsStyle = Style.EMPTY.withColor(0xFCE266);
        Component pointsLeft = Component.literal("" + skillPoints).withStyle(pointsStyle);
        pointsInfo.setMessage(Component.translatable("widget.skill_points_left", pointsLeft));
        statsInfo.setX(this.getWidth() - statsInfo.getWidth() - 10);
        statsInfo.visible = showStats;
        super.extractWidgetRenderState(graphics, mouseX, mouseY, partialTick);
    }
    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean doubleClick) {
        AbstractWidget widget = getWidgetAt(mouseButtonEvent.x(), mouseButtonEvent.y());
        if (widget != null) {
            widget.setFocused(true);
            return widget.mouseClicked(mouseButtonEvent, doubleClick);
        }
        SkillButton skillButton = skills.getWidgetAt(mouseButtonEvent.x(), mouseButtonEvent.y());
        if (skillButton == null) {
            return false;
        }
        if (mouseButtonEvent.button() == 0) {
            playButtonSound();
            skillButtonPressed(skillButton);
            return true;
        } else if (mouseButtonEvent.button() == 1) {
            ClientConfig.toggleFavoriteSkill(skillButton.skill);
            playButtonSound();
            return true;
        }
        return false;
    }
    private void playButtonSound() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
    private void updateSearch() {
        if (search.isEmpty()) {
            for (SkillButton button : skills.getWidgets()) {
                button.searched = false;
            }
            return;
        }
        outerLoop:
        for (SkillButton button : skills.getWidgets()) {
            for (MutableComponent component : button.getSkillTooltip(skillTree)) {
                if (component.getString().toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT))) {
                    button.searched = true;
                    continue outerLoop;
                }
            }
            button.searched = false;
        }
    }
    private void highlightSkills() {
        if (skillPoints == 0) {
            return;
        }
        if (getLearnedSkillsOnTree().isEmpty() && newlyLearnedSkills.isEmpty()) {
            startingPoints.stream().filter(button -> canLearnSkill(button.skill)).forEach(SkillButton::setCanLearn);
            return;
        }
        if (learnedSkills.size() + newlyLearnedSkills.size() >= ServerConfig.max_skill_points) {
            return;
        }
        alwaysStartingPoints.stream().filter(button -> canLearnSkill(button.skill)).forEach(skillButton -> {
            Identifier skillId = skillButton.skill.getId();
            if (!newlyLearnedSkills.contains(skillId) && !learnedSkills.contains(skillId)) {
                skillButton.setCanLearn();
            }
        });
        skills.getSkillConnections().forEach(connection -> {
            SkillButton button1 = connection.getFirstButton();
            SkillButton button2 = connection.getSecondButton();
            if (button1.skillLearned == button2.skillLearned) {
                return;
            }
            if (connection.getType() != SkillConnection.Type.ONE_WAY) {
                if (!button1.skillLearned && canLearnSkill(button1.skill)) {
                    button1.setCanLearn();
                    button1.setActive();
                }
            }
            if (!button2.skillLearned && canLearnSkill(button2.skill)) {
                button2.setCanLearn();
                button2.setActive();
            }
        });
    }
    private List<Identifier> getLearnedSkillsOnTree() {
        return learnedSkills.stream().filter(skillTree.getSkillIds()::contains).toList();
    }
    private void addTreeNameLabel(int currentWidth, int currentHeight) {
        Component treeName = Component.translatable(skillTree.getId().toString());
        Font font = Minecraft.getInstance().font;
        int textWidth = font.width(treeName);
        treeNameLabel = new Label(currentWidth / 2 - textWidth / 2, currentHeight - 30, treeName);
        addWidget(treeNameLabel);
    }
    private void addTopWidgets() {
        Component buyButtonText = Component.translatable("widget.buy_skill_button");
        Component pointsInfoText = Component.translatable("widget.skill_points_left", 100);
        Component confirmButtonText = Component.translatable("widget.confirm_button");
        Component cancelButtonText = Component.translatable("widget.cancel_button");
        Component showStatsButtonText = Component.translatable("widget.show_stats");
        Font font = Minecraft.getInstance().font;
        int buttonWidth = Math.max(font.width(buyButtonText), font.width(pointsInfoText));
        buttonWidth = Math.max(buttonWidth, font.width(confirmButtonText));
        buttonWidth = Math.max(buttonWidth, font.width(cancelButtonText));
        buttonWidth += 20;
        int buttonsY = 8;
        int currentWidth = this.getWidth();
        Button showStatsButton = new Button(currentWidth - buttonWidth - 8, buttonsY, buttonWidth, 14, showStatsButtonText);
        showStatsButton.setPressFunc(b -> showStats ^= true);
        addWidget(showStatsButton);
        TextField searchField = new TextField(8, buttonsY, buttonWidth, 14, search);
        addWidget(searchField).setHint("Search...").setResponder(s -> {
            search = s;
            updateSearch();
        });
        buyButton = new Button(currentWidth / 2 - 8 - buttonWidth, buttonsY, buttonWidth, 14, buyButtonText);
        buyButton.setPressFunc(b -> buySkillPoint());
        addWidget(buyButton);
        pointsInfo = new Label(currentWidth / 2 + 8, buttonsY, buttonWidth, 14, Component.empty());
        if (!ServerConfig.enable_exp_exchange) {
            pointsInfo.setX(currentWidth / 2 - buttonWidth / 2);
        }
        addWidget(pointsInfo);
        buttonsY += 20;
        Button confirmButton = new Button(currentWidth / 2 - 8 - buttonWidth, buttonsY, buttonWidth, 14, confirmButtonText);
        confirmButton.setPressFunc(b -> confirmLearnSkills());
        addWidget(confirmButton);
        Button cancelButton = new Button(currentWidth / 2 + 8, buttonsY, buttonWidth, 14, cancelButtonText);
        cancelButton.setPressFunc(b -> cancelLearnSkills());
        addWidget(cancelButton);
        boolean hasNewlyLearned = !newlyLearnedSkills.isEmpty();
        confirmButton.active = hasNewlyLearned;
        cancelButton.active = hasNewlyLearned;
    }
    private static final int MACE_MASTERY_BUTTON_SIZE = 19;
    private void addMaceMasteryButton(int currentWidth, int currentHeight) {
        if (!skillTree.getId().equals(BLACKSMITH_TREE_ID)) {
            return;
        }
        if (!learnedSkills.containsAll(MACE_MASTERY_UNLOCK_SKILLS)) {
            return;
        }
        int x = currentWidth / 2 - MACE_MASTERY_BUTTON_SIZE / 2;
        int y = currentHeight - 47 - (MACE_MASTERY_BUTTON_SIZE - 14);
        SkillTreeSelectionButton maceMasteryButton =
                new SkillTreeSelectionButton(x, y, MACE_MASTERY_BUTTON_SIZE, MACE_MASTERY_BUTTON_SIZE, MACE_MASTERY_TREE_ID);
        addWidget(maceMasteryButton);
    }
    private static void addToMergeList(SkillBonus<?> b, List<SkillBonus<?>> bonuses) {
        Optional<SkillBonus<?>> same = bonuses.stream().filter(b::canMerge).findAny();
        if (same.isPresent()) {
            bonuses.remove(same.get());
            bonuses.add(same.get().copy().merge(b));
        } else {
            bonuses.add(b);
        }
    }
    private boolean canLearnSkill(PassiveSkill skill) {
        if (skillPoints < skill.getCost()) {
            return false;
        }
        if (!player.isCreative()) {
            for (SkillRequirement<?> requirement : skill.getRequirements()) {
                if (!requirement.test(player)) {
                    return false;
                }
            }
        }
        Map<String, Integer> limitations = skillTree.getSkillLimitations();
        for (String tag : skill.getTags()) {
            int limit = limitations.getOrDefault(tag, 0);
            if (limit > 0 && getLearnedSkillsWithTag(tag) >= limit) {
                return false;
            }
        }
        return true;
    }
    private long getLearnedSkillsWithTag(String tag) {
        return Streams.concat(learnedSkills.stream(), newlyLearnedSkills.stream()).map(SkillsReloader::getSkillById)
                .filter(Objects::nonNull).filter(skill -> skill.getTags().contains(tag)).count();
    }
    private void confirmLearnSkills() {
        List<Identifier> skillsToLearn = new ArrayList<>(newlyLearnedSkills);
        newlyLearnedSkills.clear();
        skillsToLearn.forEach(id -> learnSkill(skills.getWidgetById(id).skill));
    }
    private void cancelLearnSkills() {
        int refund = newlyLearnedSkills.stream()
                .map(skills::getWidgetById)
                .filter(Objects::nonNull)
                .mapToInt(button -> button.skill.getCost())
                .sum();
        skillPoints += refund;
        newlyLearnedSkills.clear();
        rebuildWidgets();
    }
    private void buySkillPoint() {
        int currentLevel = getCurrentLevel();
        if (!canBuySkillPoint(currentLevel)) {
            return;
        }
        int cost = ServerConfig.getSkillPointCost(currentLevel);
        ClientNetworking.sendGainSkillPoint();
        player.giveExperiencePoints(-cost);
    }
    private boolean canBuySkillPoint(int currentLevel) {
        if (!ServerConfig.enable_exp_exchange) {
            return false;
        }
        if (isMaxLevel(currentLevel)) {
            return false;
        }
        int cost = ServerConfig.getSkillPointCost(currentLevel);
        return ExpHelper.getPlayerExp(player) >= cost;
    }
    private boolean isMaxLevel(int currentLevel) {
        return currentLevel >= ServerConfig.max_skill_points;
    }
    private int getCurrentLevel() {
        IPlayerSkills capability = PlayerSkillsProvider.get(player);
        int learnedSkills = capability.getPlayerSkills().size();
        int skillPoints = capability.getSkillPoints();
        return learnedSkills + skillPoints;
    }
    protected void skillButtonPressed(SkillButton button) {
        PassiveSkill skill = button.skill;
        if (!newlyLearnedSkills.isEmpty()) {
            int lastLearned = newlyLearnedSkills.size() - 1;
            if (newlyLearnedSkills.get(lastLearned).equals(skill.getId())) {
                skillPoints += skill.getCost();
                newlyLearnedSkills.remove(lastLearned);
                rebuildWidgets();
                return;
            }
        }
        if (button.canLearn) {
            skillPoints -= skill.getCost();
            newlyLearnedSkills.add(skill.getId());
            rebuildWidgets();
        }
    }
    protected void learnSkill(PassiveSkill skill) {
        learnedSkills.add(skill.getId());
        ClientNetworking.sendLearnSkill(skill);
        rebuildWidgets();
    }
    protected void updateBuyPointButton() {
        int currentLevel = getCurrentLevel();
        if (isMaxLevel(currentLevel)) {
            buyButton.active = false;
            return;
        }
        int pointCost = ServerConfig.getSkillPointCost(currentLevel);
        buyButton.active = ExpHelper.getPlayerExp(player) >= pointCost;
    }
    private void toggleProgressDisplayMode() {
        progressBar.showProgressInNumbers ^= true;
        showProgressInNumbers ^= true;
    }
    private void readPlayerData(LocalPlayer player) {
        IPlayerSkills capability = PlayerSkillsProvider.get(player);
        List<PassiveSkill> skills = capability.getPlayerSkills();
        skills.stream().map(PassiveSkill::getId).forEach(learnedSkills::add);
        skillPoints = capability.getSkillPoints();
    }
    private List<Component> getMergedSkillBonusesTooltips() {
        List<SkillBonus<?>> bonuses = new ArrayList<>();
        learnedSkills.stream().map(skills::getWidgetById).filter(Objects::nonNull).map(button -> button.skill).map(PassiveSkill::getBonuses)
                .flatMap(List::stream).filter(this::isDisplayedInStatsPanel).forEach(b -> addToMergeList(b, bonuses));
        return bonuses.stream().sorted().map(SkillBonus::getFullTooltip).flatMap(List::stream).map(Component.class::cast).toList();
    }
    private boolean isDisplayedInStatsPanel(SkillBonus<?> bonus) {
        return !(bonus instanceof VanillaRecipeUnlockBonus)
                && !(bonus instanceof RecipeUnlockBonus)
                && !(bonus instanceof ExecuteCommandBonus);
    }
    /**
     * Les paliers Mace Mastery n'ont pas de SkillBonus (les enchantements sont appliqués
     * "en dur" par MaceMasteryEvents, pas via le système de bonus du skill tree), donc
     * getMergedSkillBonusesTooltips() ne peut rien y trouver et "Show Stats" restait vide.
     * On réutilise ici le texte de description déjà écrit dans en_us.json pour le palier
     * réellement actif (appris ET seuil de kills atteint sur la masse), avec le même style
     * que SkillButton.applyDescriptionStyle pour rester cohérent visuellement.
     */
    private static final Style MACE_MASTERY_STATS_STYLE = Style.EMPTY.withColor(0x7B7BE5);
    /**
     * Largeur max (en pixels non mis à l'échelle) d'une ligne du panneau "Show Stats" pour
     * Mace Mastery. Les descriptions Mace Mastery sont des phrases longues (contrairement
     * aux bonus courts du Blacksmith), donc sans retour à la ligne forcé,
     * ScrollableComponentList#setComponents() dimensionnait le panneau sur la largeur de la
     * ligne entière (souvent > largeur d'écran), le faisant sortir de l'écran par la gauche.
     * On découpe donc chaque ligne avec TooltipHelper.split (même utilitaire que pour le
     * tooltip d'objet Mace Mastery) sur une largeur comparable à celle des lignes les plus
     * longues du panneau Blacksmith.
     */
    private static final int MACE_MASTERY_STATS_MAX_WIDTH = 400;
    private List<Component> getMaceMasteryStatsTooltip() {
        if (!skillTree.getId().equals(MACE_MASTERY_TREE_ID)) {
            return List.of();
        }
        Identifier activeNodeId = MaceMasteryEvents.getActiveMaceMasteryNodeId(player);
        if (activeNodeId == null) {
            return List.of();
        }
        String descriptionId = "skill." + activeNodeId.getNamespace() + "." + activeNodeId.getPath() + ".description";
        String description = Component.translatable(descriptionId).getString();
        if (description.equals(descriptionId)) {
            return List.of();
        }
        Font font = Minecraft.getInstance().font;
        List<Component> tooltip = new ArrayList<>();
        for (String line : description.split("/n")) {
            MutableComponent lineComponent = Component.literal(line).withStyle(MACE_MASTERY_STATS_STYLE);
            tooltip.addAll(TooltipHelper.split(lineComponent, font, MACE_MASTERY_STATS_MAX_WIDTH));
        }
        return tooltip;
    }
    public void updateSkillPoints(int skillPoints) {
        int pendingCost = newlyLearnedSkills.stream()
                .map(skills::getWidgetById)
                .filter(Objects::nonNull)
                .mapToInt(button -> button.skill.getCost())
                .sum();
        this.skillPoints = skillPoints - pendingCost;
    }
    public void addSkillButton(PassiveSkill skill, Supplier<Float> renderAnimation) {
        SkillButton button = skills.addSkillButton(skill, renderAnimation);
        if (learnedSkills.contains(skill.getId()) || newlyLearnedSkills.contains(skill.getId())) {
            button.skillLearned = true;
        }
    }
}