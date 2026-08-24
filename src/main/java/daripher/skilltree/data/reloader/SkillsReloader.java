package daripher.skilltree.data.reloader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.data.serializers.SkillBonusSerializer;
import daripher.skilltree.data.serializers.SkillRequirementSerializer;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.requirement.SkillRequirement;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class SkillsReloader extends SimplePreparableReloadListener<Map<Identifier, JsonElement>> implements IdentifiableResourceReloadListener {
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
            .registerTypeAdapter(SkillBonus.class, new SkillBonusSerializer())
            .registerTypeAdapter(SkillRequirement.class, new SkillRequirementSerializer())
            .registerTypeAdapter(Component.class, (com.google.gson.JsonDeserializer<Component>) (json, typeOfT, context) ->
                    ComponentSerialization.CODEC.decode(JsonOps.INSTANCE, json).getOrThrow(IllegalStateException::new).getFirst())
            .registerTypeAdapter(Component.class, (com.google.gson.JsonSerializer<Component>) (src, typeOfSrc, context) ->
                    ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, src).getOrThrow(IllegalStateException::new))
            .setPrettyPrinting()
            .create();

    private static final Map<Identifier, PassiveSkill> SKILLS = new HashMap<>();

    public SkillsReloader() {
        super();
    }

    @Override
    public Identifier getFabricId() {
        return Identifier.fromNamespaceAndPath(daripher.skilltree.SkillTreeMod.MOD_ID, "skills_reloader");
    }

    public static void register() {
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new SkillsReloader());
    }

    public static Map<Identifier, PassiveSkill> getSkills() {
        return SKILLS;
    }

    public static Collection<Identifier> getSkillIds() {
        return SKILLS.keySet();
    }

    public static @Nullable PassiveSkill getSkillById(Identifier id) {
        return SKILLS.get(id);
    }

    
    public static void loadFromByteBuf(RegistryFriendlyByteBuf buf) {
        SKILLS.clear();
        NetworkHelper.readPassiveSkills(buf).forEach(s -> SKILLS.put(s.getId(), s));
    }

    
    @Override
    protected Map<Identifier, JsonElement> prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        Map<Identifier, JsonElement> map = new HashMap<>();
        String folder = "skills";
        Map<Identifier, net.minecraft.server.packs.resources.Resource> resources = resourceManager.listResources(folder, loc -> loc.getPath().endsWith(".json"));
        for (Map.Entry<Identifier, net.minecraft.server.packs.resources.Resource> entry : resources.entrySet()) {
            Identifier fileLoc = entry.getKey();
            String path = fileLoc.getPath();
            String idPath = path.substring(folder.length() + 1, path.length() - ".json".length());
            Identifier recipeId = Identifier.fromNamespaceAndPath(fileLoc.getNamespace(), idPath);
            try (Reader reader = entry.getValue().openAsReader()) {
                JsonElement json = GSON.fromJson(reader, JsonElement.class);
                if (json != null) {
                    map.put(recipeId, json);
                }
            } catch (Exception exception) {
                SkillTreeMod.LOGGER.error("Couldn't parse passive skill data card file {} from {}", recipeId, fileLoc, exception);
            }
        }
        return map;
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> map, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        SKILLS.clear();
        map.forEach(this::readSkill);
    }

    protected void readSkill(Identifier id, JsonElement json) {
        try {
            PassiveSkill skill = GSON.fromJson(json, PassiveSkill.class);
            SKILLS.put(skill.getId(), skill);
        } catch (Exception exception) {
            String errorMessage = "Couldn't load passive skill: " + id;
            SkillTreeMod.LOGGER.error(errorMessage, exception);
        }
    }
}
