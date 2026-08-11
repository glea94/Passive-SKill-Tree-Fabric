package daripher.skilltree.data.client;

import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonReader;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.fabricmc.loader.api.FabricLoader;

import org.jetbrains.annotations.Nullable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SkillTreeEditorData {
    private static final Map<Identifier, PassiveSkill> EDITOR_PASSIVE_SKILLS = new HashMap<>();
    private static final Map<Identifier, PassiveSkillTree> EDITOR_TREES = new HashMap<>();
    private static final Set<Identifier> EDITOR_TREES_IDS = new HashSet<>();
    private static boolean loadedIDs = false;

    public static PassiveSkill getEditorSkill(Identifier id) {
        return EDITOR_PASSIVE_SKILLS.get(id);
    }

    public static @Nullable PassiveSkillTree getOrCreateEditorTree(Identifier treeId) {
        try {
            createSkillTreesSaveFolders(treeId);
            File mcmetaFile = new File(getEditorFolder(), "pack.mcmeta");
            if (!mcmetaFile.exists()) {
                generatePackMcmetaFile(mcmetaFile);
            }
            if (!getSkillTreeSaveFile(treeId).exists()) {
                PassiveSkillTree skillTree = SkillTreesReloader.getSkillTreeById(treeId);
                saveEditorSkillTree(skillTree);
            }
            if (!EDITOR_TREES.containsKey(treeId)) {
                loadEditorSkillTree(treeId);
            }
            if (!EDITOR_TREES.containsKey(treeId)) {
                EDITOR_TREES_IDS.add(treeId);
            }
            PassiveSkillTree skillTree = EDITOR_TREES.getOrDefault(treeId, new PassiveSkillTree(treeId));
            for (Identifier skillId : skillTree.getSkillIds()) {
                try {
                    loadOrCreateEditorSkill(skillId);
                } catch (Exception exception) {
                    SkillTreeMod.LOGGER.error(exception);
                    sendChatMessage("Couldn't read passive skill " + skillId, ChatFormatting.DARK_RED);
                    sendChatMessage("");
                    String errorMessage = exception.getMessage() == null ? "No error message" : exception.getMessage();
                    sendChatMessage(errorMessage, ChatFormatting.RED);
                    return null;
                }
            }
            return skillTree;
        } catch (Exception exception) {
            EDITOR_TREES.clear();
            EDITOR_PASSIVE_SKILLS.clear();
            sendChatMessage("Couldn't read skill tree " + treeId, ChatFormatting.DARK_RED);
            sendChatMessage("");
            String errorMessage = exception.getMessage() == null ? "No error message" : exception.getMessage();
            sendChatMessage(errorMessage, ChatFormatting.RED);
            sendChatMessage("");
            sendChatMessage("Try removing files from folder", ChatFormatting.DARK_RED);
            sendChatMessage("");
            sendChatMessage(getEditorDataFolder().getPath(), ChatFormatting.RED);
            SkillTreeMod.LOGGER.error(exception);
            return null;
        }
    }

    private static void createSkillTreesSaveFolders(Identifier treeId) {
        File folder = getSkillTreeSavesFolder(treeId);
        try {
            Files.createDirectories(folder.toPath());
        } catch (IOException exception) {
            String errorMessage = "Failed to create skill tree save directory for: " + treeId;
            SkillTreeMod.LOGGER.error(errorMessage, exception);
        }
    }

    private static void generatePackMcmetaFile(File file) {
<<<<<<< Updated upstream
=======
        // Fix 26.1.2 : confirmé par décompilation de PackFormat.IntermediaryFormat (vanilla) que
        // le codec CLIENT_RESOURCES (lastPreMinorVersion=64) interdit supported_formats dès que
        // min_format > 64, alors que le codec SERVER_DATA (lastPreMinorVersion=81) l'exige tant
        // que min_format <= 81 ; pack_format 69 étant lu par les deux codecs sur le même fichier,
        // min_format est volontairement abaissé à 64 pour faire basculer les deux lectures dans
        // le même mode (ancienne ère, supported_formats requis et cohérent des deux côtés)
>>>>>>> Stashed changes
        String fileContents = """
                {
                  "pack": {
                    "description": {
                      "text": "PST editor data"
                    },
<<<<<<< Updated upstream
                    "pack_format": 15
=======
                    "pack_format": 69,
                    "min_format": 64,
                    "max_format": 81,
                    "supported_formats": [64, 81]
>>>>>>> Stashed changes
                  }
                }
                """;
        try {
            Files.writeString(file.toPath(), fileContents);
        } catch (IOException exception) {
            throw new RuntimeException("Failed to generate pack.mcmeta", exception);
        }
    }

    private static void loadOrCreateEditorSkill(Identifier skillId) {
        createSkillsSaveFolder(skillId);
        if (!getSkillSaveFile(skillId).exists()) {
            PassiveSkill skill = SkillsReloader.getSkillById(skillId);
            if (skill != null) {
                saveEditorSkill(skill);
            }
        }
        if (!EDITOR_PASSIVE_SKILLS.containsKey(skillId)) {
            loadEditorSkill(skillId);
        }
    }

    private static void createSkillsSaveFolder(Identifier skillId) {
        File folder = getSkillSavesFolder(skillId);
        try {
            Files.createDirectories(folder.toPath());
        } catch (IOException exception) {
            String errorMessage = "Failed to create skill save directory for: " + skillId;
            SkillTreeMod.LOGGER.error(errorMessage, exception);
        }
    }

    public static void saveEditorSkillTree(PassiveSkillTree skillTree) {
        createSkillTreesSaveFolders(skillTree.getId());
        File file = getSkillTreeSaveFile(skillTree.getId());
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            SkillTreesReloader.GSON.toJson(skillTree, writer);
        } catch (JsonIOException | IOException exception) {
            Minecraft.getInstance().setScreen(null);
            sendChatMessage("Can't save editor skill tree " + skillTree.getId(), ChatFormatting.DARK_RED);
            sendChatMessage(exception.getMessage(), ChatFormatting.DARK_RED);
        }
    }

    public static void loadEditorSkillTree(Identifier treeId) throws IOException {
        File file = getSkillTreeSaveFile(treeId);
        PassiveSkillTree skillTree;
        try {
            skillTree = readFromFile(PassiveSkillTree.class, file);
        } catch (Exception exception) {
            skillTree = new PassiveSkillTree(treeId);
            saveEditorSkillTree(skillTree);
            EDITOR_TREES.put(treeId, skillTree);
            throw exception;
        }
        EDITOR_TREES.put(treeId, skillTree);
    }

    public static void saveEditorSkill(PassiveSkill skill) {
        createSkillsSaveFolder(skill.getId());
        File file = getSkillSaveFile(skill.getId());
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            SkillsReloader.GSON.toJson(skill, writer);
        } catch (JsonIOException | IOException exception) {
            Minecraft.getInstance().setScreen(null);
            sendChatMessage("Can't save editor skill " + skill.getId(), ChatFormatting.DARK_RED);
            sendChatMessage(exception.getMessage(), ChatFormatting.DARK_RED);
        }
    }

    public static void loadEditorSkill(Identifier skillId) {
        PassiveSkill skill;
        try {
            File saveFile = getSkillSaveFile(skillId);
            if (!saveFile.exists()) {
                return;
            }
            skill = readFromFile(PassiveSkill.class, saveFile);
        } catch (IOException exception) {
            sendChatMessage("Can't load editor skill " + skillId, ChatFormatting.DARK_RED);
            sendChatMessage(exception.getMessage(), ChatFormatting.DARK_RED);
            return;
        }
        EDITOR_PASSIVE_SKILLS.put(skillId, skill);
    }

    public static void deleteEditorSkill(PassiveSkill skill) {
        try {
            Files.delete(getSkillSaveFile(skill.getId()).toPath());
        } catch (IOException exception) {
            String errorMessage = "Failed to delete skill file for: " + skill.getId();
            SkillTreeMod.LOGGER.error(errorMessage, exception);
        }
        EDITOR_PASSIVE_SKILLS.remove(skill.getId());
    }

    private static File getEditorDataFolder() {
        return new File(getEditorFolder(), "data");
    }

    private static File getEditorFolder() {
        return new File(FabricLoader.getInstance().getGameDir().toFile(), "skilltree/editor");
    }

    private static File getSkillSavesFolder(Identifier skillId) {
        return new File(getEditorDataFolder(), skillId.getNamespace() + "/skills");
    }

    private static File getSkillTreeSavesFolder(Identifier skillTreeId) {
        return new File(getEditorDataFolder(), skillTreeId.getNamespace() + "/skill_trees");
    }

    private static File getSkillSaveFile(Identifier skillId) {
        return new File(getSkillSavesFolder(skillId), skillId.getPath() + ".json");
    }

    private static File getSkillTreeSaveFile(Identifier skillTreeId) {
        return new File(getSkillTreeSavesFolder(skillTreeId), skillTreeId.getPath() + ".json");
    }

    private static <T> T readFromFile(Class<T> objectType, File file) throws IOException {
        try (JsonReader reader = new JsonReader(new FileReader(file, StandardCharsets.UTF_8))) {
            return SkillsReloader.GSON.fromJson(reader, objectType);
        }
    }

    public static void sendChatMessage(String text, ChatFormatting... styles) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            MutableComponent component = Component.literal(text);
            for (ChatFormatting style : styles) {
                component.withStyle(style);
            }
