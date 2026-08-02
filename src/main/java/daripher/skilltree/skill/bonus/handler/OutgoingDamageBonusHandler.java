package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.event.PSTEvents;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.OutgoingDamageBonus;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/** Portage Fabric : @SubscribeEvent -> enregistrement explicite sur PSTEvents.LIVING_HURT (voir mixin LivingEntityMixin). Logique interne identique. */
public class OutgoingDamageBonusHandler {
    public static void register() {
        PSTEvents.LIVING_HURT.register(OutgoingDamageBonusHandler::modifyOutgoingDamage);
    }

    private static void modifyOutgoingDamage(daripher.skilltree.event.LivingHurtPSTEvent event) {
        DamageSource damageSource = event.getSource();
        if (!(damageSource.getEntity() instanceof Player player)) {
            return;
        }
        LivingEntity target = event.getEntity();
        List<OutgoingDamageBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, OutgoingDamageBonus.class);
        if (skillBonuses.isEmpty()) {
            return;
        }
        float flatDamageBonus = 0f;
        float baseDamageMultiplier = 1f;
        float totalDamageMultiplier = 1f;
        for (OutgoingDamageBonus bonus : skillBonuses) {
            flatDamageBonus += bonus.getDamageModifier(AttributeModifier.Operation.ADD_VALUE, damageSource, player, target);
            baseDamageMultiplier += bonus.getDamageModifier(AttributeModifier.Operation.ADD_MULTIPLIED_BASE, damageSource, player, target);
            totalDamageMultiplier *= 1f + bonus.getDamageModifier(AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, damageSource, player, target);
        }
        float amount = event.getAmount();
        amount += flatDamageBonus;
        amount *= baseDamageMultiplier;
        amount *= totalDamageMultiplier;
        event.setAmount(amount);
    }
}
