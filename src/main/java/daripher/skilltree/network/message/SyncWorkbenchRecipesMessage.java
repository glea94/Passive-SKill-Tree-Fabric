package daripher.skilltree.network.message;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.PSTRecipeTypes;
import daripher.skilltree.recipe.workbench.AbstractWorkbenchRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.List;
import java.util.Objects;

/**
 * 1.21.5 : côté client, RecipeAccess (retourné par ClientLevel#recipeAccess()) n'expose plus que
 * propertySet()/stonecutterRecipes() ; ClientPacketListener a aussi perdu getRecipeManager(). Il n'y a
 * donc plus aucun moyen pour le client d'obtenir la liste des recettes Workbench (venant du datapack)
 * sans passer par notre propre paquet réseau. Ce message est envoyé au join du joueur et transporte
 * l'intégralité des recettes AbstractWorkbenchRecipe en réutilisant le streamCodec() déjà exposé par
 * chaque RecipeSerializer concret du mod (WorkbenchCraftingRecipe.Serializer, etc.), sans avoir à
 * dupliquer leur logique d'encodage/décodage.
 */
public class SyncWorkbenchRecipesMessage implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncWorkbenchRecipesMessage> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "sync_workbench_recipes"));

    private static final StreamCodec<RegistryFriendlyByteBuf, AbstractWorkbenchRecipe> RECIPE_CODEC =
            StreamCodec.of(SyncWorkbenchRecipesMessage::encodeRecipe, SyncWorkbenchRecipesMessage::decodeRecipe);

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncWorkbenchRecipesMessage> STREAM_CODEC = StreamCodec.composite(
            RECIPE_CODEC.apply(ByteBufCodecs.list()), message -> message.recipes,
            SyncWorkbenchRecipesMessage::new
    );

    public final List<AbstractWorkbenchRecipe> recipes;

    private SyncWorkbenchRecipesMessage(List<AbstractWorkbenchRecipe> recipes) {
        this.recipes = recipes;
    }

    public SyncWorkbenchRecipesMessage(MinecraftServer server) {
        RecipeManager recipeManager = server.getRecipeManager();
        this.recipes = recipeManager.getRecipes().stream()
                .filter(holder -> holder.value().getType() == PSTRecipeTypes.WORKBENCH)
                .map(holder -> (AbstractWorkbenchRecipe) holder.value())
                .toList();
    }

    private static void encodeRecipe(RegistryFriendlyByteBuf buf, AbstractWorkbenchRecipe recipe) {
        Identifier.STREAM_CODEC.encode(buf, recipe.getId());
        RecipeSerializer<?> serializer = recipe.getSerializer();
        Identifier serializerId = BuiltInRegistries.RECIPE_SERIALIZER.getKey(serializer);
        Objects.requireNonNull(serializerId);
        Identifier.STREAM_CODEC.encode(buf, serializerId);
        contentCodec(serializer).encode(buf, recipe);
    }

    private static AbstractWorkbenchRecipe decodeRecipe(RegistryFriendlyByteBuf buf) {
        Identifier id = Identifier.STREAM_CODEC.decode(buf);
        Identifier serializerId = Identifier.STREAM_CODEC.decode(buf);
        RecipeSerializer<?> serializer = BuiltInRegistries.RECIPE_SERIALIZER.get(serializerId).orElseThrow().value();
        AbstractWorkbenchRecipe recipe = contentCodec(serializer).decode(buf);
        recipe.setId(id);
        return recipe;
    }

    @SuppressWarnings("unchecked")
    private static StreamCodec<RegistryFriendlyByteBuf, AbstractWorkbenchRecipe> contentCodec(RecipeSerializer<?> serializer) {
        return (StreamCodec<RegistryFriendlyByteBuf, AbstractWorkbenchRecipe>) (StreamCodec<?, ?>) serializer.streamCodec();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}