package daripher.skilltree.network.message;

import daripher.skilltree.SkillTreeMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public class GainSkillPointMessage implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<GainSkillPointMessage> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "gain_skill_point"));

    
    public static final StreamCodec<RegistryFriendlyByteBuf, GainSkillPointMessage> STREAM_CODEC = StreamCodec.of(
            (buf, msg) -> msg.encode(buf),
            buf -> decode(buf)
    );

    public GainSkillPointMessage() {
    }

    public static GainSkillPointMessage decode(RegistryFriendlyByteBuf buf) {
        return new GainSkillPointMessage();
    }

    public void encode(RegistryFriendlyByteBuf buf) {
        
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
