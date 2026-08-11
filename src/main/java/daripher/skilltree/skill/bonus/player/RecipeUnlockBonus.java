package daripher.skilltree.skill.bonus.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTRecipeTypes;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.skill.bonus.SkillBonus;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
<<<<<<< Updated upstream
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
=======
import net.minecraft.resources.Identifier;
>>>>>>> Stashed changes

import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class RecipeUnlockBonus implements SkillBonus<RecipeUnlockBonus> {
    private @NotNull Identifier recipeId;

    public RecipeUnlockBonus(@NotNull Identifier recipeId) {
        this.recipeId = recipeId;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.RECIPE_UNLOCK.get();
    }

    @Override
    public RecipeUnlockBonus copy() {
        return new RecipeUnlockBonus(recipeId);
    }

    @Override
    public RecipeUnlockBonus multiply(double multiplier) {
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof RecipeUnlockBonus otherBonus)) {
            return false;
        }
        return Objects.equals(otherBonus.recipeId, this.recipeId);
    }

    @Override
    public SkillBonus<RecipeUnlockBonus> merge(SkillBonus<?> other) {
        return this;
    }

    @Override
    public MutableComponent getSimpleTooltip() {
        String customSkillDescriptionId = TooltipHelper.getRecipeDescriptionId(recipeId) + ".custom_skill_description";
        MutableComponent customSkillTooltip = Component.translatable(customSkillDescriptionId);
        Style skillBonusTooltipStyle = TooltipHelper.getSkillBonusStyle(isPositive());
        if (!customSkillTooltip.getString().equals(customSkillDescriptionId)) {
            return customSkillTooltip.withStyle(skillBonusTooltipStyle);
        }
        Component recipeTooltip = TooltipHelper.getRecipeTooltip(recipeId);
        Style recipeTooltipStyle = TooltipHelper.getItemUpgradeStyle();
        recipeTooltip = Component.literal(recipeTooltip.getString()).withStyle(recipeTooltipStyle);
        MutableComponent tooltip = Component.translatable(getDescriptionId(), recipeTooltip);
        return tooltip.withStyle(skillBonusTooltipStyle);
    }

    @Override
    public boolean isPositive() {
        return true;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<RecipeUnlockBonus> consumer) {
        editor.addLabel(0, 0, "Recipe ID", ChatFormatting.GOLD);
        editor.increaseHeight(19);
<<<<<<< Updated upstream
        ClientLevel clientLevel = Minecraft.getInstance().level;
        Objects.requireNonNull(clientLevel);
        RecipeManager recipesManager = clientLevel.getRecipeManager();
        List<ResourceLocation> artisanRecipes = recipesManager.getAllRecipesFor(PSTRecipeTypes.WORKBENCH).stream().map(Recipe::getId)
=======
        List<Identifier> artisanRecipes = ClientWorkbenchRecipeCache.getAll().stream()
                .map(AbstractWorkbenchRecipe::getId)
>>>>>>> Stashed changes
                .toList();
        editor.addSelectionMenu(0, 0, 200, artisanRecipes).setValue(recipeId).setResponder(id -> selectRecipeId(editor, consumer, id));
        editor.increaseHeight(19);
    }

    private void selectRecipeId(SkillTreeEditor editor, Consumer<RecipeUnlockBonus> consumer, Identifier id) {
        setRecipeId(id);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    public void setRecipeId(@NotNull Identifier id) {
        this.recipeId = id;
    }

    @NotNull
    public Identifier getRecipeId() {
        return recipeId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        RecipeUnlockBonus that = (RecipeUnlockBonus) obj;
        return Objects.equals(this.recipeId, that.recipeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipeId);
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public RecipeUnlockBonus deserialize(JsonObject json) throws JsonParseException {
<<<<<<< Updated upstream
            ResourceLocation recipeId = new ResourceLocation(json.get("recipe_id").getAsString());
=======
            Identifier recipeId = Identifier.parse(json.get("recipe_id").getAsString());
>>>>>>> Stashed changes
            return new RecipeUnlockBonus(recipeId);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof RecipeUnlockBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("recipe_id", aBonus.recipeId.toString());
        }

        @Override
        public RecipeUnlockBonus deserialize(CompoundTag tag) {
<<<<<<< Updated upstream
            ResourceLocation recipeId = new ResourceLocation(tag.getString("recipe_id"));
=======
            Identifier recipeId = Identifier.parse(tag.getString("recipe_id").orElseThrow());
>>>>>>> Stashed changes
            return new RecipeUnlockBonus(recipeId);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof RecipeUnlockBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putString("recipe_id", aBonus.recipeId.toString());
            return tag;
        }

        @Override
<<<<<<< Updated upstream
        public RecipeUnlockBonus deserialize(FriendlyByteBuf buf) {
            ResourceLocation recipeId = new ResourceLocation(buf.readUtf());
=======
        public RecipeUnlockBonus deserialize(RegistryFriendlyByteBuf buf) {
            Identifier recipeId = Identifier.parse(buf.readUtf());
>>>>>>> Stashed changes
            return new RecipeUnlockBonus(recipeId);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof RecipeUnlockBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeUtf(aBonus.recipeId.toString());
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
<<<<<<< Updated upstream
            return new RecipeUnlockBonus(new ResourceLocation("unknown_recipe"));
=======
            return new RecipeUnlockBonus(Identifier.parse("unknown_recipe"));
>>>>>>> Stashed changes
        }
    }
}
