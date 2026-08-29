package daripher.skilltree.network.message;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.PSTRecipeTypes;
import daripher.skilltree.recipe.workbench.AbstractWorkbenchRecipe;
import daripher.skilltree.recipe.workbench.WorkbenchVanillaCraftingRecipe;
import daripher.skilltree.recipe.workbench.WorkbenchVanillaSmithingRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
        List<AbstractWorkbenchRecipe> allRecipes = new ArrayList<>();
        recipeManager.getRecipes().stream()
                .filter(holder -> holder.value().getType() == PSTRecipeTypes.WORKBENCH)
                .map(holder -> {
                    AbstractWorkbenchRecipe recipe = (AbstractWorkbenchRecipe) holder.value();
                    recipe.setId(holder.id().identifier());
                    return recipe;
                })
                .forEach(allRecipes::add);
        var registryAccess = server.registryAccess();
        ContextMap resolveContext = SlotDisplayContext.fromLevel(server.overworld());
        recipeManager.getRecipes().stream()
                .filter(holder -> holder.value().getType() == RecipeType.CRAFTING)
                .map(holder -> new RecipeHolder<>(holder.id(), (CraftingRecipe) holder.value()))
                .filter(recipe -> hasResolvableDisplay(recipe.value().display(), resolveContext, recipe.id()))
                .map(holder -> (AbstractWorkbenchRecipe) new WorkbenchVanillaCraftingRecipe(holder, registryAccess))
                .forEach(allRecipes::add);
        recipeManager.getRecipes().stream()
                .filter(holder -> holder.value().getType() == RecipeType.SMITHING)
                .filter(holder -> holder.value() instanceof SmithingTransformRecipe)
                .map(holder -> new RecipeHolder<>(holder.id(), (SmithingTransformRecipe) holder.value()))
                .filter(recipe -> hasResolvableDisplay(recipe.value().display(), resolveContext, recipe.id()))
                .map(holder -> (AbstractWorkbenchRecipe) new WorkbenchVanillaSmithingRecipe(holder, registryAccess))
                .forEach(allRecipes::add);
        this.recipes = List.copyOf(allRecipes);
    }
    private static boolean hasResolvableDisplay(List<RecipeDisplay> displays, ContextMap context, Object recipeId) {
        if (displays.isEmpty()) {
            return false;
        }
        try {
            ItemStack previewResult = displays.get(0).result().resolveForFirstStack(context);
            return !previewResult.isEmpty();
        } catch (Exception e) {
            SkillTreeMod.LOGGER.warn("Skipping broken recipe display for {} while syncing workbench recipes", recipeId, e);
            return false;
        }
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