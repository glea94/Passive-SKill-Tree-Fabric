package daripher.skilltree.network.message;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.network.NetworkHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;


public class SyncServerDataMessage implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncServerDataMessage> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "sync_server_data"));


    public static final StreamCodec<RegistryFriendlyByteBuf, SyncServerDataMessage> STREAM_CODEC =
            StreamCodec.of(
                    (buf, message) -> message.encode(buf),
                    SyncServerDataMessage::decode
            );

    private final RegistryFriendlyByteBuf dataBuffer;

    public SyncServerDataMessage(RegistryFriendlyByteBuf dataBuffer) {
        this.dataBuffer = dataBuffer;
    }

    public static SyncServerDataMessage decode(RegistryFriendlyByteBuf buf) {




        return new SyncServerDataMessage(new RegistryFriendlyByteBuf(buf.readBytes(buf.readableBytes()), buf.registryAccess()));
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