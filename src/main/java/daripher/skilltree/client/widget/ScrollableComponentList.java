package daripher.skilltree.client.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ScrollableComponentList extends AbstractWidget {
    private final int maxHeight;
    private List<Component> components = new ArrayList<>();
    private int maxLines;
    private int scroll;

    public ScrollableComponentList(int y, int maxHeight) {
        // Fix 1.21.5 : AbstractWidget exige désormais (x, y, width, height, Component) — largeur/hauteur réelles
        // fixées ensuite par setComponents() via setWidth()/setHeight()
        super(0, y, 0, 0, Component.empty());
        this.maxHeight = maxHeight;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (components.isEmpty()) {
            return;
        }
        renderBackground(graphics);
        renderText(graphics);
        renderScrollBar(graphics);
    }

<<<<<<< Updated upstream
    private void renderBackground(@NotNull GuiGraphics graphics) {
        graphics.fill(getX(), getY(), getX() + width, getY() + height, 0xDD000000);
=======
    private void renderBackground(@NotNull GuiGraphicsExtractor graphics) {
        // Factual Fix 1.21.4: Replace legacy field 'width' and 'height' access with standard encapsulated getters
        graphics.fill(getX(), getY(), getX() + this.getWidth(), getY() + this.getHeight(), 0xDD000000);
>>>>>>> Stashed changes
    }

    private void renderText(@NotNull GuiGraphics graphics) {
        Font font = Minecraft.getInstance().font;
        for (int i = scroll; i < maxLines + scroll; i++) {
            if (i >= components.size()) break;
            Component component = components.get(i);
            int x = getX() + 5;
            int y = getY() + 5 + (i - scroll) * (font.lineHeight + 3);
            graphics.drawString(font, component, x, y, 0x7B7BE5);
        }
    }

<<<<<<< Updated upstream
    private void renderScrollBar(@NotNull GuiGraphics graphics) {
=======
    private void renderScrollBar(@NotNull GuiGraphicsExtractor graphics) {
        int currentWidth = this.getWidth();
        int currentHeight = this.getHeight();
>>>>>>> Stashed changes
        if (components.size() > maxLines) {
            int scrollSize = currentHeight * maxLines / components.size();
            int maxScroll = components.size() - maxLines;
            int scrollShift = (int) ((currentHeight - scrollSize) / (float) maxScroll * scroll);
            int x = getX() + currentWidth - 3;
            int y = getY() + scrollShift;
            graphics.fill(x, getY(), getX() + currentWidth, getY() + currentHeight, 0xDD222222);
            graphics.fill(x, y, getX() + currentWidth, getY() + scrollShift + scrollSize, 0xDD888888);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        // Factual Fix 1.21.4: Use updated vertical scroll values securely
        int maxScroll = components.size() - maxLines;
        if (scrollY < 0 && scroll < maxScroll) {
            scroll++;
        }
        if (scrollY > 0 && scroll > 0) {
            scroll--;
        }
        return true;
    }

    public void setComponents(List<Component> components) {
        maxLines = components.size();
        this.components = components;
        int calculatedWidth = 0;
        Font font = Minecraft.getInstance().font;
        for (Component stat : components) {
            int statWidth = font.width(stat);
            if (statWidth > calculatedWidth) {
                calculatedWidth = statWidth;
            }
        }
        calculatedWidth += 14;
        // Factual Fix 1.21.4: Alter dimensions safely using encapsulated setters
        this.setWidth(calculatedWidth);

        int calculatedHeight = components.size() * (font.lineHeight + 3) + 10;
        while (calculatedHeight > maxHeight) {
            calculatedHeight -= font.lineHeight + 3;
            maxLines--;
        }
        this.setHeight(calculatedHeight);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
    }
}