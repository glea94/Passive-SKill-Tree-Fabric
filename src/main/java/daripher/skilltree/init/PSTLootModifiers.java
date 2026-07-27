package daripher.skilltree.init;

/**
 * Portage Fabric : EN ATTENTE, pas un stub silencieux - documenté ici.
 * <p>
 * Tentative initiale : LootTableEvents.MODIFY_DROPS (Fabric Loot API v2). Vérifié précisément
 * sur la doc officielle fabric-api 0.85.0+1.20.1 (notre version cible exacte) : cet event
 * n'existe PAS à cette époque de Fabric API - seuls MODIFY et REPLACE existent, qui modifient la
 * STRUCTURE d'une LootTable au chargement, sans accès au LootContext (joueur qui tue/pêche/etc.)
 * au moment de la génération du butin. MODIFY_DROPS n'apparaît que dans des versions de Fabric
 * API bien plus tardives (~0.129+, pour des Minecraft ultérieurs).
 * <p>
 * Équivalent réel nécessaire : un mixin sur LootTable (probablement getRandomItemsRaw ou
 * équivalent), à écrire avec le jar décompilé pour cibler la bonne méthode/signature avec
 * certitude plutôt que de deviner un point d'injection aussi sensible (calcul de butin).
 * <p>
 * Jusque-là, le bonus de compétence "LootAmountModifierBonus" (butin supplémentaire à la mort
 * d'un mob/pêche/coffre/minerai) n'a aucun effet - le reste du mod n'est pas affecté.
 */
public class PSTLootModifiers {
    public static void register() {
        // en attente, voir le commentaire de classe
    }
}
