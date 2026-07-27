package daripher.skilltree.mixin;

import daripher.skilltree.event.ItemTooltipPSTEvent;
import daripher.skilltree.event.PSTEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Portage Fabric de net.minecraftforge.event.entity.player.ItemTooltipEvent, sans équivalent
 * Fabric API direct. Cible ItemStack.getTooltipLines(Player, TooltipFlag), méthode vanilla
 * stable et largement utilisée dans l'écosystème Fabric pour ce cas précis (confiance élevée,
 * contrairement à d'autres mixins de cette session). La liste retournée par vanilla est mutable :
 * on la passe directement à l'event, les listeners y ajoutent des lignes comme avec
 * event.getToolTip().add(...) côté Forge.
 */
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(method = "getTooltipLines", at = @At("RETURN"), require = 1)
    private void skilltree$onGetTooltipLines(Player player, TooltipFlag flags, CallbackInfoReturnable<List<Component>> cir) {
        ItemStack self = (ItemStack) (Object) this;
        ItemTooltipPSTEvent event = new ItemTooltipPSTEvent(self, cir.getReturnValue(), player, flags);
        PSTEvents.ITEM_TOOLTIP.post(event);
    }
}
