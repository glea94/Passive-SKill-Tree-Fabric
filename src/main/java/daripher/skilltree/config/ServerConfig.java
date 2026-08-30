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
public class ServerConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(SkillTreeMod.MOD_ID + "-server.json");
    public static final int DEFAULT_MAX_SKILLS = 200;
    private static final int CONFIG_VERSION = 2;
    private static final int LEGACY_MAX_SKILLS = 100;
    private static final int LEGACY_LAST_SKILL_COST = 1400;
    private static final int NEW_LAST_SKILL_COST = 2800;

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
    private static class Data {
        int config_version = 0;
        int max_skill_points = DEFAULT_MAX_SKILLS;
        int first_skill_cost = 15;
        int last_skill_cost = NEW_LAST_SKILL_COST;
        boolean use_skill_points_array = false;
        List<Integer> skill_points_costs = generateDefaultPointsCosts(DEFAULT_MAX_SKILLS);
        boolean show_chat_messages = true;
        boolean enable_exp_exchange = true;
        double amnesia_scroll_penalty = 0.2D;
        boolean dragon_drops_amnesia_scroll = true;
        double grindstone_exp_multiplier = 0.1D;
    }
    public static void load() {
        Data data;
        boolean isNewConfig;
        if (Files.exists(CONFIG_PATH)) {
            isNewConfig = false;
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                data = GSON.fromJson(reader, Data.class);
                if (data == null) {
                    data = new Data();
                    isNewConfig = true;
                }
            } catch (IOException exception) {
                SkillTreeMod.LOGGER.error("Couldn't read server config, using defaults", exception);
                data = new Data();
                isNewConfig = true;
            }
        } else {
            data = new Data();
            isNewConfig = true;
        }
        if (isNewConfig) {

            data.config_version = CONFIG_VERSION;
        } else if (data.config_version < CONFIG_VERSION) {
            migrateLegacyDefaults(data);
            data.config_version = CONFIG_VERSION;
        }
        applyData(data);
        save(data);
    }
    private static void migrateLegacyDefaults(Data data) {
        if (data.max_skill_points == LEGACY_MAX_SKILLS) {
            SkillTreeMod.LOGGER.info("Migrating skilltree-server.json: max_skill_points {} -> {}", LEGACY_MAX_SKILLS, DEFAULT_MAX_SKILLS);
            data.max_skill_points = DEFAULT_MAX_SKILLS;
        }
        if (data.last_skill_cost == LEGACY_LAST_SKILL_COST) {
            SkillTreeMod.LOGGER.info("Migrating skilltree-server.json: last_skill_cost {} -> {}", LEGACY_LAST_SKILL_COST, NEW_LAST_SKILL_COST);
            data.last_skill_cost = NEW_LAST_SKILL_COST;
        }

        if (data.skill_points_costs != null && data.skill_points_costs.equals(generateDefaultPointsCosts(LEGACY_MAX_SKILLS))) {
            SkillTreeMod.LOGGER.info("Migrating skilltree-server.json: skill_points_costs regenerated for {} levels", DEFAULT_MAX_SKILLS);
            data.skill_points_costs = generateDefaultPointsCosts(DEFAULT_MAX_SKILLS);
        }
    }
    private static void applyData(Data data) {
        max_skill_points = data.max_skill_points <= 0 ? DEFAULT_MAX_SKILLS : data.max_skill_points;
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
    private static List<Integer> generateDefaultPointsCosts(int size) {
        List<Integer> costs = new ArrayList<>();
        costs.add(15);
        for (int i = 1; i < size; i++) {
            int previousCost = costs.get(costs.size() - 1);
            int cost = previousCost + 3 + i;
            costs.add(cost);
        }
        return costs;
    }
    public static int getSkillPointCost(int level) {
        if (use_skill_points_array) {
            if (skill_points_costs == null || skill_points_costs.isEmpty()) {
                return 15;
            }
            if (level >= skill_points_costs.size()) {
                return skill_points_costs.get(skill_points_costs.size() - 1);
            }
            return skill_points_costs.get(level);
        }
        int totalPoints = max_skill_points <= 0 ? DEFAULT_MAX_SKILLS : max_skill_points;
        return first_skill_cost + (last_skill_cost - first_skill_cost) * level / totalPoints;
    }
}