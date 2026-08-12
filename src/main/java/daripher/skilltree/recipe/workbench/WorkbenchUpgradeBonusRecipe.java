package daripher.skilltree.recipe.workbench;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTRecipeSerializers;
import daripher.skilltree.inventory.menu.WorkbenchContainer;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.ItemBonusHandler;
import daripher.skilltree.skill.bonus.predicate.item.ItemStackPredicate;
<<<<<<< Updated upstream
<<<<<<< Updated upstream
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
=======
import daripher.skilltree.skill.bonus.predicate.item.NoneItemStackPredicate;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
>>>>>>> Stashed changes
=======
import daripher.skilltree.skill.bonus.predicate.item.NoneItemStackPredicate;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
>>>>>>> Stashed changes
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class WorkbenchUpgradeBonusRecipe extends AbstractWorkbenchRecipe {
<<<<<<< Updated upstream
=======
    // CORRECTION 1.21.1 : voir la javadoc d'AbstractWorkbenchRecipe — codec()/streamCodec() ne
    // reçoivent plus l'id de la recette (contrairement à l'ancien fromJson(id, json)). Ce
    // placeholder est utilisé le temps que l'appelant réinjecte le vrai id via setId(...).
    private static final Identifier UNKNOWN_ID = Identifier.fromNamespaceAndPath("skilltree", "unknown_workbench_upgrade_bonus_recipe");

>>>>>>> Stashed changes
    private final ItemStackPredicate baseItemStackPredicate;
    private final Map<Ingredient, Integer> additionalIngredients;
    private final ItemBonus<?> itemBonus;

    public WorkbenchUpgradeBonusRecipe(Identifier id, ItemStackPredicate baseItemStackPredicate, Map<Ingredient, Integer> additionalIngredients, boolean requiresPassiveSkill, ItemBonus<?> itemBonus) {
        super(id, requiresPassiveSkill);
        this.baseItemStackPredicate = baseItemStackPredicate;
        this.itemBonus = itemBonus;
        this.additionalIngredients = additionalIngredients;
    }

    @Override
<<<<<<< Updated upstream
<<<<<<< Updated upstream
    public @NotNull ItemStack assemble(@NotNull WorkbenchContainer container, @NotNull RegistryAccess registryAccess) {
        return getResult(container);
    }

    @Override
=======
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
    public boolean isValidBaseItem(ItemStack itemStack) {
        return baseItemStackPredicate.test(itemStack);
    }

    @Override
    public Map<Ingredient, Integer> getAdditionalIngredients(ItemStack baseIngredient) {
        return additionalIngredients;
    }

    public Map<Ingredient, Integer> getAdditionalIngredients() {
        return additionalIngredients;
    }

    @Override
    public Component getShortDescription() {
        Component itemTooltip = baseItemStackPredicate.getTooltip("plural");
        return Component.translatable(getDescriptionId(), itemBonus.getFullTooltip().get(0), itemTooltip);
    }

    @Override
    public List<Component> getFullDescription() {
        List<Component> fullDescription = new ArrayList<>();
        Style style = TooltipHelper.getItemUpgradeStyle();
        for (MutableComponent mutableComponent : itemBonus.getFullTooltip()) {
            fullDescription.add(mutableComponent.withStyle(style));
        }
        Component itemTooltip = baseItemStackPredicate.getTooltip("plural");
        itemTooltip = Component.literal("[").append(itemTooltip).append("]");
        fullDescription.add(itemTooltip);
        return fullDescription;
    }

    @Override
    public @NotNull ItemStack getResult(WorkbenchContainer workbenchContainer) {
        ItemStack baseItem = workbenchContainer.getBaseItem().copy();
        Player player = workbenchContainer.getPlayer();
        int craftedBonusLimit = ItemBonusHandler.getCraftedBonusLimit(baseItem, player);
        if (craftedBonusLimit <= 0) {
            return baseItem;
        }
        List<ItemBonus<?>> originalBonuses = new ArrayList<>(ItemBonusHandler.getItemBonuses(baseItem));
        while (craftedBonusLimit <= originalBonuses.size()) {
            originalBonuses.remove(0);
        }
        originalBonuses.add(itemBonus.copy());
        ItemBonusHandler.setUpgradeBonuses(baseItem, originalBonuses);
        return baseItem;
    }

    @Override
    public int requiredBaseItemAmount() {
        return 1;
    }

    @Override
    public Pair<Ingredient, Integer> getBaseIngredient() {
        return null;
    }

    @Override
    public @NotNull RecipeSerializer<WorkbenchUpgradeBonusRecipe> getSerializer() {
        return PSTRecipeSerializers.WORKBENCH_ITEM_BONUS.get();
    }

<<<<<<< Updated upstream
<<<<<<< Updated upstream
    public static class Serializer implements RecipeSerializer<WorkbenchUpgradeBonusRecipe> {
=======
    // Portage 1.21.1 : petit conteneur interne pour représenter une entrée "ingrédient + quantité
    // requise" dans additionalIngredients, exactement comme dans WorkbenchCraftingRecipe (même
    // format JSON {"ingredient": {...}, "required_amount": N} que l'ancien fromJson lisait).
    private record IngredientAmountEntry(Ingredient ingredient, int requiredAmount) {
        static final Codec<IngredientAmountEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(IngredientAmountEntry::ingredient),
                Codec.INT.fieldOf("required_amount").forGetter(IngredientAmountEntry::requiredAmount)
        ).apply(instance, IngredientAmountEntry::new));
    }

    // Portage 1.21.1 : pont générique entre un JsonElement "brut" et n'importe quel DynamicOps<T>.
    // Permet de continuer à utiliser SerializationHelper (Gson/JsonObject) tel quel pour
    // ItemStackPredicate et ItemBonus, qui n'ont jamais été migrés vers de vrais Codec Mojang, sans
    // avoir à changer le format JSON existant dans data/ (règle absolue : pas de runDatagen).
    private static final Codec<JsonElement> JSON_ELEMENT_CODEC = new Codec<>() {
>>>>>>> Stashed changes
        @Override
        public <T> DataResult<com.mojang.datafixers.util.Pair<JsonElement, T>> decode(DynamicOps<T> ops, T input) {
            JsonElement element = ops.convertTo(JsonOps.INSTANCE, input);
            return DataResult.success(com.mojang.datafixers.util.Pair.of(element, ops.empty()));
        }

        @Override
<<<<<<< Updated upstream
        public @Nullable WorkbenchUpgradeBonusRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
=======
    // Portage 1.21.1 : petit conteneur interne pour représenter une entrée "ingrédient + quantité
    // requise" dans additionalIngredients, exactement comme dans WorkbenchCraftingRecipe (même
    // format JSON {"ingredient": {...}, "required_amount": N} que l'ancien fromJson lisait).
    private record IngredientAmountEntry(Ingredient ingredient, int requiredAmount) {
        static final Codec<IngredientAmountEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(IngredientAmountEntry::ingredient),
                Codec.INT.fieldOf("required_amount").forGetter(IngredientAmountEntry::requiredAmount)
        ).apply(instance, IngredientAmountEntry::new));
    }

    // Portage 1.21.1 : pont générique entre un JsonElement "brut" et n'importe quel DynamicOps<T>.
    // Permet de continuer à utiliser SerializationHelper (Gson/JsonObject) tel quel pour
    // ItemStackPredicate et ItemBonus, qui n'ont jamais été migrés vers de vrais Codec Mojang, sans
    // avoir à changer le format JSON existant dans data/ (règle absolue : pas de runDatagen).
    private static final Codec<JsonElement> JSON_ELEMENT_CODEC = new Codec<>() {
        @Override
        public <T> DataResult<com.mojang.datafixers.util.Pair<JsonElement, T>> decode(DynamicOps<T> ops, T input) {
            JsonElement element = ops.convertTo(JsonOps.INSTANCE, input);
            return DataResult.success(com.mojang.datafixers.util.Pair.of(element, ops.empty()));
        }

        @Override
=======
>>>>>>> Stashed changes
        public <T> DataResult<T> encode(JsonElement input, DynamicOps<T> ops, T prefix) {
            // CORRECTION 1.21.1 : DynamicOps<T> n'expose pas de merge(T,T) générique. La méthode
            // correcte pour un codec "passe-plat" (qui encode une valeur JSON brute sans la fusionner
            // dans une map) est mergeToPrimitive : quand prefix == ops.empty() (cas normal ici, car
            // fieldOf/optionalFieldOf appellent encodeStart, qui passe ops.empty() comme prefix),
            // elle renvoie directement converted. Si prefix n'est pas vide, elle renvoie une erreur
            // explicite plutôt qu'un échec de compilation silencieux.
            T converted = JsonOps.INSTANCE.convertTo(ops, input);
            return ops.mergeToPrimitive(prefix, converted);
        }
    };

    // Reproduit SerializationHelper.deserializeItemPredicate(jsonObject, "base_item_condition") :
    // champ optionnel, absent -> NoneItemStackPredicate.INSTANCE.
    private static Optional<JsonElement> serializeBaseItemCondition(ItemStackPredicate predicate) {
        JsonObject wrapper = new JsonObject();
        SerializationHelper.serializeItemPredicate(wrapper, predicate, "base_item_condition");
        return Optional.ofNullable(wrapper.get("base_item_condition"));
    }

    private static ItemStackPredicate deserializeBaseItemCondition(Optional<JsonElement> element) {
        if (element.isEmpty()) {
            return NoneItemStackPredicate.INSTANCE;
        }
        JsonObject wrapper = new JsonObject();
        wrapper.add("base_item_condition", element.get());
        return SerializationHelper.deserializeItemPredicate(wrapper, "base_item_condition");
    }

    // Reproduit SerializationHelper.deserializeItemBonus(jsonObject) / serializeItemBonus(jsonObject, ...) :
    // champ obligatoire "item_bonus" (JsonObject imbriqué avec sa propre clé "type").
    private static JsonElement serializeItemBonusField(ItemBonus<?> itemBonus) {
        JsonObject wrapper = new JsonObject();
        SerializationHelper.serializeItemBonus(wrapper, itemBonus);
        return wrapper.get("item_bonus");
    }

    private static ItemBonus<?> deserializeItemBonusField(JsonElement element) {
        JsonObject wrapper = new JsonObject();
        wrapper.add("item_bonus", element);
        return SerializationHelper.deserializeItemBonus(wrapper);
    }

    public static final class Serializer {
        // CORRECTION 26.1.2 : RecipeSerializer<T> est désormais un record final, impossible à
        // implémenter via "implements". On construit une instance directe avec CODEC/STREAM_CODEC,
        // exposée en INSTANCE et utilisée par PSTRecipeSerializers pour l'enregistrement.
        private static final MapCodec<WorkbenchUpgradeBonusRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.BOOL.fieldOf("requires_passive_skill").forGetter(AbstractWorkbenchRecipe::hasPassiveSkillRequirement),
                JSON_ELEMENT_CODEC.optionalFieldOf("base_item_condition").forGetter(recipe -> serializeBaseItemCondition(recipe.baseItemStackPredicate)),
                JSON_ELEMENT_CODEC.fieldOf("item_bonus").forGetter(recipe -> serializeItemBonusField(recipe.itemBonus)),
                IngredientAmountEntry.CODEC.listOf().fieldOf("additionalIngredients").forGetter(recipe -> recipe.getAdditionalIngredients().entrySet().stream()
                        .map(entry -> new IngredientAmountEntry(entry.getKey(), entry.getValue())).toList())
        ).apply(instance, (requiresPassiveSkill, baseItemConditionJson, itemBonusJson, ingredientEntries) -> {
            ItemStackPredicate baseItemStackPredicate = deserializeBaseItemCondition(baseItemConditionJson);
            ItemBonus<?> itemBonus = deserializeItemBonusField(itemBonusJson);
            Map<Ingredient, Integer> additionalIngredients = ingredientEntries.stream()
                    .collect(Collectors.toMap(IngredientAmountEntry::ingredient, IngredientAmountEntry::requiredAmount));
            // CORRECTION 1.21.1 : voir la remarque sur UNKNOWN_ID en haut du fichier.
            return new WorkbenchUpgradeBonusRecipe(UNKNOWN_ID, baseItemStackPredicate, additionalIngredients, requiresPassiveSkill, itemBonus);
        }));

        // CORRECTION 1.21.1 : remplace fromNetwork(id, FriendlyByteBuf) / toNetwork(buf, recipe).
        // RegistryFriendlyByteBuf est bien une FriendlyByteBuf, donc les méthodes existantes de
        // NetworkHelper (readItemPredicate/writeItemPredicate, readItemBonus/writeItemBonus)
        // restent utilisables telles quelles ; seul Ingredient.fromNetwork/toNetwork (supprimés)
        // est remplacé par le StreamCodec dédié Ingredient.CONTENTS_STREAM_CODEC.
        private static final StreamCodec<RegistryFriendlyByteBuf, WorkbenchUpgradeBonusRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::toNetwork, Serializer::fromNetwork
        );

        public static final RecipeSerializer<WorkbenchUpgradeBonusRecipe> INSTANCE = new RecipeSerializer<>(CODEC, STREAM_CODEC);

        private static @NotNull WorkbenchUpgradeBonusRecipe fromNetwork(@NotNull RegistryFriendlyByteBuf buf) {
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
            ItemStackPredicate baseItemStackPredicate = NetworkHelper.readItemPredicate(buf);
            ItemBonus<?> itemBonus = NetworkHelper.readItemBonus(buf);
            boolean requiresPassiveSkill = buf.readBoolean();
            Map<Ingredient, Integer> additionalIngredients = new HashMap<>();
            int ingredientsCount = buf.readInt();
            for (int i = 0; i < ingredientsCount; i++) {
                // CORRECTION 1.21.1: Ingredient.fromNetwork(buf) a disparu ; remplacé par le StreamCodec dédié.
                additionalIngredients.put(Ingredient.CONTENTS_STREAM_CODEC.decode(buf), buf.readInt());
            }
            // CORRECTION 1.21.1 : voir la remarque sur UNKNOWN_ID en haut du fichier.
            return new WorkbenchUpgradeBonusRecipe(UNKNOWN_ID, baseItemStackPredicate, additionalIngredients, requiresPassiveSkill, itemBonus);
        }

        private static void toNetwork(@NotNull RegistryFriendlyByteBuf buf, @NotNull WorkbenchUpgradeBonusRecipe recipe) {
            NetworkHelper.writeItemPredicate(buf, recipe.baseItemStackPredicate);
            NetworkHelper.writeItemBonus(buf, recipe.itemBonus);
            buf.writeBoolean(recipe.hasPassiveSkillRequirement());
            int ingredientsCount = recipe.getAdditionalIngredients().size();
            buf.writeInt(ingredientsCount);
            recipe.getAdditionalIngredients().forEach((ingredient, requiredAmount) -> {
                // CORRECTION 1.21.1: Ingredient#toNetwork(buf) a disparu ; remplacé par le StreamCodec dédié.
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
                buf.writeInt(requiredAmount);
            });
        }
    }
}