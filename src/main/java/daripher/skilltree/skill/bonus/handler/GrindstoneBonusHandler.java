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
 * La cible vanilla probable est une classe anonyme interne à GrindstoneMenu (le Slot de résultat
 * qui donne l'XP à la reprise de l'objet), ce qui rend le ciblage par mixin risqué sans jar
 * décompilé (nom de classe anonyme généré par le compilateur, pas un nom stable). À traiter une
 * fois genSources lancé dans IntelliJ - jusque-là, l'XP de meule n'est simplement pas
 * multipliée par ce bonus, sans que rien d'autre ne soit affecté.
 */
public class GrindstoneBonusHandler {
    public static void register() {
        // en attente, voir le commentaire de classe
    }
}
