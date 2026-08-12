package daripher.skilltree.data.reloader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class SkillsReloader extends SimplePreparableReloadListener<Map<ResourceLocation, JsonElement>> implements IdentifiableResourceReloadListener {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .registerTypeAdapter(SkillBonus.class, new SkillBonusSerializer())
            .registerTypeAdapter(SkillRequirement.class, new SkillRequirementSerializer())
            .registerTypeAdapter(Component.class, (com.google.gson.JsonDeserializer<Component>) (json, typeOfT, context) -> ComponentSerialization.CODEC.decode(JsonOps.INSTANCE, json).getOrThrow().getFirst())
            .registerTypeAdapter(Component.class, (com.google.gson.JsonSerializer<Component>) (src, typeOfSrc, context) -> ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, src).getOrThrow())
            .setPrettyPrinting()
            .create();
    private static final Map<ResourceLocation, PassiveSkill> SKILLS = new HashMap<>();
    private static final FileToIdConverter CONVERTER = FileToIdConverter.json("skills");

    @Override
    public ResourceLocation getFabricId() {
        return ResourceLocation.fromNamespaceAndPath(daripher.skilltree.SkillTreeMod.MOD_ID, "skills_reloader");
    }

    public static void register() {
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new SkillsReloader());
    }

    public static Map<ResourceLocation, PassiveSkill> getSkills() {
        return SKILLS;
    }

    public static @Nullable PassiveSkill getSkillById(ResourceLocation id) {
        return SKILLS.get(id);
    }

    public static void loadFromByteBuf(FriendlyByteBuf buf) {
        SKILLS.clear();
        NetworkHelper.readPassiveSkills(buf).forEach(s -> SKILLS.put(s.getId(), s));
    }

    @Override
    protected Map<ResourceLocation, JsonElement> prepare(@NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        Map<ResourceLocation, JsonElement> map = new HashMap<>();
        for (Map.Entry<ResourceLocation, Resource> entry : CONVERTER.listMatchingResources(resourceManager).entrySet()) {
            ResourceLocation fileId = entry.getKey();
            ResourceLocation id = CONVERTER.fileToId(fileId);
            try (Reader reader = entry.getValue().openAsReader()) {
                JsonElement json = JsonParser.parseReader(reader);
                if (map.putIfAbsent(id, json) != null) {
                    SkillTreeMod.LOGGER.error("Duplicate data file ignored with ID {}", id);
                }
            } catch (IllegalArgumentException | IOException | JsonParseException exception) {
                SkillTreeMod.LOGGER.error("Couldn't parse data file '{}' from '{}'", id, fileId, exception);
            }
        }
        return map;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        SKILLS.clear();
        map.forEach(this::readSkill);
    }

    protected void readSkill(ResourceLocation id, JsonElement json) {
        try {
            PassiveSkill skill = GSON.fromJson(json, PassiveSkill.class);
            SKILLS.put(skill.getId(), skill);
        } catch (Exception exception) {
            String errorMessage = "Couldn't load passive skill: " + id;
            SkillTreeMod.LOGGER.error(errorMessage, exception);
        }
    }
}