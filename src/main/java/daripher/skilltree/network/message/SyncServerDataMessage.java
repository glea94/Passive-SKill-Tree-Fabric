package daripher.skilltree.network.message;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.network.NetworkHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Portage Fabric : classe déjà indépendante de Forge à part le type NetworkEvent.Context du
 * receive(), retiré ici. decode() applique directement les données reçues (comportement
 * identique à l'original), encode() est inchangé. Devient un CustomPacketPayload pour
 * correspondre à la nouvelle API réseau Fabric 1.21.1.
 */
public class SyncServerDataMessage implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncServerDataMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "sync_server_data"));

    public static final StreamCodec<FriendlyByteBuf, SyncServerDataMessage> STREAM_CODEC =
            StreamCodec.of(
                    (buf, message) -> message.encode(buf),
                    SyncServerDataMessage::decode
            );

    public static SyncServerDataMessage decode(FriendlyByteBuf buf) {
        SkillsReloader.loadFromByteBuf(buf);
        SkillTreesReloader.loadFromByteBuf(buf);
        return new SyncServerDataMessage();
    }

    public void encode(FriendlyByteBuf buf) {
        NetworkHelper.writePassiveSkills(buf, SkillsReloader.getSkills().values());
        NetworkHelper.writePassiveSkillTrees(buf, SkillTreesReloader.getSkillTrees().values());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}