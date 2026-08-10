package daripher.skilltree.network.message;

import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.PassiveSkill;
<<<<<<< Updated upstream
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
=======
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
>>>>>>> Stashed changes
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

<<<<<<< Updated upstream
public class SyncPlayerSkillsMessage {
    public List<ResourceLocation> learnedSkills = new ArrayList<>();
    public int skillPoints;

    private SyncPlayerSkillsMessage() {
=======
public class SyncPlayerSkillsMessage implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncPlayerSkillsMessage> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "sync_player_skills"));

    // Factual Fix 1.21.4: Refactor StreamCodec to leverage modern composite collections layout over RegistryFriendlyByteBuf
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPlayerSkillsMessage> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()), message -> message.learnedSkills,
            ByteBufCodecs.VAR_INT, message -> message.skillPoints,
            SyncPlayerSkillsMessage::new
    );

    public final List<Identifier> learnedSkills;
    public final int skillPoints;

    // Factual Fix 1.21.4: Direct constructor addition to feed composite serialization mappings cleanly
    private SyncPlayerSkillsMessage(List<Identifier> learnedSkills, int skillPoints) {
        this.learnedSkills = learnedSkills;
        this.skillPoints = skillPoints;
>>>>>>> Stashed changes
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
            result.learnedSkills.add(new ResourceLocation(buf.readUtf()));
        }
        result.skillPoints = buf.readInt();
        return result;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(learnedSkills.size());
        learnedSkills.stream().map(ResourceLocation::toString).forEach(buf::writeUtf);
        buf.writeInt(skillPoints);
    }
}
