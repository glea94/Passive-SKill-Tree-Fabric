// Fichier : src/main/java/daripher/skilltree/network/PSTNetworkChannels.java
package daripher.skilltree.network;

import daripher.skilltree.SkillTreeMod;
import net.minecraft.resources.ResourceLocation;

public class PSTNetworkChannels {
    public static final ResourceLocation SYNC_SERVER_DATA = new ResourceLocation(SkillTreeMod.MOD_ID, "sync_server_data"); // S2C
    public static final ResourceLocation SYNC_PLAYER_SKILLS = new ResourceLocation(SkillTreeMod.MOD_ID, "sync_player_skills"); // S2C
    public static final ResourceLocation LEARN_SKILL = new ResourceLocation(SkillTreeMod.MOD_ID, "learn_skill"); // C2S
    public static final ResourceLocation GAIN_SKILL_POINT = new ResourceLocation(SkillTreeMod.MOD_ID, "gain_skill_point"); // C2S
    public static final ResourceLocation OPEN_SKILL_TREE_EDITOR = new ResourceLocation(SkillTreeMod.MOD_ID, "open_skill_tree_editor"); // S2C
}