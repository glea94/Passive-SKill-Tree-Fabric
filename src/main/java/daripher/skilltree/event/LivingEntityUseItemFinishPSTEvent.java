package daripher.skilltree.event;

import daripher.skilltree.util.event.PSTEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;


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
