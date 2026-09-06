package daripher.skilltree.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;

public class PSTTags {
    public static class DamageTypes {

        public static final TagKey<DamageType> IS_MAGIC = TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("skilltree", "is_magic"));
    }

    public static class Items {

        public static final TagKey<Item> RINGS = TagKey.create(Registries.ITEM, new ResourceLocation("curios", "ring"));
        public static final TagKey<Item> NECKLACES = TagKey.create(Registries.ITEM, new ResourceLocation("curios", "necklace"));
        public static final TagKey<Item> JEWELRY = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "curios/jewelry"));
        public static final TagKey<Item> MELEE_WEAPON = TagKey.create(Registries.ITEM, new ResourceLocation("skilltree", "melee_weapon"));
        public static final TagKey<Item> RANGED_WEAPON = TagKey.create(Registries.ITEM, new ResourceLocation("skilltree", "ranged_weapon"));
        public static final TagKey<Item> LEATHER_ARMOR = TagKey.create(Registries.ITEM, new ResourceLocation("skilltree", "armors/leather"));
    }
}
