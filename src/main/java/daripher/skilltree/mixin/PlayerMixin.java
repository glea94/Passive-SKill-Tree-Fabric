package daripher.skilltree.mixin;

import daripher.skilltree.event.CriticalHitPSTEvent;
import daripher.skilltree.event.PSTEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Portage Fabric de net.minecraftforge.event.entity.player.CriticalHitEvent, adapté pour la 1.21.4.
 * <p>
 * Factual Fix 1.21.4 : En raison de la refonte du combat par Mojang, la constante floatValue = 1.5f
 * n'est plus présente de manière prévisible sous sa forme d'origine. La logique est unifiée dans
 * l'intercepteur Redirect d'Entity#hurt pour calculer dynamiquement les modificateurs de coups critiques
 * de l'arbre de compétences sans risque d'échec d'injection.
 */
@Mixin(Player.class)
public abstract class PlayerMixin {

    @Redirect(method = "attack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;hurtOrSimulate(Lnet/minecraft/world/damagesource/DamageSource;F)Z"), require = 1)
    private boolean skilltree$onAttackHurt(Entity target, DamageSource source, float amount) {
        Player self = (Player) (Object) this;

        // Détermine de manière fiable si les conditions de coup critique vanilla sont remplies
        // (le joueur tombe, ne grimpe pas, n'est pas dans l'eau, n'a pas l'effet aveuglement, n'est pas sur un véhicule)
        boolean isVanillaCrit = !self.onGround() && self.fallDistance > 0.0F && !self.onClimbable() && !self.isInWater()
                && !self.hasEffect(net.minecraft.world.effect.MobEffects.BLINDNESS) && !self.isPassenger() && !self.isSprinting();

        CriticalHitPSTEvent event = new CriticalHitPSTEvent(self, target, isVanillaCrit);
        PSTEvents.CRITICAL_HIT.post(event);

        if (isVanillaCrit) {
            // Le calcul de base vanilla a déjà appliqué son coefficient multiplicateur (1.5f par défaut).
            // Si l'arbre de compétences demande une valeur différente, on ajuste l'écart proportionnellement.
            float defaultMultiplier = 1.5f;
            if (event.getDamageMultiplier() != defaultMultiplier) {
                amount = (amount / defaultMultiplier) * event.getDamageMultiplier();
            }
        } else if (event.isForcedCrit()) {
            // Force un coup critique si un bonus passif l'exige, même si les conditions de saut vanilla ne sont pas réunies
            amount *= event.getDamageMultiplier();
        }

        return target.hurtOrSimulate(source, amount);
    }
}