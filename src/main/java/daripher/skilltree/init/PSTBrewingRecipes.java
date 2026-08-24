package daripher.skilltree.init;

import net.fabricmc.fabric.api.registry.FabricPotionBrewingBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;

public class PSTBrewingRecipes {
    private static boolean itemRecipesRegistered = false;

    public static void addRecipes() {

        FabricPotionBrewingBuilder.BUILD.register(builder -> {
            builder.registerPotionRecipe(Potions.FIRE_RESISTANCE, Ingredient.of(Items.FERMENTED_SPIDER_EYE), wrapPotion(PSTPotions.LIQUID_FIRE_1.get()));
            builder.registerPotionRecipe(wrapPotion(PSTPotions.LIQUID_FIRE_1.get()), Ingredient.of(Items.GLOWSTONE_DUST), wrapPotion(PSTPotions.LIQUID_FIRE_2.get()));

            if (!itemRecipesRegistered) {
                itemRecipesRegistered = true;

                builder.registerItemRecipe((Item) Items.POTION, Ingredient.of(Items.GUNPOWDER), (Item) Items.SPLASH_POTION);
                builder.registerItemRecipe((Item) Items.SPLASH_POTION, Ingredient.of(Items.DRAGON_BREATH), (Item) Items.LINGERING_POTION);
            }
        });
    }

    private static Holder<Potion> wrapPotion(Potion potion) {
        return BuiltInRegistries.POTION.wrapAsHolder(potion);
    }
}