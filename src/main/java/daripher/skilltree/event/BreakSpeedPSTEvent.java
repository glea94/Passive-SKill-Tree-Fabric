package daripher.skilltree.event;

import daripher.skilltree.util.event.PSTEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;


public class BreakSpeedPSTEvent extends PSTEvent {
    private final Player player;
    private final BlockState state;
    private float newSpeed;

    public BreakSpeedPSTEvent(Player player, BlockState state, float originalSpeed) {
        this.player = player;
        this.state = state;
        this.newSpeed = originalSpeed;
    }

    public Player getEntity() {
        return player;
    }

    public BlockState getState() {
        return state;
    }

    public float getNewSpeed() {
        return newSpeed;
    }

    public void setNewSpeed(float newSpeed) {
        this.newSpeed = newSpeed;
    }
}
