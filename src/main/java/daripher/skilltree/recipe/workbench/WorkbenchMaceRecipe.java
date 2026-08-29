package daripher.skilltree.recipe.workbench;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import daripher.skilltree.event.MaceMasteryEvents;
import daripher.skilltree.init.PSTRecipeSerializers;
import daripher.skilltree.inventory.menu.WorkbenchContainer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
public class WorkbenchMaceRecipe extends AbstractWorkbenchRecipe {
    private static final Identifier UNKNOWN_ID = Identifier.fromNamespaceAndPath("skilltree", "unknown_workbench_mace_recipe");
    private final @Nullable Pair<Ingredient, Integer> baseIngredient;
    private final Map<Ingredient, Integer> additionalIngredients;
    private final Identifier resultItemId;
    private final int resultCount;
    private @Nullable ItemStack cachedResult;
    public WorkbenchMaceRecipe(Identifier id, @Nullable Pair<Ingredient, Integer> baseIngredient, Map<Ingredient, Integer> additionalIngredients, boolean requiresPassiveSkill, ItemStack result) {
        super(id, requiresPassiveSkill);
        this.baseIngredient = baseIngredient;
        // LinkedHashMap : l'ordre d'itération détermine à quel slot de la workbench
        // chaque ingrédient est associé, doit refléter l'ordre déclaré dans le JSON.
        this.additionalIngredients = new LinkedHashMap<>(additionalIngredients);
        this.resultItemId = BuiltInRegistries.ITEM.getKey(result.getItem());
        this.resultCount = result.getCount();
        this.cachedResult = result.copy();
    }
    private WorkbenchMaceRecipe(Identifier id, @Nullable Pair<Ingredient, Integer> baseIngredient, Map<Ingredient, Integer> additionalIngredients, boolean requiresPassiveSkill, Identifier resultItemId, int resultCount) {
        super(id, requiresPassiveSkill);
        this.baseIngredient = baseIngredient;
        this.additionalIngredients = new LinkedHashMap<>(additionalIngredients);
        this.resultItemId = resultItemId;
        this.resultCount = resultCount;
        this.cachedResult = null;
    }
    private ItemStack resultStack() {
        if (cachedResult == null) {
            Item item = BuiltInRegistries.ITEM.get(resultItemId).map(Holder::value).orElse(Items.AIR);
            cachedResult = new ItemStack(item, resultCount);
        }
        return cachedResult;
    }
    @Override
    public boolean isValidBaseItem(ItemStack itemStack) {
        if (baseIngredient == null) {
            return itemStack.isEmpty();
        }
        return baseIngredient.getLeft().test(itemStack) && itemStack.getCount() >= baseIngredient.getRight();
    }
    @Override
    public boolean isLockedFor(@NotNull Player player) {
        if (super.isLockedFor(player)) {
            return true;
        }
        return MaceMasteryEvents.hasMaceMasteryMace(player);
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
        return resultStack().getHoverName();
    }
    @Override
    public @NotNull ItemStack getResult(WorkbenchContainer workbenchContainer) {
        ItemStack result = resultStack().copy();
        MaceMasteryEvents.markAsMaceMasteryMace(result);
        return result;
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
    public @NotNull RecipeSerializer<WorkbenchMaceRecipe> getSerializer() {
        return PSTRecipeSerializers.WORKBENCH_MACE.get();
    }
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
    private record ResultEntry(String itemId, int count) {
        static final Codec<ResultEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("item").forGetter(ResultEntry::itemId),
                Codec.INT.optionalFieldOf("count", 1).forGetter(ResultEntry::count)
        ).apply(instance, ResultEntry::new));
    }
    public static final class Serializer {
        private static final MapCodec<WorkbenchMaceRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.BOOL.fieldOf("requires_passive_skill").forGetter(AbstractWorkbenchRecipe::hasPassiveSkillRequirement),
                IngredientEntry.CODEC.listOf().fieldOf("additionalIngredients").forGetter(recipe -> recipe.getAdditionalIngredients().entrySet().stream()
                        .map(entry -> new IngredientEntry(entry.getKey(), entry.getValue())).toList()),
                IngredientEntry.CODEC.optionalFieldOf("base_ingredient").forGetter(recipe -> Optional.ofNullable(recipe.getBaseIngredient()).map(IngredientEntry::of)),
                ResultEntry.CODEC.fieldOf("result").forGetter(recipe -> new ResultEntry(recipe.resultItemId.toString(), recipe.resultCount))
        ).apply(instance, (requiresPassiveSkill, ingredientEntries, baseIngredientEntry, resultEntry) -> {
            Map<Ingredient, Integer> additionalIngredients = ingredientEntries.stream()
                    .collect(Collectors.toMap(IngredientEntry::ingredient, IngredientEntry::requiredAmount,
                            (a, b) -> b, LinkedHashMap::new));
            Pair<Ingredient, Integer> baseIngredient = baseIngredientEntry.map(IngredientEntry::toPair).orElse(null);
            Identifier resultItemId = Identifier.parse(resultEntry.itemId());
            return new WorkbenchMaceRecipe(UNKNOWN_ID, baseIngredient, additionalIngredients, requiresPassiveSkill, resultItemId, resultEntry.count());
        }));
        private static final StreamCodec<RegistryFriendlyByteBuf, WorkbenchMaceRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::toNetwork, Serializer::fromNetwork
        );
        public static final RecipeSerializer<WorkbenchMaceRecipe> INSTANCE = new RecipeSerializer<>(CODEC, STREAM_CODEC);
        private static @NotNull WorkbenchMaceRecipe fromNetwork(@NotNull RegistryFriendlyByteBuf buf) {
            boolean requiresPassiveSkill = buf.readBoolean();
            Map<Ingredient, Integer> additionalIngredients = new LinkedHashMap<>();
            int ingredientsCount = buf.readInt();
            for (int i = 0; i < ingredientsCount; i++) {
                additionalIngredients.put(Ingredient.CONTENTS_STREAM_CODEC.decode(buf), buf.readInt());
            }
            Pair<Ingredient, Integer> baseIngredient = null;
            boolean hasBaseIngredient = buf.readBoolean();
            if (hasBaseIngredient) {
                baseIngredient = Pair.of(Ingredient.CONTENTS_STREAM_CODEC.decode(buf), buf.readInt());
            }
            ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
            return new WorkbenchMaceRecipe(UNKNOWN_ID, baseIngredient, additionalIngredients, requiresPassiveSkill, result);
        }
        private static void toNetwork(@NotNull RegistryFriendlyByteBuf buf, @NotNull WorkbenchMaceRecipe recipe) {
            buf.writeBoolean(recipe.hasPassiveSkillRequirement());
            int ingredientsCount = recipe.getAdditionalIngredients().size();
            buf.writeInt(ingredientsCount);
            recipe.getAdditionalIngredients().forEach((ingredient, requiredAmount) -> {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
                buf.writeInt(requiredAmount);
            });
            buf.writeBoolean(recipe.baseIngredient != null);
            if (recipe.baseIngredient != null) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.baseIngredient.getLeft());
                buf.writeInt(recipe.baseIngredient.getRight());
            }
            ItemStack.STREAM_CODEC.encode(buf, recipe.resultStack());
        }
    }
}