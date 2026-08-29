package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.event.LivingHurtPSTEvent;
import daripher.skilltree.event.PSTEvents;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.DamageConversionBonus;
import daripher.skilltree.skill.bonus.predicate.damage.DamageCondition;
import daripher.skilltree.util.event.EventPriority;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
<<<<<<< Updated upstream


public class DamageConversionBonusHandler {
    private static boolean isProcessingConversion; 

=======
public class DamageConversionBonusHandler {
    private static boolean isProcessingConversion; 
>>>>>>> Stashed changes
    public static void register() {
        PSTEvents.LIVING_HURT.register(EventPriority.LOWEST, DamageConversionBonusHandler::dealConvertedDamage);
    }

    private static void dealConvertedDamage(LivingHurtPSTEvent event) {
        if (isProcessingConversion) {
            return;
        }
        DamageSource originalDamageSource = event.getSource();
        if (!(originalDamageSource.getEntity() instanceof Player player)) {
            return;
        }
        List<DamageConversionBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, DamageConversionBonus.class);
        if (skillBonuses.isEmpty()) {
            return;
        }
        LivingEntity target = event.getEntity();
        float originalDamageAmount = event.getAmount();
        float totalConvertedPercentage = 0f;
        Map<DamageCondition, Float> convertedDamageMap = new HashMap<>();
        for (DamageConversionBonus skillBonus : skillBonuses) {
            if (!skillBonus.canConvertDamage(originalDamageSource)) {
                continue;
            }
            DamageCondition resultDamageSource = skillBonus.getResultDamageCondition();
            float conversionRate = skillBonus.getConversionRate(originalDamageSource, player, target);
            convertedDamageMap.put(resultDamageSource, convertedDamageMap.getOrDefault(resultDamageSource, 0f) + conversionRate);
            totalConvertedPercentage += conversionRate;
        }
        if (convertedDamageMap.isEmpty()) {
            return;
        }
        try {
            isProcessingConversion = true;
            convertedDamageMap.forEach(((damageCondition, conversionPercentage) -> {
                DamageSource newDamageSource = damageCondition.createDamageSource(player);
                float damagePayload = conversionPercentage * originalDamageAmount;
                SkillBonusHandlerUtils.hurtIgnoringInvulnerabilityTime(target, newDamageSource, damagePayload);
            }));
        } finally {
            isProcessingConversion = false;
        }
        event.setAmount(originalDamageAmount * (1 - totalConvertedPercentage));
    }
}
