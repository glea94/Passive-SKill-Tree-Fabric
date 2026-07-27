package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.event.CriticalHitPSTEvent;
import daripher.skilltree.event.LivingHurtPSTEvent;
import daripher.skilltree.event.PSTEvents;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.EventListenerBonus;
import daripher.skilltree.skill.bonus.event.*;
import daripher.skilltree.util.event.EventPriority;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Portage Fabric PARTIEL, assumé et documenté :
 * - triggerHurtEvents (LivingHurtEvent) -> PSTEvents.LIVING_HURT : porté.
 * - triggerCritEvents (CriticalHitEvent) -> PSTEvents.CRITICAL_HIT, event.isCrit() remplace le
 *   test Result.ALLOW/DEFAULT+vanillaCritical de Forge : porté.
 * - triggerKillEvents (LivingDeathEvent) -> ServerLivingEntityEvents.ALLOW_DEATH : porté.
 * <p>
 * VOLONTAIREMENT PAS ENCORE PORTÉ ICI (pas un stub, vraies dépendances manquantes) :
 * - triggerShieldBlockEvents (ShieldBlockEvent) : nécessite un mixin sur le blocage au bouclier
 *   vanilla (LivingEntity.blockUsingShield), plus complexe que les autres mixins déjà faits
 *   (corrélation entre l'attaquant et la source de dégâts pas directement disponible au point
 *   d'injection) - à traiter spécifiquement, pas de raccourci pris ici.
 * - triggerItemUsedEvents (LivingEntityUseItemEvent.Finish) : nécessite un mixin sur
 *   LivingEntity.completeUsingItem(), à traiter avec le reste des events d'utilisation d'item.
 */
public class EventListenerBonusHandler {
    public static void register() {
        PSTEvents.LIVING_HURT.register(EventListenerBonusHandler::triggerHurtEvents);
        PSTEvents.CRITICAL_HIT.register(EventPriority.LOWEST, EventListenerBonusHandler::triggerCritEvents);
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, amount) -> {
            triggerKillEvents(entity, source);
            return true;
        });
    }

    private static void triggerHurtEvents(LivingHurtPSTEvent event) {
        DamageSource damageSource = event.getSource();
        LivingEntity target = event.getEntity();
        Entity damagingEntity = damageSource.getEntity();
        if (damagingEntity instanceof Player player) {
            triggerEvent(player, OutgoingDamageEventListener.class, (eventListener, skillBonus) -> {
                eventListener.onEvent(player, target, damageSource, skillBonus);
            });
        }
        if (target instanceof Player player) {
            LivingEntity attacker = damagingEntity instanceof LivingEntity livingAttacker ? livingAttacker : null;
            triggerEvent(player, IncomingDamageEventListener.class, (eventListener, skillBonus) -> {
                eventListener.onEvent(player, attacker, damageSource, skillBonus);
            });
        }
    }

    private static void triggerCritEvents(CriticalHitPSTEvent event) {
        if (!event.isCrit()) {
            return;
        }
        if (!(event.getTarget() instanceof LivingEntity target)) {
            return;
        }
        Player player = event.getEntity();
        triggerEvent(player, CriticalHitEventListener.class, (eventListener, skillBonus) -> {
            eventListener.onEvent(player, target, skillBonus);
        });
    }

    private static void triggerKillEvents(LivingEntity killedEntity, DamageSource damageSource) {
        if (!(damageSource.getEntity() instanceof Player player)) {
            return;
        }
        triggerEvent(player, KillEventListener.class, (eventListener, skillBonus) -> {
            eventListener.onEvent(player, killedEntity, damageSource, skillBonus);
        });
    }

    @SuppressWarnings({"rawtypes"})
    public static <T extends SkillEventListener> void triggerEvent(Player player, Class<T> listenerClass, BiConsumer<T, EventListenerBonus<?>> action) {
        List<EventListenerBonus> skillBonuses = SkillBonusProvider.getMergedSkillBonuses(player, EventListenerBonus.class);
        for (EventListenerBonus<?> skillBonus : skillBonuses) {
            SkillEventListener listener = skillBonus.getEventListener();
            if (listenerClass.isInstance(listener)) {
                T eventListener = listenerClass.cast(listener);
                action.accept(eventListener, skillBonus);
            }
        }
    }
}
