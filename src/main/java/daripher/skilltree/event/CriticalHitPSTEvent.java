package daripher.skilltree.event;
import daripher.skilltree.util.event.PSTEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
public class CriticalHitPSTEvent extends PSTEvent {
    private final Player player;
    private final Entity target;
    private final boolean vanillaCritical;
    private boolean forcedCrit;
    private float damageMultiplier = 1.5f;
    public CriticalHitPSTEvent(Player player, Entity target, boolean vanillaCritical) {
        this.player = player;
        this.target = target;
        this.vanillaCritical = vanillaCritical;
    }
    public Player getEntity() {
        return player;
    }
    public Entity getTarget() {
        return target;
    }
    public boolean isVanillaCritical() {
        return vanillaCritical;
    }
    public boolean isForcedCrit() {
        return forcedCrit;
    }
    public void setForcedCrit(boolean forcedCrit) {
        this.forcedCrit = forcedCrit;
    }
    public float getDamageMultiplier() {
        return damageMultiplier;
    }
    public void setDamageMultiplier(float damageMultiplier) {
        this.damageMultiplier = damageMultiplier;
    }
    public boolean isCrit() {
        return vanillaCritical || forcedCrit;
    }
}
