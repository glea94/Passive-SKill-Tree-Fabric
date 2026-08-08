package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeBookCategory;

public class PSTRecipeBookCategories {
    public static final RecipeBookCategory WORKBENCH = register("workbench");

    private static RecipeBookCategory register(String identifier) {
        return Registry.register(BuiltInRegistries.RECIPE_BOOK_CATEGORY, ResourceLocation.fromNamespaceAndPath(SkillTreeMod.MOD_ID, identifier), new RecipeBookCategory());
    }
}