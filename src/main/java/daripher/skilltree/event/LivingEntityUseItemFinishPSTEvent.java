package daripher.skilltree.event;

import daripher.skilltree.util.event.PSTEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Équivalent de net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Finish.
 * Se déclenche quand une LivingEntity termine l'utilisation d'un item (manger, boire une potion,
 * tirer à l'arc jusqu'au bout...). Utilisé notamment pour les bonus de compétence "chance
 * d'obtenir un effet en mangeant" (ItemUseEventListener).
 */
public class LivingEntityUseItemFinishPSTEvent extends PSTEvent {
    private final LivingEntity entity;
    private final ItemStack item;

    public LivingEntityUseItemFinishPSTEvent(LivingEntity entity, ItemStack item) {
        this.entity = entity;
        this.item = item;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public ItemStack getItem() {
        return item;
    }
}
