package daripher.skilltree.mixin;
import daripher.skilltree.skill.bonus.handler.ItemDurabilityLossPreventionBonusHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(ItemStack.class)
public abstract class ItemDurabilityMixin {
    @Inject(method = "processDurabilityChange", at = @At("HEAD"), cancellable = true, require = 1)
    private void skilltree$preventDurabilityLoss(int amount, ServerLevel level, ServerPlayer player, CallbackInfoReturnable<Integer> cir) {
        if (amount <= 0 || player == null) {
            return;
        }
        ItemStack self = (ItemStack) (Object) this;
        if (!self.isDamageableItem()) {
            return;
        }
        if (ItemDurabilityLossPreventionBonusHandler.shouldPreventItemDurabilityLoss(player, self, player.getRandom())) {
            cir.setReturnValue(0);
        }
    }
}