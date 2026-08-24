package daripher.skilltree.recipe.builder;

import daripher.skilltree.recipe.workbench.WorkbenchCraftingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.Ingredient;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class WorkbenchCraftingRecipeBuilder {
    private final Identifier id;
    private final Map<Ingredient, Integer> ingredients = new HashMap<>();
    private @Nullable Pair<Ingredient, Integer> baseIngredient;
    private boolean requiresPassiveSkill;
    private ItemStack result;

    private WorkbenchCraftingRecipeBuilder(Identifier id) {
        this.id = id;
    }

    public static WorkbenchCraftingRecipeBuilder create(Identifier id) {
        return new WorkbenchCraftingRecipeBuilder(id);
    }

    public WorkbenchCraftingRecipeBuilder setBaseIngredient(Ingredient ingredient, int requiredAmount) {
        this.baseIngredient = Pair.of(ingredient, requiredAmount);
        return this;
    }

    public WorkbenchCraftingRecipeBuilder addIngredients(Ingredient ingredient, int requiredAmount) {
        this.ingredients.put(ingredient, requiredAmount);
        return this;
    }

    public WorkbenchCraftingRecipeBuilder setRequiresPassiveSkill() {
        this.requiresPassiveSkill = true;
        return this;
    }

    public WorkbenchCraftingRecipeBuilder setResult(@NotNull ItemStack result) {
        this.result = result;
        return this;
    }

    public void save(RecipeOutput recipeOutput) {
        validate();
        WorkbenchCraftingRecipe recipe =
                new WorkbenchCraftingRecipe(id, baseIngredient, ingredients, requiresPassiveSkill, result);


        ResourceKey<Recipe<?>> recipeKey = ResourceKey.create(Registries.RECIPE, id);
        recipeOutput.accept(recipeKey, recipe, null);
    }

    private void validate() {
        if (ingredients.isEmpty()) {
            throw new IllegalStateException("No ingredients set for recipe " + id);
        }
        if (ingredients.size() > 9) {
            throw new IllegalStateException("Too many ingredients set for recipe " + id);
        }
        if (result == null) {
            throw new IllegalStateException("No result item set for recipe " + id);
        }
    }
}
