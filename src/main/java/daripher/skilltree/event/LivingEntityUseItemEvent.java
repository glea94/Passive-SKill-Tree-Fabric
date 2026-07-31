package daripher.skilltree.event;

// Ajout de l'import de la classe de base
import daripher.skilltree.util.event.PSTEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

// Extension de la classe PSTEvent pour corriger les types génériques de Gradle
public class LivingEntityUseItemEvent extends PSTEvent {
    private final LivingEntity entity;
    private final ItemStack itemStack;

    public LivingEntityUseItemEvent(LivingEntity entity, ItemStack itemStack) {
        this.entity = entity;
        this.itemStack = itemStack;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
