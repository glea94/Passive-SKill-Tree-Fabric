package daripher.skilltree.data.client;

import daripher.skilltree.SkillTreeMod;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class SkillTexturesData implements IdentifiableResourceReloadListener {
    private static final Map<String, Set<ResourceLocation>> FOLDER_TO_TEXTURES = new HashMap<>();

    public static void register() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SkillTexturesData());
    }

    @Override
<<<<<<< Updated upstream
<<<<<<< Updated upstream
    public ResourceLocation getFabricId() {
        return new ResourceLocation(SkillTreeMod.MOD_ID, "skill_textures_reloader");
=======
    public Identifier getFabricId() {
        return Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "skill_textures_reloader");
>>>>>>> Stashed changes
=======
    public Identifier getFabricId() {
        return Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "skill_textures_reloader");
>>>>>>> Stashed changes
    }

    @Override
    public CompletableFuture<Void> reload(PreparableReloadListener.SharedState sharedState, Executor prepareExecutor,
                                          PreparableReloadListener.PreparationBarrier barrier, Executor applyExecutor) {
        ResourceManager resourceManager = sharedState.resourceManager();
        return CompletableFuture.supplyAsync(() -> scanTextures(resourceManager), prepareExecutor)
                .thenCompose(barrier::wait)
                .thenAcceptAsync(result -> {
                    FOLDER_TO_TEXTURES.clear();
                    FOLDER_TO_TEXTURES.putAll(result);
                }, applyExecutor);
    }

<<<<<<< Updated upstream
<<<<<<< Updated upstream
    private static Map<String, Set<ResourceLocation>> scanTextures(ResourceManager resourceManager) {
        Map<String, Set<ResourceLocation>> result = new HashMap<>();
        Map<ResourceLocation, Resource> textures = resourceManager.listResources("textures", SkillTexturesData::isTexturePath);
        List<ResourceLocation> textureLocations = textures.keySet().stream().toList();
        for (ResourceLocation textureLocation : textureLocations) {
=======
=======
>>>>>>> Stashed changes
    private static Map<String, Set<Identifier>> scanTextures(ResourceManager resourceManager) {
        Map<String, Set<Identifier>> result = new HashMap<>();

        Map<Identifier, Resource> textures = resourceManager.listResources("textures", SkillTexturesData::isTexturePath);
        List<Identifier> textureLocations = textures.keySet().stream().toList();
        for (Identifier textureLocation : textureLocations) {
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
            String folder = getTextureFolder(textureLocation);
            if (folder.isEmpty()) {
                continue;
            }
            result.computeIfAbsent(folder, f -> new HashSet<>()).add(textureLocation);
        }
        return result;
    }

    @NotNull
    public static String getTextureFolder(ResourceLocation textureLocation) {
        String path = textureLocation.getPath();
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash <= 0) {
            return "";
        }
        return path.substring(0, lastSlash);
    }

    private static boolean isTexturePath(ResourceLocation location) {
        return location.getPath().endsWith(".png");
    }

    public static Set<ResourceLocation> getTexturesInFolder(String folder) {
        return FOLDER_TO_TEXTURES.getOrDefault(folder, Set.of());
    }

    public static boolean isTextureFolder(String string) {
        return FOLDER_TO_TEXTURES.containsKey(string);
    }

    @Nullable
    public static String autocompleteFolderName(String string) {
        Set<String> folders = FOLDER_TO_TEXTURES.keySet();
        Optional<String> autocomplete = folders.stream().filter(s -> s.startsWith(string)).findAny().map(s -> s.replaceFirst(string, ""));
        return autocomplete.orElse(null);
    }
}