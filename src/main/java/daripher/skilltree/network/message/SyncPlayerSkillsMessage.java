package daripher.skilltree.network.message;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class SyncPlayerSkillsMessage implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncPlayerSkillsMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "sync_player_skills"));

    public static final StreamCodec<FriendlyByteBuf, SyncPlayerSkillsMessage> STREAM_CODEC =
            StreamCodec.of(
                    (buf, message) -> message.encode(buf),
                    SyncPlayerSkillsMessage::decode
            );

    public List<ResourceLocation> learnedSkills = new ArrayList<>();
    public int skillPoints;

    private SyncPlayerSkillsMessage() {
    }

    public SyncPlayerSkillsMessage(Player player) {
        IPlayerSkills skillsCapability = PlayerSkillsProvider.get(player);
        learnedSkills = skillsCapability.getPlayerSkills().stream().map(PassiveSkill::getId).toList();
        skillPoints = skillsCapability.getSkillPoints();
    }

    public static SyncPlayerSkillsMessage decode(FriendlyByteBuf buf) {
        SyncPlayerSkillsMessage result = new SyncPlayerSkillsMessage();
        int learnedSkillsCount = buf.readInt();
        for (int i = 0; i < learnedSkillsCount; i++) {
            result.learnedSkills.add(ResourceLocation.parse(buf.readUtf()));
        }
        result.skillPoints = buf.readInt();
        return result;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(learnedSkills.size());
        learnedSkills.stream().map(ResourceLocation::toString).forEach(buf::writeUtf);
        buf.writeInt(skillPoints);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}