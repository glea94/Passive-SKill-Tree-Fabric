package daripher.skilltree.skill.bonus.handler;
import daripher.skilltree.event.ItemTooltipPSTEvent;
import daripher.skilltree.event.LivingEquipmentChangePSTEvent;
import daripher.skilltree.event.PSTEvents;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.PreventItemUsageBonus;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import java.util.List;
public class ItemUsagePreventionBonusHandler {
    private static boolean isProcessingRejection;
    public static void register() {
        AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            ItemStack mainHandItem = player.getMainHandItem();
            if (shouldPreventItemUsage(player, mainHandItem)) {
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });
        UseItemCallback.EVENT.register((player, level, hand) -> {
            ItemStack itemStack = player.getItemInHand(hand);
            if (shouldPreventItemUsage(player, itemStack)) {
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });
        PSTEvents.LIVING_EQUIPMENT_CHANGE.register(ItemUsagePreventionBonusHandler::preventItemEquipping);
        PSTEvents.ITEM_TOOLTIP.register(ItemUsagePreventionBonusHandler::addPreventedUsageTooltip);
    }
    private static void preventItemEquipping(LivingEquipmentChangePSTEvent event) {
        if (isProcessingRejection) {
            return;
        }
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (!event.getSlot().isArmor()) {
            return;
        }
        ItemStack newArmor = event.getTo();
        if (newArmor.isEmpty()) {
            return;
        }
        if (shouldPreventItemUsage(player, newArmor)) {
            try {
                isProcessingRejection = true;
                if (!player.getInventory().add(newArmor.copy())) {
                    player.drop(newArmor.copy(), false);
                }
                player.setItemSlot(event.getSlot(), ItemStack.EMPTY);
            } finally {
                isProcessingRejection = false;
            }
        }
    }
    private static void addPreventedUsageTooltip(ItemTooltipPSTEvent event) {
        Player player = event.getEntity();
        if (player == null) {
            return;
        }
        ItemStack itemStack = event.getItemStack();
        if (shouldPreventItemUsage(player, itemStack)) {
            Component tooltip = Component.translatable("item.cant_use.info").withStyle(ChatFormatting.RED);
            event.getToolTip().add(tooltip);
        }
    }
    public static boolean shouldPreventItemUsage(Player player, ItemStack itemStack) {
        List<PreventItemUsageBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, PreventItemUsageBonus.class);
        for (PreventItemUsageBonus bonus : skillBonuses) {
            if (bonus.getItemCondition().test(itemStack)) {
                return true;
            }
        }
        return false;
    }
}
