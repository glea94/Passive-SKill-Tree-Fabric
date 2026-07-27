package daripher.skilltree.event;

import daripher.skilltree.util.event.PSTEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/** Équivalent de net.minecraftforge.event.entity.living.LivingEvent.LivingVisibilityEvent. */
public class LivingVisibilityPSTEvent extends PSTEvent {
    private final LivingEntity entity;
    private final Entity lookingEntity;
    private double visibilityModifier = 1.0;

    public LivingVisibilityPSTEvent(LivingEntity entity, Entity lookingEntity) {
        this.entity = entity;
        this.lookingEntity = lookingEntity;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public Entity getLookingEntity() {
        return lookingEntity;
    }

    /** Même comportement que Forge : multiplie (n'écrase pas) le modificateur cumulé. */
    public void modifyVisibility(double mod) {
        visibilityModifier *= mod;
    }

    public double getVisibilityModifier() {
        return visibilityModifier;
    }
}
