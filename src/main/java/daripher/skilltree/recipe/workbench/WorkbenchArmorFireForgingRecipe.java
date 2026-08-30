package daripher.skilltree.recipe.workbench;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.init.PSTRecipeSerializers;
import daripher.skilltree.inventory.menu.WorkbenchContainer;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.bonus.predicate.item.EquipmentPredicate;
import net.fabricmc.fabric.api.recipe.v1.ingredient.DefaultCustomIngredients;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
public class WorkbenchArmorFireForgingRecipe extends AbstractWorkbenchRecipe {
    private static final String FIRE_RESISTANCE_EFFECT_ID = "minecraft:fire_resistance";
    private static final Identifier UNKNOWN_ID = Identifier.fromNamespaceAndPath("skilltree", "unknown_workbench_armor_fire_forging_recipe");
    // Chaque pièce d'armure ne peut être forgée que si le node blacksmith correspondant
    // (celui qui porte réellement le champ "recipe_ids" pointant vers cette recette) est appris.
    // blacksmith_58 et blacksmith_60 sont mutuellement exclusifs dans l'arbre (un seul chemin
    // possible), mais SEUL blacksmith_60 référence armor_fire_forging via recipe_ids : 58 est
    // un keystone crit/regen indépendant qui ne débloque pas ce craft.
    private static final Identifier LEGGINGS_SKILL_ID = Identifier.fromNamespaceAndPath("skilltree", "blacksmith_38");
    private static final Identifier BOOTS_SKILL_ID = Identifier.fromNamespaceAndPath("skilltree", "blacksmith_41");
    private static final Identifier HELMET_SKILL_ID = Identifier.fromNamespaceAndPath("skilltree", "blacksmith_44");
    private static final Identifier CHESTPLATE_SKILL_ID = Identifier.fromNamespaceAndPath("skilltree", "blacksmith_60");
    public WorkbenchArmorFireForgingRecipe(Identifier id, boolean requiresPassiveSkill) {
        super(id, requiresPassiveSkill);
    }
    @Override
    public boolean isValidBaseItem(ItemStack itemStack) {
        return EquipmentPredicate.isArmor(itemStack);
    }
    @Override
    public boolean matches(@NotNull WorkbenchContainer container, @NotNull Level level) {
        if (!super.matches(container, level)) {
            return false;
        }
        return hasRequiredArmorSkill(container.getBaseItem(), container.getPlayer());
    }
    private boolean hasRequiredArmorSkill(ItemStack armorStack, Player player) {
        if (EquipmentPredicate.isHelmet(armorStack)) {
            return hasLearnedSkill(player, HELMET_SKILL_ID);
        }
        if (EquipmentPredicate.isChestplate(armorStack)) {
            return hasLearnedSkill(player, CHESTPLATE_SKILL_ID);
        }
        if (EquipmentPredicate.isLeggings(armorStack)) {
            return hasLearnedSkill(player, LEGGINGS_SKILL_ID);
        }
        if (EquipmentPredicate.isBoots(armorStack)) {
            return hasLearnedSkill(player, BOOTS_SKILL_ID);
        }
        return false;
    }
    private boolean hasLearnedSkill(Player player, Identifier skillId) {
        if (!PlayerSkillsProvider.hasSkills(player)) {
            return false;
        }
        return PlayerSkillsProvider.get(player).getPlayerSkills().stream()
                .map(PassiveSkill::getId)
                .anyMatch(skillId::equals);
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
    public List<Component> getFullDescription() {
        List<Component> fullDescription = new ArrayList<>();
        fullDescription.add(getShortDescription());
        fullDescription.add(Component.translatable(getDescriptionId() + ".skill_requirement").withStyle(ChatFormatting.GRAY));
        return fullDescription;
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