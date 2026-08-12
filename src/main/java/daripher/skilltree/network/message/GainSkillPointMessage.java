package daripher.skilltree.network.message;

import daripher.skilltree.SkillTreeMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class GainSkillPointMessage implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<GainSkillPointMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "gain_skill_point"));

    // Utilisation d'un StreamCodec dynamique au lieu de .unit() pour éviter le conflit d'instance mémoire
    public static final StreamCodec<FriendlyByteBuf, GainSkillPointMessage> STREAM_CODEC = StreamCodec.of(
            (buf, msg) -> msg.encode(buf),
            buf -> decode(buf)
    );

    public GainSkillPointMessage() {
    }

    public static GainSkillPointMessage decode(FriendlyByteBuf buf) {
        return new GainSkillPointMessage();
    }

    public void encode(FriendlyByteBuf buf) {
        // aucune donnée à envoyer, identique à l'original Forge
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
