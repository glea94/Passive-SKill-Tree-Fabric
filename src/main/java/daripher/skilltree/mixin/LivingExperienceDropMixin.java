package daripher.skilltree.mixin;

import daripher.skilltree.event.LivingExperienceDropPSTEvent;
import daripher.skilltree.event.PSTEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Portage Fabric de net.minecraftforge.event.entity.living.LivingExperienceDropEvent, sans
 * équivalent Fabric API direct. Cible LivingEntity.getExperienceReward(), méthode vanilla
 * appelée lors de la mort pour calculer l'XP à lâcher.
 * <p>
 * CORRECTIONS (confirmées par 2 crashs/erreurs successifs, message très précis à chaque fois) :
 * 1) getExperienceReward() ne prend AUCUN paramètre côté vanilla 1.20.1.
 * 2) Le joueur à l'origine du dernier coup n'est PAS exposé par une méthode publique
 *    getLastHurtByPlayer() - c'est un champ (lastHurtByPlayer), accédé ici via @Shadow, comme
 *    documenté sur plusieurs versions de la doc Forge/NeoForge (nom de champ stable dans le temps).
 */
@Mixin(LivingEntity.class)
public abstract class LivingExperienceDropMixin {
    @Shadow
    @Nullable
    private Player lastHurtByPlayer;

    @Inject(method = "getExperienceReward", at = @At("RETURN"), cancellable = true, require = 1)
    private void skilltree$onGetExperienceReward(CallbackInfoReturnable<Integer> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        LivingExperienceDropPSTEvent event = new LivingExperienceDropPSTEvent(self, lastHurtByPlayer, cir.getReturnValue());
        PSTEvents.LIVING_EXPERIENCE_DROP.post(event);
        if (event.getDroppedExperience() != cir.getReturnValue()) {
            cir.setReturnValue(event.getDroppedExperience());
        }
    }
}
