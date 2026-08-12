package daripher.skilltree.skill.bonus.handler;

/**
 * Portage Fabric : EN ATTENTE, pas un stub silencieux - documenté ici.
 * MovementInputUpdateEvent (Forge, client-only) n'a pas d'équivalent Fabric API direct.
 * Nécessite d'intercepter la lecture des touches de mouvement côté client (mixin sur
 * Options/KeyboardInput ou LocalPlayer.aiStep) pour appliquer le multiplicateur, comme le
 * faisait event.getInput() côté Forge. Pas encore écrit ici : à traiter avec le reste des events
 * client (voir aussi ItemUsageSpeedBonusHandler).
 * <p>
 * NOTE DE MISE À JOUR 1.21.4 : Une fois 'genSources' exécuté, l'implémentation client propre
 * se fera via un @Mixin ciblant la méthode aiStep() de LocalPlayer pour ajuster les vecteurs
 * de déplacement xClamped/zClamped lorsque le joueur utilise un objet.
 */
public class ItemUseMovementSpeedBonusHandler {
    public static void register() {
        // En attente de l'implémentation du Mixin client après genSources.
    }
}
