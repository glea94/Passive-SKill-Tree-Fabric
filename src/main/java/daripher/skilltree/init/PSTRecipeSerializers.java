package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.recipe.workbench.WorkbenchArmorFireForgingRecipe;
import daripher.skilltree.recipe.workbench.WorkbenchCraftingRecipe;
import daripher.skilltree.recipe.workbench.WorkbenchPotionMixingRecipe;
import daripher.skilltree.recipe.workbench.WorkbenchUpgradeBonusRecipe;
import daripher.skilltree.recipe.workbench.WorkbenchVanillaCraftingRecipe;
import daripher.skilltree.recipe.workbench.WorkbenchVanillaSmithingRecipe;
import daripher.skilltree.recipe.workbench.WorkbenchWeaponPoisoningRecipe;
import daripher.skilltree.util.registry.DeferredRegister;
import daripher.skilltree.util.registry.RegistryObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class PSTRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, SkillTreeMod.MOD_ID);

    public static final RegistryObject<RecipeSerializer<WorkbenchUpgradeBonusRecipe>> WORKBENCH_ITEM_BONUS = REGISTRY.register("workbench_item_bonus", () -> WorkbenchUpgradeBonusRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeSerializer<WorkbenchCraftingRecipe>> WORKBENCH_CRAFTING = REGISTRY.register("workbench_crafting", () -> WorkbenchCraftingRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeSerializer<WorkbenchPotionMixingRecipe>> WORKBENCH_POTION_MIXING = REGISTRY.register("workbench_potion_mixing", () -> WorkbenchPotionMixingRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeSerializer<WorkbenchWeaponPoisoningRecipe>> WORKBENCH_WEAPON_POISONING = REGISTRY.register("workbench_weapon_poisoning", () -> WorkbenchWeaponPoisoningRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeSerializer<WorkbenchVanillaCraftingRecipe>> WORKBENCH_VANILLA_CRAFTING = REGISTRY.register("workbench_vanilla_crafting", () -> WorkbenchVanillaCraftingRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeSerializer<WorkbenchVanillaSmithingRecipe>> WORKBENCH_VANILLA_SMITHING = REGISTRY.register("workbench_vanilla_smithing", () -> WorkbenchVanillaSmithingRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeSerializer<WorkbenchArmorFireForgingRecipe>> WORKBENCH_ARMOR_FIRE_FORGING = REGISTRY.register("workbench_armor_fire_forging", () -> WorkbenchArmorFireForgingRecipe.Serializer.INSTANCE);
}