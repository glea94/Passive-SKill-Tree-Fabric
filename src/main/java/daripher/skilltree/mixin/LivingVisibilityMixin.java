package daripher.skilltree.mixin;
import daripher.skilltree.event.LivingVisibilityPSTEvent;
import daripher.skilltree.event.PSTEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(LivingEntity.class)
public abstract class LivingVisibilityMixin {
    @Inject(method = "getVisibilityPercent", at = @At("RETURN"), cancellable = true, require = 1)
    private void skilltree$onGetVisibilityPercent(Entity lookingEntity, CallbackInfoReturnable<Double> cir) {
        LivingVisibilityPSTEvent event = new LivingVisibilityPSTEvent((LivingEntity) (Object) this, lookingEntity);
        PSTEvents.LIVING_VISIBILITY.post(event);
        if (event.getVisibilityModifier() != 1.0) {
            cir.setReturnValue(cir.getReturnValue() * event.getVisibilityModifier());
        }
    }
}
