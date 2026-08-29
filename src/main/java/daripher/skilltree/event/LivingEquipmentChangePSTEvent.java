package daripher.skilltree.event;
import daripher.skilltree.util.event.PSTEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
public class LivingEquipmentChangePSTEvent extends PSTEvent {
    private final LivingEntity entity;
    private final EquipmentSlot slot;
    private final ItemStack from;
    private final ItemStack to;
    public LivingEquipmentChangePSTEvent(LivingEntity entity, EquipmentSlot slot, ItemStack from, ItemStack to) {
        this.entity = entity;
        this.slot = slot;
        this.from = from;
        this.to = to;
    }
    public LivingEntity getEntity() {
        return entity;
    }
    public EquipmentSlot getSlot() {
        return slot;
    }
    public ItemStack getFrom() {
        return from;
    }
    public ItemStack getTo() {
        return to;
    }
}
