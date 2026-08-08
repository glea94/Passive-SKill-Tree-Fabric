package daripher.skilltree.mixin;

import daripher.skilltree.event.LivingAttackPSTEvent;
import daripher.skilltree.event.LivingHealPSTEvent;
import daripher.skilltree.event.LivingHurtPSTEvent;
import daripher.skilltree.event.PSTEvents;
import net.minecraft.server.level.ServerLevel;
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
 * CORRECTION (07/08/2026) : en 1.21.4, Entity#hurt(DamageSource, float) est final, renvoie void
 * et n'est qu'un relais déprécié vers hurtServer(ServerLevel, DamageSource, float) - LivingEntity
 * n'override plus hurt du tout. La cible d'injection devient donc hurtServer, qui contient
 * réellement toute la logique de dégâts et reste cancellable (CallbackInfoReturnable<Boolean>).
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Unique
    private float skilltree$modifiedHurtAmount;
    @Unique
    private boolean skilltree$hurtAmountModified;

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true, require = 1)
    private void skilltree$onHurtHead(ServerLevel serverLevel, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
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

    @ModifyVariable(method = "hurtServer", at = @At("HEAD"), argsOnly = true, require = 1)
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

    /**
     * Portage Fabric de net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Finish.
     * Injection simple à HEAD (avant que vanilla ne traite/échange l'item) : on capture l'item
     * en cours d'utilisation via getUseItem() (accesseur public vanilla) et on notifie nos
     * listeners. Pas de modification du comportement vanilla ici, juste une notification -
     * technique la plus sûre possible (pas de cancellable, pas de capture de variable locale).
     */
    @Inject(method = "completeUsingItem", at = @At("HEAD"), require = 1)
    private void skilltree$onCompleteUsingItem(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        net.minecraft.world.item.ItemStack usedItem = self.getUseItem();
        if (usedItem.isEmpty()) {
            return;
        }
        PSTEvents.ITEM_USE_FINISH.post(new daripher.skilltree.event.LivingEntityUseItemFinishPSTEvent(self, usedItem.copy()));
    }
}