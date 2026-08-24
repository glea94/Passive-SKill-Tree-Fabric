package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.event.LivingVisibilityPSTEvent;
import daripher.skilltree.event.PSTEvents;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.StealthBonus;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.List;


public class StealthBonusHandler {
    public static void register() {
        PSTEvents.LIVING_VISIBILITY.register(StealthBonusHandler::applyVisibilityMultiplier);
    }

    private static void applyVisibilityMultiplier(LivingVisibilityPSTEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (!(event.getLookingEntity() instanceof LivingEntity lookingEntity)) {
            return;
        }
        List<StealthBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, StealthBonus.class);
        if (skillBonuses.isEmpty()) {
            return;
        }
        float stealthMultiplier = 0f;
        for (StealthBonus skillBonus : skillBonuses) {
            stealthMultiplier += skillBonus.getStealthMultiplier(player, lookingEntity);
        }
        
        event.modifyVisibility(1f - stealthMultiplier);
    }
}
