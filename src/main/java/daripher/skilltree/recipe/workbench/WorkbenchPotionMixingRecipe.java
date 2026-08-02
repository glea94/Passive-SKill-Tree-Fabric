package daripher.skilltree.recipe.workbench;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import daripher.skilltree.init.PSTRecipeSerializers;
import daripher.skilltree.inventory.menu.WorkbenchContainer;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.Ingredient;
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

    // CORRECTION 1.21.1 : voir la javadoc d'AbstractWorkbenchRecipe — codec()/streamCodec() ne
    // reçoivent plus l'id de la recette (contrairement à l'ancien fromJson(id, json)). Ce
    // placeholder est utilisé le temps que l'appelant réinjecte le vrai id via setId(...).
    private static final ResourceLocation UNKNOWN_ID = ResourceLocation.fromNamespaceAndPath("skilltree", "unknown_workbench_potion_mixing_recipe");

    public WorkbenchPotionMixingRecipe(ResourceLocation id, boolean requiresPassiveSkill) {
        super(id, requiresPassiveSkill);
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull WorkbenchContainer container, HolderLookup.@NotNull Provider registries) {
        return getResult(container);
    }

    @Override
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

    // CORRECTION 1.21.1 : itemStack.getOrCreateTag() (NBT brut) a disparu. On utilise désormais le
    // Data Component vanilla DataComponents.CUSTOM_DATA, via CustomData.update(...), pour stocker
    // le même booléen isMixture de manière transparente et conforme à la 1.21.1.
    private void setIsMixtureTag(ItemStack itemStack) {
        CustomData currentData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CustomData updatedData = currentData.update(tag -> {
            tag.putBoolean(IS_MIXTURE_TAG_NAME, true);
        });
        itemStack.set(DataComponents.CUSTOM_DATA, updatedData);
    }

    // CORRECTION 1.21.1 : itemStack.hasTag()/getOrCreateTag() (NBT brut) ont disparu. On relit le
    // même booléen depuis le Data Component DataComponents.CUSTOM_DATA. CustomData.EMPTY donne un
    // CompoundTag vide par défaut, dont getBoolean(...) renvoie false si la clé est absente — donc
    // canMixPotion() renvoie bien true tant que isMixture n'a jamais été posé, exactement comme
    // avant avec hasTag().
    private boolean canMixPotion(ItemStack itemStack) {
        CompoundTag customTag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return !customTag.getBoolean(IS_MIXTURE_TAG_NAME);
    }

    private Ingredient getPotionItemIngredient(PotionItem baseItem) {
        Collection<Potion> availablePotions = Lists.newArrayList(BuiltInRegistries.POTION);
        Stream<Potion> potionsWithEffects = availablePotions.stream().filter(potion -> !potion.getEffects().isEmpty());
        Stream<ItemStack> suitablePotionStacks = potionsWithEffects.map(potion -> getPotionStack(baseItem, potion));
        return Ingredient.of(suitablePotionStacks.toList().toArray(new ItemStack[0]));
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
        return Ingredient.of(suitablePotionStacks.toArray(new ItemStack[0]));
    }

    private static @NotNull ItemStack getPotionStack(PotionItem baseItem, Potion potion) {
        ItemStack itemStack = new ItemStack(baseItem);
        itemStack.set(DataComponents.POTION_CONTENTS, new PotionContents(BuiltInRegistries.POTION.wrapAsHolder(potion)));
        return itemStack;
    }

    // CORRECTION 1.21.1 : PotionContents#getAllEffects() renvoie désormais un Iterable<MobEffectInstance>
    // et non plus un List<MobEffectInstance>. On le convertit explicitement via Lists.newArrayList(...)
    // (Guava, déjà utilisé ailleurs dans ce fichier) pour rester compatible avec le reste du code qui
    // attend un List (mobEffectInstances.addAll(...) dans setMixtureEffects).
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
                new PotionContents(existingContents.potion(), existingContents.customColor(), mobEffectInstances));
    }

    private void setMixtureColor(ItemStack potionStack1, ItemStack potionStack2, ItemStack resultItemStack) {
        int potionColor = mixHexColors(getPotionColor(potionStack1), getPotionColor(potionStack2));
        PotionContents existingContents = resultItemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        resultItemStack.set(DataComponents.POTION_CONTENTS,
                new PotionContents(existingContents.potion(), Optional.of(potionColor), existingContents.customEffects()));
    }

    // CORRECTION 1.21.1 : ItemStack#setHoverName(Component) a disparu. Le nom personnalisé passe
    // désormais par le Data Component vanilla DataComponents.CUSTOM_NAME.
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
    public @NotNull RecipeSerializer<?> getSerializer() {
        return PSTRecipeSerializers.WORKBENCH_POTION_MIXING.get();
    }

    public static class Serializer implements RecipeSerializer<WorkbenchPotionMixingRecipe> {
        // CORRECTION 1.21.1 : remplace fromJson(id, JsonObject). Une seule donnée à sérialiser
        // (requires_passive_skill), donc un simple MapCodec.xmap suffit, pas besoin de RecordCodecBuilder.
        private static final MapCodec<WorkbenchPotionMixingRecipe> CODEC = Codec.BOOL.fieldOf("requires_passive_skill")
                .xmap(requiresPassiveSkill -> new WorkbenchPotionMixingRecipe(UNKNOWN_ID, requiresPassiveSkill),
                        AbstractWorkbenchRecipe::hasPassiveSkillRequirement);

        // CORRECTION 1.21.1 : remplace fromNetwork(id, FriendlyByteBuf) / toNetwork(buf, recipe).
        private static final StreamCodec<RegistryFriendlyByteBuf, WorkbenchPotionMixingRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::toNetwork, Serializer::fromNetwork
        );

        @Override
        public com.mojang.serialization.@NotNull MapCodec<WorkbenchPotionMixingRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, WorkbenchPotionMixingRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static @NotNull WorkbenchPotionMixingRecipe fromNetwork(@NotNull RegistryFriendlyByteBuf buf) {
            boolean requiresPassiveSkill = buf.readBoolean();
            // CORRECTION 1.21.1 : voir la remarque sur UNKNOWN_ID en haut du fichier.
            return new WorkbenchPotionMixingRecipe(UNKNOWN_ID, requiresPassiveSkill);
        }

        private static void toNetwork(@NotNull RegistryFriendlyByteBuf buf, @NotNull WorkbenchPotionMixingRecipe recipe) {
            buf.writeBoolean(recipe.hasPassiveSkillRequirement());
        }
    }
}