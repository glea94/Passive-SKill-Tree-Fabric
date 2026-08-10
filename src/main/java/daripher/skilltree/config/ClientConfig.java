package daripher.skilltree.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.PassiveSkill;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Portage Fabric : même principe que ServerConfig (JSON/Gson au lieu de ForgeConfigSpec). */
public class ClientConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(SkillTreeMod.MOD_ID + "-client.json");

    public static Set<ResourceLocation> favorite_skills;
    public static int favorite_color;
    public static boolean favorite_color_is_rainbow;
    public static boolean skill_tree_background_parallax;

    private static class Data {
        List<String> favorite_skills = new ArrayList<>();
        String favorite_color_hex = "#42B0FF";
        boolean skill_tree_background_parallax = true;
    }

    public static void load() {
        Data data;
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                data = GSON.fromJson(reader, Data.class);
                if (data == null) {
                    data = new Data();
                }
            } catch (IOException exception) {
                SkillTreeMod.LOGGER.error("Couldn't read client config, using defaults", exception);
                data = new Data();
            }
        } else {
            data = new Data();
        }
        applyData(data);
        save(data);
    }

    private static void applyData(Data data) {
<<<<<<< Updated upstream
        favorite_skills = data.favorite_skills.stream().map(ResourceLocation::new).collect(Collectors.toCollection(HashSet::new));
        favorite_color_is_rainbow = data.favorite_color_hex.equals("rainbow");
=======
        // En 1.21.4, ResourceLocation.parse est parfaitement standard pour charger les clés d'identification
        favorite_skills = data.favorite_skills.stream()
                .map(ResourceLocation::parse)
                .collect(Collectors.toCollection(HashSet::new));

        favorite_color_is_rainbow = "rainbow".equalsIgnoreCase(data.favorite_color_hex);
>>>>>>> Stashed changes
        skill_tree_background_parallax = data.skill_tree_background_parallax;

        if (!favorite_color_is_rainbow) {
            try {
                favorite_color = Integer.decode(data.favorite_color_hex);
            } catch (NumberFormatException e) {
                SkillTreeMod.LOGGER.warn("Invalid color hex format in config: {}, using default", data.favorite_color_hex);
                favorite_color = Integer.decode("#42B0FF");
            }
        }
    }

    private static void save(Data data) {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(data, writer);
            }
        } catch (IOException exception) {
            SkillTreeMod.LOGGER.error("Couldn't write client config", exception);
        }
    }

    public static void toggleFavoriteSkill(PassiveSkill skill) {
        if (favorite_skills.contains(skill.getId())) {
            favorite_skills.remove(skill.getId());
        } else {
            favorite_skills.add(skill.getId());
        }
        Data data = new Data();
        data.favorite_skills = favorite_skills.stream().map(ResourceLocation::toString).collect(Collectors.toList());
        data.favorite_color_hex = favorite_color_is_rainbow ? "rainbow" : String.format("#%06X", favorite_color);
        data.skill_tree_background_parallax = skill_tree_background_parallax;
        save(data);
    }
}
