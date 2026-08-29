package daripher.skilltree.recipe.workbench;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;
import daripher.skilltree.inventory.menu.WorkbenchContainer;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.VanillaRecipeUnlockBonus;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
public class WorkbenchVanillaSmithingRecipe extends AbstractWorkbenchRecipe {
    private static final Identifier UNKNOWN_ID = Identifier.fromNamespaceAndPath("skilltree", "unknown_workbench_vanilla_smithing_recipe");
    private final Pair<Ingredient, Integer> baseIngredient;
    private final Map<Ingredient, Integer> additionalIngredients;
    private final ItemStack result;
    public WorkbenchVanillaSmithingRecipe(RecipeHolder<SmithingTransformRecipe> vanillaRecipeHolder, HolderLookup.Provider registryAccess) {
        super(vanillaRecipeHolder.id().identifier(), true);
        SmithingTransformRecipe vanillaRecipe = vanillaRecipeHolder.value();
        this.baseIngredient = Pair.of(vanillaRecipe.baseIngredient(), 1);
        this.additionalIngredients = new LinkedHashMap<>();
        Optional<Ingredient> template = vanillaRecipe.templateIngredient();
        template.ifPresent(ingredient -> additionalIngredients.put(ingredient, 1));
        Optional<Ingredient> addition = vanillaRecipe.additionIngredient();
        addition.ifPresent(ingredient -> additionalIngredients.put(ingredient, 1));
        this.result = resolveResult(vanillaRecipe, registryAccess);
    }
    private WorkbenchVanillaSmithingRecipe(@NotNull Identifier id, Pair<Ingredient, Integer> baseIngredient, Map<Ingredient, Integer> additionalIngredients, ItemStack result) {
        super(id, true);
        this.baseIngredient = baseIngredient;
        this.additionalIngredients = new LinkedHashMap<>(additionalIngredients);
        this.result = result;
    }
    private static ItemStack resolveResult(SmithingTransformRecipe vanillaRecipe, HolderLookup.Provider registryAccess) {
        List<RecipeDisplay> vanillaDisplays = vanillaRecipe.display();
        if (vanillaDisplays.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ContextMap resolveContext = new ContextMap.Builder()
                .withParameter(SlotDisplayContext.REGISTRIES, registryAccess)
                .create(SlotDisplayContext.CONTEXT);
        return vanillaDisplays.get(0).result().resolveForFirstStack(resolveContext);
    }
    @Override
    public boolean isValidBaseItem(ItemStack itemStack) {
        return baseIngredient.getKey().test(itemStack) && itemStack.getCount() >= baseIngredient.getValue();
    }
    @Override
    public Map<Ingredient, Integer> getAdditionalIngredients(ItemStack baseIngredient) {
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
        ItemStack baseItemStack = workbenchContainer.getBaseItem();
        if (baseItemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return baseItemStack.transmuteCopy(result.getItem(), result.getCount());
    }
    public @NotNull ItemStack getResult() {
        return result.copy();
    }
    @Override
    public int requiredBaseItemAmount() {
        return baseIngredient.getRight();
    }
    @Override
    public @Nullable Pair<Ingredient, Integer> getBaseIngredient() {
        return baseIngredient;
    }
    @Override
    public @NotNull RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }
    @Override
    public @NotNull RecipeSerializer<? extends Recipe<WorkbenchContainer>> getSerializer() {
        return Serializer.INSTANCE;
    }
    public static final class Serializer {
        private static final MapCodec<WorkbenchVanillaSmithingRecipe> CODEC = new MapCodec<>() {
            @Override
            public <T> DataResult<WorkbenchVanillaSmithingRecipe> decode(DynamicOps<T> ops, MapLike<T> input) {
                return DataResult.error(() -> "Attempted to load an invalid recipe type.");
            }
            @Override
            public <T> RecordBuilder<T> encode(WorkbenchVanillaSmithingRecipe input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                return prefix.withErrorsFrom(DataResult.error(() -> "Attempted to save an invalid recipe type."));
            }
            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.empty();
            }
        };
        private static final StreamCodec<RegistryFriendlyByteBuf, WorkbenchVanillaSmithingRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::toNetwork, Serializer::fromNetwork
        );
        public static final RecipeSerializer<WorkbenchVanillaSmithingRecipe> INSTANCE = new RecipeSerializer<>(CODEC, STREAM_CODEC);
        private static @NotNull WorkbenchVanillaSmithingRecipe fromNetwork(@NotNull RegistryFriendlyByteBuf buf) {
            Pair<Ingredient, Integer> baseIngredient = Pair.of(Ingredient.CONTENTS_STREAM_CODEC.decode(buf), buf.readInt());
            Map<Ingredient, Integer> additionalIngredients = new LinkedHashMap<>();
            int ingredientsCount = buf.readInt();
            for (int i = 0; i < ingredientsCount; i++) {
                additionalIngredients.put(Ingredient.CONTENTS_STREAM_CODEC.decode(buf), buf.readInt());
            }
            ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
            return new WorkbenchVanillaSmithingRecipe(UNKNOWN_ID, baseIngredient, additionalIngredients, result);
        }
        private static void toNetwork(@NotNull RegistryFriendlyByteBuf buf, @NotNull WorkbenchVanillaSmithingRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.baseIngredient.getLeft());
            buf.writeInt(recipe.baseIngredient.getRight());
            buf.writeInt(recipe.additionalIngredients.size());
            recipe.additionalIngredients.forEach((ingredient, requiredAmount) -> {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
                buf.writeInt(requiredAmount);
            });
            ItemStack.STREAM_CODEC.encode(buf, recipe.result);
        }
    }
}