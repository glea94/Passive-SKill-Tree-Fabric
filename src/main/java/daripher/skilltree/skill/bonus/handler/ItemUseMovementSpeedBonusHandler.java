package daripher.skilltree.skill.bonus.handler;

/**
 * Portage Fabric : EN ATTENTE, pas un stub silencieux - documenté ici.
 * MovementInputUpdateEvent (Forge, client-only) n'a pas d'équivalent Fabric API direct.
 * Nécessite d'intercepter la lecture des touches de mouvement côté client (mixin sur
 * Options/KeyboardInput ou LocalPlayer.aiStep) pour appliquer le multiplicateur, comme le
 * faisait event.getInput() côté Forge. Pas encore écrit ici : à traiter avec le reste des events
 * client (voir aussi ItemUsageSpeedBonusHandler).
 */
public class ItemUseMovementSpeedBonusHandler {
    public static void register() {
        // en attente, voir le commentaire de classe
    }
}
