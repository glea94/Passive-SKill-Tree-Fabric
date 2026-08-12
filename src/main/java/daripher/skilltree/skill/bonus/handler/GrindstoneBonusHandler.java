package daripher.skilltree.skill.bonus.handler;

/**
 * Portage Fabric de l'ancienne classe daripher.skilltree.event.PSTEvents (Forge), qui ne
 * contenait qu'un seul listener : GrindstoneEvent.OnTakeItem (multiplie l'XP récupérée à la
 * meule par ServerConfig.grindstone_exp_multiplier).
 * <p>
 * Renommée ici pour éviter une collision de nom avec daripher.skilltree.event.PSTEvents, notre
 * propre classe d'infrastructure (registre central des bus d'events maison), déjà largement
 * référencée dans le reste du code porté.
 * <p>
 * VOLONTAIREMENT PAS ENCORE IMPLÉMENTÉ : GrindstoneEvent n'a pas d'équivalent Fabric API direct.
 * La cible vanilla est localisée dans les slots de GrindstoneMenu (le Slot de résultat
 * qui donne l'XP à la reprise de l'objet).
 * <p>
 * NOTE DE MISE À JOUR 1.21.4 : Une fois 'genSources' exécuté, l'implémentation propre se fera
 * via un @Mixin ciblant la méthode d'extraction d'XP du slot de résultat de GrindstoneMenu,
 * en multipliant la valeur de l'expérience par vos modificateurs d'arbre de compétences.
 */
public class GrindstoneBonusHandler {
    public static void register() {
        // En attente de l'implémentation du Mixin sur GrindstoneMenu après genSources.
    }
}
