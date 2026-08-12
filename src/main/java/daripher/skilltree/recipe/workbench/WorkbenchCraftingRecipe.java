package daripher.skilltree.recipe.workbench;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import daripher.skilltree.init.PSTRecipeSerializers;
import daripher.skilltree.inventory.menu.WorkbenchContainer;
<<<<<<< Updated upstream
<<<<<<< Updated upstream
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
=======
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
>>>>>>> Stashed changes
=======
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
>>>>>>> Stashed changes
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class WorkbenchCraftingRecipe extends AbstractWorkbenchRecipe {
<<<<<<< Updated upstream
=======
    // CORRECTION 1.21.1 : codec() et streamCodec() (voir la classe Serializer plus bas) ne reçoivent plus
    // l'identifiant de la recette en cours de chargement : celui-ci vit désormais uniquement dans le
    // RecipeHolder<T> construit par le RecipeManager (id = chemin du fichier JSON), et n'est plus transmis
    // au Codec/StreamCodec de contenu de la recette elle-même (contrairement à l'ancien fromJson(id, json)
    // et fromNetwork(id, buf) qui recevaient id directement). Le champ id "maison" d'AbstractWorkbenchRecipe
    // est donc temporairement rempli avec cette valeur placeholder quand une recette est reconstruite via
    // codec()/streamCodec() : getId() renverra ce placeholder tant que le code de chargement des recettes du
    // mod (WorkbenchMenu, RecipeUnlockBonus, VanillaRecipeUnlockBonus, TooltipHelper...) n'aura pas été mis à
    // jour pour réinjecter le vrai id (celui du RecipeHolder) après le chargement. Cf. l'explication détaillée
    // envoyée avec ce fichier.
    private static final Identifier UNKNOWN_ID = Identifier.fromNamespaceAndPath("skilltree", "unknown_workbench_crafting_recipe");

>>>>>>> Stashed changes
    private final @Nullable Pair<Ingredient, Integer> baseIngredient;
    private final Map<Ingredient, Integer> additionalIngredients;
    private final ItemStack result;

    public WorkbenchCraftingRecipe(Identifier id, @Nullable Pair<Ingredient, Integer> baseIngredient, Map<Ingredient, Integer> additionalIngredients, boolean requiresPassiveSkill, ItemStack result) {
        super(id, requiresPassiveSkill);
        this.result = result;
        this.baseIngredient = baseIngredient;
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
        if (baseIngredient == null) {
            return itemStack.isEmpty();
        }
        return baseIngredient.getLeft().test(itemStack) && itemStack.getCount() >= baseIngredient.getRight();
    }

    @Override
    public Map<Ingredient, Integer> getAdditionalIngredients(ItemStack baseIngredient) {
        return getAdditionalIngredients();
    }

    public Map<Ingredient, Integer> getAdditionalIngredients() {
        return additionalIngredients;
    }

    @Override
    public Component getShortDescription() {
        return result.getHoverName();
    }

    @Override
    public @NotNull ItemStack getResult(WorkbenchContainer workbenchContainer) {
        return result.copy();
    }

    @Override
    public int requiredBaseItemAmount() {
        return baseIngredient == null ? 0 : baseIngredient.getRight();
    }

    @Override
    public @Nullable Pair<Ingredient, Integer> getBaseIngredient() {
        return baseIngredient;
    }

    @Override
    public @NotNull RecipeSerializer<WorkbenchCraftingRecipe> getSerializer() {
        return PSTRecipeSerializers.WORKBENCH_CRAFTING.get();
    }

<<<<<<< Updated upstream
<<<<<<< Updated upstream
    public static class Serializer implements RecipeSerializer<WorkbenchCraftingRecipe> {
        @Override
        public @NotNull WorkbenchCraftingRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject jsonObject) {
            boolean requiresPassiveSkill = jsonObject.get("requires_passive_skill").getAsBoolean();
            Map<Ingredient, Integer> additionalIngredients = new HashMap<>();
            JsonArray ingredientsJson = jsonObject.getAsJsonArray("additionalIngredients");
            for (JsonElement jsonElement : ingredientsJson) {
                JsonObject ingredientJson = jsonElement.getAsJsonObject();
                Ingredient ingredient = Ingredient.fromJson(ingredientJson.get("ingredient"));
                int requiredAmount = ingredientJson.get("required_amount").getAsInt();
                additionalIngredients.put(ingredient, requiredAmount);
            }
            Pair<Ingredient, Integer> baseIngredient = null;
            if (jsonObject.has("base_ingredient")) {
                JsonObject baseIngredientJson = jsonObject.get("base_ingredient").getAsJsonObject();
                Ingredient ingredient = Ingredient.fromJson(baseIngredientJson.get("ingredient"));
                int requiredAmount = baseIngredientJson.get("required_amount").getAsInt();
                baseIngredient = Pair.of(ingredient, requiredAmount);
            }
            JsonObject resultJson = jsonObject.getAsJsonObject("result");
            ItemStack result = ShapedRecipe.itemStackFromJson(resultJson);
            return new WorkbenchCraftingRecipe(id, baseIngredient, additionalIngredients, requiresPassiveSkill, result);
        }

        @Override
        public @Nullable WorkbenchCraftingRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
=======
    // Portage 1.21.1 : petit conteneur interne utilisé uniquement par les Codec/StreamCodec ci-dessous pour
    // représenter une entrée "ingrédient + quantité requise", afin de coller exactement à l'ancien format
    // JSON (celui que produisait/lisait fromJson) : {"ingredient": {...}, "required_amount": N}. Utilisé à la
    // fois pour additionalIngredients (liste) et base_ingredient (une seule entrée, optionnelle).
    private record IngredientEntry(Ingredient ingredient, int requiredAmount) {
        static final Codec<IngredientEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(IngredientEntry::ingredient),
                Codec.INT.fieldOf("required_amount").forGetter(IngredientEntry::requiredAmount)
        ).apply(instance, IngredientEntry::new));

        static IngredientEntry of(Pair<Ingredient, Integer> pair) {
            return new IngredientEntry(pair.getLeft(), pair.getRight());
        }

