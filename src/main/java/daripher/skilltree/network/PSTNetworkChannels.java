package daripher.skilltree.network;

import daripher.skilltree.network.message.GainSkillPointMessage;
import daripher.skilltree.network.message.LearnSkillMessage;
import daripher.skilltree.network.message.OpenSkillTreeEditorMessage;
import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
import daripher.skilltree.network.message.SyncServerDataMessage;
import daripher.skilltree.network.message.SyncWorkbenchRecipesMessage;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

/**
 * Portage Fabric : depuis Minecraft 1.20.5+/1.21.1, Fabric Networking API v1 identifie chaque
 * paquet par un CustomPacketPayload.Type<T> propre à la classe de message (voir chaque classe
 * dans daripher.skilltree.network.message), et non plus par un simple ResourceLocation couplé
 * à un FriendlyByteBuf brut. Cette classe enregistre les 5 types de paquets (mêmes 4 messages
 * d'origine + SyncWorkbenchRecipesMessage, ajouté en 1.21.4 pour remplacer Level#getRecipeManager()
 * disparu côté client) auprès du PayloadTypeRegistry.
 */
public class PSTNetworkChannels {
    public static void register() {
        PayloadTypeRegistry.playS2C().register(SyncServerDataMessage.TYPE, SyncServerDataMessage.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(SyncPlayerSkillsMessage.TYPE, SyncPlayerSkillsMessage.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(LearnSkillMessage.TYPE, LearnSkillMessage.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(GainSkillPointMessage.TYPE, GainSkillPointMessage.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(OpenSkillTreeEditorMessage.TYPE, OpenSkillTreeEditorMessage.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(SyncWorkbenchRecipesMessage.TYPE, SyncWorkbenchRecipesMessage.STREAM_CODEC);
    }
}