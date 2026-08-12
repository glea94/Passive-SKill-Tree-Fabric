package daripher.skilltree.recipe.workbench;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import daripher.skilltree.init.PSTRecipeSerializers;
import daripher.skilltree.inventory.menu.WorkbenchContainer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
<<<<<<< Updated upstream
<<<<<<< Updated upstream
import net.minecraft.resources.ResourceLocation;
=======
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
>>>>>>> Stashed changes
=======
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
>>>>>>> Stashed changes
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

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
    private static final Identifier UNKNOWN_ID = Identifier.fromNamespaceAndPath("skilltree", "unknown_workbench_crafting_recipe");

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
    public @NotNull ItemStack assemble(@NotNull WorkbenchContainer container, @NotNull RegistryAccess registryAccess) {
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
    public @NotNull RecipeSerializer<?> getSerializer() {
        return PSTRecipeSerializers.WORKBENCH_CRAFTING.get();
    }

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
            boolean requiresPassiveSkill = buf.readBoolean();
            Map<Ingredient, Integer> additionalIngredients = new HashMap<>();
            int ingredientsCount = buf.readInt();
            for (int i = 0; i < ingredientsCount; i++) {
                additionalIngredients.put(Ingredient.fromNetwork(buf), buf.readInt());
            }
            Pair<Ingredient, Integer> baseIngredient = null;
            boolean hasBaseIngredient = buf.readBoolean();
            if (hasBaseIngredient) {
                baseIngredient = Pair.of(Ingredient.fromNetwork(buf), buf.readInt());
            }
            ItemStack result = buf.readItem();
            return new WorkbenchCraftingRecipe(id, baseIngredient, additionalIngredients, requiresPassiveSkill, result);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull WorkbenchCraftingRecipe recipe) {
            buf.writeBoolean(recipe.hasPassiveSkillRequirement());
            int ingredientsCount = recipe.getAdditionalIngredients().size();
            buf.writeInt(ingredientsCount);
            recipe.getAdditionalIngredients().forEach((ingredient, requiredAmount) -> {
                ingredient.toNetwork(buf);
                buf.writeInt(requiredAmount);
            });
            buf.writeBoolean(recipe.baseIngredient != null);
            if (recipe.baseIngredient != null) {
                recipe.baseIngredient.getLeft().toNetwork(buf);
                buf.writeInt(recipe.baseIngredient.getRight());
            }
            buf.writeItem(recipe.result);
        }
    }
}
