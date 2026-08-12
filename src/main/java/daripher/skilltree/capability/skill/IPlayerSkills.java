package daripher.skilltree.capability.skill;

import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;

/**
 * Portage Fabric : remplace net.minecraftforge.common.util.INBTSerializable<CompoundTag>.
 * Cardinal Components utilise Component (readFromNbt/writeToNbt, méthodes void qui mutent le
 * tag passé en paramètre) au lieu de serializeNBT()/deserializeNBT() de Forge (qui retournait
 * un nouveau tag). @AutoRegisterCapability disparaît : l'enregistrement se fait explicitement
 * dans PSTComponents, pas par annotation scannée au chargement.
 */
public interface IPlayerSkills extends AutoSyncedComponent  {
    NonNullList<PassiveSkill> getPlayerSkills();

    boolean learnSkill(PassiveSkill passiveSkill);

    boolean grantSkill(PassiveSkill passiveSkill);

    int getSkillPoints();

    void setSkillPoints(int skillPoints);

    void grantSkillPoints(int skillPoints);

    boolean isTreeReset();

    void setTreeReset(boolean reset);

    void resetTree(ServerPlayer player);
}
