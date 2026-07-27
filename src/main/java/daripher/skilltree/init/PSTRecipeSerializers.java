package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.recipe.workbench.WorkbenchCraftingRecipe;
import daripher.skilltree.recipe.workbench.WorkbenchPotionMixingRecipe;
import daripher.skilltree.recipe.workbench.WorkbenchUpgradeBonusRecipe;
import daripher.skilltree.recipe.workbench.WorkbenchWeaponPoisoningRecipe;
import daripher.skilltree.util.registry.DeferredRegister;
import daripher.skilltree.util.registry.RegistryObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class PSTRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, SkillTreeMod.MOD_ID);

    public static final RegistryObject<WorkbenchUpgradeBonusRecipe.Serializer> WORKBENCH_ITEM_BONUS = REGISTRY.register("workbench_item_bonus", WorkbenchUpgradeBonusRecipe.Serializer::new);
    public static final RegistryObject<WorkbenchCraftingRecipe.Serializer> WORKBENCH_CRAFTING = REGISTRY.register("workbench_crafting", WorkbenchCraftingRecipe.Serializer::new);
    public static final RegistryObject<WorkbenchPotionMixingRecipe.Serializer> WORKBENCH_POTION_MIXING = REGISTRY.register("workbench_potion_mixing", WorkbenchPotionMixingRecipe.Serializer::new);
    public static final RegistryObject<WorkbenchWeaponPoisoningRecipe.Serializer> WORKBENCH_WEAPON_POISONING = REGISTRY.register("workbench_weapon_poisoning", WorkbenchWeaponPoisoningRecipe.Serializer::new);
}
