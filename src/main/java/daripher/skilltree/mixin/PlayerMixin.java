// 1.21.1 Fichier : src/main/java/daripher/skilltree/mixin/PlayerMixin.java
package daripher.skilltree.mixin;

import daripher.skilltree.event.CriticalHitPSTEvent;
import daripher.skilltree.event.PSTEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Portage Fabric de net.minecraftforge.event.entity.player.CriticalHitEvent, sans équivalent
 * direct dans Fabric API.
 * <p>
 * Technique choisie, plus sûre qu'une capture de variable locale par ordinal (fragile, dépend
 * de détails de compilation) :
 * 1) @ModifyConstant sur la constante 1.5F (le multiplicateur de crit vanilla dans
 *    Player.attack()) : détecte qu'un crit vanilla a eu lieu (mémorisé dans un champ) et permet
 *    d'ajuster ce multiplicateur (équivalent de CriticalHitEvent.setDamageMultiplier).
 * 2) @Redirect sur l'appel Entity.hurt(DamageSource, float) à l'intérieur de Player.attack() :
 *    poste l'event ; si aucun crit vanilla n'a eu lieu mais qu'un bonus de compétence force un
 *    crit (CriticalHitEvent.setResult(ALLOW) côté Forge), applique le multiplicateur nous-mêmes
 *    avant d'appeler hurt().
 * <p>
 * LIMITE CONNUE (mineure, cosmétique) : quand un crit est forcé par un bonus de compétence sans
 * crit vanilla, l'animation/particule de crit vanilla (déclenchée par Player.crit(target),
 * ailleurs dans attack(), sur la variable locale qu'on ne touche pas ici) ne se joue pas - seul
 * le dégât est correct. Aucun impact sur le gameplay, seulement visuel. À corriger plus tard via
 * un appel client-side explicite si besoin, pas bloquant pour la suite du portage.
 */
@Mixin(Player.class)
public abstract class PlayerMixin {
    @Unique
    private boolean skilltree$wasVanillaCrit;
    @Unique
    private float skilltree$vanillaCritMultiplier = 1.5f;

    @ModifyConstant(method = "attack", constant = @org.spongepowered.asm.mixin.injection.Constant(floatValue = 1.5f), require = 1)
    private float skilltree$onVanillaCritMultiplier(float original) {
        skilltree$wasVanillaCrit = true;
        skilltree$vanillaCritMultiplier = original;
        return original;
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"), require = 1)
    private boolean skilltree$onAttackHurt(Entity target, DamageSource source, float amount) {
        Player self = (Player) (Object) this;
        CriticalHitPSTEvent event = new CriticalHitPSTEvent(self, target, skilltree$wasVanillaCrit);
        PSTEvents.CRITICAL_HIT.post(event);
        if (skilltree$wasVanillaCrit) {
            // vanilla a déjà multiplié par 1.5 (ou la valeur ajustée à l'étape 1) ; si un bonus
            // veut un multiplicateur différent, on corrige l'écart.
            if (event.getDamageMultiplier() != skilltree$vanillaCritMultiplier) {
                amount = amount / skilltree$vanillaCritMultiplier * event.getDamageMultiplier();
            }
        } else if (event.isForcedCrit()) {
            amount *= event.getDamageMultiplier();
        }
        skilltree$wasVanillaCrit = false;
        skilltree$vanillaCritMultiplier = 1.5f;
        return target.hurt(source, amount);
    }
}
