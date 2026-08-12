package daripher.skilltree.recipe.workbench;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;
import daripher.skilltree.inventory.menu.WorkbenchContainer;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.VanillaRecipeUnlockBonus;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
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

public class WorkbenchVanillaCraftingRecipe extends AbstractWorkbenchRecipe {
    private static final ResourceLocation UNKNOWN_ID = ResourceLocation.fromNamespaceAndPath("skilltree", "unknown_workbench_vanilla_crafting_recipe");

    private @Nullable Pair<Ingredient, Integer> baseIngredient;
    private Map<Ingredient, Integer> additionalIngredients;
    private final ItemStack result;

    public WorkbenchVanillaCraftingRecipe(RecipeHolder<CraftingRecipe> vanillaRecipeHolder, RegistryAccess registryAccess) {
        super(vanillaRecipeHolder.id().location(), true);
        CraftingRecipe vanillaRecipe = vanillaRecipeHolder.value();
        this.result = assembleResult(vanillaRecipe, registryAccess);
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

    // Portage 1.21.4 : getResultItem(RegistryAccess) a disparu de CraftingRecipe. On reconstruit
    // un CraftingInput synthétique (une rangée d'un item représentatif par ingrédient) et on
    // appelle assemble() dessus, comme le ferait le jeu. Fidèle pour les recettes à résultat fixe ;
    // à vérifier si le mod utilise des recettes vanilla "spéciales" à résultat dynamique (isSpecial()).
    private static ItemStack assembleResult(CraftingRecipe vanillaRecipe, RegistryAccess registryAccess) {
        List<Ingredient> ingredients = vanillaRecipe.placementInfo().ingredients();
        List<ItemStack> gridItems = new ArrayList<>(ingredients.size());
        for (Ingredient ingredient : ingredients) {
            gridItems.add(ingredient.items().findFirst().map(holder -> new ItemStack(holder.value())).orElse(ItemStack.EMPTY));
        }
        CraftingInput craftingInput = CraftingInput.of(gridItems.size(), 1, gridItems);
        return vanillaRecipe.assemble(craftingInput, registryAccess);
    }

    private static Map<Ingredient, Integer> getIngredientsFromCraftingRecipe(CraftingRecipe vanillaRecipe) {
        record IngredientKey(Set<Item> items) {
        }
        Map<IngredientKey, Ingredient> uniqueIngredients = new HashMap<>();
        Map<IngredientKey, Integer> ingredientCounts = new HashMap<>();
        List<Ingredient> vanillaIngredients = vanillaRecipe.placementInfo().ingredients();
        for (Ingredient ingredient : vanillaIngredients) {
            Set<Item> itemSet = ingredient.items().map(Holder::value).collect(Collectors.toSet());
            if (itemSet.isEmpty()) {
                continue;
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
    public @NotNull RecipeSerializer<WorkbenchVanillaCraftingRecipe> getSerializer() {
        return daripher.skilltree.init.PSTRecipeSerializers.WORKBENCH_VANILLA_CRAFTING.get();
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