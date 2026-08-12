package daripher.skilltree.init;

<<<<<<< Updated upstream
<<<<<<< Updated upstream
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistry;
=======
import net.fabricmc.fabric.api.registry.FabricPotionBrewingBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;
>>>>>>> Stashed changes
import net.minecraft.world.item.Item;
=======
import net.fabricmc.fabric.api.registry.FabricPotionBrewingBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
>>>>>>> Stashed changes
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;

public class PSTBrewingRecipes {
    private static boolean itemRecipesRegistered = false;

    public static void addRecipes() {
<<<<<<< Updated upstream
<<<<<<< Updated upstream
        FabricBrewingRecipeRegistry.registerPotionRecipe(Potions.FIRE_RESISTANCE, Ingredient.of(Items.FERMENTED_SPIDER_EYE), PSTPotions.LIQUID_FIRE_1.get());
        FabricBrewingRecipeRegistry.registerPotionRecipe(PSTPotions.LIQUID_FIRE_1.get(), Ingredient.of(Items.GLOWSTONE_DUST), PSTPotions.LIQUID_FIRE_2.get());
        registerContainerConversions();
=======
=======
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
    }

    private static Holder<Potion> wrapPotion(Potion potion) {
        return BuiltInRegistries.POTION.wrapAsHolder(potion);
    }
}