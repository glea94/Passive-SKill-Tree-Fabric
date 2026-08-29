package daripher.skilltree.mixin;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
@Mixin(AbstractArrow.class)
public interface AbstractArrowAccessor {
    @Nullable
    @Invoker("getPickupItem")
    ItemStack invokeGetPickupItem();
    @Accessor("baseDamage")
    double getBaseDamage();
}