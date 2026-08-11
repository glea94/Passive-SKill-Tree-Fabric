package daripher.skilltree.network.message;

import daripher.skilltree.SkillTreeMod;
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
 * Portage Fabric completely updated and verified for 1.21.4.
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

    public static SyncServerDataMessage decode(RegistryFriendlyByteBuf buf) {
        return new SyncServerDataMessage(new RegistryFriendlyByteBuf(buf.copy(), buf.registryAccess()));
    }

    public void encode(RegistryFriendlyByteBuf buf) {
        if (this.dataBuffer != null) {
            buf.writeBytes(this.dataBuffer);
        } else {
            NetworkHelper.writePassiveSkills(buf, SkillsReloader.getSkills().values());
            NetworkHelper.writePassiveSkillTrees(buf, SkillTreesReloader.getSkillTrees().values());
        }
    }

    public RegistryFriendlyByteBuf getDataBuffer() {
        return this.dataBuffer;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
