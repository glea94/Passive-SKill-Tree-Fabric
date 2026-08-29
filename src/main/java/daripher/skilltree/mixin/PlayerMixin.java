package daripher.skilltree.mixin;
import daripher.skilltree.event.CriticalHitPSTEvent;
import daripher.skilltree.event.PSTEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
<<<<<<< Updated upstream

=======
>>>>>>> Stashed changes
@Mixin(Player.class)
public abstract class PlayerMixin {
    @ModifyVariable(method = "attack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;hurtOrSimulate(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
            shift = At.Shift.BEFORE), ordinal = 0)
    private float skilltree$modifyAttackDamage(float amount, Entity target) {
        Player self = (Player) (Object) this;
<<<<<<< Updated upstream



=======
>>>>>>> Stashed changes
        boolean isVanillaCrit = !self.onGround() && self.fallDistance > 0.0F && !self.onClimbable() && !self.isInWater()
                && !self.hasEffect(net.minecraft.world.effect.MobEffects.BLINDNESS) && !self.isPassenger() && !self.isSprinting();
        CriticalHitPSTEvent event = new CriticalHitPSTEvent(self, target, isVanillaCrit);
        PSTEvents.CRITICAL_HIT.post(event);
        if (isVanillaCrit) {
<<<<<<< Updated upstream


=======
>>>>>>> Stashed changes
            float defaultMultiplier = 1.5f;
            if (event.getDamageMultiplier() != defaultMultiplier) {
                amount = (amount / defaultMultiplier) * event.getDamageMultiplier();
            }
        } else if (event.isForcedCrit()) {
<<<<<<< Updated upstream

=======
>>>>>>> Stashed changes
            amount *= event.getDamageMultiplier();
        }
        return amount;
    }
}