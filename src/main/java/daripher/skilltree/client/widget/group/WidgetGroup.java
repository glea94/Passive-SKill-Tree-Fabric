package daripher.skilltree.client.widget.group;
import daripher.skilltree.client.widget.TickingWidget;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;
public class WidgetGroup<T extends AbstractWidget> extends AbstractWidget implements TickingWidget {
    protected final Set<T> widgets = new HashSet<>();
    protected Runnable rebuildFunc = () -> {
    };
    public WidgetGroup(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }
    @Override
    protected void extractWidgetRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        widgetsCopy().forEach(widget -> widget.extractRenderState(graphics, mouseX, mouseY, partialTick));
        graphics.pose().pushMatrix();
        graphics.pose().translate(0f, 0f);
        graphics.pose().popMatrix();
    }
    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
    }
    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        boolean result = false;
        for (T widget : widgetsCopy()) {
            if (widget.keyPressed(keyEvent)) {
                result = true;
            }
        }
        return result;
    }
    @Override
    public boolean keyReleased(KeyEvent keyEvent) {
        boolean result = false;
        for (T widget : widgetsCopy()) {
            if (widget.keyReleased(keyEvent)) {
                result = true;
            }
        }
        return result;
    }
    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean doubleClick) {
        boolean result = false;
        for (T widget : widgetsCopy()) {
            if (widget.mouseClicked(mouseButtonEvent, doubleClick)) {
                result = true;
            }
        }
        return result;
    }
    @Override
    public boolean mouseDragged(MouseButtonEvent mouseButtonEvent, double dragX, double dragY) {
        boolean result = false;
        for (T widget : widgetsCopy()) {
            if (widget.mouseDragged(mouseButtonEvent, dragX, dragY)) {
                result = true;
            }
        }
        return result;
    }
    @Override
    public boolean mouseReleased(MouseButtonEvent mouseButtonEvent) {
        boolean result = false;
        for (T widget : widgetsCopy()) {
            if (widget.mouseReleased(mouseButtonEvent)) {
                result = true;
            }
        }
        return result;
    }
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        boolean result = false;
        for (T widget : widgetsCopy()) {
            if (widget.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
                result = true;
            }
        }
        return result;
    }
    @Override
    public boolean charTyped(CharacterEvent characterEvent) {
        boolean result = false;
        for (T widget : widgetsCopy()) {
            if (widget.charTyped(characterEvent)) {
                result = true;
            }
        }
        return result;
    }
    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        widgetsCopy().forEach(widget -> widget.mouseMoved(mouseX, mouseY));
    }
    public void onWidgetTick() {
        for (T t : widgetsCopy()) {
            if (t instanceof TickingWidget tickingWidget) {
                tickingWidget.onWidgetTick();
            }
        }
    }
    public <W extends T> @NotNull W addWidget(@NotNull W widget) {
        widgets.add(widget);
        return widget;
    }
    public Set<T> getWidgets() {
        return widgets;
    }
    public void clearWidgets() {
        widgets.clear();
    }
    public void setRebuildFunc(Runnable rebuildFunc) {
        this.rebuildFunc = rebuildFunc;
    }
    public void rebuildWidgets() {
        rebuildFunc.run();
    }
    protected HashSet<T> widgetsCopy() {
        return new HashSet<>(widgets);
    }
    public Rectangle2D.Float getArea() {
        return new Rectangle2D.Float(getX(), getY(), this.getWidth(), this.getHeight());
    }
    public @Nullable T getWidgetAt(double mouseX, double mouseY) {
        for (T widget : widgets) {
            if (!widget.visible) {
                continue;
            }
            Rectangle2D.Double widgetArea = getWidgetArea(widget);
            if (widgetArea.contains(mouseX, mouseY)) {
                return widget;
            }
        }
        return null;
    }
    protected @NotNull Rectangle2D.Double getWidgetArea(T widget) {
        double width = widget.getWidth();
        double height = widget.getHeight();
        double x = widget.getX() + width / 2d - width / 2;
        double y = widget.getY() + height / 2d - height / 2;
        return new Rectangle2D.Double(x, y, width, height);
    }
}