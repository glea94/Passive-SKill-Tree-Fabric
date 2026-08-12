package daripher.skilltree.network;

import daripher.skilltree.SkillTreeMod;
import net.minecraft.resources.ResourceLocation;

/**
 * Portage Fabric : depuis Minecraft 1.20.5+/1.21.1, Fabric Networking API v1 identifie chaque
 * paquet par un CustomPacketPayload.Type<T> propre à la classe de message (voir chaque classe
 * dans daripher.skilltree.network.message), et non plus par un simple Identifier couplé
 * à un FriendlyByteBuf brut. Cette classe enregistre les types de paquets (mêmes messages,
 * mêmes directions que l'original Forge, plus SyncWorkbenchRecipesMessage ajouté en 1.21.5)
 * auprès du PayloadTypeRegistry.
 */
public class PSTNetworkChannels {
    public static final ResourceLocation SYNC_SERVER_DATA = new ResourceLocation(SkillTreeMod.MOD_ID, "sync_server_data"); // S2C
    public static final ResourceLocation SYNC_PLAYER_SKILLS = new ResourceLocation(SkillTreeMod.MOD_ID, "sync_player_skills"); // S2C
    public static final ResourceLocation LEARN_SKILL = new ResourceLocation(SkillTreeMod.MOD_ID, "learn_skill"); // C2S
    public static final ResourceLocation GAIN_SKILL_POINT = new ResourceLocation(SkillTreeMod.MOD_ID, "gain_skill_point"); // C2S
}
