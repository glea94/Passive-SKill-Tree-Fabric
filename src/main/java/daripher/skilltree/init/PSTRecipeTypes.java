package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.recipe.workbench.AbstractWorkbenchRecipe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public class PSTRecipeTypes {
    public static final RecipeType<AbstractWorkbenchRecipe> WORKBENCH = register("workbench");

    private static <T extends Recipe<?>> RecipeType<T> register(final String identifier) {
        RecipeType<T> recipeType = new RecipeType<>() {
            @Override
            public String toString() {
                return identifier;
            }
        };

        return Registry.register(BuiltInRegistries.RECIPE_TYPE, Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, identifier), recipeType);
    }
}
