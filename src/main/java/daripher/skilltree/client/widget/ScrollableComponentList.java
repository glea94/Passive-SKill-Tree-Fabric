package daripher.skilltree.client.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
        
        
        super(0, y, 0, 0, Component.empty());
        this.maxHeight = maxHeight;
    }

    @Override
    public void extractWidgetRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (components.isEmpty()) {
            return;
        }
        renderBackground(graphics);
        renderText(graphics);
        renderScrollBar(graphics);
    }

    private void renderBackground(@NotNull GuiGraphicsExtractor graphics) {
        
        graphics.fill(getX(), getY(), getX() + this.getWidth(), getY() + this.getHeight(), 0xDD000000);
    }

    private void renderText(@NotNull GuiGraphicsExtractor graphics) {
        Font font = Minecraft.getInstance().font;
        for (int i = scroll; i < maxLines + scroll; i++) {
            if (i >= components.size()) break;
            Component component = components.get(i);
            int x = getX() + 5;
            int y = getY() + 5 + (i - scroll) * (font.lineHeight + 3);
            
            
            graphics.text(font, component, x, y, 0xFF7B7BE5);
        }
    }

    private void renderScrollBar(@NotNull GuiGraphicsExtractor graphics) {
        int currentWidth = this.getWidth();
        int currentHeight = this.getHeight();
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