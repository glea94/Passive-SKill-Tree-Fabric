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
 * dans daripher.skilltree.network.message), et non plus par un simple Identifier couplé
 * à un FriendlyByteBuf brut. Cette classe enregistre les types de paquets (mêmes messages,
 * mêmes directions que l'original Forge, plus SyncWorkbenchRecipesMessage ajouté en 1.21.5)
 * auprès du PayloadTypeRegistry.
 */
public class PSTNetworkChannels {
    public static void register() {
        // Aligned 1.21.4: Direct registration of your custom stream codecs into modern packet payloads
        PayloadTypeRegistry.clientboundPlay().register(SyncServerDataMessage.TYPE, SyncServerDataMessage.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SyncPlayerSkillsMessage.TYPE, SyncPlayerSkillsMessage.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(LearnSkillMessage.TYPE, LearnSkillMessage.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(GainSkillPointMessage.TYPE, GainSkillPointMessage.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(OpenSkillTreeEditorMessage.TYPE, OpenSkillTreeEditorMessage.STREAM_CODEC);
        // Ajouté 1.21.5 : synchronisation des recettes Workbench vers le client (RecipeAccess client ne les expose plus)
        PayloadTypeRegistry.clientboundPlay().register(SyncWorkbenchRecipesMessage.TYPE, SyncWorkbenchRecipesMessage.STREAM_CODEC);
    }
<<<<<<< Updated upstream
}
>>>>>>> Stashed changes
=======
}
>>>>>>> Stashed changes
