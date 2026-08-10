package daripher.skilltree.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MobEffectInstance.class)
public interface MobEffectInstanceAccessor {
    // Explicitly defines the field mapping target for compile-safe obfuscation handling
    @Accessor("duration")
    void setDuration(int duration);
}
