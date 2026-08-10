package daripher.skilltree.network.message;

<<<<<<< Updated upstream
import net.minecraft.network.FriendlyByteBuf;
=======
import daripher.skilltree.SkillTreeMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public class GainSkillPointMessage implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<GainSkillPointMessage> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "gain_skill_point"));

    // Factual Fix 1.21.4: Update StreamCodec signature to utilize the mandatory RegistryFriendlyByteBuf structure
    public static final StreamCodec<RegistryFriendlyByteBuf, GainSkillPointMessage> STREAM_CODEC = StreamCodec.of(
            (buf, msg) -> msg.encode(buf),
            buf -> decode(buf)
    );
>>>>>>> Stashed changes

public class GainSkillPointMessage {
    public GainSkillPointMessage() {
    }

    public static GainSkillPointMessage decode(FriendlyByteBuf buf) {
        return new GainSkillPointMessage();
    }

    public void encode(FriendlyByteBuf buf) {
        // aucune donnée à envoyer, identique à l'original Forge
    }
}
