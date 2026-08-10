package daripher.skilltree.recipe.workbench;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import daripher.skilltree.event.PoisonedWeaponEvents;
import daripher.skilltree.init.PSTRecipeSerializers;
import daripher.skilltree.inventory.menu.WorkbenchContainer;
import daripher.skilltree.skill.bonus.predicate.item.EquipmentPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
<<<<<<< Updated upstream
=======
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
>>>>>>> Stashed changes
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class WorkbenchWeaponPoisoningRecipe extends AbstractWorkbenchRecipe {
<<<<<<< Updated upstream
=======
    // CORRECTION 1.21.1 : voir la javadoc d'AbstractWorkbenchRecipe — codec()/streamCodec() ne
    // reçoivent plus l'id de la recette (contrairement à l'ancien fromJson(id, json)). Ce
    // placeholder est utilisé le temps que l'appelant réinjecte le vrai id via setId(...).
    private static final ResourceLocation UNKNOWN_ID = ResourceLocation.fromNamespaceAndPath("skilltree", "unknown_workbench_weapon_poisoning_recipe");

>>>>>>> Stashed changes
    private final int maxUses;

    public WorkbenchWeaponPoisoningRecipe(ResourceLocation id, boolean requiresPassiveSkill, int maxUses) {
        super(id, requiresPassiveSkill);
        this.maxUses = maxUses;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull WorkbenchContainer container, HolderLookup.@NotNull Provider registries) {
        return getResult(container);
    }

    @Override
    public boolean isValidBaseItem(ItemStack itemStack) {
        return EquipmentPredicate.isMeleeWeapon(itemStack);
    }

    @Override
    public boolean isValidIngredient(ItemStack itemStack) {
        return isValidPoison(itemStack);
    }

    private boolean isValidPoison(ItemStack itemStack) {
        PotionContents potionContents = itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        Stream<MobEffectInstance> effectsStream = StreamSupport.stream(potionContents.getAllEffects().spliterator(), false);
        return effectsStream.anyMatch(mobEffectInstance -> mobEffectInstance.getEffect().value().getCategory() == MobEffectCategory.HARMFUL);
    }

    @Override
    public Pair<Ingredient, Integer> getBaseIngredient() {
        return Pair.of(getMeleeWeaponIngredient(), 1);
    }

    private Ingredient getMeleeWeaponIngredient() {
        Collection<Item> items = com.google.common.collect.Lists.newArrayList(BuiltInRegistries.ITEM);
        Stream<ItemStack> meleeWeapons = items.stream().map(ItemStack::new).filter(EquipmentPredicate::isMeleeWeapon);
        return Ingredient.of(meleeWeapons.map(ItemStack::getItem));
    }

    @Override
    public Map<Ingredient, Integer> getAdditionalIngredients(ItemStack baseIngredient) {
        return Map.of(getPoisonIngredient(), 1);
    }

    private Ingredient getPoisonIngredient() {
        Item baseItem = Items.POTION;
        Collection<Potion> availablePotions = com.google.common.collect.Lists.newArrayList(BuiltInRegistries.POTION);
        Stream<Potion> harmfulPotions = availablePotions.stream().filter(WorkbenchWeaponPoisoningRecipe::isHarmfulPotion);
        Stream<ItemStack> suitablePotionStacks = harmfulPotions.map(potion -> getPotionStack(baseItem, potion));
        return Ingredient.of(suitablePotionStacks.map(ItemStack::getItem));
    }

    private static boolean isHarmfulPotion(Potion potion) {
        List<MobEffectInstance> effects = potion.getEffects();
        for (MobEffectInstance mobEffectInstance : effects) {
            if (mobEffectInstance.getEffect().value().getCategory() == MobEffectCategory.HARMFUL) {
                return true;
            }
        }
        return false;
    }

    private static @NotNull ItemStack getPotionStack(Item baseItem, Potion potion) {
        ItemStack itemStack = new ItemStack(baseItem);
        itemStack.set(DataComponents.POTION_CONTENTS, new PotionContents(BuiltInRegistries.POTION.wrapAsHolder(potion)));
        return itemStack;
    }

    @Override
    public Component getShortDescription() {
        return Component.translatable(getDescriptionId());
    }

    @Override
    public @NotNull ItemStack getResult(WorkbenchContainer workbenchContainer) {
        ItemStack weaponStack = workbenchContainer.getBaseItem();
        ItemStack potionStack = workbenchContainer.getItem(1);
        ItemStack resultItemStack = new ItemStack(weaponStack.getItem());
        PoisonedWeaponEvents.setPoisonedWeaponEffects(resultItemStack, potionStack, maxUses);
        return resultItemStack;
    }

    @Override
    public int requiredBaseItemAmount() {
        return 1;
    }

    @Override
    public @NotNull RecipeSerializer<WorkbenchWeaponPoisoningRecipe> getSerializer() {
        return PSTRecipeSerializers.WORKBENCH_WEAPON_POISONING.get();
    }

    public static class Serializer implements RecipeSerializer<WorkbenchWeaponPoisoningRecipe> {
        // CORRECTION 1.21.1 : remplace fromJson(id, JsonObject). Recette à deux champs
        // primitifs seulement, donc pas besoin du pont JSON_ELEMENT_CODEC utilisé dans
        // WorkbenchUpgradeBonusRecipe : un simple RecordCodecBuilder.mapCodec suffit.
        private static final MapCodec<WorkbenchWeaponPoisoningRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.BOOL.fieldOf("requires_passive_skill").forGetter(AbstractWorkbenchRecipe::hasPassiveSkillRequirement),
                Codec.INT.fieldOf("max_uses").forGetter(recipe -> recipe.maxUses)
        ).apply(instance, (requiresPassiveSkill, maxUses) ->
                // CORRECTION 1.21.1 : voir la remarque sur UNKNOWN_ID en haut du fichier.
                new WorkbenchWeaponPoisoningRecipe(UNKNOWN_ID, requiresPassiveSkill, maxUses)
        ));

        // CORRECTION 1.21.1 : remplace fromNetwork(id, FriendlyByteBuf) / toNetwork(buf, recipe).
        // RegistryFriendlyByteBuf est bien une FriendlyByteBuf, donc ByteBufCodecs.BOOL et
        // ByteBufCodecs.VAR_INT suffisent ici (pas besoin de StreamCodec.of "à la main" puisqu'il
        // n'y a que deux champs primitifs à composer).
        private static final StreamCodec<RegistryFriendlyByteBuf, WorkbenchWeaponPoisoningRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL, AbstractWorkbenchRecipe::hasPassiveSkillRequirement,
                ByteBufCodecs.VAR_INT, recipe -> recipe.maxUses,
                // CORRECTION 1.21.1 : voir la remarque sur UNKNOWN_ID en haut du fichier.
                (requiresPassiveSkill, maxUses) -> new WorkbenchWeaponPoisoningRecipe(UNKNOWN_ID, requiresPassiveSkill, maxUses)
        );

        @Override
        public com.mojang.serialization.@NotNull MapCodec<WorkbenchWeaponPoisoningRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, WorkbenchWeaponPoisoningRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}