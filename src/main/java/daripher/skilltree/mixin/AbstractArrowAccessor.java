package daripher.skilltree.mixin;

import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractArrow.class)
public interface AbstractArrowAccessor {
    // Should never be null. Some mods still return null.
    @Nullable
    @Invoker("getPickupItem")
    ItemStack invokeGetPickupItem();
}
