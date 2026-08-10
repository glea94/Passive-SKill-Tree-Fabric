package daripher.skilltree.capability.skill;

import com.mojang.serialization.Codec;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.core.NonNullList;
<<<<<<< Updated upstream
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
=======
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
>>>>>>> Stashed changes
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

    // Portage : remplace serializeNBT() (qui retournait un CompoundTag) -> écrit dans le tag fourni.
    @Override
<<<<<<< Updated upstream
    public void writeToNbt(CompoundTag tag) {
        tag.putUUID("TreeVersion", TREE_VERSION);
        tag.putInt("Points", skillPoints);
        tag.putBoolean("TreeReset", treeReset);
        ListTag skillsTag = new ListTag();
        skills.forEach(skill -> skillsTag.add(StringTag.valueOf(skill.getId().toString())));
        tag.put("Skills", skillsTag);
=======
    public void writeData(ValueOutput output) {
        // Fix 1.21.8 : writeToNbt(CompoundTag, HolderLookup.Provider) remplacé par writeData(ValueOutput) (interface Component de Cardinal Components 7.0.0-beta.1), confirmé par décompilation
        output.store("TreeVersion", UUIDUtil.CODEC, TREE_VERSION);
        output.putInt("Points", skillPoints);
        output.putBoolean("TreeReset", treeReset);
        ValueOutput.TypedOutputList<String> skillsList = output.list("Skills", Codec.STRING);
        skills.forEach(skill -> skillsList.add(skill.getId().toString()));
>>>>>>> Stashed changes
    }

    // Portage : remplace deserializeNBT(CompoundTag) de Forge, même logique, juste le nom de méthode.
    @Override
<<<<<<< Updated upstream
    public void readFromNbt(CompoundTag tag) {
        skills.clear();
        UUID treeVersion = tag.hasUUID("TreeVersion") ? tag.getUUID("TreeVersion") : null;
        skillPoints = tag.getInt("Points");
        ListTag skillsTag = tag.getList("Skills", Tag.TAG_STRING);
=======
    public void readData(ValueInput input) {
        // Fix 1.21.8 : readFromNbt(CompoundTag, HolderLookup.Provider) remplacé par readData(ValueInput), confirmé par décompilation
        skills.clear();

        UUID treeVersion = input.read("TreeVersion", UUIDUtil.CODEC).orElse(null);

        skillPoints = input.getIntOr("Points", 0);
        List<String> skillIds = input.listOrEmpty("Skills", Codec.STRING).stream().toList();
>>>>>>> Stashed changes
        if (!TREE_VERSION.equals(treeVersion)) {
            skillPoints += skillIds.size();
            treeReset = true;
            return;
        }
<<<<<<< Updated upstream
        for (Tag skillTag : skillsTag) {
            ResourceLocation skillId = new ResourceLocation(skillTag.getAsString());
=======
        for (String skillIdStr : skillIds) {
            Identifier skillId = Identifier.parse(skillIdStr);
>>>>>>> Stashed changes
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
