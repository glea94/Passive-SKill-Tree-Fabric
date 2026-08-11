package daripher.skilltree.data.client;

import daripher.skilltree.SkillTreeMod;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class SkillTexturesData implements IdentifiableResourceReloadListener {
    private static final Map<String, Set<Identifier>> FOLDER_TO_TEXTURES = new HashMap<>();

    public static void register() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SkillTexturesData());
    }

    @Override
<<<<<<< Updated upstream
    public ResourceLocation getFabricId() {
        return new ResourceLocation(SkillTreeMod.MOD_ID, "skill_textures_reloader");
=======
    public Identifier getFabricId() {
        return Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "skill_textures_reloader");
>>>>>>> Stashed changes
    }

    // Portage Fabric : IdentifiableResourceReloadListener étend PreparableReloadListener
    // directement (pas le simple ResourceManagerReloadListener) : la vraie méthode abstraite à
    // implémenter est reload(...), pas onResourceManagerReload(ResourceManager). Le travail
    // (scan des textures) est simple et synchrone, donc fait entièrement côté "prepare".
    @Override
    public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier barrier, ResourceManager resourceManager,
                                           ProfilerFiller prepareProfiler, ProfilerFiller applyProfiler,
                                           Executor prepareExecutor, Executor applyExecutor) {
        return CompletableFuture.supplyAsync(() -> scanTextures(resourceManager), prepareExecutor)
                .thenCompose(barrier::wait)
                .thenAcceptAsync(result -> {
                    FOLDER_TO_TEXTURES.clear();
                    FOLDER_TO_TEXTURES.putAll(result);
                }, applyExecutor);
    }

<<<<<<< Updated upstream
    private static Map<String, Set<ResourceLocation>> scanTextures(ResourceManager resourceManager) {
        Map<String, Set<ResourceLocation>> result = new HashMap<>();
        Map<ResourceLocation, Resource> textures = resourceManager.listResources("textures", SkillTexturesData::isTexturePath);
        List<ResourceLocation> textureLocations = textures.keySet().stream().toList();
        for (ResourceLocation textureLocation : textureLocations) {
=======
    private static Map<String, Set<Identifier>> scanTextures(ResourceManager resourceManager) {
        Map<String, Set<Identifier>> result = new HashMap<>();

        Map<Identifier, Resource> textures = resourceManager.listResources("textures", SkillTexturesData::isTexturePath);
        List<Identifier> textureLocations = textures.keySet().stream().toList();
        for (Identifier textureLocation : textureLocations) {
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
    public static String getTextureFolder(Identifier textureLocation) {
        String path = textureLocation.getPath();
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash <= 0) {
            return "";
        }
        return path.substring(0, lastSlash);    }

    private static boolean isTexturePath(Identifier location) {
        return location.getPath().endsWith(".png");
    }

    public static Set<Identifier> getTexturesInFolder(String folder) {
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
