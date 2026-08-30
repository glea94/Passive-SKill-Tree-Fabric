package daripher.skilltree.init;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;
public class PSTTags {
    public static class DamageTypes {
        public static final TagKey<DamageType> IS_MAGIC = TagKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath("c", "is_magic"));
    }
    public static class Items {
        public static final TagKey<Item> RINGS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "rings"));
        public static final TagKey<Item> NECKLACES = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "necklaces"));
        public static final TagKey<Item> JEWELRY = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "jewelry"));
        public static final TagKey<Item> MELEE_WEAPON = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("skilltree", "melee_weapon"));
        public static final TagKey<Item> RANGED_WEAPON = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("skilltree", "ranged_weapon"));
        public static final TagKey<Item> LEATHER_ARMOR = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("skilltree", "armors/leather"));
    }
}
