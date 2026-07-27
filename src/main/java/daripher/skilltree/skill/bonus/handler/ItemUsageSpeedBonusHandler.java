package daripher.skilltree.skill.bonus.handler;

/**
 * Portage Fabric : EN ATTENTE, pas un stub silencieux - documenté ici.
 * LivingEntityUseItemEvent.Tick (Forge) n'a pas d'équivalent Fabric API direct. Nécessite un
 * mixin sur LivingEntity.tickUsingItem() (méthode vanilla qui décrémente le compteur d'usage
 * d'un item en cours d'utilisation - potion, arc, etc.), pour modifier la durée restante comme
 * le faisait event.setDuration() côté Forge. Pas encore écrit ici : à traiter avec le reste des
 * events "usage d'item" du tableau de suivi (voir aussi ItemUseMovementSpeedBonusHandler).
 */
public class ItemUsageSpeedBonusHandler {
    public static void register() {
        // en attente, voir le commentaire de classe
    }
}
