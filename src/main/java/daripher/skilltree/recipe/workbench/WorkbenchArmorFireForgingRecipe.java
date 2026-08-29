package daripher.skilltree.recipe.workbench;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import daripher.skilltree.init.PSTRecipeSerializers;
import daripher.skilltree.inventory.menu.WorkbenchContainer;
import daripher.skilltree.skill.bonus.predicate.item.EquipmentPredicate;
import net.fabricmc.fabric.api.recipe.v1.ingredient.DefaultCustomIngredients;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
public class WorkbenchArmorFireForgingRecipe extends AbstractWorkbenchRecipe {
    private static final String FIRE_RESISTANCE_EFFECT_ID = "minecraft:fire_resistance";
    private static final Identifier UNKNOWN_ID = Identifier.fromNamespaceAndPath("skilltree", "unknown_workbench_armor_fire_forging_recipe");
    public WorkbenchArmorFireForgingRecipe(Identifier id, boolean requiresPassiveSkill) {
        super(id, requiresPassiveSkill);
    }
    @Override
    public boolean isValidBaseItem(ItemStack itemStack) {
        return EquipmentPredicate.isArmor(itemStack);
    }
    @Override
    public boolean isValidIngredient(ItemStack itemStack) {
        return isFireResistanceSplashPotion(itemStack);
    }
    private boolean isFireResistanceSplashPotion(ItemStack itemStack) {
        return itemStack.is(Items.SPLASH_POTION) && isFireResistancePotion(itemStack);
    }
    private boolean isFireResistancePotion(ItemStack itemStack) {
        PotionContents potionContents = itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        for (MobEffectInstance effectInstance : potionContents.getAllEffects()) {
            Identifier id = BuiltInRegistries.MOB_EFFECT.getKey(effectInstance.getEffect().value());
            if (id != null && id.toString().equals(FIRE_RESISTANCE_EFFECT_ID)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public Pair<Ingredient, Integer> getBaseIngredient() {
        return Pair.of(getArmorIngredient(), 1);
    }
    private Ingredient getArmorIngredient() {
        Collection<Item> items = Lists.newArrayList(BuiltInRegistries.ITEM);
        Stream<ItemStack> armorPieces = items.stream().map(ItemStack::new).filter(EquipmentPredicate::isArmor);
        return Ingredient.of(armorPieces.map(ItemStack::getItem));
    }
    @Override
    public Map<Ingredient, Integer> getAdditionalIngredients(ItemStack baseIngredient) {
        return Map.of(getFireResistancePotionIngredient(), 1);
    }
    private Ingredient getFireResistancePotionIngredient() {
        Item baseItem = Items.SPLASH_POTION;
        Collection<Potion> availablePotions = Lists.newArrayList(BuiltInRegistries.POTION);
        List<Ingredient> fireResistanceVariants = availablePotions.stream()
                .filter(this::isFireResistancePotion)
                .map(potion -> DefaultCustomIngredients.components(
                        Ingredient.of(baseItem),
                        DataComponentPatch.builder()
                                .set(DataComponents.POTION_CONTENTS, new PotionContents(BuiltInRegistries.POTION.wrapAsHolder(potion)))
                                .build()
                ))
                .toList();
        if (fireResistanceVariants.isEmpty()) {
            return Ingredient.of(baseItem);
        }
        if (fireResistanceVariants.size() == 1) {
            return fireResistanceVariants.getFirst();
        }
        return DefaultCustomIngredients.any(fireResistanceVariants.toArray(new Ingredient[0]));
    }
    private boolean isFireResistancePotion(Potion potion) {
        for (MobEffectInstance effectInstance : potion.getEffects()) {
            Identifier id = BuiltInRegistries.MOB_EFFECT.getKey(effectInstance.getEffect().value());
            if (id != null && id.toString().equals(FIRE_RESISTANCE_EFFECT_ID)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public Component getShortDescription() {
        return Component.translatable(getDescriptionId());
    }
    @Override
    public @NotNull ItemStack getResult(WorkbenchContainer workbenchContainer) {
        return workbenchContainer.getBaseItem().copy();
    }
    @Override
    public int requiredBaseItemAmount() {
        return 1;
    }
    @Override
    public @NotNull RecipeSerializer<WorkbenchArmorFireForgingRecipe> getSerializer() {
        return PSTRecipeSerializers.WORKBENCH_ARMOR_FIRE_FORGING.get();
    }
    public static final class Serializer {
        private static final MapCodec<WorkbenchArmorFireForgingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.BOOL.fieldOf("requires_passive_skill").forGetter(AbstractWorkbenchRecipe::hasPassiveSkillRequirement)
        ).apply(instance, requiresPassiveSkill -> new WorkbenchArmorFireForgingRecipe(UNKNOWN_ID, requiresPassiveSkill)));
        private static final StreamCodec<RegistryFriendlyByteBuf, WorkbenchArmorFireForgingRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL, AbstractWorkbenchRecipe::hasPassiveSkillRequirement,
                requiresPassiveSkill -> new WorkbenchArmorFireForgingRecipe(UNKNOWN_ID, requiresPassiveSkill)
        );
        public static final RecipeSerializer<WorkbenchArmorFireForgingRecipe> INSTANCE = new RecipeSerializer<>(CODEC, STREAM_CODEC);
    }
}