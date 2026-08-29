package daripher.skilltree.util.event;
import net.minecraft.world.entity.Entity;
public class EntityLoadHelper {
    public static boolean isFreshlySpawned(Entity entity) {
        return entity.tickCount == 0;
    }
}
