package daripher.skilltree.network.message;

import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.network.NetworkHelper;
<<<<<<< Updated upstream
import net.minecraft.network.FriendlyByteBuf;
=======
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
>>>>>>> Stashed changes

/**
 * Portage Fabric : classe déjà indépendante de Forge à part le type NetworkEvent.Context du
 * receive(), retiré ici. decode() applique directement les données reçues (comportement
 * identique à l'original), encode() est inchangé.
 */
<<<<<<< Updated upstream
public class SyncServerDataMessage {
    public static SyncServerDataMessage decode(FriendlyByteBuf buf) {
        SkillsReloader.loadFromByteBuf(buf);
        SkillTreesReloader.loadFromByteBuf(buf);
        return new SyncServerDataMessage();
=======
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
>>>>>>> Stashed changes
    }

    public void encode(FriendlyByteBuf buf) {
        NetworkHelper.writePassiveSkills(buf, SkillsReloader.getSkills().values());
        NetworkHelper.writePassiveSkillTrees(buf, SkillTreesReloader.getSkillTrees().values());
    }
}