=======
    // Portage 1.21.1 : petit conteneur interne utilisé uniquement par les Codec/StreamCodec ci-dessous pour
    // représenter une entrée "ingrédient + quantité requise", afin de coller exactement à l'ancien format
    // JSON (celui que produisait/lisait fromJson) : {"ingredient": {...}, "required_amount": N}. Utilisé à la
    // fois pour additionalIngredients (liste) et base_ingredient (une seule entrée, optionnelle).
    private record IngredientEntry(Ingredient ingredient, int requiredAmount) {
        static final Codec<IngredientEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(IngredientEntry::ingredient),
                Codec.INT.fieldOf("required_amount").forGetter(IngredientEntry::requiredAmount)
        ).apply(instance, IngredientEntry::new));

        static IngredientEntry of(Pair<Ingredient, Integer> pair) {
            return new IngredientEntry(pair.getLeft(), pair.getRight());
        }

>>>>>>> Stashed changes
        Pair<Ingredient, Integer> toPair() {
            return Pair.of(ingredient, requiredAmount);
        }
    }

    // Portage 1.21.1 : reproduit exactement l'ancien format simplifié de ShapedRecipe.itemStackFromJson(...),
    // c'est-à-dire {"item": "modid:item_id", "count": N} (sans components), pour rester compatible avec les
    // fichiers JSON déjà présents dans data/ sans avoir besoin de les régénérer (règle absolue : pas de
    // runDatagen).
    private static final Codec<ItemStack> SIMPLE_RESULT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(ItemStack::getItem),
            Codec.INT.optionalFieldOf("count", 1).forGetter(ItemStack::getCount)
    ).apply(instance, ItemStack::new));

    public static final class Serializer {
        // CORRECTION 26.1.2 : RecipeSerializer<T> est désormais un record final
        // (net.minecraft.world.item.crafting.RecipeSerializer(MapCodec<T>, StreamCodec<...,T>)),
        // donc impossible de l'implémenter via "implements". On construit directement une
        // instance de RecipeSerializer avec CODEC/STREAM_CODEC, exposée en INSTANCE et utilisée
        // par PSTRecipeSerializers pour l'enregistrement.
        private static final MapCodec<WorkbenchCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.BOOL.fieldOf("requires_passive_skill").forGetter(AbstractWorkbenchRecipe::hasPassiveSkillRequirement),
                IngredientEntry.CODEC.listOf().fieldOf("additionalIngredients").forGetter(recipe -> recipe.getAdditionalIngredients().entrySet().stream()
                        .map(entry -> new IngredientEntry(entry.getKey(), entry.getValue())).toList()),
                IngredientEntry.CODEC.optionalFieldOf("base_ingredient").forGetter(recipe -> Optional.ofNullable(recipe.getBaseIngredient()).map(IngredientEntry::of)),
                SIMPLE_RESULT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
        ).apply(instance, (requiresPassiveSkill, ingredientEntries, baseIngredientEntry, result) -> {
            Map<Ingredient, Integer> additionalIngredients = ingredientEntries.stream()
                    .collect(Collectors.toMap(IngredientEntry::ingredient, IngredientEntry::requiredAmount));
            Pair<Ingredient, Integer> baseIngredient = baseIngredientEntry.map(IngredientEntry::toPair).orElse(null);
            // CORRECTION 1.21.1 : voir la remarque sur UNKNOWN_ID en haut du fichier.
            return new WorkbenchCraftingRecipe(UNKNOWN_ID, baseIngredient, additionalIngredients, requiresPassiveSkill, result);
        }));

        private static final StreamCodec<RegistryFriendlyByteBuf, WorkbenchCraftingRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::toNetwork, Serializer::fromNetwork
        );

        public static final RecipeSerializer<WorkbenchCraftingRecipe> INSTANCE = new RecipeSerializer<>(CODEC, STREAM_CODEC);

        private static @NotNull WorkbenchCraftingRecipe fromNetwork(@NotNull RegistryFriendlyByteBuf buf) {
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
            boolean requiresPassiveSkill = buf.readBoolean();
            Map<Ingredient, Integer> additionalIngredients = new HashMap<>();
            int ingredientsCount = buf.readInt();
            for (int i = 0; i < ingredientsCount; i++) {
                // CORRECTION 1.21.1: Ingredient.fromNetwork(buf) a disparu ; remplacé par le StreamCodec dédié.
                additionalIngredients.put(Ingredient.CONTENTS_STREAM_CODEC.decode(buf), buf.readInt());
            }
            Pair<Ingredient, Integer> baseIngredient = null;
            boolean hasBaseIngredient = buf.readBoolean();
            if (hasBaseIngredient) {
                baseIngredient = Pair.of(Ingredient.CONTENTS_STREAM_CODEC.decode(buf), buf.readInt());
            }
            // CORRECTION 1.21.1: buf.readItem() a disparu ; remplacé par ItemStack.STREAM_CODEC.
            ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
            // CORRECTION 1.21.1 : voir la remarque sur UNKNOWN_ID en haut du fichier.
            return new WorkbenchCraftingRecipe(UNKNOWN_ID, baseIngredient, additionalIngredients, requiresPassiveSkill, result);
        }

        private static void toNetwork(@NotNull RegistryFriendlyByteBuf buf, @NotNull WorkbenchCraftingRecipe recipe) {
            buf.writeBoolean(recipe.hasPassiveSkillRequirement());
            int ingredientsCount = recipe.getAdditionalIngredients().size();
            buf.writeInt(ingredientsCount);
            recipe.getAdditionalIngredients().forEach((ingredient, requiredAmount) -> {
                // CORRECTION 1.21.1: Ingredient#toNetwork(buf) a disparu ; remplacé par le StreamCodec dédié.
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
                buf.writeInt(requiredAmount);
            });
            buf.writeBoolean(recipe.baseIngredient != null);
            if (recipe.baseIngredient != null) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.baseIngredient.getLeft());
                buf.writeInt(recipe.baseIngredient.getRight());
            }
            // CORRECTION 1.21.1: buf.writeItem(...) a disparu ; remplacé par ItemStack.STREAM_CODEC.
            ItemStack.STREAM_CODEC.encode(buf, recipe.result);
        }
    }
}