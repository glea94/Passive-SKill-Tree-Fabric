package daripher.skilltree.event;

import daripher.skilltree.util.event.PSTEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;


public class AnvilUpdatePSTEvent extends PSTEvent {
    private final Player player;
    private final ItemStack left;
    private final ItemStack right;
    private ItemStack output;

    public AnvilUpdatePSTEvent(Player player, ItemStack left, ItemStack right, ItemStack output) {
        this.player = player;
        this.left = left;
        this.right = right;
        this.output = output;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getLeft() {
        return left;
    }

    public ItemStack getRight() {
        return right;
    }

    public ItemStack getOutput() {
        return output;
    }

    public void setOutput(ItemStack output) {

        this.output = output != null ? output : ItemStack.EMPTY;
    }
}
