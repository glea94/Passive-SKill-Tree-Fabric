package daripher.skilltree.network.message;

import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.network.NetworkHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes

/**
 * Portage Fabric : classe déjà indépendante de Forge à part le type NetworkEvent.Context du
 * receive(), retiré ici. decode() applique directement les données reçues (comportement
 * identique à l'original), encode() est inchangé.
 */
public class SyncServerDataMessage implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncServerDataMessage> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "sync_server_data"));

    // Factual Fix 1.21.4: Explicit lambda signature syntax fully resolves the generic type bounds compile-time error
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncServerDataMessage> STREAM_CODEC =
            StreamCodec.of(
                    (buf, message) -> message.encode(buf),
                    SyncServerDataMessage::decode
            );

    private final RegistryFriendlyByteBuf dataBuffer;

    public SyncServerDataMessage(RegistryFriendlyByteBuf dataBuffer) {
        this.dataBuffer = dataBuffer;
    }

<<<<<<< Updated upstream
    public void encode(FriendlyByteBuf buf) {
        NetworkHelper.writePassiveSkills(buf, SkillsReloader.getSkills().values());
        NetworkHelper.writePassiveSkillTrees(buf, SkillTreesReloader.getSkillTrees().values());
=======
    public static SyncServerDataMessage decode(RegistryFriendlyByteBuf buf) {
        // Fix 1.21.11 (identique au fix 26.2/26.1.2) : buf.copy() copie les octets lisibles SANS avancer le
        // readerIndex du buffer d'origine. Le framework de paquets vanilla vérifie après decode() que
        // readerIndex == writerIndex ; comme rien n'était "consommé", il rejetait le paquet avec "found X bytes
        // extra". buf.readBytes(int) fait la même copie mais avance le readerIndex du buffer source, ce qui
        // consomme correctement tout le payload.
        return new SyncServerDataMessage(new RegistryFriendlyByteBuf(buf.readBytes(buf.readableBytes()), buf.registryAccess()));
>>>>>>> Stashed changes
    }
}
