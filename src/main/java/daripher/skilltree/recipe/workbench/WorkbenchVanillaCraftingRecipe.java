package daripher.skilltree.recipe.workbench;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;
import daripher.skilltree.inventory.menu.WorkbenchContainer;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.VanillaRecipeUnlockBonus;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Portage Fabric 1.21.1 : recette purement synthétique, jamais chargée depuis un fichier JSON
 * (elle est reconstruite à la volée par WorkbenchMenu#convertVanillaRecipe à partir des
 * RecipeHolder<CraftingRecipe> vanilla renvoyés par le RecipeManager). Son Serializer n'est
 * volontairement PAS enregistré dans PSTRecipeSerializers (vérifié : aucune référence dans tout
 * le mod à un WorkbenchVanillaCraftingRecipe.Serializer::codec ou ::streamCodec en dehors de
 * cette classe elle-même) : il n'existe que pour satisfaire le contrat de l'interface
 * Recipe#getSerializer(). codec()/streamCodec() ne sont donc jamais réellement invoqués en jeu,
 * mais doivent tout de même être implémentés pour compiler et respecter le contrat de
 * RecipeSerializer (l'ancien couple fromJson/fromNetwork/toNetwork de Forge a disparu de
 * l'interface 1.21.1).
 */
public class WorkbenchVanillaCraftingRecipe extends AbstractWorkbenchRecipe {
    // Portage 1.21.1 : id placeholder utilisé uniquement si cette recette était un jour
    // reconstruite via streamCodec() (aujourd'hui : jamais). getId() renverrait ce placeholder
    // tant qu'AbstractWorkbenchRecipe#setId(ResourceLocation) n'aurait pas été rappelé avec le
    // véritable id — voir la javadoc d'AbstractWorkbenchRecipe.
    private static final ResourceLocation UNKNOWN_ID = ResourceLocation.fromNamespaceAndPath("skilltree", "unknown_workbench_vanilla_crafting_recipe");

    private @Nullable Pair<Ingredient, Integer> baseIngredient;
    private Map<Ingredient, Integer> additionalIngredients;
    private final ItemStack result;

    public WorkbenchVanillaCraftingRecipe(RecipeHolder<CraftingRecipe> vanillaRecipeHolder, RegistryAccess registryAccess) {
        super(vanillaRecipeHolder.id(), true);
        CraftingRecipe vanillaRecipe = vanillaRecipeHolder.value();
        this.result = vanillaRecipe.getResultItem(registryAccess);
        additionalIngredients = getIngredientsFromCraftingRecipe(vanillaRecipe);
        List<Pair<Ingredient, Integer>> ingredients = new ArrayList<>(additionalIngredients.entrySet().stream().map(Pair::of).toList());
        if (!ingredients.isEmpty()) {
            this.baseIngredient = ingredients.remove(0);
            additionalIngredients = ingredients.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
    }

    private WorkbenchVanillaCraftingRecipe(@NotNull ResourceLocation id, @Nullable Pair<Ingredient, Integer> baseIngredient, Map<Ingredient, Integer> additionalIngredients, ItemStack result) {
        super(id, true);
        this.result = result;
        this.baseIngredient = baseIngredient;
        this.additionalIngredients = additionalIngredients;
    }

    private static Map<Ingredient, Integer> getIngredientsFromCraftingRecipe(CraftingRecipe vanillaRecipe) {
        record IngredientKey(Set<Item> items) {
        }
        Map<IngredientKey, Ingredient> uniqueIngredients = new HashMap<>();
        Map<IngredientKey, Integer> ingredientCounts = new HashMap<>();
        NonNullList<Ingredient> vanillaIngredients = vanillaRecipe.getIngredients();
        for (Ingredient ingredient : vanillaIngredients) {
            ItemStack[] matchingStacks = ingredient.getItems();
            if (matchingStacks.length == 0) {
                continue;
            }
            Set<Item> itemSet = new HashSet<>(matchingStacks.length);
            for (ItemStack matchingStack : matchingStacks) {
                itemSet.add(matchingStack.getItem());
            }
            IngredientKey key = new IngredientKey(itemSet);
            uniqueIngredients.putIfAbsent(key, ingredient);
            ingredientCounts.put(key, ingredientCounts.getOrDefault(key, 0) + 1);
        }
        Map<Ingredient, Integer> result = new HashMap<>(ingredientCounts.size());
        for (Map.Entry<IngredientKey, Integer> entry : ingredientCounts.entrySet()) {
            result.put(uniqueIngredients.get(entry.getKey()), entry.getValue());
        }
        return result;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull WorkbenchContainer container, HolderLookup.@NotNull Provider registries) {
        return getResult(container);
    }

    @Override
    public boolean isValidBaseItem(ItemStack itemStack) {
        if (baseIngredient == null) {
            return false;
        }
        return baseIngredient.getKey().test(itemStack) && itemStack.getCount() >= baseIngredient.getValue();
    }

    @Override
    public Map<Ingredient, Integer> getAdditionalIngredients(ItemStack baseIngredient) {
        return getAdditionalIngredients();
    }

    public Map<Ingredient, Integer> getAdditionalIngredients() {
        return additionalIngredients;
    }

    @Override
    public boolean isLockedFor(@NotNull Player player) {
        List<VanillaRecipeUnlockBonus> recipeUnlockBonuses = SkillBonusProvider.getSkillBonuses(player, VanillaRecipeUnlockBonus.class);
        for (VanillaRecipeUnlockBonus skillBonus : recipeUnlockBonuses) {
            if (skillBonus.canUnlockRecipe(this)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Component getShortDescription() {
        return result.getHoverName();
    }

    @Override
    public @NotNull ItemStack getResult(WorkbenchContainer workbenchContainer) {
        return result.copy();
    }

    public @NotNull ItemStack getResult() {
        return result.copy();
    }

    @Override
    public int requiredBaseItemAmount() {
        if (baseIngredient == null) {
            return 0;
        }
        return baseIngredient.getRight();
    }

    @Override
    public @Nullable Pair<Ingredient, Integer> getBaseIngredient() {
        return baseIngredient;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return new Serializer();
    }

    public static class Serializer implements RecipeSerializer<WorkbenchVanillaCraftingRecipe> {
        // CORRECTION 1.21.1 : RecipeSerializer#fromJson(id, JsonObject) a disparu de l'interface.
        // Comme cette recette n'est jamais chargée depuis un fichier de datapack (elle est
        // synthétisée en Java depuis une CraftingRecipe vanilla, cf. javadoc de la classe), on
        // reproduit le refus explicite de l'ancien fromJson en faisant échouer le decode()
        // du MapCodec plutôt que de fournir un vrai schéma JSON.
        private static final MapCodec<WorkbenchVanillaCraftingRecipe> CODEC = new MapCodec<>() {
            @Override
            public <T> DataResult<WorkbenchVanillaCraftingRecipe> decode(DynamicOps<T> ops, MapLike<T> input) {
                return DataResult.error(() -> "Attempted to load an invalid recipe type.");
            }

            @Override
            public <T> RecordBuilder<T> encode(WorkbenchVanillaCraftingRecipe input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                return prefix.withErrorsFrom(DataResult.error(() -> "Attempted to save an invalid recipe type."));
            }

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.empty();
            }
        };

        // CORRECTION 1.21.1 : RecipeSerializer#fromNetwork(id, FriendlyByteBuf) / #toNetwork(buf, recipe)
        // ont disparu de l'interface, remplacés par streamCodec() (StreamCodec<RegistryFriendlyByteBuf, T>).
        // Contrairement au JSON, on garde ici une implémentation réseau réellement fonctionnelle
        // (au cas où ce serializer serait un jour effectivement utilisé pour une synchronisation),
        // avec les StreamCodec d'ingrédients 1.21.1 (Ingredient.CONTENTS_STREAM_CODEC) et
        // ItemStack.STREAM_CODEC à la place des anciens buf.readItem()/writeItem() et
        // Ingredient#toNetwork/fromNetwork.
        private static final StreamCodec<RegistryFriendlyByteBuf, WorkbenchVanillaCraftingRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::toNetwork, Serializer::fromNetwork
        );

        @Override
        public com.mojang.serialization.@NotNull MapCodec<WorkbenchVanillaCraftingRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, WorkbenchVanillaCraftingRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static @NotNull WorkbenchVanillaCraftingRecipe fromNetwork(@NotNull RegistryFriendlyByteBuf buf) {
            Map<Ingredient, Integer> ingredients = new HashMap<>();
            int ingredientsCount = buf.readInt();
            for (int i = 0; i < ingredientsCount; i++) {
                // CORRECTION 1.21.1: Ingredient.fromNetwork(buf) a disparu ; remplacé par le StreamCodec dédié.
                ingredients.put(Ingredient.CONTENTS_STREAM_CODEC.decode(buf), buf.readInt());
            }
            Pair<Ingredient, Integer> baseIngredient = null;
            boolean hasBaseIngredient = buf.readBoolean();
            if (hasBaseIngredient) {
                baseIngredient = Pair.of(Ingredient.CONTENTS_STREAM_CODEC.decode(buf), buf.readInt());
            }
            // CORRECTION 1.21.1: buf.readItem() a disparu ; remplacé par ItemStack.STREAM_CODEC.
            ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
            // CORRECTION 1.21.1 : voir la remarque sur UNKNOWN_ID en haut du fichier — le véritable
            // id devra être réinjecté via AbstractWorkbenchRecipe#setId(...) par l'appelant si ce
            // chemin réseau est un jour effectivement emprunté.
            return new WorkbenchVanillaCraftingRecipe(UNKNOWN_ID, baseIngredient, ingredients, result);
        }

        private static void toNetwork(@NotNull RegistryFriendlyByteBuf buf, @NotNull WorkbenchVanillaCraftingRecipe recipe) {
            int ingredientsCount = recipe.getAdditionalIngredients().size();
            buf.writeInt(ingredientsCount);
            recipe.getAdditionalIngredients().forEach((ingredient, requiredAmount) -> {
                // CORRECTION 1.21.1: Ingredient#toNetwork(buf) a disparu ; remplacé par le StreamCodec dédié.
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
                buf.writeInt(requiredAmount);
            });
            Pair<Ingredient, Integer> baseIngredient = recipe.baseIngredient;
            buf.writeBoolean(baseIngredient != null);
            if (baseIngredient != null) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, baseIngredient.getLeft());
                buf.writeInt(baseIngredient.getRight());
            }
            // CORRECTION 1.21.1: buf.writeItem(...) a disparu ; remplacé par ItemStack.STREAM_CODEC.
            ItemStack.STREAM_CODEC.encode(buf, recipe.result);
        }
    }
}