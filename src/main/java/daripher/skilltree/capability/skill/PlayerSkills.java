package daripher.skilltree.capability.skill;

import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import org.jetbrains.annotations.NotNull;
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
    public boolean learnSkill(@NotNull PassiveSkill passiveSkill) {
        if (skillPoints == 0) {
            return false;
        }
        if (skills.contains(passiveSkill)) {
            return false;
        }
        skillPoints--;
        return skills.add(passiveSkill);
    }

    @Override
    public boolean grantSkill(PassiveSkill passiveSkill) {
        if (skills.contains(passiveSkill)) {
            return false;
        }
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
    public void writeToNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
        tag.store("TreeVersion", UUIDUtil.CODEC, TREE_VERSION);
        tag.putInt("Points", skillPoints);
        tag.putBoolean("TreeReset", treeReset);
        ListTag skillsTag = new ListTag();
        skills.forEach(skill -> skillsTag.add(StringTag.valueOf(skill.getId().toString())));
        tag.put("Skills", skillsTag);
    }

    @Override
    public void readFromNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
        skills.clear();

        // En 1.21.5, plus de contains(key, type) : on lit directement via le Codec, Optional.empty() si absent/invalide
        UUID treeVersion = tag.read("TreeVersion", UUIDUtil.CODEC).orElse(null);

        skillPoints = tag.getIntOr("Points", 0);
        ListTag skillsTag = tag.getListOrEmpty("Skills");
        if (!TREE_VERSION.equals(treeVersion)) {
            skillPoints += skillsTag.size();
            treeReset = true;
            return;
        }
        for (Tag skillTag : skillsTag) {
            ResourceLocation skillId = ResourceLocation.parse(skillTag.asString().orElse(""));
            PassiveSkill passiveSkill = SkillsReloader.getSkillById(skillId);
            if (passiveSkill == null || passiveSkill.isInvalid()) {
                skills.clear();
                treeReset = true;
                skillPoints += skillsTag.size();
                return;
            }
            skills.add(passiveSkill);
        }
    }
}