package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;


public class PSTStats {
    public static final Identifier RANGED_WEAPON_CRAFTED_ID = Registry.register(
            BuiltInRegistries.CUSTOM_STAT,
            Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "ranged_weapon_crafted"),
            Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "ranged_weapon_crafted")
    );

    public static Stat<Identifier> rangedWeaponCrafted() {
        return Stats.CUSTOM.get(RANGED_WEAPON_CRAFTED_ID);
    }
}