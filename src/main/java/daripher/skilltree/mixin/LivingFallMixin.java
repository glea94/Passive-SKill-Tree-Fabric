package daripher.skilltree.mixin;
import daripher.skilltree.event.LivingFallPSTEvent;
import daripher.skilltree.event.PSTEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(LivingEntity.class)
public abstract class LivingFallMixin {
    @Unique
    private float skilltree$modifiedFallDistance;
    @Unique
    private boolean skilltree$fallDistanceModified;
    @Inject(method = "causeFallDamage", at = @At("HEAD"), cancellable = true, require = 1)
    private void skilltree$postLivingFallEvent(double distance, float damageMultiplier, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        LivingFallPSTEvent event = new LivingFallPSTEvent((LivingEntity) (Object) this, (float) distance);
        PSTEvents.LIVING_FALL.post(event);
        if (event.isCanceled()) {
            skilltree$fallDistanceModified = false;
            cir.setReturnValue(false);
            cir.cancel();
            return;
        }
        skilltree$fallDistanceModified = event.getDistance() != (float) distance;
        skilltree$modifiedFallDistance = event.getDistance();
    }
    @ModifyVariable(method = "causeFallDamage", at = @At("HEAD"), argsOnly = true, require = 1, ordinal = 0)
    private double skilltree$applyModifiedFallDistance(double distance) {
        return skilltree$fallDistanceModified ? (double) skilltree$modifiedFallDistance : distance;
    }
}