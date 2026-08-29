package daripher.skilltree.event;
import daripher.skilltree.util.event.PSTEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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
    public void modifyVisibility(double mod) {
        visibilityModifier *= mod;
    }
    public double getVisibilityModifier() {
        return visibilityModifier;
    }
}
