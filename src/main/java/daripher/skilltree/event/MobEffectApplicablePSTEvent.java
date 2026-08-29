package daripher.skilltree.event;
import daripher.skilltree.util.event.PSTEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
public class MobEffectApplicablePSTEvent extends PSTEvent {
    public enum Result { ALLOW, DEFAULT, DENY }
    private final LivingEntity entity;
    private final MobEffectInstance effectInstance;
    private Result result = Result.DEFAULT;
    public MobEffectApplicablePSTEvent(LivingEntity entity, MobEffectInstance effectInstance) {
        this.entity = entity;
        this.effectInstance = effectInstance;
    }
    public LivingEntity getEntity() {
        return entity;
    }
    public MobEffectInstance getEffectInstance() {
        return effectInstance;
    }
    public void setResult(Result result) {
        this.result = result;
    }
    public Result getResult() {
        return result;
    }
}
