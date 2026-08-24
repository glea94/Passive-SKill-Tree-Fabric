package daripher.skilltree.init.client;

import com.mojang.blaze3d.platform.InputConstants;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.screen.SkillTreeScreen;
import daripher.skilltree.client.screen.SkillTreeSelectionScreen;
import daripher.skilltree.data.client.SkillTreeEditorData;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.skill.PassiveSkillTree;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;


public class PSTKeybinds {
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "main"));
    private static final KeyMapping SKILL_TREE_KEY = new KeyMapping("key.display_skill_tree", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_O, CATEGORY);

    public static void register() {
        KeyMappingHelper.registerKeyMapping(SKILL_TREE_KEY);
        ClientTickEvents.END_CLIENT_TICK.register(PSTKeybinds::handleKeyPress);
    }

    private static void handleKeyPress(Minecraft minecraft) {
        while (SKILL_TREE_KEY.consumeClick()) {
            if (minecraft.screen != null) {
                continue;
            }
            if (minecraft.player == null) {
                continue;
            }
            Identifier defaultTreeId = SkillTreesReloader.getDefaultSkillTreeId();
            if (defaultTreeId == null) {
                SkillTreeEditorData.sendChatMessage("No skill trees found.", ChatFormatting.DARK_RED);
                continue;
            }
            if (SkillTreesReloader.getSkillTrees().size() == 1) {
                PassiveSkillTree skillTree = SkillTreesReloader.getSkillTreeById(defaultTreeId);
                boolean broken = false;
                for (Identifier skillId : skillTree.getSkillIds()) {
                    if (SkillsReloader.getSkillById(skillId) == null) {
                        SkillTreeEditorData.sendChatMessage("This skill tree is broken.", ChatFormatting.DARK_RED);
                        SkillTreeEditorData.sendChatMessage("Open it in the editor to resolve issues.", ChatFormatting.RED);
                        broken = true;
                        break;
                    }
                }
                if (broken) {
                    continue;
                }
                SkillTreeScreen screen = new SkillTreeScreen(defaultTreeId);
                minecraft.setScreen(screen);
            } else {
                minecraft.setScreen(new SkillTreeSelectionScreen());
            }
        }
    }
}