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

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Unique
    private float skilltree$modifiedHurtAmount;
    @Unique
    private boolean skilltree$hurtAmountModified;

    // Factual Fix 1.21.4: Target shifted from "hurt" to "hurtServer", added ServerLevel parameter
    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true, require = 1)
    private void skilltree$onHurtServerHead(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;

        // 1) LivingAttackEvent: Intercept attack details before raw calculations
        LivingAttackPSTEvent attackEvent = new LivingAttackPSTEvent(self, source, amount);
        PSTEvents.LIVING_ATTACK.post(attackEvent);
        if (attackEvent.isCanceled()) {
            skilltree$hurtAmountModified = false;
            cir.setReturnValue(false);
            cir.cancel();
            return;
        }

        // 2) LivingHurtEvent: Allow modification or mitigation of the processed final values
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

    // Factual Fix 1.21.4: Refactored target from "hurt" to "hurtServer"
    @ModifyVariable(method = "hurtServer", at = @At("HEAD"), argsOnly = true, require = 1)
    private float skilltree$applyModifiedAmount(float amount) {
        return skilltree$hurtAmountModified ? skilltree$modifiedHurtAmount : amount;
    }

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
        // Secure a localized detached copy to safely pass historical data down to your triggers
        PSTEvents.ITEM_USE_FINISH.post(new daripher.skilltree.event.LivingEntityUseItemFinishPSTEvent(self, usedItem.copy()));
    }
}
