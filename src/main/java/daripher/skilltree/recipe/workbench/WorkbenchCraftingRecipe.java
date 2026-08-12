package daripher.skilltree.recipe.workbench;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import daripher.skilltree.init.PSTRecipeSerializers;
import daripher.skilltree.inventory.menu.WorkbenchContainer;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class WorkbenchCraftingRecipe extends AbstractWorkbenchRecipe {
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
    private static final ResourceLocation UNKNOWN_ID = ResourceLocation.fromNamespaceAndPath("skilltree", "unknown_workbench_crafting_recipe");

    private final @Nullable Pair<Ingredient, Integer> baseIngredient;
    private final Map<Ingredient, Integer> additionalIngredients;
    private final ItemStack result;

    public WorkbenchCraftingRecipe(ResourceLocation id, @Nullable Pair<Ingredient, Integer> baseIngredient, Map<Ingredient, Integer> additionalIngredients, boolean requiresPassiveSkill, ItemStack result) {
        super(id, requiresPassiveSkill);
        this.result = result;
        this.baseIngredient = baseIngredient;
        this.additionalIngredients = additionalIngredients;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull WorkbenchContainer container, HolderLookup.Provider registries) {
        return getResult(container);
    }

    @Override
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
    public @NotNull RecipeSerializer<? extends Recipe<WorkbenchContainer>> getSerializer() {
        return PSTRecipeSerializers.WORKBENCH_CRAFTING.get();
    }

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

    public static class Serializer implements RecipeSerializer<WorkbenchCraftingRecipe> {
        // CORRECTION 1.21.1 : RecipeSerializer n'a plus fromJson/fromNetwork/toNetwork ; il expose désormais
        // codec() (MapCodec, pour le chargement JSON depuis les datapacks) et streamCodec() (StreamCodec, pour
        // la synchronisation réseau). Contrairement à WorkbenchVanillaCraftingRecipe (recette purement
        // synthétique, jamais chargée depuis un fichier), CETTE recette EST bien chargée depuis de vrais
        // fichiers JSON dans data/ : codec()/streamCodec() doivent donc fonctionner réellement, pas se
        // contenter de lever une exception.
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

        @Override
        public com.mojang.serialization.@NotNull MapCodec<WorkbenchCraftingRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, WorkbenchCraftingRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static @NotNull WorkbenchCraftingRecipe fromNetwork(@NotNull RegistryFriendlyByteBuf buf) {
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