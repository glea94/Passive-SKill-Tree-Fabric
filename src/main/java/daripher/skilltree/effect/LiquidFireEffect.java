package daripher.skilltree.effect;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LiquidFireEffect extends MobEffect {
    public LiquidFireEffect() {
        super(MobEffectCategory.HARMFUL, 0xfa440c);
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }

    @Override
    public void applyInstantenousEffect(ServerLevel serverLevel, @Nullable Entity source, @Nullable Entity indirectSource, @NotNull LivingEntity target, int amplifier, double health) {
        float damage = (int) (health * (double) (6 << amplifier) + 0.5);
        DamageSources damageSources = target.damageSources();
        if (source == null) {
            target.hurt(damageSources.onFire(), damage);
        } else {
            Holder<DamageType> damageType = target.level().registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.ON_FIRE);
            DamageSource damageSource = new DamageSource(damageType, source, indirectSource);
            target.hurt(damageSource, damage);
        }
<<<<<<< Updated upstream
        target.setSecondsOnFire((int) damage / 2);
=======
        target.igniteForSeconds((int) damage / 2);
>>>>>>> Stashed changes
    }
}
