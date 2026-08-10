package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.TickingSkillBonus;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

/**
 * Portage Fabric : TickEvent.PlayerTickEvent n'existe pas côté Fabric API (pas d'event par
 * joueur dédié). Technique standard : ServerTickEvents.END_SERVER_TICK, en itérant la liste des
 * joueurs connectés à chaque tick - fonctionnellement identique (s'exécute une fois par joueur
 * et par tick, comme l'original).
 */
public class TickingSkillBonusHandler {
    public static void register() {
        // Aligned 1.21.4: Continuously loops player rosters on the server main thread to execute ticking skill multipliers
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                tickSkillBonuses(player);
            }
        });
    }

    private static void tickSkillBonuses(ServerPlayer player) {
        if (player.isDeadOrDying()) {
            return;
        }
        List<TickingSkillBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, TickingSkillBonus.class);
        if (skillBonuses.isEmpty()) {
            return;
        }
        for (TickingSkillBonus bonus : skillBonuses) {
            bonus.tick(player);
        }
    }
}
