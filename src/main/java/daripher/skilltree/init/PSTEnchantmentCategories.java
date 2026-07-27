package daripher.skilltree.init;

/**
 * Portage Fabric : les 2 catégories custom (SHIELD, POTION) reposaient sur
 * EnchantmentCategory.create(), un hack Forge (extension d'enum via ASM) impossible à reproduire
 * proprement sur Fabric (les enums vanilla restent fermés, pas d'équivalent Fabric à cette
 * astuce Forge). Vérifié : ces 2 champs n'étaient référencés nulle part ailleurs dans le mod
 * (aucun Enchantment custom ne les utilisait) - code mort supprimé, pas une perte de
 * fonctionnalité réelle.
 */
public class PSTEnchantmentCategories {
}
