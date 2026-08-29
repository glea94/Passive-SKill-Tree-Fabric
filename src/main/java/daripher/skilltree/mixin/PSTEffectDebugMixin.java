// ATTENTION : à placer dans le MÊME package que MobEffectMixin.java (adapte la ligne
// "package" ci-dessous si besoin), puis à enregistrer dans le mixins.json principal du
// mod (celui utilisé côté serveur / commun, PAS le mixins.json "client"), dans le tableau
// "mixins": [ ..., "PSTEffectDebugMixin" ]
package daripher.skilltree.mixin;

import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin de DIAGNOSTIC uniquement — n'altère aucun comportement, ne fait que logguer.
 * Objectif : déterminer si un appel à addEffect/removeEffect/forceAddEffect se produit
 * en ré-entrance pendant que LivingEntity#tickEffects() itère sur activeEffects (ce qui
 * provoquerait une ConcurrentModificationException silencieusement avalée par le code
 * vanilla, et donc un effet qui reste bloqué dans la map sans jamais être retiré).
 *
 * A retirer (ou commenter dans le mixins.json) une fois le vrai bug identifié et corrigé,
 * car les logs sont volontairement bavards (niveau WARN + stacktrace à chaque appel).
 */
@Mixin(LivingEntity.class)
public abstract class PSTEffectDebugMixin {
    private static final Logger PST_DEBUG = LogUtils.getLogger();

    @Shadow
    private Map<Holder<MobEffect>, MobEffectInstance> activeEffects;

    private String pst$snapshot() {
        LivingEntity self = (LivingEntity) (Object) this;
        String who;
        try {
            who = self.getName().getString() + "#" + self.getId();
        } catch (Throwable t) {
            who = self.toString();
        }
        String effects;
        try {
            effects = this.activeEffects.entrySet().stream()
                    .map(e -> e.getKey().value().getDescriptionId() + "=" + e.getValue().getDuration())
                    .collect(Collectors.joining(", "));
        } catch (Throwable t) {
            effects = "<erreur lecture map: " + t + ">";
        }
        return "entity=" + who + " activeEffects={" + effects + "}";
    }

    @Inject(
            method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
            at = @At("HEAD"))
    private void pst$logAddEffect(MobEffectInstance newEffect, Entity source, CallbackInfoReturnable<Boolean> cir) {
        PST_DEBUG.warn(
                "[PST-DEBUG] >>> addEffect({}, duree={}, source={}) | {}",
                newEffect.getEffect().value().getDescriptionId(),
                newEffect.getDuration(),
                source,
                pst$snapshot(),
                new Throwable("PST-DEBUG stacktrace addEffect"));
    }

    @Inject(method = "removeEffect(Lnet/minecraft/core/Holder;)Z", at = @At("HEAD"))
    private void pst$logRemoveEffect(Holder<MobEffect> effect, CallbackInfoReturnable<Boolean> cir) {
        PST_DEBUG.warn(
                "[PST-DEBUG] >>> removeEffect({}) | {}",
                effect.value().getDescriptionId(),
                pst$snapshot(),
                new Throwable("PST-DEBUG stacktrace removeEffect"));
    }

    @Inject(method = "forceAddEffect", at = @At("HEAD"))
    private void pst$logForceAddEffect(MobEffectInstance newEffect, Entity source, CallbackInfo ci) {
        PST_DEBUG.warn(
                "[PST-DEBUG] >>> forceAddEffect({}, duree={}) | {}",
                newEffect.getEffect().value().getDescriptionId(),
                newEffect.getDuration(),
                pst$snapshot(),
                new Throwable("PST-DEBUG stacktrace forceAddEffect"));
    }

    @Inject(method = "onEffectUpdated", at = @At("HEAD"))
    private void pst$logOnEffectUpdated(
            MobEffectInstance effect, boolean doRefreshAttributes, Entity source, CallbackInfo ci) {
        PST_DEBUG.warn(
                "[PST-DEBUG] >>> onEffectUpdated({}, refresh={}) | {}",
                effect.getEffect().value().getDescriptionId(),
                doRefreshAttributes,
                pst$snapshot(),
                new Throwable("PST-DEBUG stacktrace onEffectUpdated"));
    }

    @Inject(method = "onEffectsRemoved", at = @At("HEAD"))
    private void pst$logOnEffectsRemoved(Collection<MobEffectInstance> effects, CallbackInfo ci) {
        PST_DEBUG.warn(
                "[PST-DEBUG] >>> onEffectsRemoved({}) | {}",
                effects.stream()
                        .map(e -> e.getEffect().value().getDescriptionId())
                        .collect(Collectors.joining(", ")),
                pst$snapshot(),
                new Throwable("PST-DEBUG stacktrace onEffectsRemoved"));
    }
}