package daripher.skilltree.network.message;

import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class LearnSkillMessage {
    private ResourceLocation skillId;

    public LearnSkillMessage(PassiveSkill passiveSkill) {
        skillId = passiveSkill.getId();
    }

    private LearnSkillMessage() {
    }

    public static LearnSkillMessage decode(FriendlyByteBuf buf) {
        LearnSkillMessage message = new LearnSkillMessage();
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
