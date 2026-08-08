package daripher.skilltree.client.recipe;

import daripher.skilltree.recipe.workbench.AbstractWorkbenchRecipe;

import java.util.ArrayList;
import java.util.List;

public class WorkbenchRecipeClientCache {
    private static final List<AbstractWorkbenchRecipe> RECIPES = new ArrayList<>();

    public static void set(List<AbstractWorkbenchRecipe> recipes) {
        RECIPES.clear();
        RECIPES.addAll(recipes);
    }

    public static List<AbstractWorkbenchRecipe> get() {
        return RECIPES;
    }
}