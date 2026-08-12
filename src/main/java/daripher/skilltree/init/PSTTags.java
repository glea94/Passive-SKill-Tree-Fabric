package daripher.skilltree.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;

public class PSTTags {
    public static class DamageTypes {
        // Factual Fix 1.21.4: Map magic damage using the unified community convention namespace ('c')
        public static final TagKey<DamageType> IS_MAGIC = TagKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath("c", "is_magic"));
    }

    public static class Items {
        // Factual Fix 1.21.4: Map accessories to standard unified cross-loader tags ('c:rings', 'c:necklaces')
        public static final TagKey<Item> RINGS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "rings"));
        public static final TagKey<Item> NECKLACES = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "necklaces"));
        public static final TagKey<Item> JEWELRY = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "jewelry"));

        // Mod specific classification tags remains under the local mod namespace
        public static final TagKey<Item> MELEE_WEAPON = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("skilltree", "melee_weapon"));
        public static final TagKey<Item> RANGED_WEAPON = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("skilltree", "ranged_weapon"));
        public static final TagKey<Item> LEATHER_ARMOR = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("skilltree", "armors/leather"));
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
    }
}
