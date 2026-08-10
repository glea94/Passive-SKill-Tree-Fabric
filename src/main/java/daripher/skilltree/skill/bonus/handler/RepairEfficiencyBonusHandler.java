package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.event.AnvilUpdatePSTEvent;
import daripher.skilltree.event.PSTEvents;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.RepairEfficiencyBonus;
import daripher.skilltree.util.event.EventPriority;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Portage Fabric : logique interne prête (branchée sur PSTEvents.ANVIL_UPDATE, déjà défini),
 * MAIS l'event ne se déclenche pas encore : le mixin sur AnvilMenu qui doit le déclencher est
 * en attente (menu Forge complexe, cf. étape 8 - pas assez de certitude sans jar décompilé pour
 * cibler la bonne méthode sans risque). Ce handler ne fera donc rien tant que ce mixin n'existe
 * pas, mais compile et s'enregistre proprement, prêt à fonctionner dès que l'event sera câblé.
 * <p>
 * NOTE DE MISE À JOUR 1.21.4 : Une fois 'genSources' exécuté, l'implémentation propre se fera
 * via un @Mixin ciblant la méthode updateResult() d'AnvilMenu pour déclencher l'event maison.
 */
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
