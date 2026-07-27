package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.util.registry.DeferredRegister;
import daripher.skilltree.util.registry.RegistryObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;

public class PSTPotions {
    public static final DeferredRegister<Potion> REGISTRY = DeferredRegister.create(BuiltInRegistries.POTION, SkillTreeMod.MOD_ID);

    public static final RegistryObject<Potion> LIQUID_FIRE_1 = REGISTRY.register("liquid_fire_1", () -> new Potion(new MobEffectInstance(PSTMobEffects.LIQUID_FIRE.get())));
    public static final RegistryObject<Potion> LIQUID_FIRE_2 = REGISTRY.register("liquid_fire_2", () -> new Potion(new MobEffectInstance(PSTMobEffects.LIQUID_FIRE.get(), 0, 1)));
}
