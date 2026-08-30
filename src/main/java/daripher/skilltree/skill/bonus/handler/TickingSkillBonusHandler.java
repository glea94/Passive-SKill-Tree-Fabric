package daripher.skilltree.skill.bonus.handler;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.TickingSkillBonus;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;
import java.util.List;
public class TickingSkillBonusHandler {
    public static void register() {
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
