package daripher.skilltree.init;

import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Portage Fabric : Forge utilisait BrewingRecipeRegistry.addRecipe(Ingredient, Ingredient,
 * ItemStack) avec un StrictNBTIngredient pour matcher précisément la potion d'entrée par NBT.
 * Fabric expose FabricBrewingRecipeRegistryBuilder en 1.21.1, l'équivalent
 * officiel pensé pour les mods, avec exactement les deux mêmes opérations :
 * - registerPotionRecipe(Holder<Potion>, Ingredient, Holder<Potion>) : potion précise -> potion précise
 *   (équivalent exact du cas "liquid_fire_1 + glowstone -> liquid_fire_2")
 * - registerItemRecipe(PotionItem, Ingredient, PotionItem) : conversion de contenant
 *   (potion -> splash -> lingering), valable pour TOUTES les potions de ce type d'item en un
 *   seul appel.
 *
 * CORRECTION 1.21.1 : registerPotionRecipe exige désormais des Holder<Potion> et non plus
 * des Potion brutes. Potions.FIRE_RESISTANCE est déjà un Holder<Potion> côté vanilla, mais
 * PSTPotions.LIQUID_FIRE_1/2.get() renvoie une Potion brute qu'il faut emballer via
 * BuiltInRegistries.POTION.wrapAsHolder(...).
 */
public class PSTBrewingRecipes {
    private static boolean itemRecipesRegistered = false;

    public static void addRecipes() {
        // CORRECTION 1.21.1: Uses the new mandatory builder registry pattern required by modern Fabric API
        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            builder.registerPotionRecipe(Potions.FIRE_RESISTANCE, Ingredient.of(Items.FERMENTED_SPIDER_EYE), wrapPotion(PSTPotions.LIQUID_FIRE_1.get()));
            builder.registerPotionRecipe(wrapPotion(PSTPotions.LIQUID_FIRE_1.get()), Ingredient.of(Items.GLOWSTONE_DUST), wrapPotion(PSTPotions.LIQUID_FIRE_2.get()));

            if (!itemRecipesRegistered) {
                itemRecipesRegistered = true;
                builder.registerItemRecipe((PotionItem) Items.POTION, Ingredient.of(Items.GUNPOWDER), (PotionItem) Items.SPLASH_POTION);
                builder.registerItemRecipe((PotionItem) Items.SPLASH_POTION, Ingredient.of(Items.DRAGON_BREATH), (PotionItem) Items.LINGERING_POTION);
            }
        });
    }

    private static Holder<Potion> wrapPotion(Potion potion) {
        return BuiltInRegistries.POTION.wrapAsHolder(potion);
    }
}