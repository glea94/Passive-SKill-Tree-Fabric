package daripher.skilltree.event;

import daripher.skilltree.util.event.PSTEvent;
import net.minecraft.world.entity.LivingEntity;


public class LivingFallPSTEvent extends PSTEvent {
    private final LivingEntity entity;
    private float distance;

    public LivingFallPSTEvent(LivingEntity entity, float distance) {
        this.entity = entity;
        this.distance = distance;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}
