package daripher.skilltree.recipe.workbench;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import daripher.skilltree.init.PSTRecipeSerializers;
import daripher.skilltree.inventory.menu.WorkbenchContainer;
<<<<<<< Updated upstream
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
=======
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
>>>>>>> Stashed changes
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class WorkbenchPotionMixingRecipe extends AbstractWorkbenchRecipe {
    public static final String IS_MIXTURE_TAG_NAME = "isMixture";

<<<<<<< Updated upstream
    public WorkbenchPotionMixingRecipe(ResourceLocation id, boolean requiresPassiveSkill) {
=======
    private static final Identifier UNKNOWN_ID = Identifier.fromNamespaceAndPath("skilltree", "unknown_workbench_potion_mixing_recipe");

    public WorkbenchPotionMixingRecipe(Identifier id, boolean requiresPassiveSkill) {
>>>>>>> Stashed changes
        super(id, requiresPassiveSkill);
    }

    @Override
<<<<<<< Updated upstream
    public @NotNull ItemStack assemble(@NotNull WorkbenchContainer container, @NotNull RegistryAccess registryAccess) {
        return getResult(container);
    }

    @Override
=======
>>>>>>> Stashed changes
    public boolean isValidBaseItem(ItemStack itemStack) {
        return isValidPotion(itemStack);
    }

    @Override
    public boolean isValidIngredient(ItemStack itemStack) {
        return isValidPotion(itemStack);
    }

    private boolean isValidPotion(ItemStack itemStack) {
        return itemStack.getItem() instanceof PotionItem && canMixPotion(itemStack);
    }

    @Override
    public Pair<Ingredient, Integer> getBaseIngredient() {
        return Pair.of(getAllPotionsIngredient(), 1);
    }

    private void setIsMixtureTag(ItemStack itemStack) {
        CustomData currentData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CustomData updatedData = currentData.update(tag -> {
            tag.putBoolean(IS_MIXTURE_TAG_NAME, true);
        });
        itemStack.set(DataComponents.CUSTOM_DATA, updatedData);
    }

    private boolean canMixPotion(ItemStack itemStack) {
        CompoundTag customTag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return !customTag.getBooleanOr(IS_MIXTURE_TAG_NAME, false);
    }

    private Ingredient getPotionItemIngredient(PotionItem baseItem) {
        Collection<Potion> availablePotions = Lists.newArrayList(BuiltInRegistries.POTION);
        Stream<Potion> potionsWithEffects = availablePotions.stream().filter(potion -> !potion.getEffects().isEmpty());
        Stream<ItemStack> suitablePotionStacks = potionsWithEffects.map(potion -> getPotionStack(baseItem, potion));
        return Ingredient.of(suitablePotionStacks.map(ItemStack::getItem));
    }

    private Ingredient getAllPotionsIngredient() {
        Collection<Potion> availablePotions = Lists.newArrayList(BuiltInRegistries.POTION);
        Stream<Potion> potions = availablePotions.stream().filter(potion -> !potion.getEffects().isEmpty());
        List<ItemStack> suitablePotionStacks = new ArrayList<>();
        potions.forEach(potion -> {
            List<PotionItem> potionItems = Lists.newArrayList(BuiltInRegistries.ITEM).stream().filter(PotionItem.class::isInstance)
                    .map(PotionItem.class::cast).toList();
            potionItems.forEach(potionItem -> suitablePotionStacks.add(getPotionStack(potionItem, potion)));
        });
        return Ingredient.of(suitablePotionStacks.stream().map(ItemStack::getItem));
    }

    private static @NotNull ItemStack getPotionStack(PotionItem baseItem, Potion potion) {
        ItemStack itemStack = new ItemStack(baseItem);
        itemStack.set(DataComponents.POTION_CONTENTS, new PotionContents(BuiltInRegistries.POTION.wrapAsHolder(potion)));
        return itemStack;
    }

    private static List<MobEffectInstance> getPotionEffects(ItemStack potionStack) {
        PotionContents contents = potionStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        return Lists.newArrayList(contents.getAllEffects());
    }

    private static int getPotionColor(ItemStack potionStack) {
        return potionStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getColor();
    }

    @Override
    public Map<Ingredient, Integer> getAdditionalIngredients(ItemStack baseItem) {
        Map<Ingredient, Integer> allPotionsIngredient = Map.of(getAllPotionsIngredient(), 1);
        if (baseItem.isEmpty()) {
            return allPotionsIngredient;
        }
        Item item = baseItem.getItem();
        if (!(item instanceof PotionItem potionItem)) {
            return allPotionsIngredient;
        }
        return Map.of(getPotionItemIngredient(potionItem), 1);
    }

    @Override
    public Component getShortDescription() {
        return Component.translatable(getDescriptionId());
    }

    @Override
    public @NotNull ItemStack getResult(WorkbenchContainer workbenchContainer) {
        ItemStack potionStack1 = workbenchContainer.getBaseItem();
        ItemStack potionStack2 = workbenchContainer.getItem(1);
        ItemStack resultItemStack = new ItemStack(potionStack1.getItem());
        setMixtureEffects(potionStack1, potionStack2, resultItemStack);
        setMixtureColor(potionStack1, potionStack2, resultItemStack);
        setMixtureName(potionStack1, resultItemStack);
        setIsMixtureTag(resultItemStack);
        return resultItemStack;
    }

    private void setMixtureEffects(ItemStack potionStack1, ItemStack potionStack2, ItemStack resultItemStack) {
        List<MobEffectInstance> mobEffectInstances = new ArrayList<>();
        mobEffectInstances.addAll(getPotionEffects(potionStack1));
        mobEffectInstances.addAll(getPotionEffects(potionStack2));
        PotionContents existingContents = resultItemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        resultItemStack.set(DataComponents.POTION_CONTENTS,
                new PotionContents(existingContents.potion(), existingContents.customColor(), mobEffectInstances, existingContents.customName()));
    }

    private void setMixtureColor(ItemStack potionStack1, ItemStack potionStack2, ItemStack resultItemStack) {
        int potionColor = mixHexColors(getPotionColor(potionStack1), getPotionColor(potionStack2));
        PotionContents existingContents = resultItemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        resultItemStack.set(DataComponents.POTION_CONTENTS,
                new PotionContents(existingContents.potion(), Optional.of(potionColor), existingContents.customEffects(), existingContents.customName()));
    }

    private void setMixtureName(ItemStack potionStack1, ItemStack resultItemStack) {
        String descriptionId = potionStack1.getItem().getDescriptionId() + ".mixture";
        MutableComponent itemStackName = Component.translatable(descriptionId);
        resultItemStack.set(DataComponents.CUSTOM_NAME, itemStackName);
    }

    private int mixHexColors(int color1, int color2) {
        return ((color1 ^ color2) & 0xFEFEFE) >> 1 + (color1 & color2);
    }

    @Override
    public int requiredBaseItemAmount() {
        return 1;
    }

    @Override
    public @NotNull RecipeSerializer<? extends Recipe<WorkbenchContainer>> getSerializer() {
        return PSTRecipeSerializers.WORKBENCH_POTION_MIXING.get();
    }

<<<<<<< Updated upstream
    public static class Serializer implements RecipeSerializer<WorkbenchPotionMixingRecipe> {
        @Override
        public @NotNull WorkbenchPotionMixingRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject jsonObject) {
            boolean requiresPassiveSkill = jsonObject.get("requires_passive_skill").getAsBoolean();
            return new WorkbenchPotionMixingRecipe(id, requiresPassiveSkill);
        }

        @Override
        public @Nullable WorkbenchPotionMixingRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
=======
    public static final class Serializer {
        // CORRECTION 26.1.2 : RecipeSerializer<T> est désormais un record final, impossible à
        // implémenter via "implements". On construit une instance directe avec CODEC/STREAM_CODEC,
        // exposée en INSTANCE et utilisée par PSTRecipeSerializers pour l'enregistrement.
        private static final MapCodec<WorkbenchPotionMixingRecipe> CODEC = Codec.BOOL.fieldOf("requires_passive_skill")
                .xmap(requiresPassiveSkill -> new WorkbenchPotionMixingRecipe(UNKNOWN_ID, requiresPassiveSkill),
                        AbstractWorkbenchRecipe::hasPassiveSkillRequirement);

        private static final StreamCodec<RegistryFriendlyByteBuf, WorkbenchPotionMixingRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::toNetwork, Serializer::fromNetwork
        );

        public static final RecipeSerializer<WorkbenchPotionMixingRecipe> INSTANCE = new RecipeSerializer<>(CODEC, STREAM_CODEC);

        private static @NotNull WorkbenchPotionMixingRecipe fromNetwork(@NotNull RegistryFriendlyByteBuf buf) {
>>>>>>> Stashed changes
            boolean requiresPassiveSkill = buf.readBoolean();
            return new WorkbenchPotionMixingRecipe(UNKNOWN_ID, requiresPassiveSkill);
        }

        private static void toNetwork(@NotNull RegistryFriendlyByteBuf buf, @NotNull WorkbenchPotionMixingRecipe recipe) {
            buf.writeBoolean(recipe.hasPassiveSkillRequirement());
        }
    }
}