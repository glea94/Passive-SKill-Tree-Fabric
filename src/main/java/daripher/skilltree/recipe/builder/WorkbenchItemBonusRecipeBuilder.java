package daripher.skilltree.recipe.builder;

import daripher.skilltree.recipe.workbench.WorkbenchUpgradeBonusRecipe;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.EquipmentBonus;
import daripher.skilltree.skill.bonus.predicate.item.ItemStackPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.HashMap;
import java.util.Map;

public class WorkbenchItemBonusRecipeBuilder {
    private final ResourceLocation id;
    private ItemStackPredicate baseItemStackPredicate;
    private final Map<Ingredient, Integer> ingredients = new HashMap<>();
    private boolean requiresPassiveSkill;
    private ItemBonus<?> itemBonus;

    private WorkbenchItemBonusRecipeBuilder(ResourceLocation id) {
        this.id = id;
    }

    public static WorkbenchItemBonusRecipeBuilder create(ResourceLocation id) {
        return new WorkbenchItemBonusRecipeBuilder(id);
    }

    public WorkbenchItemBonusRecipeBuilder setBaseItemCondition(ItemStackPredicate baseItemStackPredicate) {
        this.baseItemStackPredicate = baseItemStackPredicate;
        return this;
    }

    public WorkbenchItemBonusRecipeBuilder addIngredients(Ingredient ingredient, int requiredAmount) {
        this.ingredients.put(ingredient, requiredAmount);
        return this;
    }

    public WorkbenchItemBonusRecipeBuilder setRequiresPassiveSkill() {
        this.requiresPassiveSkill = true;
        return this;
    }

    public WorkbenchItemBonusRecipeBuilder setItemBonus(ItemBonus<?> itemBonus) {
        this.itemBonus = itemBonus;
        return this;
    }

    public WorkbenchItemBonusRecipeBuilder setItemBonus(SkillBonus<?> skillBonus) {
        this.itemBonus = new EquipmentBonus(skillBonus);
        return this;
    }

    public void save(RecipeOutput recipeOutput) {
        validate();
        WorkbenchUpgradeBonusRecipe recipe =
                new WorkbenchUpgradeBonusRecipe(id, baseItemStackPredicate, ingredients, requiresPassiveSkill, itemBonus);

        // Factual Fix 1.21.4: Convert the Identifier into a modern type-safe ResourceKey for the recipe registry
        ResourceKey<Recipe<?>> recipeKey = ResourceKey.create(Registries.RECIPE, id);
        recipeOutput.accept(recipeKey, recipe, null);
    }

    private void validate() {
        if (baseItemStackPredicate == null) {
            throw new IllegalStateException("No base item condition set for recipe " + id);
        }
        if (ingredients.isEmpty()) {
            throw new IllegalStateException("No ingredients set for recipe " + id);
        }
        if (ingredients.size() > 9) {
            throw new IllegalStateException("Too many ingredients set for recipe " + id);
        }
        if (itemBonus == null) {
            throw new IllegalStateException("No item bonus set for recipe " + id);
        }
    }
}
