package daripher.skilltree.network.message;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class LearnSkillMessage implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<LearnSkillMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "learn_skill"));

    public static final StreamCodec<FriendlyByteBuf, LearnSkillMessage> STREAM_CODEC =
            StreamCodec.of(
                    (buf, message) -> message.encode(buf),
                    LearnSkillMessage::decode
            );

    private ResourceLocation skillId;

    public LearnSkillMessage(PassiveSkill passiveSkill) {
        skillId = passiveSkill.getId();
    }

    private LearnSkillMessage() {
    }

    public static LearnSkillMessage decode(FriendlyByteBuf buf) {
        LearnSkillMessage message = new LearnSkillMessage();
        message.skillId = ResourceLocation.parse(buf.readUtf());
        return message;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(skillId.toString());
    }

    public ResourceLocation getSkillId() {
        return skillId;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}