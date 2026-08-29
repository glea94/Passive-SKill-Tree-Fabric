package daripher.skilltree.mixin;
import daripher.skilltree.event.LivingExperienceDropPSTEvent;
import daripher.skilltree.event.PSTEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(LivingEntity.class)
public abstract class LivingExperienceDropMixin {
    @Inject(method = "getExperienceReward", at = @At("RETURN"), cancellable = true, require = 1)
    private void skilltree$onGetExperienceReward(CallbackInfoReturnable<Integer> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        Player lastHurtByPlayer = self.getLastHurtByPlayer();
        LivingExperienceDropPSTEvent event = new LivingExperienceDropPSTEvent(self, lastHurtByPlayer, cir.getReturnValue());
        PSTEvents.LIVING_EXPERIENCE_DROP.post(event);
        if (event.getDroppedExperience() != cir.getReturnValue()) {
            cir.setReturnValue(event.getDroppedExperience());
        }
    }
}