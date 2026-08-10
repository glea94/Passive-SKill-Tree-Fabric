package daripher.skilltree.capability.skill;

import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;

// Ajout explicite de Component en plus de AutoSyncedComponent pour la robustesse de l'API V6
public interface IPlayerSkills extends Component, AutoSyncedComponent {
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
