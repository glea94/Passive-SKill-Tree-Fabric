package daripher.skilltree.event;

import daripher.skilltree.util.event.PSTEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

/**
 * Équivalent de net.minecraftforge.event.entity.living.MobEffectEvent.Applicable. Reproduit le
 * système Result de Forge (ALLOW/DENY/DEFAULT) : le dernier appel à setResult() qui n'est pas
 * DEFAULT l'emporte, comme côté Forge.
 */
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
