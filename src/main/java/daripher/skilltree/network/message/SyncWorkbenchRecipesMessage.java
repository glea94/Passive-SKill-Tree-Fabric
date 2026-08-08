package daripher.skilltree.network.message;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.recipe.workbench.AbstractWorkbenchRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import java.util.List;

/**
 * Nouveau : remplace l'accès direct au RecipeManager côté client (disparu en 1.21.2+, cf.
 * RecipeAccess). Le serveur envoie la liste complète des recettes du workbench (natives +
 * converties depuis les CraftingRecipe vanilla) à la connexion du joueur, réutilisant le dispatch
 * générique Recipe.STREAM_CODEC (RecipeSerializer::streamCodec) déjà fourni par vanilla.
 */
public record SyncWorkbenchRecipesMessage(List<AbstractWorkbenchRecipe> recipes) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncWorkbenchRecipesMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "sync_workbench_recipes"));

    @SuppressWarnings("unchecked")
    private static final StreamCodec<RegistryFriendlyByteBuf, AbstractWorkbenchRecipe> RECIPE_STREAM_CODEC =
            Recipe.STREAM_CODEC.map(recipe -> (AbstractWorkbenchRecipe) recipe, recipe -> (Recipe<?>) recipe);

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncWorkbenchRecipesMessage> STREAM_CODEC =
            RECIPE_STREAM_CODEC.apply(ByteBufCodecs.list()).map(SyncWorkbenchRecipesMessage::new, SyncWorkbenchRecipesMessage::recipes);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}