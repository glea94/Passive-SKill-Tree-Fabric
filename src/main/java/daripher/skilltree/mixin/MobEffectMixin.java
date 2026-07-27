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

/**
 * Portage Fabric de net.minecraftforge.event.entity.living.MobEffectEvent (Applicable + Added),
 * sans équivalent Fabric API direct.
 * <p>
 * - canBeAffected(MobEffectInstance) : méthode vanilla qui détermine l'immunité (ex. les morts-
 *   vivants ignorent le poison). Injection à RETURN, le Result (ALLOW/DENY/DEFAULT) de notre
 *   event maison peut forcer ou bloquer l'effet indépendamment du résultat vanilla.
 * - addEffect(MobEffectInstance, Entity) : après un ajout réussi, on récupère l'instance
 *   canonique via getEffect() (plutôt que de suivre la référence du paramètre, qui peut différer
 *   en cas de fusion avec un effet déjà actif) pour poster l'event avec l'instance réellement
 *   active.
 */
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

    @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At("RETURN"), require = 1)
    private void skilltree$onAddEffect(MobEffectInstance effectInstance, Entity source, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            return;
        }
        LivingEntity self = (LivingEntity) (Object) this;
        MobEffectInstance activeInstance = self.getEffect(effectInstance.getEffect());
        if (activeInstance == null) {
            return;
        }
        PSTEvents.MOB_EFFECT_ADDED.post(new MobEffectAddedPSTEvent(self, activeInstance, source));
    }
}
