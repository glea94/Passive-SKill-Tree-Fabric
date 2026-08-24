package daripher.skilltree.mixin;

import daripher.skilltree.event.BreakSpeedPSTEvent;
import daripher.skilltree.event.PSTEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerBreakSpeedMixin {
    @Inject(method = "getDestroySpeed", at = @At("RETURN"), cancellable = true, require = 1)
    private void skilltree$onGetDestroySpeed(BlockState state, CallbackInfoReturnable<Float> cir) {
        Player self = (Player) (Object) this;

        BreakSpeedPSTEvent event = new BreakSpeedPSTEvent(self, state, cir.getReturnValue());
        PSTEvents.BREAK_SPEED.post(event);

        if (event.getNewSpeed() != cir.getReturnValue()) {
            cir.setReturnValue(event.getNewSpeed());
        }
    }
}
