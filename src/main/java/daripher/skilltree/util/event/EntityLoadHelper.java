package daripher.skilltree.util.event;

import net.minecraft.world.entity.Entity;

/**
 * Portage Fabric de net.minecraftforge.event.entity.EntityJoinLevelEvent.loadedFromDisk().
 * Fabric API (ServerEntityEvents.ENTITY_LOAD) ne fournit pas cette distinction nativement.
 * Heuristique utilisée à la place, standard dans l'écosystème Fabric pour ce cas précis : une
 * entité qui vient d'être créée (projectile tiré, etc.) a un tickCount de 0 au moment où elle
 * rejoint le niveau ; une entité rechargée depuis une sauvegarde de chunk a un tickCount > 0
 * (repris de son NBT). À VÉRIFIER EN JEU : cette heuristique est correcte dans l'immense
 * majorité des cas mais n'est pas une garantie à 100% aussi stricte que le flag Forge dédié.
 */
public class EntityLoadHelper {
    public static boolean isFreshlySpawned(Entity entity) {
        return entity.tickCount == 0;
    }
}
