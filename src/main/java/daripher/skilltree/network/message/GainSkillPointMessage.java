package daripher.skilltree.network.message;

import net.minecraft.network.FriendlyByteBuf;

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
