package daripher.skilltree.mixin;
import daripher.skilltree.event.MobEffectAddedPSTEvent;
import daripher.skilltree.event.MobEffectApplicablePSTEvent;
import daripher.skilltree.event.PSTEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(LivingEntity.class)
public abstract class MobEffectMixin {
    @Inject(method = "canBeAffected", at = @At("RETURN"), cancellable = true, require = 1)
    private void skilltree$onCanBeAffected(MobEffectInstance effectInstance, CallbackInfoReturnable<Boolean> cir) {
        MobEffectApplicablePSTEvent event = new MobEffectApplicablePSTEvent((LivingEntity) (Object) this, effectInstance);
        PSTEvents.MOB_EFFECT_APPLICABLE.post(event);
        if (event.getResult() == MobEffectApplicablePSTEvent.Result.ALLOW) {
            cir.setReturnValue(true);
        } else if (event.getResult() == MobEffectApplicablePSTEvent.Result.DENY) {
            cir.setReturnValue(false);
        }
    }
    @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"), require = 1)
    private void skilltree$onAddEffect(MobEffectInstance effectInstance, Entity source, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        PSTEvents.MOB_EFFECT_ADDED.post(new MobEffectAddedPSTEvent(self, effectInstance, source));
    }
}