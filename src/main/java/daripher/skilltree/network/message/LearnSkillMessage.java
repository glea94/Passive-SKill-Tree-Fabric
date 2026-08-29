package daripher.skilltree.network.message;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
public class LearnSkillMessage implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<LearnSkillMessage> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "learn_skill"));
    public static final StreamCodec<RegistryFriendlyByteBuf, LearnSkillMessage> STREAM_CODEC =
            StreamCodec.of(
                    (buf, message) -> message.encode(buf),
                    LearnSkillMessage::decode
            );
    private Identifier skillId;
    public LearnSkillMessage(PassiveSkill passiveSkill) {
        this.skillId = passiveSkill.getId();
    }
    private LearnSkillMessage() {
    }
    public static LearnSkillMessage decode(RegistryFriendlyByteBuf buf) {
        LearnSkillMessage message = new LearnSkillMessage();
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