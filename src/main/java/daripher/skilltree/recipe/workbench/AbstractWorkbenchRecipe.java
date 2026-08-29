package daripher.skilltree.recipe.workbench;
import daripher.skilltree.init.PSTRecipeTypes;
import daripher.skilltree.inventory.menu.WorkbenchContainer;
import daripher.skilltree.recipe.SkillRequiringRecipe;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.RecipeUnlockBonus;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.core.registries.BuiltInRegistries;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
public abstract class AbstractWorkbenchRecipe implements Recipe<WorkbenchContainer>, SkillRequiringRecipe {
    private Identifier id;
    private final boolean requiresPassiveSkill;
    protected AbstractWorkbenchRecipe(Identifier id, boolean requiresPassiveSkill) {
        this.requiresPassiveSkill = requiresPassiveSkill;
        this.id = id;
    }
    @Override
    public boolean matches(@NotNull WorkbenchContainer container, @NotNull Level level) {
        ItemStack baseItem = container.getBaseItem();
        if (!isValidBaseItem(baseItem)) {
            return false;
        }
        if (isLockedFor(container.getPlayer())) {
            return false;
        }
        return hasIngredients(container, getAdditionalIngredients(baseItem));
    }
    public String getDescriptionId() {
        Identifier serializerId = BuiltInRegistries.RECIPE_SERIALIZER.getKey(getSerializer());
        Objects.requireNonNull(serializerId);
        return "recipe.%s.%s".formatted(serializerId.getNamespace(), serializerId.getPath());
    }
    public boolean isLockedFor(@NotNull Player player) {
        return requiresPassiveSkill && !hasRecipeLearned(player);
    }
    public abstract boolean isValidBaseItem(ItemStack itemStack);
    public boolean isValidIngredient(ItemStack itemStack) {
        return true;
    }
    public abstract Component getShortDescription();
    public List<Component> getFullDescription() {
        return List.of(getShortDescription());
    }
    public abstract @NotNull ItemStack getResult(WorkbenchContainer workbenchContainer);
    @Override
    public @NotNull ItemStack assemble(@NotNull WorkbenchContainer container) {
        return getResult(container);
    }
    @Override
    public boolean showNotification() {
        return false;
    }
    @Override
    public String group() {
        return "";
    }
    public abstract int requiredBaseItemAmount();
    public abstract @Nullable Pair<Ingredient, Integer> getBaseIngredient();
    public abstract Map<Ingredient, Integer> getAdditionalIngredients(ItemStack baseIngredient);
    protected final boolean hasRecipeLearned(@NotNull Player player) {
        return SkillBonusProvider.getSkillBonuses(player, RecipeUnlockBonus.class).stream().map(RecipeUnlockBonus::getRecipeId)
                .anyMatch(getId()::equals);
    }
    protected boolean hasIngredients(@NotNull WorkbenchContainer container, Map<Ingredient, Integer> ingredients) {
        return container.hasIngredients(ingredients);
    }
    public boolean canCraftInDimensions(int width, int height) {
        return width == 5 && height == 2;
    }
    public @NotNull Identifier getId() {
        return id;
    }
    public void setId(@NotNull Identifier id) {
        this.id = id;
    }
    @Deprecated
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registries) {
        return ItemStack.EMPTY;
    }
    @Override
    public @NotNull RecipeType<? extends Recipe<WorkbenchContainer>> getType() {
        return PSTRecipeTypes.WORKBENCH;
    }
    @Override
    public @NotNull PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }
    @Override
    public @NotNull RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }
    @Override
    public boolean hasPassiveSkillRequirement() {
        return requiresPassiveSkill;
    }
}