<<<<<<< Updated upstream
            player.sendSystemMessage(component);
=======
            Component chatMessage = component;
            player.sendSystemMessage(chatMessage);
>>>>>>> Stashed changes
        }
    }

    public static Set<Identifier> getEditorTreesIDs() {
        if (loadedIDs) {
            return EDITOR_TREES_IDS;
        }
        File dataFolder = getEditorDataFolder();
        File[] dataFiles = dataFolder.listFiles();
        if (!dataFolder.exists() || dataFiles == null) {
            return EDITOR_TREES_IDS;
        }
        for (File namespaceDirectory : dataFiles) {
            if (!namespaceDirectory.isDirectory()) {
                continue;
            }
            File skillTreesDirectory = new File(namespaceDirectory, "skill_trees");
            if (!skillTreesDirectory.exists()) {
                continue;
            }
            File[] skillTreeFiles = skillTreesDirectory.listFiles();
            if (skillTreeFiles == null) {
                continue;
            }
            String namespace = namespaceDirectory.getName();
            for (File skillTreeFile : skillTreeFiles) {
                String skillTreeFileName = skillTreeFile.getName();
                if (!skillTreeFileName.endsWith(".json")) {
                    continue;
                }
                String skillTreeName = skillTreeFileName.substring(0, skillTreeFileName.lastIndexOf('.'));
<<<<<<< Updated upstream
                EDITOR_TREES_IDS.add(new ResourceLocation(namespace, skillTreeName));
=======
                EDITOR_TREES_IDS.add(Identifier.fromNamespaceAndPath(namespace, skillTreeName));
>>>>>>> Stashed changes
            }
        }
        loadedIDs = true;
        return EDITOR_TREES_IDS;
    }
}
