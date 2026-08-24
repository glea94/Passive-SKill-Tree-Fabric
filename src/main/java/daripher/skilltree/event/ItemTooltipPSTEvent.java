package daripher.skilltree.event;

import daripher.skilltree.util.event.PSTEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class ItemTooltipPSTEvent extends PSTEvent {
    private final ItemStack itemStack;
    private final List<Component> tooltip;
    private final @Nullable Player player;
    private final TooltipFlag flags;

    public ItemTooltipPSTEvent(ItemStack itemStack, List<Component> tooltip, @Nullable Player player, TooltipFlag flags) {
        this.itemStack = itemStack;
        this.tooltip = tooltip;
        this.player = player;
        this.flags = flags;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public List<Component> getToolTip() {
        return tooltip;
    }

    public @Nullable Player getEntity() {
        return player;
    }

    public TooltipFlag getFlags() {
        return flags;
    }
}
