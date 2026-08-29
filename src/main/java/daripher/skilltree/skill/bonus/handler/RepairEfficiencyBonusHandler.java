package daripher.skilltree.skill.bonus.handler;
import daripher.skilltree.event.AnvilUpdatePSTEvent;
import daripher.skilltree.event.PSTEvents;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.RepairEfficiencyBonus;
import daripher.skilltree.util.event.EventPriority;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import java.util.List;
public class RepairEfficiencyBonusHandler {
    public static void register() {
        PSTEvents.ANVIL_UPDATE.register(EventPriority.HIGH, RepairEfficiencyBonusHandler::applyRepairEfficiency);
    }
    private static void applyRepairEfficiency(AnvilUpdatePSTEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        ItemStack resultItem = event.getOutput();
        if (resultItem.isEmpty() || !resultItem.isDamageableItem()) {
            return;
        }
        ItemStack baseItem = event.getLeft();
        if (baseItem.getItem() != resultItem.getItem()) {
            return;
        }
        int vanillaDurabilityRestored = baseItem.getDamageValue() - resultItem.getDamageValue();
        if (vanillaDurabilityRestored <= 0) {
            return;
        }
        List<RepairEfficiencyBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, RepairEfficiencyBonus.class);
        if (skillBonuses.isEmpty()) {
            return;
        }
        float efficiencyBonus = 0f;
        for (RepairEfficiencyBonus bonus : skillBonuses) {
            efficiencyBonus += bonus.getRepairEfficiencyMultiplier(baseItem);
        }
        if (efficiencyBonus <= 0f) {
            return;
        }
        int totalDurabilityToRestore = (int) (vanillaDurabilityRestored * (1f + efficiencyBonus));
        int currentItemDamage = baseItem.getDamageValue();
        resultItem.setDamageValue(Math.max(0, currentItemDamage - totalDurabilityToRestore));
        event.setOutput(resultItem);
    }
}
