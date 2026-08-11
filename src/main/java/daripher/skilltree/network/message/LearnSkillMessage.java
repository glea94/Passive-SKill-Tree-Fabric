package daripher.skilltree.network.message;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.PassiveSkill;
<<<<<<< Updated upstream
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class LearnSkillMessage {
    private ResourceLocation skillId;
=======
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public class LearnSkillMessage implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<LearnSkillMessage> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "learn_skill"));

    // Factual Fix 1.21.4: Concrete lambda formatting completely resolves the StreamCodec method inference type bounds mismatch
    public static final StreamCodec<RegistryFriendlyByteBuf, LearnSkillMessage> STREAM_CODEC =
            StreamCodec.of(
                    (buf, message) -> message.encode(buf),
                    LearnSkillMessage::decode
            );

    private Identifier skillId;
>>>>>>> Stashed changes

    public LearnSkillMessage(PassiveSkill passiveSkill) {
        this.skillId = passiveSkill.getId();
    }

    private LearnSkillMessage() {
    }

    public static LearnSkillMessage decode(RegistryFriendlyByteBuf buf) {
        LearnSkillMessage message = new LearnSkillMessage();
<<<<<<< Updated upstream
        message.skillId = new ResourceLocation(buf.readUtf());
        return message;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(skillId.toString());
    }

    public ResourceLocation getSkillId() {
        return skillId;
    }
}
=======
        message.skillId = buf.readIdentifier();
        return message;
    }

    public void encode(RegistryFriendlyByteBuf buf) {
        buf.writeIdentifier(this.skillId);
    }

    public Identifier getSkillId() {
        return this.skillId;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
>>>>>>> Stashed changes
