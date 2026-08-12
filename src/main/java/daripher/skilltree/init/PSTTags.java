package daripher.skilltree.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;

public class PSTTags {
    public static class DamageTypes {
        // TODO (étape events/dégâts) : à l'origine "forge:is_magic". Sous Fabric 1.20.1, il n'existe
        // pas encore de tag conventionnel unifié pour "dégâts magiques" (l'unification des tags
        // communs forge/fabric sous le namespace "c:" n'arrive qu'en 1.21+). Deux options réelles :
        // 1) garder un tag propre au mod ("skilltree:is_magic") et laisser les packs de données/
        //    autres mods compatibles l'y ajouter, 2) vérifier si Fabric API expose un
        //    ConventionalDamageTypeTags équivalent au moment du portage des events de dégâts.
        // Décision à prendre à l'étape "events Forge -> mixins/Fabric API", pas ici.
        public static final TagKey<DamageType> IS_MAGIC = TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("skilltree", "is_magic"));
    }

    public static class Items {
        // TODO (étape compat Curios -> Trinkets) : ces 3 tags ("curios:ring", "curios:necklace",
        // "forge:curios/jewelry") correspondaient au système de tags d'items de Curios. Trinkets
        // ne détecte pas ses emplacements de la même façon (slots définis par JSON dans
        // data/trinkets/..., pas par tag générique d'item) : la vraie logique équivalente sera
        // écrite à l'étape de réécriture de la compat accessoires, pas ici. Les TagKey ci-dessous
        // sont conservés tels quels pour l'instant (juste des identifiants, sans effet avant
        // d'être branchés à un système) afin de ne rien casser dans le reste du code qui les
        // référence déjà.
        public static final TagKey<Item> RINGS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("curios", "ring"));
        public static final TagKey<Item> NECKLACES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("curios", "necklace"));
        public static final TagKey<Item> JEWELRY = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("forge", "curios/jewelry"));
        public static final TagKey<Item> MELEE_WEAPON = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("skilltree", "melee_weapon"));
        public static final TagKey<Item> RANGED_WEAPON = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("skilltree", "ranged_weapon"));
        public static final TagKey<Item> LEATHER_ARMOR = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("skilltree", "armors/leather"));
    }
}
