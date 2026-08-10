package daripher.skilltree.network;

import daripher.skilltree.network.message.GainSkillPointMessage;
import daripher.skilltree.network.message.LearnSkillMessage;
import daripher.skilltree.network.message.OpenSkillTreeEditorMessage;
import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
import daripher.skilltree.network.message.SyncServerDataMessage;
import daripher.skilltree.network.message.SyncWorkbenchRecipesMessage;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

/**
<<<<<<< Updated upstream
 * Portage Fabric : Forge utilisait un seul SimpleChannel avec 4 messages numérotés (1 à 4).
 * Fabric Networking API v1 identifie chaque type de paquet par son propre ResourceLocation.
 * Un identifiant par message ci-dessous = équivalent fonctionnel exact des 4 registerMessage
 * de l'original (mêmes 4 messages, mêmes directions : cf. NetworkDispatcher Forge d'origine).
=======
 * Portage Fabric : depuis Minecraft 1.20.5+/1.21.1, Fabric Networking API v1 identifie chaque
 * paquet par un CustomPacketPayload.Type<T> propre à la classe de message (voir chaque classe
 * dans daripher.skilltree.network.message), et non plus par un simple ResourceLocation couplé
 * à un FriendlyByteBuf brut. Cette classe enregistre les types de paquets (mêmes messages,
 * mêmes directions que l'original Forge, plus SyncWorkbenchRecipesMessage ajouté en 1.21.5)
 * auprès du PayloadTypeRegistry.
>>>>>>> Stashed changes
 */
public class PSTNetworkChannels {
    public static void register() {
        // Aligned 1.21.4: Direct registration of your custom stream codecs into modern packet payloads
        PayloadTypeRegistry.playS2C().register(SyncServerDataMessage.TYPE, SyncServerDataMessage.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(SyncPlayerSkillsMessage.TYPE, SyncPlayerSkillsMessage.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(LearnSkillMessage.TYPE, LearnSkillMessage.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(GainSkillPointMessage.TYPE, GainSkillPointMessage.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(OpenSkillTreeEditorMessage.TYPE, OpenSkillTreeEditorMessage.STREAM_CODEC);
        // Ajouté 1.21.5 : synchronisation des recettes Workbench vers le client (RecipeAccess client ne les expose plus)
        PayloadTypeRegistry.playS2C().register(SyncWorkbenchRecipesMessage.TYPE, SyncWorkbenchRecipesMessage.STREAM_CODEC);
    }
}