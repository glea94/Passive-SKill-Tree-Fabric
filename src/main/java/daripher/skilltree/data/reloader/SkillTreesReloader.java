package daripher.skilltree.data.reloader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.PassiveSkillTree;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;


public class SkillTreesReloader extends SimplePreparableReloadListener<Map<Identifier, JsonElement>> implements IdentifiableResourceReloadListener {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Identifier.class, new com.google.gson.TypeAdapter<Identifier>() {
                @Override
                public void write(com.google.gson.stream.JsonWriter out, Identifier value) throws java.io.IOException {
                    out.value(value.toString());
                }
                @Override
                public Identifier read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
                    return Identifier.parse(in.nextString());
                }
            })
            .setPrettyPrinting()
            .create();

    private static final Map<Identifier, PassiveSkillTree> SKILL_TREES = new HashMap<>();

    public SkillTreesReloader() {
        super();
    }

    @Override
    public Identifier getFabricId() {
        return Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "skill_trees_reloader");
    }

    public static void register() {
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new SkillTreesReloader());
    }

    public static Map<Identifier, PassiveSkillTree> getSkillTrees() {
        return SKILL_TREES;
    }

    public static PassiveSkillTree getSkillTreeById(Identifier id) {
        return SKILL_TREES.getOrDefault(id, new PassiveSkillTree(id));
    }

    public static @Nullable Identifier getDefaultSkillTreeId() {
        return getSkillTrees().keySet().stream().findAny().orElse(null);
    }
<<<<<<< Updated upstream

    
    
=======
>>>>>>> Stashed changes
    public static java.util.List<Identifier> getOrderedSkillTreeIds() {
        return SKILL_TREES.values().stream()
                .filter(tree -> !tree.getSkillIds().isEmpty())
                .map(PassiveSkillTree::getId)
                .toList();
    }
<<<<<<< Updated upstream

    
=======
>>>>>>> Stashed changes
    public static void loadFromByteBuf(RegistryFriendlyByteBuf buf) {
        SKILL_TREES.clear();
        NetworkHelper.readPassiveSkillTrees(buf).forEach(t -> SKILL_TREES.put(t.getId(), t));
    }
<<<<<<< Updated upstream

    
=======
>>>>>>> Stashed changes
    @Override
    protected Map<Identifier, JsonElement> prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        Map<Identifier, JsonElement> map = new HashMap<>();
        String folder = "skill_trees";
        Map<Identifier, net.minecraft.server.packs.resources.Resource> resources = resourceManager.listResources(folder, loc -> loc.getPath().endsWith(".json"));
        for (Map.Entry<Identifier, net.minecraft.server.packs.resources.Resource> entry : resources.entrySet()) {
            Identifier fileLoc = entry.getKey();
            String path = fileLoc.getPath();
            String idPath = path.substring(folder.length() + 1, path.length() - ".json".length());
            Identifier treeId = Identifier.fromNamespaceAndPath(fileLoc.getNamespace(), idPath);
            try (Reader reader = entry.getValue().openAsReader()) {
                JsonElement json = GSON.fromJson(reader, JsonElement.class);
                if (json != null) {
                    map.put(treeId, json);
                }
            } catch (Exception exception) {
                SkillTreeMod.LOGGER.error("Couldn't parse skill tree data file {} from {}", treeId, fileLoc, exception);
            }
        }
        return map;
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> map, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        SKILL_TREES.clear();
        map.forEach(this::readSkillTree);
    }

    protected void readSkillTree(Identifier id, JsonElement json) {
        try {
            PassiveSkillTree tree = GSON.fromJson(json, PassiveSkillTree.class);
            SKILL_TREES.put(tree.getId(), tree);
        } catch (Exception exception) {
            String errorMessage = "Couldn't load passive skill tree: " + id;
            SkillTreeMod.LOGGER.error(errorMessage, exception);
        }
    }
}