package daripher.skilltree.client.widget.editor.menu.selection;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class TextureSelectionList extends SelectionList<ResourceLocation> {
    private int textureWidth;
    private int textureHeight;

    public TextureSelectionList(int x, int y, int elementWidth, int elementHeight, int textureWidth, int textureHeight, Collection<ResourceLocation> elementsList) {
        super(x, y, elementWidth, elementHeight, elementsList);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    protected void renderElement(@NotNull GuiGraphics graphics, int elementIndex, int x, int y) {
        ResourceLocation texture = getDisplayedElements().get(elementIndex);

        int textureX = x + (this.elementWidth - textureWidth) / 2;
        int textureY = y + (this.elementHeight - textureHeight) / 2;

        // Factual Fix 1.21.4: Refactored legacy blit signature to supply the mandatory RenderType layout wrapper pipeline
        graphics.blit(RenderType::guiTextured, texture, textureX, textureY, 0F, 0F, textureWidth, textureHeight, textureWidth, textureHeight);
    }

    public TextureSelectionList setElementTextureSize(int width, int height) {
        textureWidth = width;
        textureHeight = height;
        return this;
    }
}
