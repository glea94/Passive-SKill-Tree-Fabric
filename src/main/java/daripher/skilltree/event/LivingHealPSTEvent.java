package daripher.skilltree.event;

import daripher.skilltree.util.event.PSTEvent;
import net.minecraft.world.entity.LivingEntity;


public class LivingHealPSTEvent extends PSTEvent {
    private final LivingEntity entity;
    private float amount;

    public LivingHealPSTEvent(LivingEntity entity, float amount) {
        this.entity = entity;
        this.amount = amount;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}
