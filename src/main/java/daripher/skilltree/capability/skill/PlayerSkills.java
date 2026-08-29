package daripher.skilltree.capability.skill;
import com.mojang.serialization.Codec;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.core.NonNullList;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.UUID;
public class PlayerSkills implements IPlayerSkills {
    private static final UUID TREE_VERSION = UUID.fromString("fd21c2a9-7ab5-4a1e-b06d-ddb87b56047f");
    private final NonNullList<PassiveSkill> skills = NonNullList.create();
    private int skillPoints;
    private boolean treeReset;
    @Override
    public NonNullList<PassiveSkill> getPlayerSkills() {
        return skills;
    }
    @Override
    public int getSkillPoints() {
        return skillPoints;
    }
    @Override
    public void setSkillPoints(int skillPoints) {
        this.skillPoints = skillPoints;
    }
    @Override
    public void grantSkillPoints(int skillPoints) {
        this.skillPoints += skillPoints;
    }
    @Override
    public boolean grantSkill(PassiveSkill passiveSkill) {
        if (skills.contains(passiveSkill)) {
            return false;
        }
        return skills.add(passiveSkill);
    }
    @Override
    public boolean learnSkill(@NotNull PassiveSkill passiveSkill) {
        int cost = passiveSkill.getCost();
        if (skillPoints < cost) {
            return false;
        }
        if (skills.contains(passiveSkill)) {
            return false;
        }
        skillPoints -= cost;
        return skills.add(passiveSkill);
    }
    @Override
    public boolean isTreeReset() {
        return treeReset;
    }
    @Override
    public void setTreeReset(boolean reset) {
        treeReset = reset;
    }
    @Override
    public void resetTree(ServerPlayer player) {
        skillPoints += getPlayerSkills().size();
        getPlayerSkills().forEach(skill -> skill.remove(player));
        getPlayerSkills().clear();
    }
    @Override
    public void writeData(ValueOutput output) {
        output.store("TreeVersion", UUIDUtil.CODEC, TREE_VERSION);
        output.putInt("Points", skillPoints);
        output.putBoolean("TreeReset", treeReset);
        ValueOutput.TypedOutputList<String> skillsList = output.list("Skills", Codec.STRING);
        skills.forEach(skill -> skillsList.add(skill.getId().toString()));
    }
    @Override
    public void readData(ValueInput input) {
        skills.clear();
        UUID treeVersion = input.read("TreeVersion", UUIDUtil.CODEC).orElse(null);
        skillPoints = input.getIntOr("Points", 0);
        List<String> skillIds = input.listOrEmpty("Skills", Codec.STRING).stream().toList();
        if (!TREE_VERSION.equals(treeVersion)) {
            skillPoints += skillIds.size();
            treeReset = true;
            return;
        }
        for (String skillIdStr : skillIds) {
            Identifier skillId = Identifier.parse(skillIdStr);
            PassiveSkill passiveSkill = SkillsReloader.getSkillById(skillId);
            if (passiveSkill == null || passiveSkill.isInvalid()) {
                skills.clear();
                treeReset = true;
                skillPoints += skillIds.size();
                return;
            }
            skills.add(passiveSkill);
        }
    }
}