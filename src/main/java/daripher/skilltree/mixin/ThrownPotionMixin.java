// Fichier : src/main/java/daripher/skilltree/mixin/ThrownPotionMixin.java
package daripher.skilltree.mixin;

import daripher.skilltree.skill.bonus.handler.SelfSplashImmunityBonusHandler;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
<<<<<<< Updated upstream
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
=======
import net.minecraft.world.entity.projectile.AbstractThrownPotion;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrownSplashPotion;
>>>>>>> Stashed changes
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@Mixin(ThrownSplashPotion.class)
public abstract class ThrownPotionMixin extends AbstractThrownPotion implements ItemSupplier {
    @SuppressWarnings("DataFlowIssue")
    private ThrownPotionMixin() {
        super(null, null);
    }

    @Redirect(method = "onHitAsPotion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean setAttackerOnHit(LivingEntity entity, MobEffectInstance effectInstance, Entity effectSource) {
        if (getOwner() instanceof Player player) {
            entity.setLastHurtByPlayer(player, LivingEntity.PLAYER_HURT_EXPERIENCE_TIME);
        }
        return entity.addEffect(effectInstance, effectSource);
    }

    @Redirect(method = "onHitAsPotion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
    private <T extends Entity> List<T> removePlayerTarget(Level level, Class<T> entityClass, AABB area) {
        List<T> baseTargets = level.getEntitiesOfClass(entityClass, area);
        Entity owner = getOwner();
        if (!(owner instanceof Player player)) {
            return baseTargets;
        }
        if (!baseTargets.contains(player)) {
            return baseTargets;
        }
        if (!SelfSplashImmunityBonusHandler.isPlayerImmuneToOwnSplashPotions(player)) {
            return baseTargets;
        }
        // Factual Fix 1.21.4: Wrap into a mutable ArrayList to prevent a runtime UnsupportedOperationException
        List<T> mutableTargets = new ArrayList<>(baseTargets);
        mutableTargets.removeIf(owner::equals);
        return mutableTargets;
    }
}