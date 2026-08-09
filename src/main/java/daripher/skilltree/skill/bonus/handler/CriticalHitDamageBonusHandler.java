package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.event.CriticalHitPSTEvent;
import daripher.skilltree.event.LivingHurtPSTEvent;
import daripher.skilltree.event.PSTEvents;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.CritDamageBonus;
import daripher.skilltree.util.event.EventPriority;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;

/** Portage Fabric : logique identique, event.isCrit() remplace vanillaCritical||result==ALLOW. */
public class CriticalHitDamageBonusHandler {
    public static void register() {
        PSTEvents.CRITICAL_HIT.register(EventPriority.LOW, CriticalHitDamageBonusHandler::applyCritBonuses);
        PSTEvents.LIVING_HURT.register(EventPriority.LOW, CriticalHitDamageBonusHandler::applyIndirectHitCritDamage);
    }

    private static void applyCritBonuses(CriticalHitPSTEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) {
            return;
        }
        if (!(event.getTarget() instanceof LivingEntity hurtEntity)) {
            return;
        }
        if (!event.isCrit()) {
            return;
        }
        DamageSource damageSource = player.level().damageSources().playerAttack(player);
        float modCritMultiplier = getCritDamageModifier(player, damageSource, hurtEntity);
        event.setDamageMultiplier(1.5f + modCritMultiplier);
    }

    private static void applyIndirectHitCritDamage(LivingHurtPSTEvent event) {
        DamageSource damageSource = event.getSource();
        Entity directDamagingEntity = damageSource.getDirectEntity();
        // Dégâts directs, gérés par la méthode au-dessus, on ignore ici
        if (directDamagingEntity instanceof Player) {
            return;
        }
        Entity damagingEntity = damageSource.getEntity();
        if (!(damagingEntity instanceof ServerPlayer player)) {
            return;
        }
        boolean isVanillaCrit = false;
        if (directDamagingEntity instanceof AbstractArrow arrow) {
            isVanillaCrit = arrow.isCritArrow();
        }
        float critChance = CriticalHitChanceBonusHandler.getCritChance(player, damageSource, event.getEntity());
        boolean isModCrit = player.getRandom().nextFloat() < critChance;
        if (!isVanillaCrit && !isModCrit) {
            return;
        }
        LivingEntity hurtEntity = event.getEntity();
        float modCritMultiplier = getCritDamageModifier(player, damageSource, hurtEntity);
        if (isVanillaCrit) {
            event.setAmount(event.getAmount() * (1f + modCritMultiplier));
        } else {
            float vanillaCritMultiplier = 1.5f;
            event.setAmount(event.getAmount() * (vanillaCritMultiplier + modCritMultiplier));
        }
    }

    public static float getCritDamageModifier(Player player, DamageSource source, LivingEntity target) {
        float multiplier = 0f;
        for (CritDamageBonus bonus : SkillBonusProvider.getSkillBonuses(player, CritDamageBonus.class)) {
            multiplier += bonus.getDamageBonus(source, player, target);
        }
        return multiplier;
    }
}
