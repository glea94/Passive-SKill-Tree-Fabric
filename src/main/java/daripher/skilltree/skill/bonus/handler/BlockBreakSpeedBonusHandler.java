package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.event.BreakSpeedPSTEvent;
import daripher.skilltree.event.PSTEvents;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.BlockBreakSpeedBonus;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/** Portage Fabric : logique identique, event.setNewSpeed remplace event.setNewSpeed (mêmes noms côté event maison). */
public class BlockBreakSpeedBonusHandler {
    public static void register() {
        PSTEvents.BREAK_SPEED.register(BlockBreakSpeedBonusHandler::modifyBlockBreakSpeed);
    }

    private static void modifyBlockBreakSpeed(BreakSpeedPSTEvent event) {
        Player player = event.getEntity();
        List<BlockBreakSpeedBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, BlockBreakSpeedBonus.class);
        if (skillBonuses.isEmpty()) {
            return;
        }
        float multiplier = 1f;
        for (BlockBreakSpeedBonus bonus : skillBonuses) {
            multiplier += bonus.getMultiplier(player);
        }
        event.setNewSpeed(event.getNewSpeed() * multiplier);
    }
}
