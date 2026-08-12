package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.event.MobEffectApplicablePSTEvent;
import daripher.skilltree.event.PSTEvents;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.EffectImmunityBypassBonus;
import daripher.skilltree.util.event.EventPriority;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.List;

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

        // Aligned 1.21.4: Map status effect holders down cleanly using value() to evaluate bypass filters
        Holder<MobEffect> mobEffectHolder = event.getEffectInstance().getEffect();
        MobEffect mobEffect = mobEffectHolder.value();

        for (EffectImmunityBypassBonus skillBonus : skillBonuses) {
            if (skillBonus.shouldIgnoreEffectImmunity(mobEffect, effectSource, affectedEntity)) {
                // Overrides the application result to force the effect onto the target entity
                event.setResult(MobEffectApplicablePSTEvent.Result.ALLOW);
                return;
            }
        }
    }
}
