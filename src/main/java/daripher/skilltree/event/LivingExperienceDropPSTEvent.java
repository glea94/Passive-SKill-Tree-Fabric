package daripher.skilltree.event;

import daripher.skilltree.util.event.PSTEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;


public class LivingExperienceDropPSTEvent extends PSTEvent {
    private final LivingEntity entity;
    private final Player attackingPlayer;
    private int droppedExperience;

    public LivingExperienceDropPSTEvent(LivingEntity entity, @Nullable Player attackingPlayer, int droppedExperience) {
        this.entity = entity;
        this.attackingPlayer = attackingPlayer;
        this.droppedExperience = droppedExperience;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public @Nullable Player getAttackingPlayer() {
        return attackingPlayer;
    }

    public int getDroppedExperience() {
        return droppedExperience;
    }

    public void setDroppedExperience(int droppedExperience) {
        this.droppedExperience = droppedExperience;
    }
}
