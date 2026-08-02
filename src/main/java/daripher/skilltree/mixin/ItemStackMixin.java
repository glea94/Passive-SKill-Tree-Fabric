package daripher.skilltree.mixin;

import daripher.skilltree.event.ItemTooltipPSTEvent;
import daripher.skilltree.event.PSTEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Portage Fabric de net.minecraftforge.event.entity.player.ItemTooltipEvent, mis à jour pour la 1.21.1.
 * Cible ItemStack.getTooltipLines(Item.TooltipContext, Player, TooltipFlag) qui intègre désormais
 * le contexte des registres.
 */
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(method = "getTooltipLines", at = @At("RETURN"), require = 1)
    private void skilltree$onGetTooltipLines(Item.TooltipContext context, Player player, TooltipFlag flags, CallbackInfoReturnable<List<Component>> cir) {
        ItemStack self = (ItemStack) (Object) this;
        ItemTooltipPSTEvent event = new ItemTooltipPSTEvent(self, cir.getReturnValue(), player, flags);
        PSTEvents.ITEM_TOOLTIP.post(event);
    }
}
