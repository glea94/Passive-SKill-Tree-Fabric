package daripher.skilltree.mixin;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(AbstractWidget.class)
public interface AbstractWidgetAccessor {
    @Accessor("width")
    void setWidth(int width);
    @Accessor("height")
    void setHeight(int height);
}
