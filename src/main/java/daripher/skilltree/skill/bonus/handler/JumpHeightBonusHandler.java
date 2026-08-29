package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.event.LivingFallPSTEvent;
import daripher.skilltree.event.PSTEvents;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.JumpHeightBonus;
import net.minecraft.world.entity.player.Player;

import java.util.List;


public class JumpHeightBonusHandler {
    public static void register() {
        PSTEvents.LIVING_FALL.register(JumpHeightBonusHandler::reduceFallDistance);
    }

    public static float getJumpHeightMultiplier(Player player) {
        float multiplier = 1f;
        List<JumpHeightBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, JumpHeightBonus.class);
        for (JumpHeightBonus bonus : skillBonuses) {
            multiplier += bonus.getJumpHeightMultiplier(player);
        }
        return multiplier;
    }

    private static void reduceFallDistance(LivingFallPSTEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        float multiplier = getJumpHeightMultiplier(player);
        if (multiplier <= 1) {
            return;
        }
<<<<<<< Updated upstream
        
=======
>>>>>>> Stashed changes
        event.setDistance(event.getDistance() / multiplier);
    }
}
