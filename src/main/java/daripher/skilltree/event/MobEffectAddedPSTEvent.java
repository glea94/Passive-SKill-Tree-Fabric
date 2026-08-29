package daripher.skilltree.event;
import daripher.skilltree.util.event.PSTEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
public class MobEffectAddedPSTEvent extends PSTEvent {
    private final LivingEntity entity;
    private final MobEffectInstance effectInstance;
    private final Entity effectSource;
    public MobEffectAddedPSTEvent(LivingEntity entity, MobEffectInstance effectInstance, Entity effectSource) {
        this.entity = entity;
        this.effectInstance = effectInstance;
        this.effectSource = effectSource;
    }
    public LivingEntity getEntity() {
        return entity;
    }
    public MobEffectInstance getEffectInstance() {
        return effectInstance;
    }
    public Entity getEffectSource() {
        return effectSource;
    }
}