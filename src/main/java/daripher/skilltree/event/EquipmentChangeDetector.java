package daripher.skilltree.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Portage Fabric de net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent, sans
 * équivalent Fabric API ni point d'injection vanilla unique (Forge lui-même le détecte par
 * comparaison à chaque tick en interne, pas via un event vanilla natif - il n'y a donc pas de
 * perte de fidélité à utiliser la même technique ici).
 * <p>
 * Portée volontairement limitée aux joueurs (pas toutes les LivingEntity comme Forge) : vérifié
 * dans le code d'origine du mod, les deux seuls usages (ItemBonusHandler, ItemUsagePreventionBonusHandler)
 * ne traitent que des instances Player. Élargir à toutes les entités vivantes serait possible si
 * un besoin apparaît plus tard (même technique, itérer les entités au lieu des joueurs).
 */
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
            if (!ItemStack.matches(before, current)) {
                previous.put(slot, current.copy());
                PSTEvents.LIVING_EQUIPMENT_CHANGE.post(new LivingEquipmentChangePSTEvent(player, slot, before, current));
            }
        }
    }
}
