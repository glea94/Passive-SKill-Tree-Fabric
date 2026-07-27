package daripher.skilltree.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import daripher.skilltree.SkillTreeMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Portage Fabric : remplace net.minecraftforge.common.ForgeConfigSpec (pas d'équivalent direct
 * sur Fabric, pas de "config API" officielle) par un simple fichier JSON dans le dossier de
 * config, lu/écrit avec Gson (déjà fourni par Minecraft, aucune dépendance supplémentaire).
 * Mêmes noms de champs statiques publics que la version Forge (max_skill_points,
 * first_skill_cost, etc.) : tout le reste du mod qui les référence déjà n'a rien à changer.
 */
public class ServerConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(SkillTreeMod.MOD_ID + "-server.json");
    public static final int DEFAULT_MAX_SKILLS = 100;

    public static int max_skill_points;
    public static int first_skill_cost;
    public static int last_skill_cost;
    public static double amnesia_scroll_penalty;
    public static double grindstone_exp_multiplier;
    public static boolean show_chat_messages;
    public static boolean use_skill_points_array;
    public static boolean enable_exp_exchange;
    public static boolean dragon_drops_amnesia_scroll;
    public static List<Integer> skill_points_costs;

    /** Miroir du contenu du fichier JSON - uniquement pour la (dé)sérialisation Gson. */
    private static class Data {
        int max_skill_points = DEFAULT_MAX_SKILLS;
        int first_skill_cost = 15;
        int last_skill_cost = 1400;
        boolean use_skill_points_array = false;
        List<Integer> skill_points_costs = generateDefaultPointsCosts();
        boolean show_chat_messages = true;
        boolean enable_exp_exchange = true;
        double amnesia_scroll_penalty = 0.2D;
        boolean dragon_drops_amnesia_scroll = true;
        double grindstone_exp_multiplier = 0.1D;
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
                SkillTreeMod.LOGGER.error("Couldn't read server config, using defaults", exception);
                data = new Data();
            }
        } else {
            data = new Data();
        }
        applyData(data);
        save(data);
    }

    private static void applyData(Data data) {
        max_skill_points = data.max_skill_points;
        first_skill_cost = data.first_skill_cost;
        last_skill_cost = data.last_skill_cost;
        use_skill_points_array = data.use_skill_points_array;
        skill_points_costs = data.skill_points_costs;
        show_chat_messages = data.show_chat_messages;
        enable_exp_exchange = data.enable_exp_exchange;
        amnesia_scroll_penalty = data.amnesia_scroll_penalty;
        dragon_drops_amnesia_scroll = data.dragon_drops_amnesia_scroll;
        grindstone_exp_multiplier = data.grindstone_exp_multiplier;
    }

    private static void save(Data data) {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(data, writer);
            }
        } catch (IOException exception) {
            SkillTreeMod.LOGGER.error("Couldn't write server config", exception);
        }
    }

    static List<Integer> generateDefaultPointsCosts() {
        List<Integer> costs = new ArrayList<>();
        costs.add(15);
        for (int i = 1; i < DEFAULT_MAX_SKILLS; i++) {
            int previousCost = costs.get(costs.size() - 1);
            int cost = previousCost + 3 + i;
            costs.add(cost);
        }
        return costs;
    }

    public static int getSkillPointCost(int level) {
        if (use_skill_points_array) {
            if (level >= skill_points_costs.size()) {
                return skill_points_costs.get(skill_points_costs.size() - 1);
            }
            return skill_points_costs.get(level);
        }
        return first_skill_cost + (last_skill_cost - first_skill_cost) * level / max_skill_points;
    }
}
