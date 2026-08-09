package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.event.CriticalHitPSTEvent;
import daripher.skilltree.event.PSTEvents;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.CritChanceBonus;
import daripher.skilltree.util.event.EventPriority;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/** Portage Fabric : event.setResult(ALLOW) -> event.setForcedCrit(true), même logique. */
public class CriticalHitChanceBonusHandler {
    public static void register() {
        PSTEvents.CRITICAL_HIT.register(EventPriority.HIGH, CriticalHitChanceBonusHandler::applyDirectHitCritChance);
    }

    private static void applyDirectHitCritChance(CriticalHitPSTEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) {
            return;
        }
        if (!(event.getTarget() instanceof LivingEntity hurtEntity)) {
            return;
        }
        DamageSource damageSource = player.level().damageSources().playerAttack(player);
        boolean isVanillaCrit = event.isVanillaCritical();
        float critChance = getCritChance(player, damageSource, hurtEntity);
        boolean isModCrit = player.getRandom().nextFloat() < critChance;
        if (!isVanillaCrit && !isModCrit) {
            return;
        }
        if (!isVanillaCrit) {
            // Forces critical calculation on the current attack instance, bypassing vanilla jump bounds
            event.setForcedCrit(true);
        }
    }

    public static float getCritChance(Player player, DamageSource source, LivingEntity target) {
        float critChance = 0f;
        for (CritChanceBonus bonus : SkillBonusProvider.getSkillBonuses(player, CritChanceBonus.class)) {
            critChance += bonus.getChanceBonus(source, player, target);
        }
        return critChance;
    }
}
