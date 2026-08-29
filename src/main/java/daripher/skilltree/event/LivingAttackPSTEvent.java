package daripher.skilltree.event;
import daripher.skilltree.util.event.PSTEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
public class LivingAttackPSTEvent extends PSTEvent {
    private final LivingEntity entity;
    private final DamageSource source;
    private final float amount;
    public LivingAttackPSTEvent(LivingEntity entity, DamageSource source, float amount) {
        this.entity = entity;
        this.source = source;
        this.amount = amount;
    }
    public LivingEntity getEntity() {
        return entity;
    }
    public DamageSource getSource() {
        return source;
    }
    public float getAmount() {
        return amount;
    }
}
