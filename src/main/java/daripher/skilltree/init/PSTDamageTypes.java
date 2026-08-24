package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.damagesource.DamageType;

public class PSTDamageTypes {
    
    public static final ResourceKey<DamageType> POISON = ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "poison"));
}
