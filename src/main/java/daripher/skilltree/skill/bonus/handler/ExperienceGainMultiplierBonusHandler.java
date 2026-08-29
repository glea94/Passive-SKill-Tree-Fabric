package daripher.skilltree.skill.bonus.handler;
import daripher.skilltree.event.LivingExperienceDropPSTEvent;
import daripher.skilltree.event.PSTEvents;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.ExperienceGainMultiplierBonus;
import net.minecraft.world.entity.player.Player;
import java.util.List;
public class ExperienceGainMultiplierBonusHandler {
    public static void register() {
        PSTEvents.LIVING_EXPERIENCE_DROP.register(ExperienceGainMultiplierBonusHandler::applyMobExpBonus);
    }
    private static void applyMobExpBonus(LivingExperienceDropPSTEvent event) {
        Player player = event.getAttackingPlayer();
        if (player == null) {
            return;
        }
        float multiplier = 1f;
        multiplier += getExperienceMultiplierBonus(player, ExperienceGainMultiplierBonus.ExperienceSource.MOBS);
        event.setDroppedExperience((int) (event.getDroppedExperience() * multiplier));
    }
    static float getExperienceMultiplierBonus(Player player, ExperienceGainMultiplierBonus.ExperienceSource source) {
        float multiplier = 0f;
        List<ExperienceGainMultiplierBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, ExperienceGainMultiplierBonus.class);
        for (ExperienceGainMultiplierBonus bonus : skillBonuses) {
            if (bonus.getSource() == source) {
                multiplier += bonus.getMultiplier();
            }
        }
        return multiplier;
    }
}
