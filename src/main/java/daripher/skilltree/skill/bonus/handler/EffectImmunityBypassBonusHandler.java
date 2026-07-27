package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.event.MobEffectApplicablePSTEvent;
import daripher.skilltree.event.PSTEvents;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.EffectImmunityBypassBonus;
import daripher.skilltree.util.event.EventPriority;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * Portage Fabric : logique identique. Le "receiveCanceled = true" de Forge n'a pas d'équivalent
 * à traduire ici - notre bus maison appelle toujours tous les listeners dans l'ordre de
 * priorité, donc ce comportement (recevoir l'event même si déjà refusé par un listener
 * précédent) est déjà le fonctionnement par défaut.
 */
public class EffectImmunityBypassBonusHandler {
    public static void register() {
        PSTEvents.MOB_EFFECT_APPLICABLE.register(EventPriority.LOWEST, EffectImmunityBypassBonusHandler::bypassEffectImmunity);
    }

    private static void bypassEffectImmunity(MobEffectApplicablePSTEvent event) {
        LivingEntity affectedEntity = event.getEntity();
        if (!(affectedEntity.getKillCredit() instanceof Player effectSource)) {
            return;
        }
        List<EffectImmunityBypassBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(effectSource, EffectImmunityBypassBonus.class);
        MobEffect mobEffect = event.getEffectInstance().getEffect();
        for (EffectImmunityBypassBonus skillBonus : skillBonuses) {
            if (skillBonus.shouldIgnoreEffectImmunity(mobEffect, effectSource, affectedEntity)) {
                event.setResult(MobEffectApplicablePSTEvent.Result.ALLOW);
                return;
            }
        }
    }
}
