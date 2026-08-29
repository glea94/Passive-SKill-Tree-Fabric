package daripher.skilltree.effect;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
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
    public void applyInstantaneousEffect(@NotNull ServerLevel serverLevel, @Nullable Entity source, @Nullable Entity indirectSource, @NotNull LivingEntity target, int amplifier, double fraction) {
        float damage = (float) (fraction * (double) (6 << amplifier) + 0.5);
        DamageSources damageSources = target.damageSources();
        if (source == null) {
            target.hurt(damageSources.onFire(), damage);
        } else {
            DamageSource damageSource = damageSources.source(DamageTypes.ON_FIRE, source, indirectSource);
            target.hurt(damageSource, damage);
        }
        target.igniteForSeconds(damage / 2.0F);
    }
}