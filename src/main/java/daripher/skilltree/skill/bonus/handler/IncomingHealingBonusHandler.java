package daripher.skilltree.skill.bonus.handler;
import daripher.skilltree.event.LivingHealPSTEvent;
import daripher.skilltree.event.PSTEvents;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.IncomingHealingBonus;
import net.minecraft.world.entity.player.Player;
import java.util.List;
public class IncomingHealingBonusHandler {
    public static void register() {
        PSTEvents.LIVING_HEAL.register(IncomingHealingBonusHandler::modifyIncomingHealing);
    }
    private static void modifyIncomingHealing(LivingHealPSTEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        List<IncomingHealingBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, IncomingHealingBonus.class);
        if (skillBonuses.isEmpty()) {
            return;
        }
        float multiplier = 1f;
        for (IncomingHealingBonus bonus : skillBonuses) {
            multiplier += bonus.getHealingMultiplier(player);
        }
        event.setAmount(event.getAmount() * multiplier);
    }
}
