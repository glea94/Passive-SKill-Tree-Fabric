package daripher.skilltree.skill.bonus.handler;
import daripher.skilltree.event.LivingHealPSTEvent;
import daripher.skilltree.event.PSTEvents;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.HealthReservationBonus;
import daripher.skilltree.util.event.EventPriority;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import java.util.List;
public class HealthReservationBonusHandler {
    public static void register() {
<<<<<<< Updated upstream

=======
>>>>>>> Stashed changes
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                applyHealthReservation(player);
            }
        });
        PSTEvents.LIVING_HEAL.register(EventPriority.LOWEST, HealthReservationBonusHandler::preventReservedHealthHealing);
    }
    private static void applyHealthReservation(Player player) {
        float reservation = getHealthReservation(player);
        if (reservation == 0) {
            return;
        }
        float maxHealth = player.getMaxHealth();
        float currentHealth = player.getHealth();
        float maxAllowedHealth = maxHealth * (1f - reservation);
        if (currentHealth > maxAllowedHealth) {
            player.setHealth(maxAllowedHealth);
        }
    }
    private static void preventReservedHealthHealing(LivingHealPSTEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        float reservation = getHealthReservation(player);
        if (reservation == 0) {
            return;
        }
        float maxHealth = player.getMaxHealth();
        float currentHealth = player.getHealth();
        float maxAllowedHealth = maxHealth * (1f - reservation);
        if (currentHealth > maxAllowedHealth) {
            event.setCanceled(true);
            return;
        }
        if (currentHealth + event.getAmount() > maxAllowedHealth) {
            float maxAllowedHealing = maxAllowedHealth - currentHealth;
            event.setAmount(maxAllowedHealing);
        }
    }
    private static float getHealthReservation(Player player) {
        float reservation = 0f;
        List<HealthReservationBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, HealthReservationBonus.class);
        for (HealthReservationBonus bonus : skillBonuses) {
            reservation += bonus.getAmount(player);
        }
        return reservation;
    }
}
