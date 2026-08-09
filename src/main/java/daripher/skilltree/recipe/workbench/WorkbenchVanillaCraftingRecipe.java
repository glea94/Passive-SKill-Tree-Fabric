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
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WorkbenchVanillaCraftingRecipe extends AbstractWorkbenchRecipe {
    private static final ResourceLocation UNKNOWN_ID = ResourceLocation.fromNamespaceAndPath("skilltree", "unknown_workbench_vanilla_crafting_recipe");

    private @Nullable Pair<Ingredient, Integer> baseIngredient;
    private Map<Ingredient, Integer> additionalIngredients;
    private final ItemStack result;

    public WorkbenchVanillaCraftingRecipe(RecipeHolder<CraftingRecipe> vanillaRecipeHolder, HolderLookup.Provider registryAccess) {
        // Factual Fix 1.21.4: Extracted raw ResourceLocation path out of the RecipeHolder's ResourceKey handle
        super(vanillaRecipeHolder.id().location(), true);
        CraftingRecipe vanillaRecipe = vanillaRecipeHolder.value();
        // Factual Fix 1.21.5 : CraftingRecipe#getResultItem(HolderLookup.Provider) a été retiré de l'interface Recipe<T>,
        // remplacé par le système RecipeDisplay (confirmé par décompilation Fernflower de Recipe<T>, RecipeDisplay et
        // SlotDisplay) : Recipe#display() -> List<RecipeDisplay>, RecipeDisplay#result() -> SlotDisplay,
        // SlotDisplay#resolveForFirstStack(ContextMap) -> ItemStack. Ici on n'a qu'un HolderLookup.Provider (pas un
        // Level), donc le ContextMap ne peut pas être construit via SlotDisplayContext.fromLevel(Level) comme dans
        // WorkbenchMenu.java ; il est construit à la main avec la même API (ContextMap.Builder#withParameter/#create,
        // confirmée par le corps décompilé de SlotDisplayContext.fromLevel) en ne renseignant que REGISTRIES, seule clé
        // utilisée par les CraftingRecipe vanilla standards (FUEL_VALUES ne concerne que les recettes de fourneau).
        List<RecipeDisplay> vanillaDisplays = vanillaRecipe.display();
        ItemStack resolvedResult = ItemStack.EMPTY;
        if (!vanillaDisplays.isEmpty()) {
            ContextMap resolveContext = new ContextMap.Builder()
                    .withParameter(SlotDisplayContext.REGISTRIES, registryAccess)
                    .create(SlotDisplayContext.CONTEXT);
            resolvedResult = vanillaDisplays.get(0).result().resolveForFirstStack(resolveContext);
        }
        this.result = resolvedResult;
        this.additionalIngredients = getIngredientsFromCraftingRecipe(vanillaRecipe, registryAccess);
        List<Pair<Ingredient, Integer>> ingredients = new ArrayList<>(additionalIngredients.entrySet().stream().map(Pair::of).toList());
        if (!ingredients.isEmpty()) {
            this.baseIngredient = ingredients.remove(0);
            this.additionalIngredients = ingredients.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
    }

    private WorkbenchVanillaCraftingRecipe(@NotNull ResourceLocation id, @Nullable Pair<Ingredient, Integer> baseIngredient, Map<Ingredient, Integer> additionalIngredients, ItemStack result) {
        super(id, true);
        this.result = result;
        this.baseIngredient = baseIngredient;
        this.additionalIngredients = additionalIngredients;
    }

    private static Map<Ingredient, Integer> getIngredientsFromCraftingRecipe(CraftingRecipe vanillaRecipe, HolderLookup.Provider registryAccess) {
        record IngredientKey(Set<Item> items) {
        }
        Map<IngredientKey, Ingredient> uniqueIngredients = new HashMap<>();
        Map<IngredientKey, Integer> ingredientCounts = new HashMap<>();

        // Factual Fix 1.21.4: Read recipe ingredient configurations out of vanilla container placement lists
        List<Ingredient> vanillaIngredients = vanillaRecipe.placementInfo().ingredients();
        for (Ingredient ingredient : vanillaIngredients) {
            // Factual Fix 1.21.5 : ingredient.items() renvoie désormais directement un Stream<Holder<Item>>
            // (confirmé par l'erreur de compilation "cannot find symbol: method stream() location: interface
            // Stream<Holder<Item>>" -> le .stream() supplémentaire était appelé sur un Stream déjà construit)
            List<ItemStack> matchingItemList = ingredient.items().map(Holder::value).map(ItemStack::new).toList();
            if (matchingItemList.isEmpty()) {
                continue;
            }
            Set<Item> itemSet = new HashSet<>(matchingItemList.size());
            for (ItemStack matchingStack : matchingItemList) {
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

    // Factual Fix 1.21.4: Resolve recipe book classification categories using type-safe registration lookups directly
    @Override
    public @NotNull RecipeBookCategory recipeBookCategory() {
        return net.minecraft.world.item.crafting.RecipeBookCategories.CRAFTING_MISC;
    }


    // Factual Fix 1.21.5 : le type de retour de Recipe#getSerializer() est désormais covariant. Le message du
    // compilateur donne lui-même le type exact attendu ("RecipeSerializer<? extends Recipe<WorkbenchContainer>>"),
    // donc ce correctif est sûr, sans décompilation nécessaire (même erreur/même fix que WorkbenchCraftingRecipe,
    // WorkbenchUpgradeBonusRecipe, WorkbenchWeaponPoisoningRecipe listés dans le rapport de 76 erreurs).
    @Override
    public @NotNull RecipeSerializer<? extends Recipe<WorkbenchContainer>> getSerializer() {
        return new Serializer();
    }

    public static class Serializer implements RecipeSerializer<WorkbenchVanillaCraftingRecipe> {
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

        // Factual Fix 1.21.5 : confirmé par décompilation Fernflower (net.minecraft.world.item.crafting.Ingredient) —
        // Ingredient.STREAM_CODEC n'a jamais existé, c'est Ingredient.CONTENTS_STREAM_CODEC (déjà présent en 1.21.1)
        // qui est le bon champ, inchangé en 1.21.5. Le renommage supposé lors du fix 1.21.4 était erroné.
        private static @NotNull WorkbenchVanillaCraftingRecipe fromNetwork(@NotNull RegistryFriendlyByteBuf buf) {
            Map<Ingredient, Integer> ingredients = new HashMap<>();
            int ingredientsCount = buf.readInt();
            for (int i = 0; i < ingredientsCount; i++) {
                ingredients.put(Ingredient.CONTENTS_STREAM_CODEC.decode(buf), buf.readInt());
            }
            Pair<Ingredient, Integer> baseIngredient = null;
            boolean hasBaseIngredient = buf.readBoolean();
            if (hasBaseIngredient) {
                baseIngredient = Pair.of(Ingredient.CONTENTS_STREAM_CODEC.decode(buf), buf.readInt());
            }
            ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
            return new WorkbenchVanillaCraftingRecipe(UNKNOWN_ID, baseIngredient, ingredients, result);
        }

        private static void toNetwork(@NotNull RegistryFriendlyByteBuf buf, @NotNull WorkbenchVanillaCraftingRecipe recipe) {
            int ingredientsCount = recipe.getAdditionalIngredients().size();
            buf.writeInt(ingredientsCount);
            recipe.getAdditionalIngredients().forEach((ingredient, requiredAmount) -> {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
                buf.writeInt(requiredAmount);
            });
            Pair<Ingredient, Integer> baseIngredient = recipe.baseIngredient;
            buf.writeBoolean(baseIngredient != null);
            if (baseIngredient != null) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, baseIngredient.getLeft());
                buf.writeInt(baseIngredient.getRight());
            }
            ItemStack.STREAM_CODEC.encode(buf, recipe.result);
        }
    }
}