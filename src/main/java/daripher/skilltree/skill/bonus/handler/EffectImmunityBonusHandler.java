package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.event.MobEffectApplicablePSTEvent;
import daripher.skilltree.event.PSTEvents;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.EffectImmunityBonus;
import daripher.skilltree.util.event.EventPriority;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;

import java.util.List;


public class EffectImmunityBonusHandler {
    public static void register() {
        PSTEvents.MOB_EFFECT_APPLICABLE.register(EventPriority.HIGHEST, EffectImmunityBonusHandler::applyEffectImmunity);
    }

    private static void applyEffectImmunity(MobEffectApplicablePSTEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }


        Holder<MobEffect> mobEffectHolder = event.getEffectInstance().getEffect();
        MobEffect mobEffect = mobEffectHolder.value();

        List<EffectImmunityBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, EffectImmunityBonus.class);
        for (EffectImmunityBonus skillBonus : skillBonuses) {
            if (skillBonus.shouldProvideImmunity(mobEffect, player)) {

                event.setResult(MobEffectApplicablePSTEvent.Result.DENY);
                return;
            }
        }
    }
}
