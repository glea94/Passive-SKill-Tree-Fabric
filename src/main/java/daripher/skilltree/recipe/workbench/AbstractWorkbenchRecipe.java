package daripher.skilltree.recipe.workbench;

import daripher.skilltree.init.PSTRecipeTypes;
import daripher.skilltree.inventory.menu.WorkbenchContainer;
import daripher.skilltree.recipe.SkillRequiringRecipe;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.RecipeUnlockBonus;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.core.registries.BuiltInRegistries;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Portage Fabric 1.21.1 : l'interface {@link Recipe} n'expose plus {@code getId()}. En 1.21.1,
 * l'identifiant d'une recette ne vit plus DANS l'objet recette : il vit à côté, dans le
 * {@link RecipeHolder} construit par le {@link net.minecraft.world.item.crafting.RecipeManager}
 * au moment du chargement (JSON via codec(), ou réseau via streamCodec()). Concrètement, les
 * Codec/StreamCodec des sous-classes concrètes (WorkbenchCraftingRecipe, etc.) ne reçoivent
 * PLUS l'id en paramètre lors de la reconstruction de l'objet — contrairement à l'ancien
 * fromJson(id, json) / fromNetwork(id, buf) de la 1.20.1.
 *
 * Comme tout le reste du mod (tri des recettes dans WorkbenchMenu, vérification de déblocage
 * via RecipeUnlockBonus, tooltips, éditeur d'arbre de compétences...) dépend d'un getId() "à la
 * 1.20.1" directement accessible sur l'objet recette, on garde ici un champ id "maison" — mais
 * il n'est plus final : il est rempli avec un placeholder à la construction (par les
 * sous-classes, via le constructeur), PUIS corrigé par {@link #setId(ResourceLocation)} dès que
 * le vrai id est connu, c'est-à-dire dès qu'on tient un RecipeHolder<AbstractWorkbenchRecipe>.
 *
 * Point d'ancrage obligatoire : partout où le mod récupère des RecipeHolder<AbstractWorkbenchRecipe>
 * depuis le RecipeManager (typiquement WorkbenchMenu#getAllWorkbenchRecipes(), qui fait
 * aujourd'hui .map(RecipeHolder::value) et jette l'id au passage), il faut désormais appeler
 * recipeHolder.value().setId(recipeHolder.id()) avant d'utiliser la recette, sans quoi getId()
 * ne renverra que le placeholder et le déblocage de recette par l'arbre de compétences restera
 * cassé silencieusement. De même, RecipeUnlockBonus#addEditorWidgets doit utiliser
 * RecipeHolder::id (et non plus Recipe::getId, qui n'existe plus) pour peupler la liste des
 * recettes sélectionnables.
 */
public abstract class AbstractWorkbenchRecipe implements Recipe<WorkbenchContainer>, SkillRequiringRecipe {
    private ResourceLocation id;
    private final boolean requiresPassiveSkill;

    protected AbstractWorkbenchRecipe(ResourceLocation id, boolean requiresPassiveSkill) {
        this.requiresPassiveSkill = requiresPassiveSkill;
        this.id = id;
    }

    @Override
    public boolean matches(@NotNull WorkbenchContainer container, @NotNull Level level) {
        ItemStack baseItem = container.getBaseItem();
        if (!isValidBaseItem(baseItem)) {
            return false;
        }
        if (isLockedFor(container.getPlayer())) {
            return false;
        }
        return hasIngredients(container, getAdditionalIngredients(baseItem));
    }

    public String getDescriptionId() {
        ResourceLocation serializerId = BuiltInRegistries.RECIPE_SERIALIZER.getKey(getSerializer());
        Objects.requireNonNull(serializerId);
        return "recipe.%s.%s".formatted(serializerId.getNamespace(), serializerId.getPath());
    }

    public boolean isLockedFor(@NotNull Player player) {
        return requiresPassiveSkill && !hasRecipeLearned(player);
    }

    public abstract boolean isValidBaseItem(ItemStack itemStack);

    public boolean isValidIngredient(ItemStack itemStack) {
        return true;
    }

    public abstract Component getShortDescription();

    public List<Component> getFullDescription() {
        return List.of(getShortDescription());
    }

    public abstract @NotNull ItemStack getResult(WorkbenchContainer workbenchContainer);

    public abstract int requiredBaseItemAmount();

    public abstract @Nullable Pair<Ingredient, Integer> getBaseIngredient();

    public abstract Map<Ingredient, Integer> getAdditionalIngredients(ItemStack baseIngredient);

    protected final boolean hasRecipeLearned(@NotNull Player player) {
        return SkillBonusProvider.getSkillBonuses(player, RecipeUnlockBonus.class).stream().map(RecipeUnlockBonus::getRecipeId)
                .anyMatch(getId()::equals);
    }

    protected boolean hasIngredients(@NotNull WorkbenchContainer container, Map<Ingredient, Integer> ingredients) {
        return container.hasIngredients(ingredients);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width == 5 && height == 2;
    }

    // Portage Fabric 1.21.1 : getId() n'appartient plus à l'interface Recipe, donc plus de
    // @Override ici. C'est maintenant une méthode "maison" utilisée partout ailleurs dans le
    // mod (tri des recettes, vérification de déblocage, tooltips, éditeur...). Sa valeur n'est
    // fiable qu'après que setId(...) ait été appelé avec l'id réel issu du RecipeHolder — voir
    // la javadoc de la classe.
    public @NotNull ResourceLocation getId() {
        return id;
    }

    // Portage Fabric 1.21.1 : point d'ancrage pour réinjecter l'id réel (celui du RecipeHolder
    // fourni par le RecipeManager) une fois la recette reconstruite par codec()/streamCodec().
    // À appeler obligatoirement par tout code qui récupère des RecipeHolder<AbstractWorkbenchRecipe>
    // depuis le RecipeManager, avant toute utilisation de getId() (tri, déblocage, tooltips...).
    public void setId(@NotNull ResourceLocation id) {
        this.id = id;
    }

    @Deprecated
    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return PSTRecipeTypes.WORKBENCH;
    }

    @Override
    public boolean hasPassiveSkillRequirement() {
        return requiresPassiveSkill;
    }
}