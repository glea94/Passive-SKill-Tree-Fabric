package daripher.skilltree.mixin;

import daripher.skilltree.event.LivingAttackPSTEvent;
import daripher.skilltree.event.LivingHealPSTEvent;
import daripher.skilltree.event.LivingHurtPSTEvent;
import daripher.skilltree.event.PSTEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Portage Fabric de LivingAttackEvent/LivingHurtEvent/LivingHealEvent (Forge), sans équivalent
 * direct Fabric API.
 * <p>
 * CORRECTION (23/07/2026) : l'annotation "priority" n'existe pas sur @Inject/@ModifyVariable en
 * Mixin standard (erreur de ma part dans une version précédente - ça ne compilait pas). Au lieu
 * de deux injecteurs séparés avec un ordre supposé par priorité, chaque paire
 * event-attaque + event-dégâts est maintenant fusionnée dans UN SEUL @Inject qui exécute les
 * deux vérifications dans l'ordre voulu directement en code Java (garanti, pas d'ambiguïté
 * Mixin) : d'abord LivingAttackEvent (peut tout annuler), puis si pas annulé, LivingHurtEvent
 * (peut annuler ou modifier le montant). Le @ModifyVariable associé applique ensuite le montant
 * mémorisé - un seul par méthode, donc pas de question d'ordre entre plusieurs.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Unique
    private float skilltree$modifiedHurtAmount;
    @Unique
    private boolean skilltree$hurtAmountModified;

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true, require = 1)
    private void skilltree$onHurtHead(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;

        // 1) LivingAttackEvent : peut tout annuler avant même le calcul de dégâts.
        LivingAttackPSTEvent attackEvent = new LivingAttackPSTEvent(self, source, amount);
        PSTEvents.LIVING_ATTACK.post(attackEvent);
        if (attackEvent.isCanceled()) {
            skilltree$hurtAmountModified = false;
            cir.setReturnValue(false);
            cir.cancel();
            return;
        }

        // 2) LivingHurtEvent : peut annuler ou modifier le montant de dégâts.
        LivingHurtPSTEvent hurtEvent = new LivingHurtPSTEvent(self, source, amount);
        PSTEvents.LIVING_HURT.post(hurtEvent);
        if (hurtEvent.isCanceled()) {
            skilltree$hurtAmountModified = false;
            cir.setReturnValue(false);
            cir.cancel();
            return;
        }
        skilltree$hurtAmountModified = hurtEvent.getAmount() != amount;
        skilltree$modifiedHurtAmount = hurtEvent.getAmount();
    }

    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true, require = 1)
    private float skilltree$applyModifiedAmount(float amount) {
        return skilltree$hurtAmountModified ? skilltree$modifiedHurtAmount : amount;
    }

    /** Portage Fabric de LivingHealEvent (Forge), même schéma (annulation + modification du montant). */
    @Unique
    private float skilltree$modifiedHealAmount;
    @Unique
    private boolean skilltree$healAmountModified;

    @Inject(method = "heal", at = @At("HEAD"), cancellable = true, require = 1)
    private void skilltree$onHealHead(float amount, CallbackInfo ci) {
        LivingHealPSTEvent event = new LivingHealPSTEvent((LivingEntity) (Object) this, amount);
        PSTEvents.LIVING_HEAL.post(event);
        if (event.isCanceled()) {
            skilltree$healAmountModified = false;
            ci.cancel();
            return;
        }
        skilltree$healAmountModified = event.getAmount() != amount;
        skilltree$modifiedHealAmount = event.getAmount();
    }

    @ModifyVariable(method = "heal", at = @At("HEAD"), argsOnly = true, require = 1)
    private float skilltree$applyModifiedHealAmount(float amount) {
        return skilltree$healAmountModified ? skilltree$modifiedHealAmount : amount;
    }
}
