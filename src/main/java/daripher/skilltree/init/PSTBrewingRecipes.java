package daripher.skilltree.init;

<<<<<<< Updated upstream
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistry;
=======
import net.fabricmc.fabric.api.registry.FabricPotionBrewingBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;
>>>>>>> Stashed changes
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

/**
 * Portage Fabric : Forge utilisait BrewingRecipeRegistry.addRecipe(Ingredient, Ingredient,
 * ItemStack) avec un StrictNBTIngredient pour matcher précisément la potion d'entrée par NBT.
 * Fabric expose net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistry, l'équivalent
 * officiel pensé pour les mods, avec exactement les deux mêmes opérations :
 * - registerPotionRecipe(Potion, Ingredient, Potion) : potion précise -> potion précise
 *   (équivalent exact du cas "liquid_fire_1 + glowstone -> liquid_fire_2")
 * - registerItemRecipe(PotionItem, Ingredient, PotionItem) : conversion de contenant
 *   (potion -> splash -> lingering), valable pour TOUTES les potions de ce type d'item en un
 *   seul appel (donc appelée une seule fois globalement, pas par potion, contrairement au code
 *   Forge d'origine qui la répétait par sécurité pour chaque potion custom).
 */
public class PSTBrewingRecipes {
    private static boolean itemRecipesRegistered = false;

    public static void addRecipes() {
<<<<<<< Updated upstream
        FabricBrewingRecipeRegistry.registerPotionRecipe(Potions.FIRE_RESISTANCE, Ingredient.of(Items.FERMENTED_SPIDER_EYE), PSTPotions.LIQUID_FIRE_1.get());
        FabricBrewingRecipeRegistry.registerPotionRecipe(PSTPotions.LIQUID_FIRE_1.get(), Ingredient.of(Items.GLOWSTONE_DUST), PSTPotions.LIQUID_FIRE_2.get());
        registerContainerConversions();
=======
        // Aligned 26.1.2: FabricBrewingRecipeRegistryBuilder renommé FabricPotionBrewingBuilder (mappings officiels)
        FabricPotionBrewingBuilder.BUILD.register(builder -> {
            builder.registerPotionRecipe(Potions.FIRE_RESISTANCE, Ingredient.of(Items.FERMENTED_SPIDER_EYE), wrapPotion(PSTPotions.LIQUID_FIRE_1.get()));
            builder.registerPotionRecipe(wrapPotion(PSTPotions.LIQUID_FIRE_1.get()), Ingredient.of(Items.GLOWSTONE_DUST), wrapPotion(PSTPotions.LIQUID_FIRE_2.get()));

            if (!itemRecipesRegistered) {
                itemRecipesRegistered = true;
                // Factual Fix 1.21.4: Cast potion item types to base Item to prevent ClassCastException on splash/lingering items
                builder.registerItemRecipe((Item) Items.POTION, Ingredient.of(Items.GUNPOWDER), (Item) Items.SPLASH_POTION);
                builder.registerItemRecipe((Item) Items.SPLASH_POTION, Ingredient.of(Items.DRAGON_BREATH), (Item) Items.LINGERING_POTION);
            }
        });
>>>>>>> Stashed changes
    }

    // potion -> splash -> lingering : générique pour tout type de potion, à enregistrer une seule fois
    private static void registerContainerConversions() {
        if (itemRecipesRegistered) return;
        itemRecipesRegistered = true;
        FabricBrewingRecipeRegistry.registerItemRecipe((PotionItem) Items.POTION, Ingredient.of(Items.GUNPOWDER), (PotionItem) Items.SPLASH_POTION);
        FabricBrewingRecipeRegistry.registerItemRecipe((PotionItem) Items.SPLASH_POTION, Ingredient.of(Items.DRAGON_BREATH), (PotionItem) Items.LINGERING_POTION);
    }
}