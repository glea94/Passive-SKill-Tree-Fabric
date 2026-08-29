package daripher.skilltree.skill.bonus.handler;
import daripher.skilltree.event.LivingAttackPSTEvent;
import daripher.skilltree.event.PSTEvents;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.event.EvasionEventListener;
import daripher.skilltree.skill.bonus.player.DamageAvoidanceChanceBonus;
import daripher.skilltree.util.event.EventPriority;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import org.jetbrains.annotations.Nullable;
import java.util.List;
public class DamageAvoidanceBonusHandler {
    public static void register() {
        PSTEvents.LIVING_ATTACK.register(EventPriority.LOWEST, DamageAvoidanceBonusHandler::applyDamageAvoidanceBonuses);
    }
    private static void applyDamageAvoidanceBonuses(LivingAttackPSTEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        List<DamageAvoidanceChanceBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, DamageAvoidanceChanceBonus.class);
        if (skillBonuses.isEmpty()) {
            return;
        }
        DamageSource damageSource = event.getSource();
        LivingEntity attacker = getAttacker(damageSource);
        float avoidanceChance = 0f;
        for (DamageAvoidanceChanceBonus skillBonus : skillBonuses) {
            avoidanceChance += skillBonus.getChance(damageSource, player, attacker);
        }
        if (player.getRandom().nextFloat() < avoidanceChance) {
<<<<<<< Updated upstream

=======
>>>>>>> Stashed changes
            event.setCanceled(true);
            EventListenerBonusHandler.triggerEvent(player, EvasionEventListener.class, (eventListener, skillBonus) -> {
                eventListener.onEvent(player, attacker, skillBonus);
            });
        }
    }
    private static @Nullable LivingEntity getAttacker(DamageSource damageSource) {
        Entity sourceEntity = damageSource.getEntity();
        if (sourceEntity instanceof LivingEntity attacker) {
            return attacker;
        } else if (sourceEntity instanceof Projectile projectile && projectile.getOwner() instanceof LivingEntity attacker) {
            return attacker;
        }
        return null;
    }
}
