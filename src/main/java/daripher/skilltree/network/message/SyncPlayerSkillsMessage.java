package daripher.skilltree.network.message;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class SyncPlayerSkillsMessage implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncPlayerSkillsMessage> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "sync_player_skills"));

    
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPlayerSkillsMessage> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()), message -> message.learnedSkills,
            ByteBufCodecs.VAR_INT, message -> message.skillPoints,
            SyncPlayerSkillsMessage::new
    );

    public final List<Identifier> learnedSkills;
    public final int skillPoints;

    
    private SyncPlayerSkillsMessage(List<Identifier> learnedSkills, int skillPoints) {
        this.learnedSkills = learnedSkills;
        this.skillPoints = skillPoints;
    }

    public SyncPlayerSkillsMessage(Player player) {
        IPlayerSkills skillsCapability = PlayerSkillsProvider.get(player);
        this.learnedSkills = skillsCapability.getPlayerSkills().stream().map(PassiveSkill::getId).toList();
        this.skillPoints = skillsCapability.getSkillPoints();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
