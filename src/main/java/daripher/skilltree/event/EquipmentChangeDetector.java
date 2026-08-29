package daripher.skilltree.event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
public class EquipmentChangeDetector {
    private static final Map<UUID, EnumMap<EquipmentSlot, ItemStack>> lastKnownEquipment = new WeakHashMap<>();
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                checkEquipmentChanges(player);
            }
        });
    }
    private static void checkEquipmentChanges(ServerPlayer player) {
        EnumMap<EquipmentSlot, ItemStack> previous = lastKnownEquipment.computeIfAbsent(player.getUUID(), id -> new EnumMap<>(EquipmentSlot.class));
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack current = player.getItemBySlot(slot);
            ItemStack before = previous.getOrDefault(slot, ItemStack.EMPTY);
<<<<<<< Updated upstream


=======
>>>>>>> Stashed changes
            if (!ItemStack.isSameItemSameComponents(before, current)) {
                previous.put(slot, current.copy());
                PSTEvents.LIVING_EQUIPMENT_CHANGE.post(new LivingEquipmentChangePSTEvent(player, slot, before, current));
            }
        }
    }
}
