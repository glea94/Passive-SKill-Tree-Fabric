package daripher.skilltree.network.message;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class SyncPlayerSkillsMessage implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncPlayerSkillsMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "sync_player_skills"));

    // Factual Fix 1.21.4: Refactor StreamCodec to leverage modern composite collections layout over RegistryFriendlyByteBuf
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPlayerSkillsMessage> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), message -> message.learnedSkills,
            ByteBufCodecs.VAR_INT, message -> message.skillPoints,
            SyncPlayerSkillsMessage::new
    );

    public final List<ResourceLocation> learnedSkills;
    public final int skillPoints;

    // Factual Fix 1.21.4: Direct constructor addition to feed composite serialization mappings cleanly
    private SyncPlayerSkillsMessage(List<ResourceLocation> learnedSkills, int skillPoints) {
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
