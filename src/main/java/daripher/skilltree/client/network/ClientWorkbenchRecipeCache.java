package daripher.skilltree.client.network;

import daripher.skilltree.recipe.workbench.AbstractWorkbenchRecipe;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Optional;

/**
 * 1.21.5 : remplace les anciens appels client à Minecraft.getInstance().level.getRecipeManager() /
 * Minecraft.getInstance().getConnection().getRecipeManager(), tous deux supprimés. Rempli côté client
 * à la réception de SyncWorkbenchRecipesMessage (voir ClientNetworking).
 */
public class ClientWorkbenchRecipeCache {
    private static List<AbstractWorkbenchRecipe> recipes = List.of();

    public static void set(List<AbstractWorkbenchRecipe> newRecipes) {
        recipes = newRecipes;
    }

    public static List<AbstractWorkbenchRecipe> getAll() {
        return recipes;
    }

    public static Optional<AbstractWorkbenchRecipe> getById(Identifier id) {
        return recipes.stream().filter(recipe -> recipe.getId().equals(id)).findFirst();
    }

    public static void clear() {
        recipes = List.of();
    }
}