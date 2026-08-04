// Fichier : src/main/java/daripher/skilltree/network/PSTNetworkChannels.java
package daripher.skilltree.network;

import daripher.skilltree.SkillTreeMod;
import net.minecraft.resources.ResourceLocation;

<<<<<<< Updated upstream
/**
 * Portage Fabric : Forge utilisait un seul SimpleChannel avec 4 messages numérotés (1 à 4).
 * Fabric Networking API v1 identifie chaque type de paquet par son propre ResourceLocation.
 * Un identifiant par message ci-dessous = équivalent fonctionnel exact des 4 registerMessage
 * de l'original (mêmes 4 messages, mêmes directions : cf. NetworkDispatcher Forge d'origine).
 */
=======
>>>>>>> Stashed changes
public class PSTNetworkChannels {
    public static final ResourceLocation SYNC_SERVER_DATA = new ResourceLocation(SkillTreeMod.MOD_ID, "sync_server_data"); // S2C
    public static final ResourceLocation SYNC_PLAYER_SKILLS = new ResourceLocation(SkillTreeMod.MOD_ID, "sync_player_skills"); // S2C
    public static final ResourceLocation LEARN_SKILL = new ResourceLocation(SkillTreeMod.MOD_ID, "learn_skill"); // C2S
    public static final ResourceLocation GAIN_SKILL_POINT = new ResourceLocation(SkillTreeMod.MOD_ID, "gain_skill_point"); // C2S
<<<<<<< Updated upstream
}
=======
    public static final ResourceLocation OPEN_SKILL_TREE_EDITOR = new ResourceLocation(SkillTreeMod.MOD_ID, "open_skill_tree_editor"); // S2C
}
>>>>>>> Stashed changes
